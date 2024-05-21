package com.github.netty.monitor.agent.monitor;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.NettyElementMatcher;
import com.github.netty.monitor.agent.NettyMonitor;
import com.github.netty.monitor.agent.NettyMonitorCache;
import com.github.netty.monitor.agent.display.MonitorOutService;
import com.github.netty.monitor.agent.task.GlobalIntervalStatisticsTask;
import com.github.netty.monitor.agent.task.MonitorDataIntervalTask;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date: 2022/6/19 18:22
 * @Description: 全局的监控数据
 * @author: xzc-coder
 */
public class BootstrapMonitor implements NettyMonitor {

    private static ScheduledExecutorService scheduledExecutorService;

    @Override
    public void afterIntercept(Object target, Method method, Object[] args, Object result, Map<String, Object> contextMap) throws Exception {
        String methodName = method.getName();
        if(NettyElementMatcher.METHOD_CONNECT.equals(methodName)) {
            //客户端的启动监控 connect方法被调用时
            if(target instanceof Bootstrap) {
                enableEventLoopStatisticsScheduled();
                Bootstrap bootstrap = (Bootstrap) target;
                EventLoopGroup eventLoopGroup = bootstrap.config().group();
                NettyMonitorCache.addEventLoopGroup(eventLoopGroup);
            }
        }else if(NettyElementMatcher.METHOD_BIND.equals(methodName)) {
            //服务器的启动监控 bind方法被调用时
            if(target instanceof ServerBootstrap) {
                enableEventLoopStatisticsScheduled();
                ServerBootstrap serverBootstrap = (ServerBootstrap) target;
                EventLoopGroup eventLoopGroup = serverBootstrap.config().group();
                NettyMonitorCache.addEventLoopGroup(eventLoopGroup);
            }
        }
    }

    /**
     * 开启EventLoop的定时任务监控
     * @throws Exception
     */
    private static synchronized void enableEventLoopStatisticsScheduled() throws Exception {
        if(scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    String name = "pool-thread-" + threadNumber.getAndIncrement();
                    Thread thread = new Thread(r);
                    thread.setName(name);
                    thread.setDaemon(true);
                    return thread;
                }
            });
            scheduledExecutorService.schedule(new GlobalIntervalStatisticsTask(scheduledExecutorService), Constant.STATISTICS_INTERVAL_SECOND,TimeUnit.SECONDS);
            scheduledExecutorService.schedule(new MonitorDataIntervalTask(scheduledExecutorService, MonitorOutService.getInstance()), Constant.STATISTICS_INTERVAL_SECOND,TimeUnit.SECONDS);
        }
    }

}
