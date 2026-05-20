package org.server;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class UhenduseLooja implements Runnable {

    private Socket uhenduja;
    Map<String, List<Socket>> ruumid;
    Map<String, CountDownLatch> latchid;

    public UhenduseLooja(Socket uhenduja, Map<String, List<Socket>> ruumid, Map<String, CountDownLatch> latchid) {
        this.uhenduja = uhenduja;
        this.ruumid = ruumid;
        this.latchid = latchid;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(uhenduja.getInputStream());
            boolean bot = in.readBoolean();
            System.out.println(bot);

            if (bot) {
                Bot vastane = new ParemBotv1(5);
                new Thread(new Mang(uhenduja, vastane.createSocket(), true)).start();
            } else {
                String ruumiKood = in.readUTF();
                System.out.println(ruumiKood);
                while (true) {
                    List<Socket> ruum = ruumid.computeIfAbsent(ruumiKood, _ -> new CopyOnWriteArrayList<>());
                    CountDownLatch latch = latchid.computeIfAbsent(ruumiKood, k -> new CountDownLatch(2));

                    ruum.add(uhenduja);
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    ruum = ruumid.remove(ruumiKood);
                    latchid.remove(ruumiKood);


                    if (ruum != null) {
                        try {
                            new DataOutputStream(ruum.get(0).getOutputStream()).writeInt(1);
                            new DataInputStream(ruum.get(0).getInputStream()).readInt();
                            new DataOutputStream(ruum.get(1).getOutputStream()).writeInt(1);
                            new DataInputStream(ruum.get(1).getInputStream()).readInt();
                            new Thread(new Mang(ruum.get(0), ruum.get(1), false)).start();
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }
}
