package com.pq.pure.netty.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @Author wmf
 * @Date 2022/6/25 11:52
 * @Description
 */
public class ThreadPerTaskExecutor implements Executor {
    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
