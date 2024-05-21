package com.github.netty.monitor.agent.data;

import lombok.Data;

/**
 * @Date: 2022/6/11 16:32
 * @Description: EventLoop的监控数据
 * @author: xzc-coder
 */
@Data
public class EventLoopMonitorData {

    /**
     * eventLoop名
     */
    private String eventLoopName;
    /**
     * eventLoop下的Channel总数（活跃的）
     */
    private Integer channelCount;
    /**
     * eventLoop中的任务总数
     */
    private Integer pendingTaskCount;

    /**
     * eventLoop所绑定的线程的状态
     */
    private Thread.State state;


    public EventLoopMonitorData(String eventLoopName) {
        this.eventLoopName = eventLoopName;
    }

}
