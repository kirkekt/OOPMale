package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Suhtlus {

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

    /**
     * Annab mõlemale kliendile teada mis värv nad on
     * @param valgeOut esimesena liitunud klient on valge
     * teisena liitunud klient on must
     */
    public static void init(DataOutputStream valgeOut, DataInputStream valgeIn, DataOutputStream mustOut, DataInputStream mustIn) throws IOException {
        mustOut.writeInt(2);
        mustOut.writeInt(manguAlgus);
        mustOut.writeInt(must);
        mustIn.readInt();


        valgeOut.writeInt(2);
        valgeOut.writeInt(manguAlgus);
        valgeOut.writeInt(valge);
        valgeIn.readInt();
    }

    /**
     * Annab edasi teavituse mängu lõpust
     * @param tulemus -1 kui teavitaja kaotas või 0 kui jäi viiki
     * @param out klient, kellele teavitada
     */
    public static void teavitaEtManguLopp(int tulemus, DataOutputStream out) throws IOException {
        out.writeInt(2);
        out.writeInt(manguLopp);
        out.writeInt(-tulemus);
    }

    /**
     * Loeb sisse kogu kliendi saadetud käigu info
     * @param in klient kelle käiku lugeda
     * @return tagastab array kogu käigu kohta käiva infoga
     */
    public static int[] loeKoik(DataInputStream in) throws IOException {
        int pikkus = in.readInt();
        System.out.println("Sain:");
        System.out.println("pikkus - " + pikkus);
        int[] tagastus = new int[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readInt();
        }
        System.out.println("sisu - " + Arrays.toString(tagastus));
        return tagastus;
    }

    public static void saadaKood(DataOutputStream out, int kood) throws IOException {
        out.writeInt(1);
        out.writeInt(kood);
    }

    public static void saadaInfo(DataOutputStream out, DataInputStream in, int kood, int[] sisu) throws IOException {
        System.out.println("Saadan:");
        System.out.println("pikkus - " + sisu.length+1 + ", kood - " + kood + ", sisu - " + Arrays.toString(sisu));
        out.writeInt(sisu.length +1);
        out.writeInt(kood);
        for (int i : sisu) {
            out.writeInt(i);
        }
        in.readInt();
    }

    public static int[] eemaldaKood(int[] sisu) {
        int[] tagastus = new int[sisu.length-1];
        for (int i = 1; i < sisu.length; i++) {
            tagastus[i-1] = sisu[i];
        }
        return tagastus;
    }
}
