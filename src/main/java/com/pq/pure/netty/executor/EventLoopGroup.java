package com.pq.pure.netty.executor;

import com.pq.pure.netty.channel.Channel;

import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * @Author wmf
 * @Date 2022/6/24 17:35
 * @Description
 */
public interface EventLoopGroup extends Executor, Iterable<EventLoop>{

    void register(Channel channel);

    EventLoop next();

    @Override
    Iterator<EventLoop> iterator();
}
