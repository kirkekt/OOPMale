package com.example.oopmale;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;

public class Peaklass {

    private String ipAdress = "";

    public Peaklass() throws IOException {

        try (Socket server = new Socket(ipAdress, 1337);
             DataInputStream in = new DataInputStream(server.getInputStream());
             DataOutputStream out = new DataOutputStream(server.getOutputStream())) {

            int[] info = loeKoik(in);

            if (info[0] != 0) {
                throw new RuntimeException("Oodatud \"mängu algus\", saadud kood: " + info[0]);
            }

            int värv = info[1];
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
}
