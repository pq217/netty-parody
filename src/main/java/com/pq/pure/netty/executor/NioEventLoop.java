package com.pq.pure.netty.executor;

import com.pq.pure.netty.channel.AbstractNioChannel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @Author wmf
 * @Date 2022/6/17 14:08
 * @Description
 */
public class NioEventLoop extends SingleThreadEventLoop {

    private Selector selector;

    private final SelectorProvider provider;

    public NioEventLoop(NioEventLoopGroup parent, SelectorProvider selectorProvider, Executor executor) {
        super(parent, executor);
        this.provider = selectorProvider;
        this.selector = openSelector();
    }

    public SelectorProvider selectorProvider() {
        return provider;
    }

    private Selector openSelector() {
        try {
            return selectorProvider().openSelector();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 由于没做装饰，所以selector即unwrappedSelector
     *
     * @return
     */
    public Selector unwrappedSelector() {
        return selector;
    }

    /**
     * 运行
     */
    @Override
    protected void run() {
        for (;;) {
            try {
                select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                processSelectedKeys();
            } finally {
                runAllTasks();
            }
        }
    }

    private void select() throws IOException {
        // 拿到多路复用器
        Selector selector = this.selector;
        for (;;) {
            // 等待，简化固定1秒
            int selectedKeys = selector.select(1000);
            // 如果有事件发生或当前有任务跳出循环
            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }

    private void processSelectedKeys() {
        processSelectedKeysPlain(selector.selectedKeys());
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        for (;;) {
            final SelectionKey k = i.next();
            final Object a = k.attachment();
            i.remove();
            // 获取注册时绑定的参数
            if (a instanceof AbstractNioChannel) {
                processSelectedKey(k, (AbstractNioChannel) a);
            } else {
                // 由于手写简版只attach了AbstractNioChannel所以不会出现，但源码有其它的attach
            }
            if (!i.hasNext()) {
                break;
            }
        }
    }

    /**
     * 处理单个事件
     * @param k
     * @param ch
     */
    private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
        // 这个try是为了和源码尽量长得像，简版不处理异常
        try {
            // 获取发生的事件标识
            int readyOps = k.readyOps();
            // 如果是read事件 或 accpet事件
            if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
                // channel读取
                ch.read();
            }
        } catch (Exception e) {

        }
    }
}
