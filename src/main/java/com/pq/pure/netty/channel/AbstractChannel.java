package com.pq.pure.netty.channel;

import com.pq.pure.netty.channel.pipeline.ChannelPipeline;
import com.pq.pure.netty.executor.EventLoop;

import java.net.SocketAddress;

/**
 * @Author wmf
 * @Date 2022/6/23 11:34
 * @Description 抽象的通道
 */
public abstract class AbstractChannel implements Channel {

    /**
     * 父通道
     */
    private final Channel parent;

    /**
     * 绑定的事件循环器
     */
    private volatile EventLoop eventLoop;

    /**
     * 管道
     */
    private final ChannelPipeline pipeline;

    public AbstractChannel(Channel parent) {
        this.parent = parent;
        pipeline = newChannelPipeline();
    }

    protected ChannelPipeline newChannelPipeline() {
        return new ChannelPipeline(this);
    }

    /**
     * 返回绑定的事件处理器
     *
     * @return
     */
    @Override
    public EventLoop eventLoop() {
        return eventLoop;
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    @Override
    public void beginRead() {
        doBeginRead();
    }

    protected abstract void doBeginRead();

    @Override
    public void bind(SocketAddress localAddress) {
        try {
            doBind(localAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void doBind(SocketAddress localAddress) throws Exception;

    protected void doRegister() throws Exception {
        // NOOP
    }

    /**
     * Channel绑定eventLoop
     *
     * @param eventLoop
     */
    @Override
    public final void register(EventLoop eventLoop) {
        // 省去乱七八遭的判断，源码实在内部类Unsafe下，所以是：AbstractChannel.this
        AbstractChannel.this.eventLoop = eventLoop;
        eventLoop.execute(() -> {
            register0();
        });
    }

    /**
     * 实际注册
     */
    private void register0() {
        try {
            doRegister();
            // 开始读取感兴趣事件
            beginRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
