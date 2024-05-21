package com.github.netty.monitor.agent.task;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.LogUtils;
import com.github.netty.monitor.agent.data.GlobalMonitorData;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocatorMetric;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.internal.PlatformDependent;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Date: 2022/6/12 13:21
 * @Description: 常量类
 * @author: xzc-coder
 */
public class GlobalIntervalStatisticsTask implements Runnable {

    private ScheduledExecutorService scheduledExecutorService;

    public GlobalIntervalStatisticsTask(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }


    @Override
    public void run() {
        try {
            GlobalMonitorData globalMonitorData = GlobalMonitorData.getInstance();
            int globalInputRateByte = calculateInputRateByte(globalMonitorData);
            globalMonitorData.getInputRateByte().set(globalInputRateByte);
            globalMonitorData.getIntervalInputByte().set(0);
            int globalOutputRateByte = calculateOutputRateByte(globalMonitorData);
            globalMonitorData.getOutputRateByte().set(globalOutputRateByte);
            globalMonitorData.getIntervalOutputByte().set(0);
            //池化和非池化的低版本没有该类，低版本需要通过反射去获取（4.0.45.Final及之后的版本有）
            PooledByteBufAllocatorMetric pooledByteBufAllocatorMetric = PooledByteBufAllocator.DEFAULT.metric();
            globalMonitorData.getPooledUsedDirectMemory().set(pooledByteBufAllocatorMetric.usedDirectMemory());
            globalMonitorData.getPooledUsedHeapMemory().set(pooledByteBufAllocatorMetric.usedHeapMemory());
            ByteBufAllocatorMetric unpooledByteBufAllocatorMetric = UnpooledByteBufAllocator.DEFAULT.metric();
            globalMonitorData.getUnpooledUsedDirectMemory().set(unpooledByteBufAllocatorMetric.usedDirectMemory());
            globalMonitorData.getUnpooledUsedHeapMemory().set(unpooledByteBufAllocatorMetric.usedHeapMemory());
            Field field = PlatformDependent.class.getDeclaredField("DIRECT_MEMORY_COUNTER");
            field.setAccessible(true);
            AtomicLong directMemoryCounter = (AtomicLong) field.get(PlatformDependent.class);
            globalMonitorData.setUsedDirectMemory(directMemoryCounter);
        }catch (Exception e) {
            LogUtils.error(this.getClass(),"全局数据统计任务异常",e);
        }finally {
            this.scheduledExecutorService.schedule(this, Constant.STATISTICS_INTERVAL_SECOND, TimeUnit.SECONDS);
        }
    }

    private int calculateInputRateByte(GlobalMonitorData globalMonitorData) {
        return (int) (globalMonitorData.getIntervalInputByte().get() / Constant.STATISTICS_INTERVAL_SECOND);
    }

    private int calculateOutputRateByte(GlobalMonitorData globalMonitorData) {
        return (int) (globalMonitorData.getIntervalOutputByte().get() / Constant.STATISTICS_INTERVAL_SECOND);
    }
}
