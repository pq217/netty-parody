package com.pq.pure.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author wmf
 * @Date 2022/5/25 14:54
 * @Description
 */
@SuppressWarnings("ALL")
public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        // 任务处理线程池
        ExecutorService pool = Executors.newFixedThreadPool(10);
        while (true) {
            System.out.println("server start");
            //阻塞方法等待客户端连接
            Socket clientSocket = serverSocket.accept();
            System.out.println("new client");
            pool.execute(()->{
                handler(clientSocket);
            });
        }
    }

    private static void handler(Socket clientSocket) {
        try {
            byte[] bytes = new byte[1024];
            System.out.println("reading");
            //接收客户端的数据，阻塞方法，没有数据可读时就阻塞
            int read = clientSocket.getInputStream().read(bytes);
            System.out.println("read over");
            if (read != -1) {
                System.out.println("received data：" + new String(bytes, 0, read));
            }
            clientSocket.getOutputStream().write("HelloClient".getBytes());
            clientSocket.getOutputStream().flush();
        } catch (Exception e) {
        }
    }
}
