package com.github.netty.monitor.agent.monitor;

import com.github.netty.monitor.agent.NettyElementMatcher;
import com.github.netty.monitor.agent.NettyMonitor;
import com.github.netty.monitor.agent.NettyMonitorCache;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;


/**
 * @Date: 2022/6/19 21:43
 * @Description: 输入的数据监控
 * @author: xzc-coder
 */
public class InputDataMonitor implements NettyMonitor {

    @Override
    public void beforeIntercept(Object target, Method method, Object[] args, Map<String, Object> contextMap) {
        String methodName = method.getName();
        if(NettyElementMatcher.METHOD_CHANNEL_READ.equals(methodName)) {
            channelRead((ChannelHandlerContext) args[0],args[1]);
        } else if(NettyElementMatcher.METHOD_EXCEPTION_CAUGHT.equals(methodName)) {
            exceptionCaught((ChannelHandlerContext)args[0],(Throwable)args[1]);
        } else if(NettyElementMatcher.METHOD_USER_EVENT_TRIGGERED.equals(methodName)) {
            userEventTriggered((ChannelHandlerContext) args[0],args[1]);
        }
    }

    private void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            Channel channel = ctx.channel();
            int readableBytes = byteBuf.readableBytes();
            readStatistics(channel,readableBytes);
        }else if(msg instanceof DatagramPacket) {
            DatagramPacket datagramPacket = (DatagramPacket) msg;
            Channel channel = ctx.channel();
            ByteBuf byteBuf = datagramPacket.content();
            int readableBytes = byteBuf.readableBytes();
            readStatistics(channel,readableBytes);
        }
    }

    private void readStatistics(Channel channel,int readableBytes) {
        ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
        channelMonitorData.addIntervalInputByte(readableBytes);
        channelMonitorData.addTotalInputByte(readableBytes);
        channelMonitorData.setLastInputDateTime(new Date());
        GlobalMonitorData globalMonitorData = GlobalMonitorData.getInstance();
        globalMonitorData.addTotalInputByte(readableBytes);
        globalMonitorData.addIntervalInputByte(readableBytes);
    }


    private void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
        channelMonitorData.setLastExceptionDateTime(new Date());
        channelMonitorData.setLastExceptionMessage(cause.getMessage());
    }

    private void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        Channel channel = ctx.channel();
        ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
        channelMonitorData.setLastEventTriggeredDateTime(new Date());
        channelMonitorData.setEventTriggeredCount(channelMonitorData.getEventTriggeredCount() + 1);

    }
}
