package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Bot implements Runnable {
    public final Malelaud malelaud;
    public boolean onValge;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean mangLabi = false;

    public Bot() {
        this.malelaud = new Malelaud();
    }

    /**
     * Tagastab käigu kujul: [algX, algY, sihtX, sihtY]
     */
    abstract public int[] annaKaik();

    public void teeKaik(int[] kaik) {
        malelaud.teeKaik(kaik);
    }

    public BotSocket createSocket() throws IOException {
        BotSocket socket = new BotSocket();
        this.in = socket.getBotIn();
        this.out = socket.getBotOut();
        Thread t = new Thread(this, "Bot-" + (onValge ? "white" : "black"));
        t.setDaemon(true);
        t.start();
        return socket;
    }

    @Override
    public void run() {
        try {
            // Mängu alguse initsialiseerimine
            in.readInt(); // pikkus (2)
            in.readInt(); // kood (manguAlgus - 126)
            int varv = in.readInt(); // 1 (valge) või 2 (must)
            this.onValge = (varv == Suhtlus.valge);
            out.writeInt(1);

            // Loeb laua algseisu
            Suhtlus.loeKoik(in, out);

            boolean minuKord = this.onValge;

            // Põhiline mängutsükkel
            while (!mangLabi) {
                if (minuKord) {
                    endaKaik();
                } else {
                    teiseKaik();
                }
                minuKord = !minuKord;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endaKaik() throws IOException {
        int[] kaik = annaKaik();

        out.writeInt(3);
        out.writeInt(Suhtlus.klikkTehti);
        out.writeInt(kaik[0]);
        out.writeInt(kaik[1]);
        in.readInt();

        int vastus = in.readInt();
        if (vastus == Suhtlus.saadanVoimalikud) {
            Suhtlus.loeKoik(in, out);
        } else if (vastus == Suhtlus.voimalikudPuuduvad || vastus == Suhtlus.saadaUusKlikk) {
            return;
        }

        out.writeInt(3);
        out.writeInt(Suhtlus.klikkTehti);
        out.writeInt(kaik[2]);
        out.writeInt(kaik[3]);
        in.readInt();

        vastus = in.readInt();
        if (vastus == Suhtlus.kaiguLopp) {
            teeKaik(kaik);
        }

        int pikkus = in.readInt();
        int kood = in.readInt();
        if (kood == Suhtlus.kaiguKood) {
            in.readInt(); // startX
            in.readInt(); // startY
            in.readInt(); // endX
            in.readInt(); // endY
            out.writeInt(1);
        }
    }

    public void teiseKaik() throws IOException {
        int pikkus = in.readInt();
        int kood = in.readInt();

        if (kood == Suhtlus.manguLopp) {
            int tulemus = in.readInt();
            mangLabi = true;
        } else if (kood == Suhtlus.kaiguKood) {
            int startX = in.readInt();
            int startY = in.readInt();
            int endX = in.readInt();
            int endY = in.readInt();

            out.writeInt(1);
            teeKaik(new int[]{startX, startY, endX, endY});
        }
    }
}