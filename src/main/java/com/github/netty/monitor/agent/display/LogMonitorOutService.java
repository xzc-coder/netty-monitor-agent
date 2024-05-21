package com.github.netty.monitor.agent.display;

import com.github.netty.monitor.agent.Constant;
import com.github.netty.monitor.agent.LogUtils;
import com.github.netty.monitor.agent.data.ChannelMonitorData;
import com.github.netty.monitor.agent.data.EventLoopMonitorData;
import com.github.netty.monitor.agent.data.GlobalMonitorData;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Date: 2022/6/20 11:09
 * @Description: 日志的形式输出监控数据（没有日志则控制台打印）
 * @author: xzc-coder
 */
public class LogMonitorOutService implements MonitorOutService {

    private static final String CHANNEL_DATE_FORMAT = "MM-dd HH:mm:ss";

    @Override
    public void outputMonitorData(GlobalMonitorData globalMonitorData, List<EventLoopMonitorData> eventLoopMonitorDataList, List<ChannelMonitorData> channelMonitorDataList) {
        //输出全局监控数据
        outputGlobalMonitorData(globalMonitorData);
        //输出EventLoop监控数据
        outputEventLoopMonitorData(eventLoopMonitorDataList);
        //输出Channel监控数据
        outputChannelMonitorData(channelMonitorDataList);
    }


    private void outputChannelMonitorData(List<ChannelMonitorData> channelMonitorDataList) {
        if (!channelMonitorDataList.isEmpty()) {
            for (ChannelMonitorData channelMonitorData : channelMonitorDataList) {
                final int longContentCount = 21;
                final int midContentCount = 16;
                final int shortContentCount = 14;
                final int tinyContent = 12;
                String localAddress = channelMonitorData.getLocalAddress().getAddress().getHostAddress() + ":" + channelMonitorData.getLocalAddress().getPort();
                String remoteAddress = Constant.EMPTY;
                if (channelMonitorData.getRemoteAddress() != null) {
                    remoteAddress = channelMonitorData.getRemoteAddress().getAddress().getHostAddress() + ":" + channelMonitorData.getRemoteAddress().getPort();
                }
                String adaptionTotalInput = selfAdaptionValue(channelMonitorData.getTotalInputByte(), false);
                String adaptionTotalOutput = selfAdaptionValue(channelMonitorData.getTotalOutputByte(), false);
                String adaptionInputRate = selfAdaptionValue(channelMonitorData.getInputRateByte(), true);
                String adaptionOutputRate = selfAdaptionValue(channelMonitorData.getOutputRateByte(), true);
                String activeDate = toDateStr(channelMonitorData.getActiveDateTime());
                String inactiveDate = toDateStr(channelMonitorData.getInactiveDateTime());
                String lastInputDate = toDateStr(channelMonitorData.getLastInputDateTime());
                String lastOutputDate = toDateStr(channelMonitorData.getLastOutputDateTime());
                String lastEventDate = toDateStr(channelMonitorData.getLastEventTriggeredDateTime());
                String lastExceptDate = toDateStr(channelMonitorData.getLastExceptionDateTime());
                String lastExceptMsg = channelMonitorData.getLastExceptionMessage();

                String localAddressStr = fillContent(localAddress, longContentCount);
                String remoteAddressStr = fillContent(remoteAddress, longContentCount);
                String totalInputStr = fillContent(adaptionTotalInput, tinyContent);
                String totalOutputStr = fillContent(adaptionTotalOutput, tinyContent);
                String inputRateStr = fillContent(adaptionInputRate, shortContentCount);
                String outputRateStr = fillContent(adaptionOutputRate, shortContentCount);
                String activeDateStr = fillContent(activeDate, midContentCount);
                String inactiveDateStr = fillContent(inactiveDate, midContentCount);
                String lastInputDateStr = fillContent(lastInputDate, midContentCount);
                String lastOutputDateStr = fillContent(lastOutputDate, midContentCount);
                String lastEventDateStr = fillContent(lastEventDate, midContentCount);
                String lastExceptDateStr = fillContent(lastExceptDate, midContentCount);
                String lastExceptMsgStr = fillContent(lastExceptMsg, midContentCount);

                LogUtils.debug(this.getClass(), "+-----------------------------------------------------------NettyChannelMonitorData----------------------------------------------------------------------------------------------------------------------------------------+");
                LogUtils.debug(this.getClass(), "|    LocalAddress     |    RemoteAddress    | TotalInput | TotalOutput|   InputRate  |  OutputRate  |   activeDate   |  inactiveDate  |  LastInputDate | LastOutputDate |  LastEventDate | LastExceptDate |  LastExceptMsg |");
                LogUtils.debug(this.getClass(), "+---------------------+---------------------+------------+------------+--------------+--------------+----------------+----------------+----------------+----------------+----------------+----------------+----------------+");
                LogUtils.debug(this.getClass(), String.format("|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|", localAddressStr, remoteAddressStr,
                        totalInputStr, totalOutputStr, inputRateStr, outputRateStr,
                        activeDateStr, inactiveDateStr, lastInputDateStr, lastOutputDateStr,
                        lastEventDateStr, lastExceptDateStr, lastExceptMsgStr));
                LogUtils.debug(this.getClass(), "+-----------------------------------------------------------NettyChannelMonitorData----------------------------------------------------------------------------------------------------------------------------------------+");
            }
        }
    }


