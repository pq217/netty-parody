package com.pq.pure.netty.bootstrap;

import com.pq.pure.netty.channel.Channel;
import com.pq.pure.netty.executor.EventLoopGroup;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author wmf
 * @Date 2022/6/24 17:33
 * @Description 抽象的Bootstrap
 */
public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> {

    volatile EventLoopGroup group;

    /**
     * 源码使用工厂模式存储是channelFactory，这里简化处理
     */
    private volatile Channel channel;

    AbstractBootstrap() {
        // Disallow extending from a different package.
    }

    private B self() {
        return (B) this;
    }

    public B group(EventLoopGroup group) {
        this.group = group;
        return self();
    }

    public B channel(Class<? extends C> channelClass) {
        try {
            channel = channelClass.getConstructor().newInstance();
        } catch (Exception e) {
        }
        return self();
    }

    public void bind(int inetPort) {
        doBind(new InetSocketAddress(inetPort));
    }

    private void doBind(final SocketAddress localAddress) {
        initAndRegister();
        // 让channel绑定的线程去实际绑定
        channel.eventLoop().execute(()->{
            channel.bind(localAddress);
        });
    }

    final void initAndRegister() {
        init(channel);
        group.register(channel);
    }

    abstract void init(Channel channel);
}
