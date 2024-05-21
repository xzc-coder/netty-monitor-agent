package com.github.netty.monitor.agent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.SocketAddress;


/**
 * @Date: 2022/6/11 10:58
 * @Description: Netty元素匹配器，既匹配一些拦截点
 * @author: xzc-coder
 */
public class NettyElementMatcher {

    private NettyElementMatcher() {
    }


    public static final String NETTY = "netty";

    public static final String CLASS_HEAD_CONTEXT = "io.netty.channel.DefaultChannelPipeline$HeadContext";

    public static final String CLASS_SERVER_BOOTSTRAP = "io.netty.bootstrap.ServerBootstrap";

    public static final String CLASS_BOOTSTRAP = "io.netty.bootstrap.Bootstrap";

    public static final String METHOD_CHANNEL_READ = "channelRead";

    public static final String METHOD_WRITE = "write";

    public static final String METHOD_CHANNEL_ACTIVE = "channelActive";

    public static final String METHOD_CHANNEL_INACTIVE = "channelInactive";

    public static final String METHOD_USER_EVENT_TRIGGERED = "userEventTriggered";

    public static final String METHOD_EXCEPTION_CAUGHT = "exceptionCaught";

    public static final String METHOD_CONNECT = "connect";

    public static final String METHOD_BIND = "bind";

    public static final ElementMatcher.Junction CLASS_HEAD_CONTEXT_MATCHER = ElementMatchers.named(CLASS_HEAD_CONTEXT);

    public static final ElementMatcher.Junction CLASS_SERVER_BOOTSTRAP_MATCHER = ElementMatchers.named(CLASS_SERVER_BOOTSTRAP);

    public static final ElementMatcher.Junction CLASS_BOOTSTRAP_MATCHER = ElementMatchers.named(CLASS_BOOTSTRAP);

    public static final ElementMatcher.Junction HEAD_CONTEXT_METHOD_CHANNEL_READ_MATCHER = ElementMatchers.named(METHOD_CHANNEL_READ).and(ElementMatchers.takesArguments(ChannelHandlerContext.class, Object.class));

    public static final ElementMatcher.Junction HEAD_CONTEXT_METHOD_WRITE_MATCHER = ElementMatchers.named(METHOD_WRITE).and(ElementMatchers.takesArguments(ChannelHandlerContext.class, Object.class, ChannelPromise.class));

    public static final ElementMatcher.Junction HEAD_CONTEXT_METHOD_CHANNEL_ACTIVE_MATCHER = ElementMatchers.named(METHOD_CHANNEL_ACTIVE).and(ElementMatchers.takesArguments(ChannelHandlerContext.class));

    public static final ElementMatcher.Junction HEAD_CONTEXT_METHOD_CHANNEL_INACTIVE_MATCHER = ElementMatchers.named(METHOD_CHANNEL_INACTIVE).and(ElementMatchers.takesArguments(ChannelHandlerContext.class));

    public static final ElementMatcher.Junction HEAD_CONTEXT_METHOD_USER_EVENT_TRIGGERED_MATCHER = ElementMatchers.named(METHOD_USER_EVENT_TRIGGERED).and(ElementMatchers.takesArguments(ChannelHandlerContext.class, Object.class));

    public static final ElementMatcher.Junction HEAD_CONTEXT_METHOD_EXCEPTION_CAUGHT_MATCHER = ElementMatchers.named(METHOD_EXCEPTION_CAUGHT).and(ElementMatchers.takesArguments(Throwable.class));

    public static final ElementMatcher.Junction SERVER_BOOTSTRAP_METHOD_BIND_MATCHER = (ElementMatchers.named(METHOD_BIND).and(ElementMatchers.takesArguments(SocketAddress.class)))
            .or(ElementMatchers.named(METHOD_BIND).and(ElementMatchers.takesNoArguments()));

    public static final ElementMatcher.Junction BOOTSTRAP_METHOD_CONNECT_MATCHER = (ElementMatchers.named(METHOD_CONNECT).and(ElementMatchers.takesArguments(SocketAddress.class, SocketAddress.class)))
            .or(ElementMatchers.named(METHOD_CONNECT).and(ElementMatchers.takesArguments(SocketAddress.class)))
            .or(ElementMatchers.named(METHOD_CONNECT).and(ElementMatchers.takesNoArguments()));


}
