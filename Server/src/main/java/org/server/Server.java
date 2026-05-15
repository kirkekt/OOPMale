package org.server;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class Server {
    public static void main(String[] args) throws Exception {
        File storeFile = new File("keystore.p12");
        String storePass = "secret";

        KeyStore store = KeyStore.getInstance(storeFile, storePass.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(store, storePass.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagers, null, null);

        try (ServerSocket ss = ctx.getServerSocketFactory().createServerSocket(1337)) {
            System.out.println("now listening on localhost:1337");
            while (true) {
                Socket valge = ss.accept();
                Bot must = new RandoBot(false);
                Socket botSocket = must.createSocket();
                new Thread(new Mang(valge, botSocket)).start();
            }
        }
    }
}
