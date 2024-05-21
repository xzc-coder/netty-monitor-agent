package com.github.netty.monitor.agent.task;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.NettyMonitorCache;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * @Date: 2022/6/12 16:35
 * @Description: 常量类
 * @author: xzc-coder
 */
public class ChannelIntervalStatisticsTask implements Runnable {

    private Channel channel;

    public ChannelIntervalStatisticsTask(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        if(channel.isActive()) {
            EventLoop eventLoop = channel.eventLoop();
            ChannelMonitorData channelMonitorData = NettyMonitorCache.getNettyMonitorData(channel);
            long intervalInputByte = channelMonitorData.getIntervalInputByte();
            int inputRateByte = (int) (intervalInputByte / Constant.STATISTICS_INTERVAL_SECOND);
            channelMonitorData.setInputRateByte(inputRateByte);
            channelMonitorData.setIntervalInputByte(0);
            long intervalOutputByte = channelMonitorData.getIntervalOutputByte();
            int outputRateByte = (int) (intervalOutputByte / Constant.STATISTICS_INTERVAL_SECOND);
            channelMonitorData.setOutputRateByte(outputRateByte);
            channelMonitorData.setIntervalOutputByte(0);
            String localAddress = channelMonitorData.getLocalAddress().toString();
            String remoteAddress = "";
            if(channelMonitorData.getRemoteAddress() != null) {
                remoteAddress = channelMonitorData.getRemoteAddress().toString();
            }
//            System.out.println(String.format("通道输入信息，Channel：%s，local：%s，remote：%s，速率：%d/b，累计读取字节数：%d", channelMonitorData.getChannelId(),
//                    localAddress, remoteAddress, inputRateByte, channelMonitorData.getAccumulateInputByte()));
//            System.out.println(String.format("通道输入出信息，Channel：%s，local：%s，remote：%s，速率：%d/b，累计输出字节数：%d", channelMonitorData.getChannelId(),
//                    localAddress, remoteAddress, inputRateByte, channelMonitorData.getAccumulateOutputByte()));
            eventLoop.schedule(this,Constant.STATISTICS_INTERVAL_SECOND, TimeUnit.SECONDS);
        }
    }

}
