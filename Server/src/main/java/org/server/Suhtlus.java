package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    // SAATMISED
    public static void init(DataOutputStream valgeOut, DataInputStream valgeIn, DataOutputStream mustOut, DataInputStream mustIn, Malelaud malelaud) throws IOException {
        mustOut.writeInt(2);
        mustOut.writeInt(manguAlgus);
        mustOut.writeInt(must);
        if (mustIn.readInt() != 1) throw new RuntimeException("Ei saanud confirmation koodi");
        saadaLaud(mustIn, mustOut, malelaud.getKoikNupud());


        valgeOut.writeInt(2);
        valgeOut.writeInt(manguAlgus);
        valgeOut.writeInt(valge);
        if (valgeIn.readInt() != 1) throw new RuntimeException("Ei saanud confirmation koodi");
        saadaLaud(valgeIn, valgeOut, malelaud.getKoikNupud());
    }

    public static void teavitaEtManguLopp(DataOutputStream valgeOut, DataOutputStream mustOut, int tulemus) throws IOException {
        valgeOut.writeInt(2);
        valgeOut.writeInt(manguLopp);
        mustOut.writeInt(2);
        mustOut.writeInt(manguLopp);

        if (tulemus == viik) {
            valgeOut.writeInt(viik);
            mustOut.writeInt(viik);
        } else {
            valgeOut.writeInt(tulemus);
            mustOut.writeInt(-tulemus);
        }
    }

    public static void saadaLaud(DataInputStream in, DataOutputStream out, List<Malenupp> nupud) throws IOException {
        out.writeInt(nupud.size()*3+1);
        out.writeInt(lauaOlek);
        for (Malenupp nupp : nupud) {
            if (!nupp.isElus()) continue;
            out.writeInt(nupp.getNupuKood());
            out.writeInt(nupp.getX());
            out.writeInt(nupp.getY());
        }
        if (in.readInt() != 1) throw new RuntimeException("Ei sanud kliendilt koikOK koodi");
    }

    public static void saadaVoimalikud(DataInputStream in, DataOutputStream out, List<Asukoht> voimalikud) throws IOException {
        out.writeInt(1+(voimalikud.size()*2));
        out.writeInt(voimalikudKaigud);
        for (Asukoht asukoht : voimalikud) {
            out.writeInt(asukoht.getX());
            out.writeInt(asukoht.getY());
        }
        if (in.readInt() != 1) throw new RuntimeException("Ei sanud kliendilt koikOK koodi");
    }


    // LUGEMISED
    public static int[] loeKoik(DataInputStream in, DataOutputStream out) throws IOException {
        int pikkus = in.readInt();
        if (pikkus < 0 || pikkus > 1000) throw new IOException("Packet liiga suur");
        int[] tagastus = new int[pikkus];

        for (int i = 0; i < pikkus; i++) {
            tagastus[i] = in.readInt();
        }
        //System.out.println("Sain: pikkus - " + pikkus + ", sisu - " + Arrays.toString(tagastus));

        out.writeInt(1);
        return tagastus;
    }

    public static Asukoht loeKlikk(DataInputStream in, DataOutputStream out) throws IOException {
        int[] klikk = loeKoik(in, out);
        if (klikk[0] != klikkTehti) throw new RuntimeException("Ei saadetud klikki, saadeti: " + Arrays.toString(klikk));
        return new Asukoht(klikk[1], klikk[2]);
    }
}