    private void outputEventLoopMonitorData(List<EventLoopMonitorData> eventLoopMonitorDataList) {
        if (!eventLoopMonitorDataList.isEmpty()) {
            final int firstContentCount = 26;
            final int otherContentCount = 16;
            for (EventLoopMonitorData eventLoopMonitorData : eventLoopMonitorDataList) {
                Integer channelCount = eventLoopMonitorData.getChannelCount();
                if (channelCount == null) {
                    channelCount = 0;
                }
                String eventLoopName = fillContent(eventLoopMonitorData.getEventLoopName(), firstContentCount);
                String channelCountStr = fillContent(channelCount, otherContentCount);
                String pendingTaskStr = fillContent(eventLoopMonitorData.getPendingTaskCount(), otherContentCount);
                String state = fillContent(eventLoopMonitorData.getState(), otherContentCount);
                LogUtils.debug(this.getClass(), "+------------------------NettyEventLoopMonitorData----------------------------+");
                LogUtils.debug(this.getClass(), "|      EventLoopName       |  ChannelCount  |   PendingTask  |      State     |");
                LogUtils.debug(this.getClass(), "+--------------------------+----------------+----------------+----------------+");
                LogUtils.debug(this.getClass(), String.format("|%s|%s|%s|%s|", eventLoopName, channelCountStr, pendingTaskStr, state));
                LogUtils.debug(this.getClass(), "+-------------------------NettyEventLoopMonitorData---------------------------+");
            }
        }
    }

