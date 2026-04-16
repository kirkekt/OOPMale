package com.example.oopmale;

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


    // LUGEMISED
    private static int[] loeKoik(DataInputStream in, DataOutputStream out) throws IOException {
        int pikkus = in.readInt();
        int[] tagastus = new int[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readInt();
        }
        System.out.println("Sain serverilt: pikkus - " + pikkus + ", sisu - " + Arrays.toString(tagastus));
        out.writeInt(1);
        return tagastus;
    }

    public static boolean kasValge(DataInputStream in, DataOutputStream out) throws IOException {
        int[] info = loeKoik(in, out);
        if (info[0] != manguAlgus) {
            throw new RuntimeException("Oodatud \"mängu algus\", saadud kood: " + info[0]);
        }
        return (info[1] == valge);
    }

    public static int[] loeKaik(DataInputStream in, DataOutputStream out) throws IOException {
        int[] sisse = loeKoik(in, out);
        if (sisse[0] == manguLopp) {
            return new int[]{manguLopp, sisse[1]};
        }
        return kustutaKood(sisse);
    }



    // SAATMISED
    public static void saadaTegevus(DataInputStream in, DataOutputStream out, int kood, int[] sisu) throws IOException {
        System.out.println("Saadan: " + "pikkus - " + (1+sisu.length) + ", kood - " + kood + ", sisu - " + Arrays.toString(sisu));
        out.writeInt(1 + sisu.length);
        out.writeInt(kood);
        for (int i : sisu) {
            out.writeInt(i);
        }
        if (in.readInt() != 1) throw new RuntimeException("Ei saanud serverilt OK koodi");
    }

    public static void saadaKaik(DataInputStream in, DataOutputStream out, int[] kaik) throws IOException {
        saadaTegevus(in, out, kaiguKood, kaik);

    }

    public static void saadaManguLopp(DataInputStream in, DataOutputStream out, int tulemus) throws IOException {
        saadaTegevus(in, out, manguLopp, new int[]{tulemus});
    }



    // ABI
    private static int[] kustutaKood(int[] n) {
        int[] tagastus = new int[n.length - 1];
        for (int i = 1; i < n.length; i++) {
            tagastus[i-1] = n[i];
        }
        return tagastus;
    }
}
