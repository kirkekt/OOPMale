package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Mang implements Runnable {

    private final Socket valgeSocket;
    private final Socket mustSocket;

    private final int manguLopp = 256;
    private final int manguAlgus = 0;
    private final int valge = 1;
    private final int must = 2;


    public Mang(Socket valge, Socket must) {
        this.valgeSocket = valge;
        this.mustSocket = must;
    }

    /**
     * Annab mõlemale kliendile teada mis värv nad on
     * @param valgeOut esimesena liitunud klient on valge
     * @param mustOut teisena liitunud klient on must
     */
    private void init(DataOutputStream valgeOut, DataOutputStream mustOut) throws IOException {
        mustOut.writeInt(2);
        mustOut.writeInt(manguAlgus);
        mustOut.writeInt(must);

        valgeOut.writeInt(2);
        valgeOut.writeInt(manguAlgus);
        valgeOut.writeInt(valge);
    }

    /**
     * Annab edasi teavituse mängu lõpust
     * @param tulemus -1 kui teavitaja kaotas või 0 kui jäi viiki
     * @param out klient, kellele teavitada
     */
    private void teavitaEtManguLopp(int tulemus, DataOutputStream out) throws IOException {
        out.writeInt(2);
        out.writeInt(manguLopp);
        out.writeInt(tulemus);
    }

    /**
     * Loeb sisse kogu kliendi saadetud käigu info
     * @param in klient kelle käiku lugeda
     * @return tagastab array kogu käigu kohta käiva infoga
     */
    private int[] kaiguInfo(DataInputStream in) throws IOException {
        int pikkus = in.readInt();
        int[] tagastus = new int[pikkus + 1];
        tagastus[0] = pikkus;

        for (int i = 0; i < pikkus; i++) {
            tagastus[i+1] = in.readInt();
        }

        return tagastus;
    }


    @Override
    public void run() {
        System.out.println("Mäng algas...");

        try (DataInputStream mustIn = new DataInputStream(mustSocket.getInputStream());
             DataOutputStream mustOut = new DataOutputStream(mustSocket.getOutputStream());
             DataInputStream valgeIn = new DataInputStream(valgeSocket.getInputStream());
             DataOutputStream valgeOut = new DataOutputStream(valgeSocket.getOutputStream())) {

            init(valgeOut, mustOut);

            while (true) {
                //valge käik mustale
                int[] valgeKaik = kaiguInfo(valgeIn);

                System.out.println("Valge käis");

                if (valgeKaik[1] == manguLopp) {
                    teavitaEtManguLopp(valgeKaik[2], mustOut);
                    break;
                }

                for (int i : valgeKaik) {
                    mustOut.writeInt(i);
                }


                //musta käik valgele
                int[] mustaKaik = kaiguInfo(mustIn);

                System.out.println("Must käis");

                if (mustaKaik[1] == manguLopp) {
                    teavitaEtManguLopp(mustaKaik[2], valgeOut);
                    break;
                }

                for (int i : mustaKaik) {
                    valgeOut.writeInt(i);
                }
            }
            System.out.println("Mäng läbi");
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
