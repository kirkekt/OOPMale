package com.example.oopmale;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HelloApplication extends Application {

    private String ipAdress = "localhost";

    @Override
    public void start(Stage stage) throws IOException {

        // Serveri ühenduse loomine
        Socket server = new Socket(ipAdress, 1337);
        DataInputStream in = new DataInputStream(server.getInputStream());
        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        // Muutujate ette valmistamine
        final boolean onValge = Suhtlus.kasValge(in, out);
        BlockingQueue<int[]> kaigud = new LinkedBlockingQueue<>();

        // Malelaua ette valmistamine
        Malelaud malelaud = new Malelaud();
        LauaVaade lauaVaade = new LauaVaade(malelaud, onValge, kaigud);
        Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);
        stage.setTitle("Male, Valge: " + onValge);
        stage.setScene(scene);
        stage.show();

        // Akna sulgumisel sulgub ka ühendus serveriga
        stage.setOnCloseRequest(e -> {try {server.close();} catch (Exception e1) {throw new RuntimeException(e1);}});

        // Alustab mängu loop-i teises threadis
        Thread manguThread = new Thread(() -> {
            try {
                if (!onValge) {
                    int[] vastaseKaik = Suhtlus.loeKaik(in, out);
                    malelaud.teeKaik(vastaseKaik, lauaVaade);
                }
                while (true) {
                    int[] kordinaadid = kaigud.take();

                    System.out.println("Proovin käiku: " + kordinaadid[0] + "," + kordinaadid[1] + " -> " + kordinaadid[2] + "," + kordinaadid[3]);

                    Suhtlus.saadaKaik(in, out, kordinaadid);
                    if (in.readInt() == Suhtlus.illegaalneKaik) {
                        //lauaVaade.klikidReset();
                        continue;
                    }
                    else {
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
                    }
                    else {malelaud.teeKaik(vastaseKaik, lauaVaade);}
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        manguThread.setDaemon(true);
        manguThread.start();
    }
}
