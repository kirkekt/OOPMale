package com.example.oopmale;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Peaklass {

    private String ipAdress = ""; //tuleb enne mängu täita


    private int[] vastaseTegevus;

    public Peaklass() throws IOException {

        try (Socket server = new Socket(ipAdress, 1337);
             DataInputStream in = new DataInputStream(server.getInputStream());
             DataOutputStream out = new DataOutputStream(server.getOutputStream())) {

            Malelaud malelaud = new Malelaud();
            boolean valge = kasValge(in);


            if (!valge) {
                vastaseTegevus = loeKoik(in);
                malelaud.teeKaik(vastaseTegevus[1], vastaseTegevus[2], vastaseTegevus[3], vastaseTegevus[4]);
            }

            while (true) {
                //malelaud.kaiguKatse(valge, placeholder_mangija_valik_x, placeholder_mangija_valik_y, placeholder_mangija_valik_uusx, placeholder_mangija_valik_uusy);
                break;
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
    public int[] loeKoik(DataInputStream in) throws IOException {
        int pikkus = in.readInt();
        int[] tagastus = new int[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readInt();
        }

        return tagastus;
    }

    public void saadaTegevus(DataOutputStream out, int kood, int[] sisu) throws IOException {
        out.writeInt(1+ sisu.length);
        out.writeInt(kood);
        for (int i : sisu) {
            out.writeInt(i);
        }
    }

    private boolean kasValge(DataInputStream in) throws IOException {
        int[] info = loeKoik(in);
        if (info[1] != 0) {
            throw new RuntimeException("Oodatud \"mängu algus\", saadud kood: " + info[0]);
        }
        return (info[2] == 1);
    }
}
