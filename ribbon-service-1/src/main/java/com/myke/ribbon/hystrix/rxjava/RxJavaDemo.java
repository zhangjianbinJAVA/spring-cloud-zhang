package com.myke.ribbon.hystrix.rxjava;


import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

import java.util.logging.Logger;

/**
 * 参考 https://blog.csdn.net/qq_33553515/article/details/62898070
 */
public class RxJavaDemo {

    public static Logger Log = Logger.getLogger(RxJavaDemo.class.getName());

    /**
     * 1. 创建 观察者 Observer 也可以看做是 创建订阅者 subscriber
     */
    public static Observer<String> observer = new Observer<String>() {

        /**
         * 向下传递事件时 调用
         *
         * 观察者的 onNext()方法中 成功的打印出来 “面包事件”
         * 这样就完成的了从 被观察者 到 观察者的 事件传递。
         *
         * @param s
         */
        @Override
        public void onNext(String s) {
            Log.info("======" + s);

        }

        /**
         * 事件传递完成时 调用
         */
        @Override
        public void onCompleted() {
            Log.info("onCompleted 传递面包事件完成");
        }

        /**
         * 事件传递出错时 调用
         * @param e
         */
        @Override
        public void onError(Throwable e) {
            Log.info("onError 传递面包事件出错了");
        }
    };

    /**
     * 2. 创建 被观察者 Observable 也可以看做是 创建事件源 Observable
     * <p>
     * 当 Observable 被订阅的时候，OnSubscribe 的 call() 方法会自动被 调用
     * 事件序列就会依照设定依次触发。就是 观察者 Subscriber 将会被调用onNext()方法，
     * 将“面包事件”进行传递
     * <p>
     * 由 被观察者 调用了 观察者 的回调方法，就实现了由 被观察者向观察者的事件传递
     */
    public static Observable observable = Observable.create(new Observable.OnSubscribe<String>() {

        @Override
        public void call(Subscriber<? super String> subscriber) {


            /**
             *  4. 订阅结果
             */
            //观察者的调用了两次onNext()方法。可以说明 被观察者可以发射很多次事件。
//            subscriber.onNext("面包事件");
//            subscriber.onNext("面包事件1");
//            subscriber.onNext("面包事件2");

            // 当调用 onCompleted()方法时 在其之后再调用onNext()方法 将不会在进行事件传递
//            subscriber.onNext("面包事件");
//            subscriber.onCompleted();
//            subscriber.onNext("面包事件2"); //面包事件2不会打印出来


            // 当调用 onError()方法时 其后在调用onNext()方法 不在进行事件传递。
//            subscriber.onNext("面包事件");
//            subscriber.onError(new NullPointerException());
//            subscriber.onNext("面包事件2");

            //只调用了 onError()方法。
            subscriber.onNext("面包事件");
            subscriber.onError(new NullPointerException());
            subscriber.onCompleted();
            subscriber.onNext("面包事件2");

            /**
             * 总结
             * 1. onNext()方法可以多次调用，但是只能在 onError()方法或者onCompleted()方法之前调用。
             * 2. onError()方法和 onCompleted()方法同时只能出现一个。当同时出现出现时，只会调用 第一个时间出现的。
             */
        }
    });


    public static void main(String[] args) {
        /**
         * 3. 建立 观察关系
         *
         * 被观察者 调用 subscribe 方法 传入 观察者 observer 建立订阅关系
         *
         * 触发事件的发布。 observable.subscribe(observer)
         */
        Subscription subscribe = observable.subscribe(observer);


    }
}
