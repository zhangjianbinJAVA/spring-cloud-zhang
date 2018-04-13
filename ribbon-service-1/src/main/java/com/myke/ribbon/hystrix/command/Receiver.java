package com.myke.ribbon.hystrix.command;

/**
 * 抽象命令
 */
interface Command {
    void execute();
}

/**
 * 接收者
 */
public class Receiver {
    public void action() {
        // 真正的业务逻辑
        System.out.println("真正的业务逻辑");
    }

}

/**
 * 具体命令实现
 */
class ConcreteCommand implements Command {

    private Receiver receiver;

    public ConcreteCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.action();
    }
}

/**
 * 客户端调用者
 */
class Invoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void action() {
        command.execute();
    }
}

/**
 * Receiver 接收者，它知道如何处理具体的业务逻辑
 * <p>
 * Command 抽象命令，它定义了一个命令对象应具备的一系列命令操作，
 * <p>
 * 当命令操作被调用的时候就会触发接收者去做具 体命令对应的业务逻辑
 * <p>
 * ConcreteCommand 具体的命令实现，在这里它绑定了命令操作与接收者之间的关系
 * <p>
 * Invoker 调用者，持有命令对象，通过命令对象完成具体的业务回逻辑
 * <p>
 * <p>
 * 调用者Invoker 与操作者 Receiver 通过 Command 命令接口实现了解耦，
 * <p>
 * Invoker 和 Receiver 的关系类似 请求 -- 响应 模式
 * <p>
 * 命令模式比较适用于实现记录日志，撤销操作、队列请求等
 */
class Client {
    // 客户端
    public static void main(String[] args) {
        Receiver receiver = new Receiver();// 模拟 响应
        Command command = new ConcreteCommand(receiver);

        Invoker invoker = new Invoker();// 模拟 请求
        invoker.setCommand(command);

        // 客户端通过调用者来执行命令，执行请求。
        invoker.action();
    }
}
