package com.example.oopmale;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameLoop implements Runnable{

    private boolean onValge;
    private DataInputStream in;
    private DataOutputStream out;
    private LauaVaade lauaVaade;
    private BlockingQueue<int[]> klikid = new LinkedBlockingQueue<>();

    public GameLoop(boolean onValge, DataInputStream in, DataOutputStream out) {
        this.onValge = onValge;
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
            boolean kaib = true;
            while (kaib) {
                Set<Nupp> laud = Suhtlus.loeLaud(in, out);
                if (laud == null) {
                    break;
                }
                Platform.runLater(() -> lauaVaade.uuendaLaud(laud));
                lauaVaade.setMinuKaik(true);

                while (true) {
                    int[] klikk = klikid.take();
                    int serveriVastus = Suhtlus.saadaKlikk(in, out, klikk[0], klikk[1]);
                    System.out.println("Server vastas klikile koodiga: "+serveriVastus);

                    //kaik sai läbi, uuendab laua ja jääb vastase laua uuendust ootama
                    if (serveriVastus == Suhtlus.kaiguLopp) {
                        lauaVaade.setMinuKaik(false);
                        klikid.clear();
                        Set<Nupp> uuslaud = Suhtlus.loeLaud(in, out);
                        if (uuslaud == null) {kaib = false; break;}
                        Platform.runLater(() -> lauaVaade.uuendaLaud(uuslaud));
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
            lauaVaade.mangLabi(Suhtlus.tulemus);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
