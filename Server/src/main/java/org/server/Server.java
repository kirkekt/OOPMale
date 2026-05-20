package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Server {
    public static void main(String[] args) throws Exception {

        int port = 1337;

        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Kuulan portil: " + port);

            Map<String, List<Socket>> ruumid = new ConcurrentHashMap<>();
            Map<String, CountDownLatch> latchid = new ConcurrentHashMap<>();

            while (true) {
                Socket uhenduja = ss.accept();
                System.out.println("Keegi ühendub: " + uhenduja.getRemoteSocketAddress());
                    try {
                        new Thread(new UhenduseLooja(uhenduja, ruumid, latchid)).start();
                    } catch (Exception e) {
                        throw new RuntimeException("Klient feilis: " + e);
                    };
            }
        }
    }
}