    private void outputGlobalMonitorData(GlobalMonitorData globalMonitorData) {
        final int longContentCount = 15;
        final int shortContentCount = 13;
        long activeChannel = globalMonitorData.getActiveChannelCount().get();
        String adaptionTotalInput = selfAdaptionValue(globalMonitorData.getTotalInputByte().get(), false);
        String adaptionTotalOutput = selfAdaptionValue(globalMonitorData.getTotalOutputByte().get(), false);
        String adaptionInputRate = selfAdaptionValue(globalMonitorData.getInputRateByte().get(), true);
        String adaptionOutputRate = selfAdaptionValue(globalMonitorData.getOutputRateByte().get(), true);
        String adaptionPooledUsedHeap = selfAdaptionValue(globalMonitorData.getPooledUsedHeapMemory().get(), false, Constant.LOG_MEMORY_SCALE);
        String adaptionPooledUsedDirect = selfAdaptionValue(globalMonitorData.getPooledUsedDirectMemory().get(), false, Constant.LOG_MEMORY_SCALE);
        String adaptionUnpooledUsedHeap = selfAdaptionValue(globalMonitorData.getUnpooledUsedHeapMemory().get(), false, Constant.LOG_MEMORY_SCALE);
        String adaptionUnpooledUsedDirect = selfAdaptionValue(globalMonitorData.getUnpooledUsedDirectMemory().get(), false, Constant.LOG_MEMORY_SCALE);
        String adaptionUsedDirect = selfAdaptionValue(globalMonitorData.getUsedDirectMemory().get(), false, Constant.LOG_MEMORY_SCALE);

        String activeChannelStr = fillContent(activeChannel, shortContentCount);
        String totalInputStr = fillContent(adaptionTotalInput, shortContentCount);
        String totalOutputStr = fillContent(adaptionTotalOutput, shortContentCount);
        String inputRateStr = fillContent(adaptionInputRate, shortContentCount);
        String outputRateStr = fillContent(adaptionOutputRate, shortContentCount);
        String adaptionPooledUsedHeapStr = fillContent(adaptionPooledUsedHeap, longContentCount);
        String adaptionPooledUsedDirectStr = fillContent(adaptionPooledUsedDirect, longContentCount);
        String adaptionUnpooledUsedHeapStr = fillContent(adaptionUnpooledUsedHeap, longContentCount);
        String adaptionUnpooledUsedDirectStr = fillContent(adaptionUnpooledUsedDirect, longContentCount);
        String adaptionUsedDirectStr = fillContent(adaptionUsedDirect, shortContentCount);
        LogUtils.info(this.getClass(), "+-------------------------NettyGlobalMonitorData--------------------------------+");
        LogUtils.info(this.getClass(), "|ActiveChannel|  TotalInput | TotalOutput |  InputRate  |  OutputRate |  PoolUseHeap  | PoolUseDirect | unpoolUseHeap |unpoolUseDirect|  useDirect  |");
        LogUtils.info(this.getClass(), "+-------------+-------------+-------------+-------------+-------------+---------------+---------------+---------------+---------------+");
        LogUtils.info(this.getClass(), String.format("|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|", activeChannelStr, totalInputStr, totalOutputStr, inputRateStr, outputRateStr,
                adaptionPooledUsedHeapStr, adaptionPooledUsedDirectStr, adaptionUnpooledUsedHeapStr, adaptionUnpooledUsedDirectStr, adaptionUsedDirectStr));
        LogUtils.info(this.getClass(), "+-------------------------NettyGlobalMonitorData--------------------------------+");
    }

    private String fillContent(Object value, int count) {
        String str = Constant.EMPTY;
        if (value != null) {
            str = value.toString();
        }
        if (str.length() <= count) {
            int difference = count - str.length();
            int padNum = difference / 2;
            if (difference % 2 == 0) {
                //偶数
                String padContent = repeat(Constant.SPACE.charAt(0), padNum);
                str = padContent + str + padContent;
            } else {
                str = repeat(Constant.SPACE.charAt(0), padNum + 1) + str + repeat(Constant.SPACE.charAt(0), padNum);
            }
        }
        return str;
    }

    public String selfAdaptionValue(long content, boolean isRate) {
        return selfAdaptionValue(content, isRate, Constant.LOG_SCALE);
    }

    public String selfAdaptionValue(long content, boolean isRate, int scale) {
        String unit;
        int scaleNumber = scale;
        BigDecimal bigDecimal = new BigDecimal(content);
        if (content / Constant.TB_CARRY >= 1) {
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(Constant.TB_CARRY));
            unit = Constant.TB;
        } else if (content / Constant.GB_CARRY >= 1) {
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(Constant.GB_CARRY));
            unit = Constant.GB;
        } else if (content / Constant.MB_CARRY >= 1) {
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(Constant.MB_CARRY));
            unit = Constant.MB;
        } else if (content / Constant.KB_CARRY >= 1) {
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(Constant.KB_CARRY));
            unit = Constant.KB;
        } else {
            unit = Constant.B;
            scaleNumber = Constant.LOG_SCALE_B;
        }
        String value = bigDecimal.setScale(scaleNumber, BigDecimal.ROUND_DOWN).toString() + unit;
        if (isRate) {
            value = value + Constant.SPACE + Constant.RATE;
        }
        return value;
    }

    private String repeat(char c, int count) {
        if (count <= 0) {
            return Constant.EMPTY;
        }

        char[] result = new char[count];
        for (int i = 0; i < count; i++) {
            result[i] = c;
        }
        return new String(result);
    }


    private String toDateStr(Date date) {
        String result = Constant.EMPTY;
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(CHANNEL_DATE_FORMAT);
            result = dateFormat.format(date);
        }
        return result;
    }
}
