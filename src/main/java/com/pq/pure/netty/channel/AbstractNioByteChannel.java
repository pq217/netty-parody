package com.pq.pure.netty.channel;

import com.pq.pure.netty.channel.pipeline.ChannelPipeline;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;

/**
 * @Author wmf
 * @Date 2022/6/24 11:43
 * @Description
 */
public abstract class AbstractNioByteChannel extends AbstractNioChannel {

    public AbstractNioByteChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }

    @Override
    public void read() {
        final ChannelPipeline pipeline = pipeline();
        // 这里做了简化处理，源码用的自封装ByteBuf
        ByteBuffer byteBuf = ByteBuffer.allocate(128);
        doReadBytes(byteBuf);
        pipeline.fireChannelRead(byteBuf);
    }

    protected abstract int doReadBytes(ByteBuffer buf);
}
