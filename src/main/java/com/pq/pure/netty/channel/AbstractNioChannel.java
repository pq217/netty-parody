package com.pq.pure.netty.channel;

import com.pq.pure.netty.executor.NioEventLoop;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * @Author wmf
 * @Date 2022/6/23 11:09
 * @Description Abstract base class which use a Selector based approach
 */
public abstract class AbstractNioChannel extends AbstractChannel {

    /**
     * java的channel 包括ServerSocketChannel和SocketChannel
     */
    private final SelectableChannel ch;

    private SelectionKey selectionKey;

    /**
     * 感兴趣的事件
     */
    protected final int readInterestOp;

    public AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent);
        this.ch = ch;
        this.readInterestOp = readInterestOp;
        try {
            ch.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回java的channel
     *
     * @return
     */
    protected SelectableChannel javaChannel() {
        return ch;
    }

    /**
     * 把通道注册到多路复用器
     * @throws Exception
     */
    @Override
    protected void doRegister() throws Exception {
        // 最后一个字段this,相当于selectionKey.attach(this)，后续可以通过attachment()方法取到
        // 由于多个channel注册到一个eventLoop，所以需要传递当前的channel以便eventLoop获取到事件时可以知道是哪个channel产生的事件
        selectionKey = javaChannel().register(((NioEventLoop) eventLoop()).unwrappedSelector(), 0, this);
    }

    /**
     * 注册感兴趣事件
     */
    @Override
    protected void doBeginRead() {
        selectionKey.interestOps(readInterestOp);
    }

    /**
     * 从 {@link SelectableChannel} 读取事件，源码是写在Unsafe里
     */
    public abstract void read();

}
