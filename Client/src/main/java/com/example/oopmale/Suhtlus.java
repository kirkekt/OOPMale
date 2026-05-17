package com.example.oopmale;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Suhtlus {

    /* ------- koodid ------- */
    public static final int kuidasMangLoppes = 128;
    public static final int manguLopp = 127;
    public static final int manguAlgus = 126;
    public static final int kaiguKood = 125;
    public static final int klikkTehti = 124;
    public static final int lauaOlek = 123;
    public static final int voimalikudKaigud = 122;
    public static final int vangerdus = 121;
    // vastused
    public static final int error = 0;
    public static final int koikOk = 1;
    public static final int kaiguLopp = 3;
    public static final int saadanVoimalikud = 4;
    public static final int voimalikudPuuduvad = 5;
    public static final int saadaUusKlikk = 6;


    /* ------- sisud ------- */
    // nupud laua olekus
    public static final int vEttur = 1;
    public static final int mEttur = 2;
    public static final int vVanker = 3;
    public static final int mVanker = 4;
    public static final int vRatsu= 5;
    public static final int mRatsu = 6;
    public static final int vOda= 7;
    public static final int mOda = 8;
    public static final int vLipp = 9;
    public static final int mLipp = 10;
    public static final int vKuningas = 11;
    public static final int mKuningas = 12;
    // mängu algus
    public static final int valge = 1;
    public static final int must = 2;
    // mängu lõpp
    public static final int kaotus = -1;
    public static final int viik = 67;
    public static final int voit = 1;

    public static int tulemus = -1;


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

    public static Set<Nupp> loeLaud(DataInputStream in, DataOutputStream out) throws IOException {
        Set<Nupp> tagastus = new HashSet<>();
        int[] info = loeKoik(in, out);
        switch (info[0]) {
            case lauaOlek:
                info = kustutaKood(info);
                for (int i = 0; i < info.length; i+=3) {
                    tagastus.add(new Nupp(info[i], info[i+1], info[i+2]));
                }
                return tagastus;
            case manguLopp:
                tulemus = info[1];
                return null;
            default:
                throw new RuntimeException("Oodatud \"laua olek\", kuid saadud: " + info[0]);
        }
    }

    public static int[][] loeVoimalikud(DataInputStream in, DataOutputStream out) throws IOException {
        int len = (in.readInt()-1)/2;
        int kood = in.readInt();
        if (kood != voimalikudKaigud) throw new RuntimeException("Vale kood, ootasin voimalikud kaigud, aga sain: " + kood);
        int[][] tagastus = new int[len][];
        for (int i = 0; i < len; i++) {
            tagastus[i] = new int[]{in.readInt(), in.readInt()};
        }
        out.writeInt(1);
        System.out.println("sain pikkus "+(len*2+1)+", kood " + kood + ", sisu " + Arrays.deepToString(tagastus));
        return tagastus;
    }

    // SAATMISED
    public static int saadaKlikk(DataInputStream in, DataOutputStream out, int x, int y) throws IOException {
        System.out.println("Saadan: " + "pikkus - " + 3 + ", kood - " + klikkTehti + ", sisu - " + x + ", " +y);
        out.writeInt(3);
        out.writeInt(klikkTehti);
        out.writeInt(x);
        out.writeInt(y);
        in.readInt();
        return in.readInt();
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
