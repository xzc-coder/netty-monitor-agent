package com.github.netty.monitor.agent;


/**
 * @Date: 2022/6/11 10:52
 * @Description: 常量类
 * @author: xzc-coder
 */
public class Constant {

    private Constant() {

    }

    /**
     * 统计间隔，每5秒统计一次
     */
    public static int STATISTICS_INTERVAL_SECOND = 5;

    /**
     * Channel断开连接后延迟多少秒移除
     */

    public static int CHANNEL_MONITOR_REMOVE_DELAY_SECOND = 30;

    public static String SPACE = " ";

    public static String EMPTY = "";

    public static final String B = "B";

    public static final String KB = "KB";

    public static final String MB = "MB";

    public static final String GB = "GB";

    public static final String TB = "TB";

    public static final String RATE = "/s";

    public static final long CARRY = 1024L;

    public static final long KB_CARRY = CARRY;

    public static final long MB_CARRY = KB_CARRY * CARRY;

    public static final long GB_CARRY = MB_CARRY * CARRY;

    public static final long TB_CARRY = GB_CARRY * CARRY;

    public static final int LOG_MEMORY_SCALE = 6;

    public static final int LOG_SCALE = 3;

    public static final int LOG_SCALE_B = 0;

    public static final String MONITOR_OUT_SERVICE_CLASS = "monitorOutServiceClass";
}
