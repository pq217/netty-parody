package com.pq.pure.netty.channel;

import com.pq.pure.netty.channel.pipeline.ChannelPipeline;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wmf
 * @Date 2022/6/24 10:12
 * @Description
 */
public abstract class AbstractNioMessageChannel extends AbstractNioChannel {
    /**
     * 读取到的缓存
     */
    private final List<Object> readBuf = new ArrayList<Object>();

    public AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }

    /**
     * 从SelectableChannel中读取信息
     */
    @Override
    public void read() {
        final ChannelPipeline pipeline = pipeline();
        doReadMessages(readBuf);
        int size = readBuf.size();
        for (int i = 0; i < size; i ++) {
            pipeline.fireChannelRead(readBuf.get(i));
        }
        readBuf.clear();
    }

    protected abstract int doReadMessages(List<Object> buf);
}
