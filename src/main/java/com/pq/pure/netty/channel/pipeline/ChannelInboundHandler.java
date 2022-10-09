package com.pq.pure.netty.channel.pipeline;

/**
 * @Author wmf
 * @Date 2022/6/24 13:42
 * @Description read自定义处理器
 */
public interface ChannelInboundHandler extends ChannelHandler{

    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;
}
