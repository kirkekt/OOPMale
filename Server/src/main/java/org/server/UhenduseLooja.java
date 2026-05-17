package org.server;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class UhenduseLooja implements Runnable {

    private SSLSocket uhenduja;
    Map<String, List<Socket>> ruumid;
    Map<String, CountDownLatch> latchid;

    public UhenduseLooja(SSLSocket uhenduja, Map<String, List<Socket>> ruumid, Map<String, CountDownLatch> latchid) {
        this.uhenduja = uhenduja;
        this.ruumid = ruumid;
        this.latchid = latchid;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(uhenduja.getInputStream());
            System.out.println("siin");
            boolean bot = in.readBoolean();
            System.out.println(bot);

            if (bot) {
                new Thread(new Mang(uhenduja, uhenduja /*new BotSocket()*/, true)).start();
            } else {
                String ruumiKood = in.readUTF();
                System.out.println(ruumiKood);
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

                if (ruum != null) new Thread(new Mang(ruum.get(0), ruum.get(1), false)).start();
            }
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }
}
