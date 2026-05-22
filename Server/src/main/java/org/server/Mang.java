package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Mang implements Runnable {

    private final Socket valgeSocket;
    private final Socket mustSocket;
    private final boolean botiVastu;


    public Mang(Socket valge, Socket must, boolean botiVastu) {
        this.valgeSocket = valge;
        this.mustSocket = must;
        this.botiVastu = botiVastu;
    }

    @Override
    public void run() {
        // Tekitab malelaua
        Malelaud malelaud = new Malelaud();
        int kasManguLopp = 0;

        // Avab suhtlus-Streamid
        try (DataInputStream mustIn = new DataInputStream(mustSocket.getInputStream());
             DataOutputStream mustOut = new DataOutputStream(mustSocket.getOutputStream());
             DataInputStream valgeIn = new DataInputStream(valgeSocket.getInputStream());
             DataOutputStream valgeOut = new DataOutputStream(valgeSocket.getOutputStream())) {

            // Teavitab mõlemat mängijat
            Suhtlus.init(valgeOut, valgeIn, mustOut, mustIn, malelaud);

            boolean valgeKord = true;

            Suhtlus.saadaLaud(valgeIn, valgeOut, malelaud.getKoikNupud());

            // Algatab mängu loop-i
            while (true) {
                DataInputStream kaiguTegijaIn = valgeKord ? valgeIn : mustIn;
                DataOutputStream kaiguTegijaOut = valgeKord ? valgeOut : mustOut;
                List<Asukoht> voimalikud = new ArrayList<>();
                Asukoht liigutatav = null;

                // küsib kliendilt klikke kuni klient saab käigu tehtud
                while (true) {
                    Asukoht klikk = Suhtlus.loeKlikk(kaiguTegijaIn, kaiguTegijaOut);

                    // vaatab, kas tahetakse teha käiku (else) või klikitakse kuhugi mujale
                    if (!voimalikud.contains(klikk)) {

                        // vaatab kas klikitud ruudul on nupp, millega käigu tegija tohib käiku teha ning mis selle nupu võimalikud käigud on
                        Malenupp nupp = malelaud.misNuppRuudul(klikk);
                        if (nupp != null && nupp.onValge() == valgeKord) {
                            liigutatav = klikk;
                            voimalikud = malelaud.nupuVoimalikudKaigud(nupp);
                            if (voimalikud.isEmpty()) kaiguTegijaOut.writeInt(Suhtlus.voimalikudPuuduvad);
                            else {
                                kaiguTegijaOut.writeInt(Suhtlus.saadanVoimalikud);
                                Suhtlus.saadaVoimalikud(kaiguTegijaIn, kaiguTegijaOut, voimalikud);
                            }
                        }
                        else {
                            kaiguTegijaOut.writeInt(Suhtlus.saadaUusKlikk);
                        }

                    }
                    else {

                        // teeb käigu ära (kuna klikitud ruut oli eelnevalt valitud nupu võimalike käikude seas)
                        // ning teeb ka kõik muud sellega kaasnevad protseduurid
                        malelaud.teeKaik(liigutatav, klikk);
                        //System.out.println("liigutan: " + liigutatav + " -> " + klikk);
                        if (malelaud.asendatavaEtturiAsukoht() != null) malelaud.asendaEttur("Lipp");
                        malelaud.uuendaSeisuLoendur();
                        kaiguTegijaOut.writeInt(Suhtlus.kaiguLopp);
                        valgeKord = !valgeKord;

                        // vaatab kas mäng on läbi
                        kasManguLopp = malelaud.mangLabi(valgeKord);
                        if (kasManguLopp != 0) break;


                        // võtab nuppude nimekirja ning eemaldab kõik surnud nupud enne klientidele saatmist
                        List<Malenupp> koikNupud = malelaud.getKoikNupud();
                        koikNupud.removeIf(x -> !x.isElus());

                        //saadab klientidele laua (või botile käigu, mis tehti)
                        Suhtlus.saadaLaud(valgeIn, valgeOut, koikNupud);
                        if (botiVastu) Suhtlus.saadaBotileKaik(mustIn, mustOut, liigutatav, klikk);
                        else Suhtlus.saadaLaud(mustIn, mustOut, koikNupud);

                        break;
                    }
                }

                // kui mäng lõppes lõpetab mängu loopi
                if (kasManguLopp != 0) {
                    String tulemus = switch (kasManguLopp) {
                        case 1 -> "Valge võitis";
                        case -1 -> "Must võitis";
                        case 67 -> "Haahaa viiki jäi";
                        default -> "midagi läks valesti";
                    };
                    System.out.println(tulemus);
                    Suhtlus.teavitaEtManguLopp(valgeOut, mustOut, kasManguLopp);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

