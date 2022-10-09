package com.pq.pure.netty.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.List;

/**
 * @Author wmf
 * @Date 2022/6/16 11:49
 * @Description
 */
public class NioServerSocketChannel extends AbstractNioMessageChannel {

    /**
     * 多路复用器提供者
     */
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();

    /**
     * 开启一个 java ServerSocketChannel
     * @param provider
     * @return
     */
    private static ServerSocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openServerSocketChannel();
        } catch (IOException e) {
            return null;
        }
    }

    public NioServerSocketChannel() {
        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioServerSocketChannel(ServerSocketChannel channel) {
        super(null, channel, SelectionKey.OP_ACCEPT);
    }

    /**
     * 覆盖，因为可以确定返回的是ServerSocketChannel
     * @return
     */
    @Override
    protected ServerSocketChannel javaChannel() {
        return (ServerSocketChannel) super.javaChannel();
    }

    /**
     * 绑定端socket
     * @param localAddress
     * @throws Exception
     */
    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        javaChannel().socket().bind(localAddress);
    }

    /**
     * 读取信息，作为SeverSocketChannel(服务端通道)，读取信息即accept后的SocketChannel(客户端通道)
     * @param buf
     * @return
     */
    @Override
    protected int doReadMessages(List<Object> buf) {
        SocketChannel ch = null;
        try {
            ch = javaChannel().accept();
        } catch (IOException e) {
        }
        if (ch != null) {
            buf.add(new NioSocketChannel(this, ch));
            return 1;
        }
        return 0;
    }

}
