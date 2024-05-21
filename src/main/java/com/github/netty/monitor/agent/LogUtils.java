package com.github.netty.monitor.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Date: 2022/6/18 20:05
 * @Description: 日志工具
 * @author: xzc-coder
 */
public class LogUtils {

    private volatile static boolean hasLoggingFramework;

    private static final List<String> LOGGING_FRAMEWORKS = Arrays.asList(
            "org.apache.logging.log4j.core.Logger",
            "ch.qos.logback.classic.Logger",
            "org.apache.log4j.Logger"
    );

    static {
        hasLoggingFramework = isLoggingFrameworkAvailable();
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DEBUG = "DEBUG";
    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";

    private static boolean isLoggingFrameworkAvailable() {
        boolean result = false;
        for(String className : LOGGING_FRAMEWORKS) {
            boolean loggingFrameworkAvailable = isLoggingFrameworkAvailable(className);
            if(loggingFrameworkAvailable) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean isLoggingFrameworkAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isLoggingFramework() {
        return hasLoggingFramework;
    }

    public static void debug(Class clazz,String message) {
        if(hasLoggingFramework) {
            Logger logger = LoggerFactory.getLogger(clazz);
            logger.debug(message);
        }else {
            System.out.println(getPrintContent(clazz,message,DEBUG));
        }
    }


    public static void info(Class clazz,String message) {
        if(hasLoggingFramework) {
            Logger logger = LoggerFactory.getLogger(clazz);
            logger.info(message);
        }else {
            System.out.println(getPrintContent(clazz,message,INFO));
        }
    }


    public static void warn(Class clazz,String message) {
        if(hasLoggingFramework) {
            Logger logger = LoggerFactory.getLogger(clazz);
            logger.warn(message);
        }else {
            System.out.println(getPrintContent(clazz,message,WARN));
        }
    }

    public static void error(Class clazz,String message) {
        if(hasLoggingFramework) {
            Logger logger = LoggerFactory.getLogger(clazz);
            logger.error(message);
        }else {
            System.out.println(getPrintContent(clazz,message,ERROR));
        }
    }

    public static void error(Class clazz,String message,Throwable throwable) {
        if(hasLoggingFramework) {
            Logger logger = LoggerFactory.getLogger(clazz);
            logger.error(message,throwable);
        }else {
            System.out.println(getPrintContent(clazz,message,ERROR));
            throwable.printStackTrace();
        }
    }


    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(new Date());
    }

    private static String getThreadName() {
        return "["+Thread.currentThread().getName()+"]";
    }

    private static String getPrintContent(Class clazz, String message,String level) {
        StringBuilder content = new StringBuilder();
        content.append(getDate())
                .append(Constant.SPACE)
                .append(getThreadName())
                .append(Constant.SPACE)
                .append(level)
                .append(Constant.SPACE)
                .append(clazz.getName())
                .append(Constant.SPACE)
                .append("-")
                .append(Constant.SPACE)
                .append(message);
        return content.toString();
    }
}
