package org.server;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class Server {
    public static void main(String[] args) throws Exception {

        try (ServerSocket ss = new ServerSocket(1337)) {
            System.out.println("now listening on localhost:1337");

            Map<String, List<Socket>> ruumid = new ConcurrentHashMap<>();
            Map<String, CountDownLatch> latchid = new ConcurrentHashMap<>();

            while (true) {
                Socket uhenduja = ss.accept();
                System.out.println("Keegi ühendub: " + uhenduja.getRemoteSocketAddress());
                new Thread(new UhenduseLooja(uhenduja, ruumid, latchid)).start();
            }
        }
    }
}
