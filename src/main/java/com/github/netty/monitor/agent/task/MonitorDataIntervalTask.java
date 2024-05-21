package com.github.netty.monitor.agent.task;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.LogUtils;
import com.github.netty.monitor.agent.NettyMonitorCache;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.EventLoopMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;
import com.github.netty.monitor.agent.display.MonitorOutService;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.concurrent.ThreadProperties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Date: 2022/6/12 10:16
 * @Description: 总监控数据的定时任务
 * @author: xzc-coder
 */
public class MonitorDataIntervalTask implements Runnable {

    private ScheduledExecutorService scheduledExecutorService;
    private MonitorOutService monitorDataDisplayService;

    public MonitorDataIntervalTask(ScheduledExecutorService scheduledExecutorService, MonitorOutService monitorDataDisplayService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.monitorDataDisplayService = monitorDataDisplayService;
    }

    @Override
    public void run() {
        try {
            //eventLoop的监控
            List<EventLoopMonitorData> eventLoopMonitorDataList = eventLoopStatistics();
            //全局数据监控
            GlobalMonitorData globalMonitorData = NettyMonitorCache.getGlobalMonitorData();
            //channel数据监控
            List<ChannelMonitorData> channelMonitorDataList = NettyMonitorCache.getChannelMonitorDataList();
            //数据展示，默认就日志打印出来
            monitorDataDisplayService.outputMonitorData(globalMonitorData,eventLoopMonitorDataList,channelMonitorDataList);
        } catch (Exception e) {
            LogUtils.error(this.getClass(),"间隔数据统计任务异常",e);
        }finally {
            scheduledExecutorService.schedule(this, Constant.STATISTICS_INTERVAL_SECOND, TimeUnit.SECONDS);
        }
    }


    private List<EventLoopMonitorData> eventLoopStatistics() throws ExecutionException, InterruptedException {
        List<EventLoopMonitorData> result = new ArrayList<>();
        Iterator<EventLoopGroup> iterator = NettyMonitorCache.getEventLoopGroupSet().iterator();
        while (iterator.hasNext()) {
            EventLoopGroup eventLoopGroup = iterator.next();
            if(eventLoopGroup.isShutdown() || eventLoopGroup.isShuttingDown()) {
                iterator.remove();
            }
            for(EventExecutor eventExecutor : eventLoopGroup) {
                if(eventExecutor instanceof SingleThreadEventExecutor) {
                    SingleThreadEventExecutor singleThreadEventExecutor = (SingleThreadEventExecutor) eventExecutor;
                    ThreadProperties threadProperties = singleThreadEventExecutor.threadProperties();
                    long threadId = threadProperties.id();
                    if(singleThreadEventExecutor.isShutdown() || singleThreadEventExecutor.isShuttingDown()) {
                        NettyMonitorCache.removeChannelCount(threadId);
                    }else {
                        EventLoopMonitorData eventLoopMonitorData = new EventLoopMonitorData(threadProperties.name());
                        int pendingTasks = singleThreadEventExecutor.pendingTasks();
                        eventLoopMonitorData.setPendingTaskCount(pendingTasks);
                        Thread.State state = threadProperties.state();
                        eventLoopMonitorData.setState(state);
                        Integer channelCount = NettyMonitorCache.getChannelCount(threadId);
                        eventLoopMonitorData.setChannelCount(channelCount);
                        result.add(eventLoopMonitorData);
                    }
                }
            }
        }
        return result;
    }
}
