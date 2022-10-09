package com.pq.pure.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author wmf
 * @Date 2022/5/25 14:54
 * @Description
 */
public class NioServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        // 创建NIO ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        serverSocket.configureBlocking(false);
        // 打开Selector处理Channel，底层调用epoll_create
        Selector selector = Selector.open();
        // 把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣，底层调用epoll_ctl
//        SelectionKey registerKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        SelectionKey serverRegisterKey = serverSocket.register(selector, 0);
        serverRegisterKey.interestOps(SelectionKey.OP_ACCEPT);
        // 测试attach
        serverRegisterKey.attach(new NioServer());
        System.out.println("服务启动成功");

        while (true) {
            // 阻塞等待需要处理的事件发生，即调用epoll_wait
            selector.select();
            // 获取selector中注册的全部事件的 SelectionKey 实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            // 遍历SelectionKey对事件进行处理
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 测试attachment
                System.out.println("attachment: "+ key.attachment());
                // 测试key
                System.out.println("key is serverRegisterKey:" + key.equals(serverRegisterKey));
                // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件，底层调用epoll_ctl
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                } else if (key.isReadable()) {  // 如果是OP_READ事件，则进行读取和打印
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = socketChannel.read(byteBuffer);
                    // 如果有数据，把数据打印出来
                    if (len > 0) {
                        System.out.println("接收到消息：" + new String(byteBuffer.array()));
                    } else if (len == -1) { // 如果客户端断开连接，关闭Socket
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }
                //从事件集合里删除本次处理的key，防止下次select重复处理
                iterator.remove();
            }
        }
    }
}
