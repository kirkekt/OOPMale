package com.example.oopmale;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Peaklass {

    private Set<MaleNupp> lauaOlek = new HashSet<>();
    private int[] vastaseTegevus;
    private String ipAdress = ""; //tuleb enne mängu täita

    public Peaklass() throws IOException {

        try (Socket server = new Socket(ipAdress, 1337);
             DataInputStream in = new DataInputStream(server.getInputStream());
             DataOutputStream out = new DataOutputStream(server.getOutputStream())) {

            initMalelaud();

            int[] info = loeKoik(in);
            if (info[1] != 0) {
                throw new RuntimeException("Oodatud \"mängu algus\", saadud kood: " + info[0]);
            }
            boolean valge = (info[2] == 1);

            if (!valge) {
                vastaseTegevus = loeKoik(in);
            }

            while (true) {
                //oma käigu alustamine
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

    private void initMalelaud() {

        for (int x = 0; x < 8; x++) {
            lauaOlek.add(new Ettur(x, 1, true));
            lauaOlek.add(new Ettur(x, 6, false));
        }

        boolean[] l = new boolean[]{true, false};
        for (boolean b : l) {
            int y;
            if (b) {y = 0;}
            else {y = 7;}

            for (int i = 0; i < 2; i++) {
                lauaOlek.add(new Vanker(7*i, y, b));
                lauaOlek.add(new Ratsu(1+5*i, y, b));
                lauaOlek.add(new Oda(2+3*i, y, b));
            }
            lauaOlek.add(new Lipp(3, y, b));
            lauaOlek.add(new Kuningas(4, y, b));
        }
    }
}
