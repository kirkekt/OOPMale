package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        try (ServerSocket ss = new ServerSocket(1337)) {
            System.out.println("now listening on :1337");
            while (true) {
                Socket valge = ss.accept();
                //Socket must = ss.accept();
                new Thread(new Mang(valge/*, must*/)).start();
            }
        }
    }
}
