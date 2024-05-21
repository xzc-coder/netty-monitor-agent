package com.github.netty.monitor.agent.display;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.EventLoopMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;

import java.lang.reflect.Constructor;
import java.util.List;

public interface MonitorOutService {


    void outputMonitorData(GlobalMonitorData globalMonitorData, List<EventLoopMonitorData> eventLoopMonitorDataList, List<ChannelMonitorData> channelMonitorDataList);

    /**
     * 获取监控数据输出实现类
     * @return 监控数据输出实现类
     * @throws Exception
     */
    static MonitorOutService getInstance() throws Exception {
        MonitorOutService monitorOutService;
        String monitorOutServiceClass = System.getProperty(Constant.MONITOR_OUT_SERVICE_CLASS);
        if(monitorOutServiceClass == null || monitorOutServiceClass.length() == 0) {
            monitorOutService = new LogMonitorOutService();
        }else {
            Class<?> monitorOutServiceClazz = Class.forName(monitorOutServiceClass);
            if(MonitorOutService.class.isAssignableFrom(monitorOutServiceClazz)) {
                Constructor<?> constructor = monitorOutServiceClazz.getConstructor();
                monitorOutService = (MonitorOutService) constructor.newInstance();
            }else {
                throw new IllegalArgumentException(monitorOutServiceClass + "不是 com.xy.agent.display.MonitorOutService 实现类");
            }
        }
        return monitorOutService;
    }
}
