package com.pq.pure.netty.executor;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Author wmf
 * @Date 2022/6/22 18:21
 * @Description Abstract base class that execute all its submitted tasks in a single thread.
 * 这是一种任务Executor的抽象，这种Executor保存一个任务队列，当外界让其执行任务时，他们会把任务保存在任务队列
 * 他们有一套自己的运行方式，并且会开启一个线程去执行这个运行方式，这种运行方式是不停止的(除非手动关闭)，而这种执行器会按
 * 自己的运行方式来执行任务
 */
@SuppressWarnings("ALL")
public abstract class SingleThreadEventExecutor implements Executor {
    /**
     * 默认任务列表长度
     */
    protected static final int DEFAULT_MAX_PENDING_TASKS = 16;
    /**
     * 待完成的任务
     */
    private final Queue<Runnable> taskQueue;
    /**
     * 实际工作者
     */
    private final Executor executor;
    /**
     * 当前运行线程
     */
    private volatile Thread thread;

    /**
     * ST_NOT_STARTED: 未启动, ST_STARTED 已启动
     */
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;

    /**
     * 标记是否启动
     */
    private volatile int state = ST_NOT_STARTED;

    /**
     * 原子启动标记更新器
     */
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");

    public SingleThreadEventExecutor(Executor executor) {
        taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.executor = executor;
    }

    /**
     * 初始化一个新的任务对列
     * @param maxPendingTasks
     * @return
     */
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    /**
     * 添加任务
     * @param task
     */
    protected void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!taskQueue.offer(task)) {
            throw new RejectedExecutionException("event executor terminated");
        }
    }

    /**
     * 检查是否有任务
     *
     * @return
     */
    protected boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    /**
     * 运行所有任务
     * @return
     */
    protected boolean runAllTasks() {
        // 省略乱七八糟的判断，把多个子方法简化
        Runnable task = taskQueue.poll();
        if (task == null) {
            return false;
        }
        for (;;) {
            task.run();
            task = taskQueue.poll();
            if (task == null) {
                return true;
            }
        }
    }

    @Override
    public void execute(Runnable task) {
        addTask(task);
        startThread();
    }

    /**
     * 开启线程执行(判断已启动过不再启动)
     */
    private void startThread() {
        // 未启动才能启动，也就是只启动一次
        if (state == ST_NOT_STARTED) {
            // 再次CAS判断避免线程不安全
            if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
                doStartThread();
            }
        }
    }

    /**
     * 实际开启线程执行run方法
     */
    private void doStartThread() {
        // 使用真实的执行者执行任务
        executor.execute(() -> {
            // 保存执行的 线程
            thread = Thread.currentThread();
            // 省去乱起八遭的判断
            SingleThreadEventExecutor.this.run();
            // 如果执行结束，则报错
            System.out.println("Buggy EventExecutor implementation; SingleThreadEventExecutor.confirmShutdown() must be called before run() implementation terminates");
        });
    }

    /**
     * 抽象run方法，是一个不能运行结束的方法(除非手动关闭)，即loop
     */
    protected abstract void run();

}
