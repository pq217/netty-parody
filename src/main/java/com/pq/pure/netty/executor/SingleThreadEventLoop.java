package com.pq.pure.netty.executor;

import com.pq.pure.netty.channel.Channel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * @Author wmf
 * @Date 2022/6/23 13:28
 * @Description
 */
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    private final Collection<EventLoop> selfCollection = Collections.<EventLoop>singleton(this);

    private final EventLoopGroup parent;

    public SingleThreadEventLoop(EventLoopGroup parent, Executor executor) {
        super(executor);
        this.parent = parent;
    }

    @Override
    public EventLoopGroup parent() {
        return parent;
    }


    @Override
    public void register(Channel channel) {
        // netty源码  promise.channel().unsafe().register(this, promise); 简化不区分unsafe，如下
        channel.register(this);
    }

    @Override
    public EventLoop next() {
        return this;
    }

    @Override
    public Iterator<EventLoop> iterator() {
        return selfCollection.iterator();
    }

}
