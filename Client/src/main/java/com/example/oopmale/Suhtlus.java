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
    // vastused
    public static final int error = 0;
    public static final int koikOk = 1;
    public static final int kaiguLopp = 3;

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
                return null;
            default:
                throw new RuntimeException("Oodatud \"laua olek\", kuid saadud: " + info[0]);
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

    public static int saadaKlikk(DataInputStream in, DataOutputStream out, int[] klikk) throws IOException {
        System.out.println("Saadan: " + "pikkus - " + (1+klikk.length) + ", kood - " + klikkTehti + ", sisu - " + Arrays.toString(klikk));
        out.writeInt(1 + klikk.length);
        out.writeInt(klikkTehti);
        for (int i : klikk) {
            out.writeInt(i);
        }
        int serveriTagastus = in.readInt();
        if (in.readInt() == 0) throw new RuntimeException("Sain serverilt error koodi");
        return serveriTagastus;
    }

    public static int kusiManguTulemust(DataInputStream in, DataOutputStream out) throws IOException {
        System.out.println("Saadan: pikkus - 1, kood - " + kuidasMangLoppes);
        out.writeInt(1);
        out.writeInt(kuidasMangLoppes);
        if (in.readInt() != 1) throw new RuntimeException("Server tagastas mängu lõpus liiga pika sõnumi");
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
