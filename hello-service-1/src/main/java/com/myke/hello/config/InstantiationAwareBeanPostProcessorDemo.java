package com.myke.hello.config;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 参考 https://blog.csdn.net/bluetjs/article/details/52574103
 */
@Component
@Slf4j
public class InstantiationAwareBeanPostProcessorDemo implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private void processFields(Object bean, Method[] declaredFields) throws NoSuchFieldException, IllegalAccessException {
        log.error("bean:{}", bean.getClass());
        for (Method method : declaredFields) {
            ApiOperation annotation = AnnotationUtils.getAnnotation(method, ApiOperation.class);
            if (annotation == null) {
                continue;
            }

            Extension[] extensions = annotation.extensions();
            for (Extension ext : extensions) {
                ExtensionProperty[] properties = ext.properties();
                for (ExtensionProperty extensionProperty : properties) {
                    String value = extensionProperty.value();

                    log.info("ApiOperation ExtensionProperty:{}", value);

                    InvocationHandler invocationHandler =
                            Proxy.getInvocationHandler(extensionProperty);
                    // 获取 AnnotationInvocationHandler 的 memberValues 字段
                    Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
                    // 因为这个字段事 private final 修饰，所以要打开权限
                    declaredField.setAccessible(true);
                    // 获取 memberValues
                    Map memberValues = (Map) declaredField.get(invocationHandler);
                    memberValues.put("value", "myke");

                    String newValue = extensionProperty.value();
                    log.info("修改之后的注解值@ExtensionProperty:{}", newValue);
                    ReflectionUtils.makeAccessible(method);

                    method.setAccessible(true);
                }
            }
        }
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        Annotation annotation = clazz.getAnnotation(RestController.class);
        if (null != annotation) {
            try {
                processFields(bean, clazz.getMethods());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
