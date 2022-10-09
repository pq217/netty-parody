package com.pq.pure.netty.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wmf
 * @Date 2022/6/25 11:53
 * @Description
 */
public class DefaultThreadFactory implements ThreadFactory {
    private AtomicInteger no = new AtomicInteger(0);
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "nio-thread-"+(no.incrementAndGet()));
    }
}
