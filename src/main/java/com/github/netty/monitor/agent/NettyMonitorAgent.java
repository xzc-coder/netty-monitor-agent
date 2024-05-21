package com.github.netty.monitor.agent;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;


/**
 * @Date: 2022/6/11 10:26
 * @Description: Netty监控器Agent类
 * @author: xzc-coder
 */
@Slf4j
public class NettyMonitorAgent {

    /**
     * 启动时重新加载class
     *
     * @param agentArgs
     * @param inst      插桩器
     * @throws Exception
     */
    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        LogUtils.info(NettyMonitorAgent.class, "-----------netty agent start-----------");
        Set<TypeDescription> transformedClasses = new HashSet<>();
        //按类添加拦截点匹配器
        inst.addTransformer(new AgentBuilder.Default()
                .type(NettyElementMatcher.CLASS_HEAD_CONTEXT_MATCHER)
                .transform((builder, type, classLoader, module) -> {
                    if (!transformedClasses.contains(type)) {
                        transformedClasses.add(type);
                        return builder
                                .method(NettyElementMatcher.HEAD_CONTEXT_METHOD_CHANNEL_READ_MATCHER
                                        .or(NettyElementMatcher.HEAD_CONTEXT_METHOD_WRITE_MATCHER)
                                        .or(NettyElementMatcher.HEAD_CONTEXT_METHOD_CHANNEL_ACTIVE_MATCHER)
                                        .or(NettyElementMatcher.HEAD_CONTEXT_METHOD_CHANNEL_INACTIVE_MATCHER)
                                        .or(NettyElementMatcher.HEAD_CONTEXT_METHOD_USER_EVENT_TRIGGERED_MATCHER)
                                        .or(NettyElementMatcher.HEAD_CONTEXT_METHOD_EXCEPTION_CAUGHT_MATCHER))
                                .intercept(MethodDelegation.to(NettyInterceptor.class));
                    }
                    return builder;
                }).installOn(inst));

        inst.addTransformer(new AgentBuilder.Default()
                .type(NettyElementMatcher.CLASS_SERVER_BOOTSTRAP_MATCHER)
                .transform((builder, type, classLoader, module) -> {
                    if (!transformedClasses.contains(type)) {
                        transformedClasses.add(type);
                        return builder
                                .method(NettyElementMatcher.SERVER_BOOTSTRAP_METHOD_BIND_MATCHER)
                                .intercept(MethodDelegation.to(NettyInterceptor.class));
                    }
                    return builder;
                }).installOn(inst));

        inst.addTransformer(new AgentBuilder.Default()
                .type(NettyElementMatcher.CLASS_BOOTSTRAP_MATCHER)
                .transform((builder, type, classLoader, module) -> {
                    if (!transformedClasses.contains(type)) {
                        transformedClasses.add(type);
                        return builder
                                .method(NettyElementMatcher.BOOTSTRAP_METHOD_CONNECT_MATCHER)
                                .intercept(MethodDelegation.to(NettyInterceptor.class));
                    }
                    return builder;
                }).installOn(inst));
    }
}
