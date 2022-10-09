package com.pq.pure.netty.channel.pipeline;

import com.pq.pure.netty.channel.Channel;

/**
 * @Author wmf
 * @Date 2022/6/24 11:59
 * @Description
 */
public class ChannelPipeline {
    /**
     * 管道的第一个处理器，管道的处理器是链式结构
     */
    final ChannelHandlerContext head;
    final ChannelHandlerContext tail;
    /**
     * 所在的通道
     */
    private final Channel channel;

    public ChannelPipeline(Channel channel) {
        this.channel = channel;
        head = new HeadContext(this);
        tail = new TailContext(this);
        // 头尾互指
        head.next = tail;
        tail.prev = head;
    }

    public final Channel channel() {
        return channel;
    }

    /**
     * 添加处理器
     * @param handler
     * @return
     */
    public final ChannelPipeline addLast(ChannelHandler handler) {
        // 把handler包装成上下文
        ChannelHandlerContext newCtx = new ChannelHandlerContext(this, handler);
        addLast0(newCtx);
        return this;
    }

    /**
     * 在链表结尾添加新的节点
     * @param newCtx
     */
    private void addLast0(ChannelHandlerContext newCtx) {
        ChannelHandlerContext prev = tail.prev;
        newCtx.prev = prev;
        newCtx.next = tail;
        prev.next = newCtx;
        tail.prev = newCtx;
    }

    /**
     * 结尾，简化处理
     */
    final class TailContext extends ChannelHandlerContext {
        public TailContext(ChannelPipeline pipeline) {
            super(pipeline, null);
        }
    }

    /**
     * 头部简化处理
     */
    final class HeadContext extends ChannelHandlerContext {
        public HeadContext(ChannelPipeline pipeline) {
            super(pipeline, null);
        }
    }

    /**
     * 开始处理read操作
     * @param msg
     * @return
     */
    public final ChannelPipeline fireChannelRead(Object msg) {
        head.fireChannelRead(msg);
        return this;
    }

    public final ChannelPipeline fireChannelReadComplete() {
        // 省略不写了，和fireChannelRead差不多道理
        return this;
    }
}
