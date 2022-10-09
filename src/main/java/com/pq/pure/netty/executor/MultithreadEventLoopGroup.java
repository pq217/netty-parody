package com.pq.pure.netty.executor;

import com.pq.pure.netty.channel.Channel;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @Author wmf
 * @Date 2022/6/24 18:03
 * @Description
 */
public abstract class MultithreadEventLoopGroup implements EventLoopGroup {

    private final EventLoop[] children;
    /**
     * 为了迭代用
     */
    private final Set<EventLoop> readonlyChildren;

    public MultithreadEventLoopGroup(int nThreads, Executor executor) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException();
        }
        if (executor == null) {
            executor = new ThreadPerTaskExecutor(new DefaultThreadFactory());
        }
        this.children = new EventLoop[nThreads];
        for (int i = 0; i < nThreads; i ++) {
            children[i] = newChild(executor);
        }

        /**
         * 为了迭代用
         */
        Set<EventLoop> childrenSet = new LinkedHashSet<EventLoop>(children.length);
        Collections.addAll(childrenSet, children);
        readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }

    protected abstract EventLoop newChild(Executor executor);

    /**
     * 源码用一个chooser对象选择子线程，这里简化一下，就轮训吧
     * @return
     */
    int i=0;

    public EventLoop chooseNext() {
        if (i>=children.length) {
            i =0;
        }
        EventLoop child = children[i];
        i++;
        return child;
    }

    @Override
    public void register(Channel channel) {
        next().register(channel);
    }

    @Override
    public EventLoop next() {
        return chooseNext();
    }

    @Override
    public Iterator<EventLoop> iterator() {
        return readonlyChildren.iterator();
    }

    @Override
    public void execute(Runnable command) {
        next().execute(command);
    }
}
