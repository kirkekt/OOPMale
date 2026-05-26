package org.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Server {
    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("server_setup.txt"));
        String pass = br.readLine().split("=")[1].strip();
        int port = Integer.parseInt(br.readLine().split("=")[1].strip());
        int botiSugavus = Integer.parseInt(br.readLine().split("=")[1].strip());

        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Kuulan portil: " + port);

            Map<String, List<Socket>> ruumid = new ConcurrentHashMap<>();
            Map<String, CountDownLatch> latchid = new ConcurrentHashMap<>();

            while (true) {
                Socket uhenduja = ss.accept();
                System.out.println("Keegi ühendub: " + uhenduja.getRemoteSocketAddress());
                    try {
                        new Thread(new UhenduseLooja(uhenduja, ruumid, latchid, pass, botiSugavus)).start();
                    } catch (Exception e) {
                        throw new RuntimeException("Klient feilis: " + e);
                    };
            }
        }
    }
}
