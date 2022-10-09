package com.pq.pure.netty.executor;

/**
 * @Author wmf
 * @Date 2022/6/23 11:38
 * @Description
 */
public interface EventLoop extends EventLoopGroup {
    EventLoopGroup parent();
}
