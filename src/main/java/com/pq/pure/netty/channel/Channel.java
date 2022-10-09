package com.pq.pure.netty.channel;

import com.pq.pure.netty.channel.pipeline.ChannelPipeline;
import com.pq.pure.netty.executor.EventLoop;

import java.net.SocketAddress;

/**
 * @Author wmf
 * @Date 2022/6/23 11:41
 * @Description
 */
public interface Channel {
    /**
     * 绑定事件持续处理器
     * @param eventLoop
     */
    void register(EventLoop eventLoop);

    /**
     * 获取事件持续处理器
     * @return
     */
    EventLoop eventLoop();

    /**
     * 通道内部的管道
     * @return
     */
    ChannelPipeline pipeline();

    /**
     * 绑定端口
     * @param localAddress
     */
    void bind(SocketAddress localAddress);

    /**
     * 开始读取，nio的实现即注册感兴趣的事件
     */
    void beginRead();
}
