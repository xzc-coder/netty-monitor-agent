package com.github.netty.monitor.agent.data;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @Date: 2022/6/11 18:22
 * @Description: 全局的监控数据
 * @author: xzc-coder
 */
@Data
public class GlobalMonitorData {

    private GlobalMonitorData() {
    }

    /**
     * 监控间隔期内读取的字节数
     */
    private AtomicLong intervalInputByte = new AtomicLong(0);
    /**
     * 全局所有channel累计读取的字节数
     */
    private AtomicLong totalInputByte = new AtomicLong(0);

    /**
     * 监控间隔期内写出的字节数
     */
    private AtomicLong intervalOutputByte = new AtomicLong(0);
    /**
     * 全局所有channel累计写出的字节数
     */
    private AtomicLong totalOutputByte = new AtomicLong(0);

    /**
     * 激活的Channel数量
     */
    private AtomicLong activeChannelCount = new AtomicLong(0);

    /**
     * 读取速率
     */
    private AtomicInteger inputRateByte = new AtomicInteger(0);

    /**
     * 写出速率
     */
    private AtomicInteger outputRateByte = new AtomicInteger(0);

    /**
     * 累计激活的Channel数量
     */
    private AtomicLong totalActiveChannelCount = new AtomicLong(0);

    /**
     * 累计失效的Channel数量
     */
    private AtomicLong totalInactiveChannelCount = new AtomicLong(0);

    /**
     * 池化使用的堆内存
     */
    private AtomicLong pooledUsedHeapMemory = new AtomicLong(0);

    /**
     * 池化使用的直接内存
     */
    private AtomicLong pooledUsedDirectMemory = new AtomicLong(0);
    /**
     * 未池化使用的堆内存
     */
    private AtomicLong unpooledUsedHeapMemory = new AtomicLong(0);
    /**
     * 未池化使用的直接内存
     */
    private AtomicLong unpooledUsedDirectMemory = new AtomicLong(0);

    /**
     * 直接内存总数
     */
    private AtomicLong usedDirectMemory = new AtomicLong(0);

    public void addIntervalInputByte(int inputByte) {
        this.intervalInputByte.addAndGet(inputByte);
    }

    public void addTotalInputByte(int inputByte) {
        this.totalInputByte.addAndGet(inputByte);
    }

    public void addIntervalOutputByte(int outputByte) {
        this.intervalOutputByte.addAndGet(outputByte);
    }

    public void addTotalOutputByte(int outputByte) {
        this.totalOutputByte.addAndGet(outputByte);
    }

    public long activeChannelCountIncrement() {
        return this.activeChannelCount.incrementAndGet();
    }

    public long activeChannelCountDecrement() {
        return this.activeChannelCount.decrementAndGet();
    }

    public long totalActiveChannelCountIncrement() {
        return this.totalActiveChannelCount.incrementAndGet();
    }

    public long totalInactiveChannelCountIncrement() {
        return this.totalInactiveChannelCount.incrementAndGet();
    }

    public static GlobalMonitorData getInstance() {
        return GlobalMonitorDataHolder.INSTANCE.globalMonitorData;
    }


    private enum GlobalMonitorDataHolder {
        INSTANCE;
        private GlobalMonitorData globalMonitorData;

        private GlobalMonitorDataHolder() {
            this.globalMonitorData = new GlobalMonitorData();
        }
    }
}
