package org.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class UhenduseLooja implements Runnable {

    private static int mangVsBot = 0;
    private final Socket uhenduja;
    private final Map<String, List<Socket>> ruumid;
    private final Map<String, CountDownLatch> latchid;
    private final String pass;
    private final int botiSugavus;

    public UhenduseLooja(Socket uhenduja, Map<String, List<Socket>> ruumid, Map<String, CountDownLatch> latchid, String pass, int botiSugavus) {
        this.uhenduja = uhenduja;
        this.ruumid = ruumid;
        this.latchid = latchid;
        this.pass = pass;
        this.botiSugavus = botiSugavus;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(uhenduja.getInputStream());
            if (!pass.equals("none")) {
                if (!in.readUTF().equals(pass)) {
                    uhenduja.close();
                    return;
                }
            } else {
                in.readUTF();
            }
            boolean bot = in.readBoolean();

            if (bot) {
                Bot vastane = new ParemBotv1(botiSugavus);
                new Thread(new Mang(uhenduja, vastane.createSocket(), true), "Mang-vs-bot-" + mangVsBot++).start();
                System.out.println("Algas " + mangVsBot + ". mäng boti vastu");
            } else {
                String ruumiKood = in.readUTF();
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
                            new Thread(new Mang(ruum.get(0), ruum.get(1), false), "Mang-ruumis-" + ruumiKood).start();
                            System.out.println("Algas mäng ruumis " + ruumiKood);
                            System.out.println(ruumid);
                            System.out.println(latchid);
                            break;
                        } catch (Exception _) {
                            continue;
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
