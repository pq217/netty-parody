package com.pq.pure.netty.bootstrap;

import com.pq.pure.netty.channel.Channel;
import com.pq.pure.netty.channel.NioServerSocketChannel;
import com.pq.pure.netty.channel.pipeline.ChannelHandler;
import com.pq.pure.netty.channel.pipeline.ChannelHandlerContext;
import com.pq.pure.netty.channel.pipeline.ChannelInboundHandler;
import com.pq.pure.netty.channel.pipeline.ChannelPipeline;
import com.pq.pure.netty.channel.NioServerSocketChannel;
import com.pq.pure.netty.channel.pipeline.ChannelHandler;
import com.pq.pure.netty.executor.EventLoopGroup;

/**
 * @Author wmf
 * @Date 2022/6/25 10:58
 * @Description
 */
public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, NioServerSocketChannel> {

    private volatile EventLoopGroup childGroup;

    /**
     * 这里简化处理
     */
    private volatile ChannelHandler[] childHandlers;

    public ServerBootstrap() { }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        this.childGroup = childGroup;
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler... childHandlers) {
        this.childHandlers = childHandlers;
        return this;
    }

    @Override
    void init(Channel channel) {
        ChannelPipeline p = channel.pipeline();
        // 这里给ServerChannel添加管道处理器，简化了代码
        p.addLast(new ServerBootstrapAcceptor(childGroup, childHandlers));
    }

    private static class ServerBootstrapAcceptor implements ChannelInboundHandler {

        private final EventLoopGroup childGroup;
        private final ChannelHandler[] childHandlers;

        private ServerBootstrapAcceptor(EventLoopGroup childGroup, ChannelHandler[] childHandlers) {
            this.childGroup = childGroup;
            this.childHandlers = childHandlers;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            final Channel child = (Channel) msg;
            for (ChannelHandler childHandler : childHandlers) {
                child.pipeline().addLast(childHandler);
            }
            childGroup.register(child);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            // 略
        }
    }
}
