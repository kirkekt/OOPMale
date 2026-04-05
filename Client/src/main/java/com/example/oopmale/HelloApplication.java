package com.example.oopmale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class HelloApplication extends Application {

    private String ipAdress = "localhost";

    @Override
    public void start(Stage stage) throws IOException {
        Malelaud malelaud = new Malelaud();
        LauaVaade lauaVaade = new LauaVaade(malelaud);
        Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);
        stage.setTitle("Male");
        stage.setScene(scene);
        stage.show();

        int[] vastaseTegevus;

        try (Socket server = new Socket(ipAdress, 1337);
             DataInputStream in = new DataInputStream(server.getInputStream());
             DataOutputStream out = new DataOutputStream(server.getOutputStream())) {

            boolean valge = kasValge(in, out);


            if (valge) {
                vastaseTegevus = loeKoik(in, out);
                System.out.println(Arrays.toString(vastaseTegevus));
                malelaud.teeKaik(vastaseTegevus[1], vastaseTegevus[2], vastaseTegevus[3], vastaseTegevus[4]);
            }

            int i = 0;
            while (i < 8) {
                //malelaud.teeKaik(click1X, click1Y, click2X, click2Y);
                lauaVaade.uuendaLaud();
                i++;
            }

            System.out.println("mäng läbi");

        }


    }

    /**
     * Praegu esitatakse info kujul int[] [sõnumi pikkus, kood, sisu], kus sõnumi pikkus on sisu pikkus+1, kood kood nullist kuni 256ni, mis ütleb packeti sisu
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
        System.out.println(Arrays.toString(tagastus));
        out.writeInt(1);
        return tagastus;
    }

    public void saadaTegevus(DataOutputStream out, int kood, int[] sisu) throws IOException {
        out.writeInt(1+ sisu.length);
        out.writeInt(kood);
        for (int i : sisu) {
            out.writeInt(i);
        }
    }



    private boolean kasValge(DataInputStream in, DataOutputStream out) throws IOException {
        int[] info = loeKoik(in, out);
        if (info[0] != 0) {
            throw new RuntimeException("Oodatud \"mängu algus\", saadud kood: " + info[0]);
        }
        return (info[1] == 1);
    }
}
