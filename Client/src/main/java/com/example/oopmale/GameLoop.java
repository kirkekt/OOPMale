package com.example.oopmale;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameLoop implements Runnable{

    private DataInputStream in;
    private DataOutputStream out;
    private LauaVaade lauaVaade;
    private BlockingQueue<int[]> klikid = new LinkedBlockingQueue<>();

    public GameLoop(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void setLauaVaade(LauaVaade lauaVaade) {
        this.lauaVaade = lauaVaade;
    }

    public void lisaKlikk(int x, int y) {
        klikid.add(new int[]{x, y});
    }

    @Override
    public void run() {
        try {
            Set<Nupp> laud;
            int[] tulemus = new int[]{0};
            boolean kaib = true;
            while (kaib) {
                laud = Suhtlus.loeLaud(in, out, tulemus);
                if (laud == null) {
                    break;
                }
                var fl = laud;
                Platform.runLater(() -> lauaVaade.uuendaLaud(fl));
                lauaVaade.setMinuKaik(true);

                while (true) {
                    int[] klikk = klikid.take();
                    int serveriVastus = Suhtlus.saadaKlikk(in, out, klikk[0], klikk[1]);
                    System.out.println("Server vastas klikile koodiga: "+serveriVastus);
                    Platform.runLater(() -> lauaVaade.clearVoimalikud());

                    //kaik sai läbi, uuendab laua ja jääb vastase laua uuendust ootama
                    if (serveriVastus == Suhtlus.kaiguLopp) {
                        lauaVaade.setMinuKaik(false);
                        klikid.clear();
                        laud = Suhtlus.loeLaud(in, out, tulemus);
                        if (laud == null) {kaib = false; break;}
                        var fl1 = laud;
                        Platform.runLater(() -> lauaVaade.uuendaLaud(fl1));
                        break;
                    }

                    //klikiti nupp, aga nupp ei saa kuhugi käia
                    if (serveriVastus == Suhtlus.voimalikudPuuduvad) {
                        Platform.runLater(() -> lauaVaade.uuendaVoimalikud(klikk[0], klikk[1], new int[0][]));
                        continue;
                    }

                    //klikiti nupp, mis saab käia
                    if (serveriVastus == Suhtlus.saadanVoimalikud) {
                        int[][] voimalikud = Suhtlus.loeVoimalikud(in, out);
                        Platform.runLater(() -> lauaVaade.uuendaVoimalikud(klikk[0], klikk[1], voimalikud));
                    }
                }
            }
            lauaVaade.mangLabi(tulemus[0]);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
