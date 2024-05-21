package com.github.netty.monitor.agent.data;

import io.netty.channel.Channel;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.Date;


/**
 * @Date: 2022/6/11 16:02
 * @Description: Channel的监控数据
 * @author: xzc-coder
 */
@Data
public class ChannelMonitorData {

    /**
     * channel的ID，短的ID，长的显示不了
     */
    private final String channelId;

    /**
     * Channel
     */
    private final Channel channel;

    /**
     * Channel的类型，class简写
     */
    private final String channelType;

    private InetSocketAddress remoteAddress;

    private InetSocketAddress localAddress;

    /**
     * Channel绑定的eventLoop名
     */
    private String eventLoopName;

    /**
     * channel间隔期内读取的字节数
     */
    private long intervalInputByte = 0;

    /**
     * channel间隔期内写出的字节数
     */
    private long intervalOutputByte = 0;
    /**
     * channel累计读取的字节数
     */
    private long totalInputByte = 0;

    /**
     * channel累计写出的字节数
     */
    private long totalOutputByte = 0;

    /**
     * 读取速率
     */
    private int inputRateByte = 0;

    /**
     * 写出速率
     */
    private int outputRateByte = 0;

    /**
     * 事件触发总数
     */
    private int eventTriggeredCount = 0;

    /**
     * 最后一次异常发生的时间
     */
    private Date lastExceptionDateTime;

    /**
     * 最后一次异常信息
     */
    private String lastExceptionMessage;

    /**
     * 最后一次事件触发时间
     */
    private Date lastEventTriggeredDateTime;

    /**
     * Channel激活时间
     */
    private Date activeDateTime;

    /**
     * Channel失效时间
     */
    private Date inactiveDateTime;

    /**
     * 最后一次读取数据时间
     */
    private Date lastInputDateTime;

    /**
     * 最后一次写出数据时间
     */
    private Date lastOutputDateTime;


    public ChannelMonitorData(Channel channel) {
        this.channel = channel;
        this.channelId = channel.id().asShortText();
        this.channelType = channel.getClass().getSimpleName();
        this.remoteAddress = (InetSocketAddress) channel.remoteAddress();
        this.localAddress = (InetSocketAddress) channel.localAddress();

    }

    public void addIntervalOutputByte(int outputByte) {
        this.intervalOutputByte += outputByte;
    }

    public void addTotalOutputByte(int outputByte) {
        this.totalOutputByte += outputByte;
    }

    public void addIntervalInputByte(int inputByte) {
        this.intervalInputByte += inputByte;
    }

    public void addTotalInputByte(int inputByte) {
        this.totalInputByte += inputByte;
    }

}
