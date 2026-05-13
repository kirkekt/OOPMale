package com.example.oopmale;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.BlockingQueue;

public class GameLoop implements Runnable{

    boolean onValge;
    DataInputStream in;
    DataOutputStream out;
    LauaVaade lauaVaade;
    BlockingQueue<int[]> kaigud;

    /*public GameLoop(boolean onValge, DataInputStream in, DataOutputStream out, LauaVaade lauaVaade, BlockingQueue<int[]> kaigud) {
        this.onValge = onValge;
        this.in = in;
        this.out = out;
        this.lauaVaade = lauaVaade;
        this.kaigud = kaigud;
    }*/

    @Override
    public void run() {
        /*try {

            while (true) {
                int[] kordinaadid = kaigud.take();

                System.out.println("Proovin käiku: " + kordinaadid[0] + "," + kordinaadid[1] + " -> " + kordinaadid[2] + "," + kordinaadid[3]);

                Suhtlus.saadaKaik(in, out, kordinaadid);
                if (in.readInt() == Suhtlus.error) {
                    //lauaVaade.klikidReset();
                    continue;
                } else {
                    malelaud.teeKaik(kordinaadid, lauaVaade);
                    Platform.runLater(lauaVaade::klikidReset);
                }

                int[] vastaseKaik = Suhtlus.loeKaik(in, out);
                if (vastaseKaik[0] == Suhtlus.manguLopp) {
                    System.out.print("mäng läbi - ");
                    if (vastaseKaik[1] == Suhtlus.kaotus) {
                        System.out.println("kaotasid");
                    } else if (vastaseKaik[1] == Suhtlus.viik) {
                        System.out.println("jäite viiki");
                    } else if (vastaseKaik[1] == Suhtlus.võit) {
                        System.out.println("võitsid");
                    }
                    break;
                } else {
                    malelaud.teeKaik(vastaseKaik, lauaVaade);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }
}
