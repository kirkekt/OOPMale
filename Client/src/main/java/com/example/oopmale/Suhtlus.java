package com.example.oopmale;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Suhtlus {

    /* ------- koodid ------- */
    public static final byte manguLopp = 127;
    public static final byte manguAlgus = 126;
    public static final byte kaiguKood = 125;
    public static final byte klikkTehti = 124;
    public static final byte lauaOlek = 123;
    public static final byte voimalikudKaigud = 122;
    // vastused
    public static final byte error = 0;
    public static final byte koikOk = 1;

    /* ------- sisud ------- */
    // nupud laua olekus
    public static final byte vEttur = 1;
    public static final byte mEttur = 2;
    public static final byte vVanker = 3;
    public static final byte mVanker = 4;
    public static final byte vRatsu= 5;
    public static final byte mRatsu = 6;
    public static final byte vOda= 7;
    public static final byte mOda = 8;
    public static final byte vLipp = 9;
    public static final byte mLipp = 10;
    public static final byte vKuningas = 11;
    public static final byte mKuningas = 12;
    // mängu algus
    public static final byte valge = 1;
    public static final byte must = 2;
    // mängu lõpp
    public static final byte kaotus = -1;
    public static final byte viik = 0;
    public static final byte võit = 1;


    // LUGEMISED
    private static byte[] loeKoik(DataInputStream in, DataOutputStream out) throws IOException {
        int pikkus = in.readInt();
        byte[] tagastus = new byte[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readByte();
        }
        System.out.println("Sain serverilt: pikkus - " + pikkus + ", sisu - " + Arrays.toString(tagastus));
        out.writeInt(1);
        return tagastus;
    }

    public static boolean kasValge(DataInputStream in, DataOutputStream out) throws IOException {
        byte[] info = loeKoik(in, out);
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

    public static Set<Nupp> loeLaud(DataInputStream in, DataOutputStream out) throws IOException {
        Set<Nupp> tagastus = new HashSet<Nupp>();
        byte[] info = loeKoik(in, out);
        if (info[0] != lauaOlek) {
            throw new RuntimeException("Oodatud \"laua olek\", kuid saadud: " + info[0]);
        }
        info = kustutaKood(info);
        for (int i = 0; i < info.length; i+=3) {
            tagastus.add(new Nupp())
        }
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
    private static byte[] kustutaKood(byte[] n) {
        byte[] tagastus = new byte[n.length - 1];
        for (int i = 1; i < n.length; i++) {
            tagastus[i-1] = n[i];
        }
        return tagastus;
    }
}
