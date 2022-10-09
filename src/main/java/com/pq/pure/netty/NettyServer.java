package com.pq.pure.netty;

import com.pq.pure.netty.bootstrap.ServerBootstrap;
import com.pq.pure.netty.channel.NioServerSocketChannel;
import com.pq.pure.netty.executor.EventLoopGroup;
import com.pq.pure.netty.executor.NioEventLoopGroup;

/**
 * @Author wmf
 * @Date 2022/6/25 11:41
 * @Description
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerHandler(), new NettyServerHandler2());
            System.out.println("netty server start...");
            bootstrap.bind(9000);
        } finally {
        }
    }
}
