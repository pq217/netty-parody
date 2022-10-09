package com.pq.pure.netty;

import com.pq.pure.netty.channel.pipeline.ChannelHandlerContext;
import com.pq.pure.netty.channel.pipeline.ChannelInboundHandler;

import java.nio.ByteBuffer;

public class NettyServerHandler2 implements ChannelInboundHandler {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("handler2:" + new String(((ByteBuffer) msg).array()));
    }
}