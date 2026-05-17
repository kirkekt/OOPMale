package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                System.out.println("uus ring");
                if (valgeKord) {
                    kaiguTegijaIn = valgeIn;
                    kaiguTegijaOut = valgeOut;
                }
                else {
                    kaiguTegijaIn = mustIn;
                    kaiguTegijaOut = mustOut;
                }

                List<Asukoht> voimalikud = new ArrayList<>();
                Asukoht liigutatav = null;

                while (true) {
                    Asukoht klikk = Suhtlus.loeKlikk(kaiguTegijaIn, kaiguTegijaOut);

                    if (voimalikud.contains(klikk)) {
                        if (liigutatav != null) {
                            malelaud.teeKaik(liigutatav, klikk);
                            System.out.println("liigutan: " + liigutatav + " -> " + klikk);
                            if (malelaud.asendatavaEtturiAsukoht() != null) {
                                malelaud.asendaEttur("Lipp");
                            }
                            malelaud.uuendaSeisuLoendur();
                            kaiguTegijaOut.writeInt(Suhtlus.kaiguLopp);
                            System.out.println(1);
                            List<Malenupp> koikNupud = malelaud.getKoikNupud();
                            koikNupud.removeIf(x -> !x.isElus());
                            Suhtlus.saadaLaud(valgeIn, valgeOut, koikNupud);
                            Suhtlus.saadaLaud(mustIn, mustOut, koikNupud);
                            System.out.println(2);
                            valgeKord = !valgeKord;
                            break;
                        }
                    }

                    List<Malenupp> nupud;
                    if (valgeKord) {
                        nupud = malelaud.getValgedNupud();
                    }
                    else {
                        nupud = malelaud.getMustadNupud();
                    }

                    Optional<Malenupp> nupp = nupud.stream().filter(x -> x.getAsukoht().equals(klikk)).findAny();

                    if (nupp.isPresent()) {
                        liigutatav = nupp.get().getAsukoht();
                        voimalikud = malelaud.nupuVoimalikudKaigud(nupp.get());
                        if (voimalikud.isEmpty()) kaiguTegijaOut.writeInt(Suhtlus.voimalikudPuuduvad);
                        else {
                            kaiguTegijaOut.writeInt(Suhtlus.saadanVoimalikud);
                            Suhtlus.saadaVoimalikud(kaiguTegijaIn, kaiguTegijaOut, voimalikud);
                        }
                    } else {
                        kaiguTegijaOut.writeInt(Suhtlus.saadaUusKlikk);
                    }
                }

                int kasManguLopp = malelaud.mangLabi(valgeKord);
                if (kasManguLopp != 0) {
                    switch (kasManguLopp) {
                        case 1:
                            System.out.println("Valge võitis");
                            break;
                        case -1:
                            System.out.println("Must võitis");
                            break;
                        case 67:
                            System.out.println("Haahaa viiki jäi");
                    }
                    Suhtlus.teavitaEtManguLopp(valgeOut, mustOut, kasManguLopp);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

