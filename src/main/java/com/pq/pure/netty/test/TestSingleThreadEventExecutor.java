package com.pq.pure.netty.test;

import com.pq.pure.netty.executor.SingleThreadEventExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wmf
 * @Date 2022/6/22 19:59
 * @Description
 */
public class TestSingleThreadEventExecutor extends SingleThreadEventExecutor {

    public TestSingleThreadEventExecutor() {
        super(command -> new Thread(command).start());
    }

    @Override
    protected void run() {
        runAllTasks();
    }

    public static void main(String[] args) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private AtomicInteger no = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "pq-thread-"+(no.incrementAndGet()));
            }
        };
        TestSingleThreadEventExecutor executor = new TestSingleThreadEventExecutor();
        for(int i=1;i<=2;i++) {
            int finalI = i;
            Runnable task = () -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+"-----任务["+finalI+"] done");
            };
            executor.execute(task);
        }
        for (;;) {}
    }
}
