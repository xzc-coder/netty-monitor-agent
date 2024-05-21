package com.github.netty.monitor.agent;

import com.github.netty.monitor.agent.monitor.BootstrapMonitor;
import com.github.netty.monitor.agent.monitor.ChannelStateMonitor;
import com.github.netty.monitor.agent.monitor.InputDataMonitor;
import com.github.netty.monitor.agent.monitor.OutputDataMonitor;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * @Date: 2022/6/11 9:58
 * @Description: Netty拦截器
 * @author: xzc-coder
 */
public class NettyInterceptor {

    private static List<NettyMonitor> nettyMonitorList = new ArrayList<>();

    static {
        nettyMonitorList.add(new InputDataMonitor());
        nettyMonitorList.add(new OutputDataMonitor());
        nettyMonitorList.add(new ChannelStateMonitor());
        nettyMonitorList.add(new BootstrapMonitor());
    }

    @RuntimeType
    public static Object intercept(@This Object target, @Origin Method method, @AllArguments Object[] args, @Pipe @SuperCall Callable callable) throws Exception {
        System.out.println("-----intercept-----------");
        Map<String, Object> contextMap = new HashMap<>();
        beforeIntercept(target, method, args, contextMap);
        Object result = callable.call();
        afterIntercept(target, method, args, result, contextMap);
        return result;
    }

    private static void beforeIntercept(Object target, Method method, Object[] args, Map<String, Object> contextMap) throws Exception{
        for (NettyMonitor nettyMonitor : nettyMonitorList) {
            nettyMonitor.beforeIntercept(target, method, args, contextMap);
        }
    }

    private static void afterIntercept(Object target, Method method, Object[] args, Object result, Map<String, Object> contextMap) throws Exception{
        for (NettyMonitor nettyMonitor : nettyMonitorList) {
            nettyMonitor.afterIntercept(target, method, args, result, contextMap);
        }
    }
}
