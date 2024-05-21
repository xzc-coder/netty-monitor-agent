package com.github.netty.monitor.agent.monitor;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.NettyElementMatcher;
import com.github.netty.monitor.agent.NettyMonitor;
import com.github.netty.monitor.agent.NettyMonitorCache;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;
import com.github.netty.monitor.agent.task.ChannelIntervalStatisticsTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Date: 2022/6/19 19:52
 * @Description: Channel数据监控
 * @author: xzc-coder
 */
public class ChannelStateMonitor implements NettyMonitor {

    @Override
    public void beforeIntercept(Object target, Method method, Object[] args, Map<String, Object> contextMap) {
        String methodName = method.getName();
        if (NettyElementMatcher.METHOD_CHANNEL_ACTIVE.equals(methodName)) {
            channelActive((ChannelHandlerContext) args[0]);
        } else if(NettyElementMatcher.METHOD_CHANNEL_INACTIVE.equals(methodName)) {
            channelInactive((ChannelHandlerContext) args[0]);
        }
    }

    private void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        //不算监听端口的Channel
        if(channel instanceof ServerSocketChannel) {
            return;
        }
        GlobalMonitorData globalMonitorData = GlobalMonitorData.getInstance();
        globalMonitorData.activeChannelCountIncrement();
        globalMonitorData.totalActiveChannelCountIncrement();
        ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
        channelMonitorData.setActiveDateTime(new Date());
        EventLoop eventLoop = channel.eventLoop();
        if(eventLoop instanceof SingleThreadEventExecutor) {
            SingleThreadEventExecutor singleThreadEventExecutor = (SingleThreadEventExecutor) eventLoop;
            long threadId = singleThreadEventExecutor.threadProperties().id();
            NettyMonitorCache.channelCountIncrement(threadId);
        }
        eventLoop.schedule(new ChannelIntervalStatisticsTask(channel), Constant.STATISTICS_INTERVAL_SECOND,TimeUnit.SECONDS);
    }

    private void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        if(channel instanceof ServerSocketChannel) {
            return;
        }
        GlobalMonitorData globalMonitorData = GlobalMonitorData.getInstance();
        globalMonitorData.activeChannelCountDecrement();
        globalMonitorData.totalInactiveChannelCountIncrement();
        ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
        channelMonitorData.setInactiveDateTime(new Date());
        EventLoop eventLoop = channel.eventLoop();
        if(eventLoop instanceof SingleThreadEventExecutor) {
            SingleThreadEventExecutor singleThreadEventExecutor = (SingleThreadEventExecutor) eventLoop;
            long threadId = singleThreadEventExecutor.threadProperties().id();
            NettyMonitorCache.channelCountDecrement(threadId);
        }
        if(Constant.CHANNEL_MONITOR_REMOVE_DELAY_SECOND > 0) {
            channel.eventLoop().schedule(() -> NettyMonitorCache.removeNettyMonitorData(channel), Constant.CHANNEL_MONITOR_REMOVE_DELAY_SECOND, TimeUnit.SECONDS);
        }else {
            NettyMonitorCache.removeNettyMonitorData(channel);
        }
    }
}
