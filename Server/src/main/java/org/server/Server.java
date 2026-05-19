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
        File storeFile = new File("keystore.p12");
        String storePass = "a1g!HD1uUBX@YE";

        KeyStore store = KeyStore.getInstance(storeFile, storePass.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(store, storePass.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagers, null, null);

        try (ServerSocket ss = ctx.getServerSocketFactory().createServerSocket(1337)) {
            System.out.println("now listening on localhost:1337");

            Map<String, List<Socket>> ruumid = new ConcurrentHashMap<>();
            Map<String, CountDownLatch> latchid = new ConcurrentHashMap<>();

            while (true) {
                SSLSocket uhenduja = (SSLSocket) ss.accept();
                uhenduja.startHandshake();
                new Thread(new UhenduseLooja(uhenduja, ruumid, latchid)).start();
            }
        }
    }
}
