package com.github.netty.monitor.agent;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * @Date: 2022/6/11 10:02
 * @Description: Netty监控器接口
 * @author: xzc-coder
 */
public interface NettyMonitor {

    /**
     * 拦截的方法调用前拦截
     *
     * @param target     目标对象
     * @param method     方法
     * @param args       参数
     * @param contextMap 上下文
     */
    default void beforeIntercept(Object target, Method method, Object[] args, Map<String, Object> contextMap) throws Exception{

    }

    /**
     * 拦截的方法调用后拦截
     *
     * @param target     目标对象
     * @param method     方法
     * @param args       参数
     * @param result     返回值
     * @param contextMap 上下文
     */
    default void afterIntercept(Object target, Method method, Object[] args, Object result, Map<String, Object> contextMap) throws Exception{

    }
}
