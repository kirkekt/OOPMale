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

            // Valmistab ette muutujad
            boolean valgeKord = true;
            DataInputStream kaiguTegijaIn;
            DataOutputStream kaiguTegijaOut;

            Suhtlus.saadaLaud(valgeIn, valgeOut, malelaud.getKoikNupud());

            // Algatab mängu loop-i
            while (true) {
                if (valgeKord) {
                    kaiguTegijaIn = valgeIn;
                    kaiguTegijaOut = valgeOut;
                } else {
                    kaiguTegijaIn = mustIn;
                    kaiguTegijaOut = mustOut;
                }

                List<Asukoht> voimalikud = new ArrayList<>();
                Asukoht liigutatav = null;

                while (true) {
                    Asukoht klikk = Suhtlus.loeKlikk(kaiguTegijaIn, kaiguTegijaOut);

                    if (voimalikud.contains(klikk)) {
                        System.out.println("liigutan: " + liigutatav + " -> " + klikk);
                        malelaud.teeKaik(liigutatav, klikk);
                        if (malelaud.asendatavaEtturiAsukoht() != null) {
                            malelaud.asendaEttur("Lipp");
                        }
                        malelaud.uuendaSeisuLoendur();
                        kaiguTegijaOut.writeInt(Suhtlus.kaiguLopp);

                        List<Malenupp> koikNupud = malelaud.getKoikNupud();
                        koikNupud.removeIf(x -> !x.isElus());

                        kasManguLopp = malelaud.mangLabi(valgeKord);
                        if (kasManguLopp != 0) {
                            break;
                        }
                        Suhtlus.saadaLaud(valgeIn, valgeOut, koikNupud);
                        if (botiVastu) {
                            mustOut.writeInt(5);
                            mustOut.writeInt(Suhtlus.kaiguKood);
                            mustOut.writeInt(liigutatav.getX());
                            mustOut.writeInt(liigutatav.getY());
                            mustOut.writeInt(klikk.getX());
                            mustOut.writeInt(klikk.getY());
                            mustIn.readInt();
                        } else {
                            Suhtlus.saadaLaud(mustIn, mustOut, koikNupud);
                        }

                        valgeKord = !valgeKord;
                        break;
                    }

                    Malenupp nupp = malelaud.misNuppRuudul(klikk);
                    if (nupp != null && nupp.onValge() == valgeKord) {
                        liigutatav = klikk;
                        voimalikud = malelaud.nupuVoimalikudKaigud(nupp);
                        if (voimalikud.isEmpty()) kaiguTegijaOut.writeInt(Suhtlus.voimalikudPuuduvad);
                        else {
                            kaiguTegijaOut.writeInt(Suhtlus.saadanVoimalikud);
                            Suhtlus.saadaVoimalikud(kaiguTegijaIn, kaiguTegijaOut, voimalikud);
                        }
                    } else {
                        kaiguTegijaOut.writeInt(Suhtlus.saadaUusKlikk);
                    }
                }

                kasManguLopp = malelaud.mangLabi(valgeKord);

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

