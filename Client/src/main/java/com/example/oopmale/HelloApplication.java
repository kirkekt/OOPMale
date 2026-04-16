package com.example.oopmale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HelloApplication extends Application {

    private String ipAdress = "localhost";


    /* ------- koodid ------- */
    public static final int manguLopp = 256;
    public static final int manguAlgus = 128;
    public static final int kaiguKood = 129;
    public static final int illegaalneKaik = 0;
    public static final int kaikOk = 1;

    /* ------- sisud ------- */
    public static final int valge = 1;
    public static final int must = 2;
    public static final int kaotus = -1;
    public static final int viik = 0;
    public static final int võit = 1;


    @Override
    public void start(Stage stage) throws IOException {
        BlockingQueue<int[]> kaigud = new LinkedBlockingQueue<>();

        Socket server = new Socket(ipAdress, 1337);
        DataInputStream in = new DataInputStream(server.getInputStream());
        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        boolean onValge = kasValge(in, out);

        Malelaud malelaud = new Malelaud();
        LauaVaade lauaVaade = new LauaVaade(malelaud, onValge, kaigud);
        Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);
        stage.setTitle("Male");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> {try {server.close();} catch (Exception e1) {throw new RuntimeException(e1);}});

        int[] vastaseTegevus = new int[0];
        if (!onValge) {
            vastaseTegevus = loeKoik(in, out);
            if (vastaseTegevus[0] == kaiguKood) {
                malelaud.teeKaik(kustutaKood(vastaseTegevus), lauaVaade);
            } else {
                throw new RuntimeException();
            }
        }
        Thread manguThread = new Thread(() -> {
            try {
                while (true) {
                    int[] kordinaadid = kaigud.take();

                    System.out.println("Proovin käiku: " + kordinaadid[0] + "," + kordinaadid[1] + " -> " + kordinaadid[2] + "," + kordinaadid[3]);

                    saadaTegevus(out, kaiguKood, kordinaadid);
                    if (in.readInt() == illegaalneKaik) {
                        lauaVaade.klikidReset();
                        continue;
                    }
                    malelaud.teeKaik(kordinaadid, lauaVaade);
                    lauaVaade.klikidReset();

                    int[] vastaseKaik = loeKoik(in, out);
                    if (vastaseKaik[0] == kaiguKood) {
                        malelaud.teeKaik(kustutaKood(vastaseKaik), lauaVaade);
                    } else if (vastaseKaik[0] == manguLopp) {
                        System.out.print("mäng läbi - ");
                        if (vastaseKaik[1] == kaotus) {
                            System.out.println("kaotasid");
                        } else if (vastaseKaik[1] == viik) {
                            System.out.println("jäite viiki");
                        } else if (vastaseKaik[1] == võit) {
                            System.out.println("võitsid");
                        }
                        break;
                    } else {
                        throw new RuntimeException();
                    }
                    lauaVaade.uuendaLaud();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        manguThread.setDaemon(true);
        manguThread.start();


    }

    /**
     * Praegu esitatakse info kujul int[] [sõnumi pikkus, kood, sisu], kus sõnumi pikkus on sisu pikkus+1, kood kood nullist kuni 256ni, mis ütleb packeti sisu
     *
     * @param in võtab DataInputStreami kust lugeda
     * @return tagastab int[], kus esimesel kohal on kood ja ülejäänu on sisu
     * @throws IOException, kui ühendust ei leita
     */
    public int[] loeKoik(DataInputStream in, DataOutputStream out) throws IOException {
        int pikkus = in.readInt();
        int[] tagastus = new int[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readInt();
        }
        System.out.println("Sain serverilt:");
        System.out.println(Arrays.toString(tagastus));
        out.writeInt(1);
        return tagastus;
    }

    public void saadaTegevus(DataOutputStream out, int kood, int[] sisu) throws IOException {
        System.out.println("Saadan:");
        System.out.println("pikkus - " + (1+sisu.length));
        System.out.println("kood - " + kood);
        System.out.println("sisu - " + Arrays.toString(sisu));
        out.writeInt(1 + sisu.length);
        out.writeInt(kood);
        for (int i : sisu) {
            out.writeInt(i);
        }
    }

    private boolean kasValge(DataInputStream in, DataOutputStream out) throws IOException {
        int[] info = loeKoik(in, out);
        if (info[0] != manguAlgus) {
            throw new RuntimeException("Oodatud \"mängu algus\", saadud kood: " + info[0]);
        }
        return (info[1] == valge);
    }

    private static int[] kustutaKood(int[] n) {
        int[] tagastus = new int[n.length - 1];
        for (int i = 1; i < n.length; i++) {
            tagastus[i-1] = n[i];
        }
        return tagastus;
    }
}
