package com.github.netty.monitor.agent.monitor;

import com.github.netty.monitor.agent.NettyElementMatcher;
import com.github.netty.monitor.agent.NettyMonitor;
import com.github.netty.monitor.agent.NettyMonitorCache;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;


/**
 * @Date: 2022/6/19 23:34
 * @Description: 输出的数据监控
 * @author: xzc-coder
 */
public class OutputDataMonitor implements NettyMonitor {


    @Override
    public void beforeIntercept(Object target, Method method, Object[] args, Map<String, Object> contextMap) {
        String methodName = method.getName();
        if(NettyElementMatcher.METHOD_WRITE.equals(methodName)) {
            write((ChannelHandlerContext) args[0],args[1], (ChannelPromise) args[2]);
        }
    }

    private void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise){
        if(msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            promise.addListener((future -> {
                int writableBytes = byteBuf.writableBytes();
                Channel channel = ctx.channel();
                writeStatistics(channel,writableBytes);
            }));
        }else if(msg instanceof DatagramPacket) {
            DatagramPacket datagramPacket = (DatagramPacket) msg;
            ByteBuf byteBuf = datagramPacket.content();
            promise.addListener((future -> {
                int writableBytes = byteBuf.writableBytes();
                Channel channel = ctx.channel();
                writeStatistics(channel,writableBytes);
            }));

        }
    }

    private void writeStatistics(Channel channel,int writableBytes) {
        ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
        channelMonitorData.addTotalOutputByte(writableBytes);
        channelMonitorData.addIntervalOutputByte(writableBytes);
        channelMonitorData.setLastOutputDateTime(new Date());
        GlobalMonitorData globalMonitorData = GlobalMonitorData.getInstance();
        globalMonitorData.addTotalOutputByte(writableBytes);
        globalMonitorData.addIntervalOutputByte(writableBytes);
    }
}
