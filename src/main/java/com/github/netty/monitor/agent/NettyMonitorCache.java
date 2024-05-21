package com.github.netty.monitor.agent;

import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;
import io.netty.channel.*;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Date: 2022/6/11 11:48
 * @Description: Netty监控器缓存
 * @author: xzc-coder
 */
public class NettyMonitorCache {

    private NettyMonitorCache() {
    }

    /**
     * 统计Channel相关的数据缓存
     */
    private static final Map<ChannelId, ChannelMonitorData> channelMonitorDataMap = new ConcurrentHashMap<>();
    /**
     * EventLoopGroup的缓存，不存数据是因为通过定时任务去从EventLoopGroup中获取数据并统计
     */
    private static final Set<EventLoopGroup> eventLoopGroupSet = new CopyOnWriteArraySet<>();
    /**
     * 统计某个线程下的Channel数量缓存
     */
    private static final Map<Long, Integer> eventLoopChannelCountMap = new ConcurrentHashMap<>();

    public static ChannelMonitorData removeNettyMonitorData(Channel channel) {
        return removeNettyMonitorData(channel.id());
    }

    public static ChannelMonitorData removeNettyMonitorData(ChannelId channelId) {
        return channelMonitorDataMap.remove(channelId);
    }

    public static ChannelMonitorData getNettyMonitorData(Channel channel) {
        ChannelId channelId = channel.id();
        ChannelMonitorData channelMonitorData = channelMonitorDataMap.get(channelId);
        if (channelMonitorData == null) {
            channelMonitorData = new ChannelMonitorData(channel);
            EventLoop eventLoop = channel.eventLoop();
            if (eventLoop instanceof SingleThreadEventExecutor) {
                SingleThreadEventExecutor singleThreadEventExecutor = (SingleThreadEventExecutor) eventLoop;
                String name = singleThreadEventExecutor.threadProperties().name();
                channelMonitorData.setEventLoopName(name);
            }
            channelMonitorDataMap.put(channelId, channelMonitorData);
        }
        return channelMonitorData;
    }

    public static List<ChannelMonitorData> getChannelMonitorDataList() {
        return new ArrayList<>(channelMonitorDataMap.values());
    }


    public static GlobalMonitorData getGlobalMonitorData() {
        return GlobalMonitorData.getInstance();
    }

    public static Set<EventLoopGroup> getEventLoopGroupSet() {
        return eventLoopGroupSet;
    }

    public static void addEventLoopGroup(EventLoopGroup eventLoopGroup) {
        eventLoopGroupSet.add(eventLoopGroup);
    }

    public static void removeEventLoopGroup(EventLoopGroup eventLoopGroup) {
        eventLoopGroupSet.remove(eventLoopGroup);
    }

    public static void channelCountDecrement(long threadId) {
        Integer channelCount = eventLoopChannelCountMap.get(threadId);
        if (channelCount != null) {
            --channelCount;
            eventLoopChannelCountMap.put(threadId, channelCount);
        }
    }

    public static void channelCountIncrement(long threadId) {
        Integer channelCount = eventLoopChannelCountMap.get(threadId);
        if (channelCount == null) {
            channelCount = 0;
        }
        ++channelCount;
        eventLoopChannelCountMap.put(threadId, channelCount);
    }

    public static Integer getChannelCount(long threadId) {
        return eventLoopChannelCountMap.get(threadId);
    }

    public static Integer removeChannelCount(long threadId) {
        return eventLoopChannelCountMap.remove(threadId);
    }

}
