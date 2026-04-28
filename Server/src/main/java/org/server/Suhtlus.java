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


    // SAATMISED
    public static void init(DataOutputStream valgeOut, DataInputStream valgeIn, DataOutputStream mustOut, DataInputStream mustIn) throws IOException {
        mustOut.writeInt(2);
        mustOut.writeInt(manguAlgus);
        mustOut.writeInt(must);
        if (mustIn.readInt() != 1) throw new RuntimeException("Ei saanud confirmation koodi");


        valgeOut.writeInt(2);
        valgeOut.writeInt(manguAlgus);
        valgeOut.writeInt(valge);
        if (valgeIn.readInt() != 1) throw new RuntimeException("Ei saanud confirmation koodi");
    }

    public static void teavitaEtManguLopp(int tulemus, DataOutputStream out) throws IOException {
        out.writeInt(2);
        out.writeInt(manguLopp);
        out.writeInt(-tulemus);
    }

    public static void saadaInfo(DataOutputStream out, DataInputStream in, int kood, int[] sisu) throws IOException {
        System.out.println("Saadan: pikkus - " + sisu.length+1 + ", kood - " + kood + ", sisu - " + Arrays.toString(sisu));
        out.writeInt(sisu.length +1);
        out.writeInt(kood);
        for (int i : sisu) {
            out.writeInt(i);
        }
        if (in.readInt() != 1) throw new RuntimeException("Ei saanud õiget confirmation koodi");
    }

    public static void saadaKaiguInfo(DataOutputStream tegijaOut, DataOutputStream out, DataInputStream in, int[] kaik) throws IOException {
        saadaInfo(out, in, kaiguKood, kaik);
        tegijaOut.writeInt(kaikOk);
    }


    // LUGEMISED
    public static int[] loeKoik(DataInputStream in, DataOutputStream out) throws IOException {
        int pikkus = in.readInt();
        int[] tagastus = new int[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readInt();
        }
        System.out.println("Sain: pikkus - " + pikkus + ", sisu - " + Arrays.toString(tagastus));

        out.writeInt(1);
        return tagastus;
    }

    public static int[] loeKaik(DataInputStream tegijaIn, DataOutputStream tegijaOut, DataOutputStream vastaneOut) throws IOException {
        int[] sisse = loeKoik(tegijaIn, tegijaOut);
        if (sisse[0] == manguLopp) {
            teavitaEtManguLopp(sisse[1], vastaneOut);
            return new int[]{0};
        }
        return kustutaKood(sisse);
    }


    // ABI
    public static int[] kustutaKood(int[] sisu) {
        int[] tagastus = new int[sisu.length-1];
        for (int i = 1; i < sisu.length; i++) {
            tagastus[i-1] = sisu[i];
        }
        return tagastus;
    }
}
