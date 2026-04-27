package com.example.oopmale;

import javafx.application.Application;
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
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        DataInputStream in = new DataInputStream(server.getInputStream());

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

        stage.setOnCloseRequest(e -> {
            try {
                server.close();
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        });

        // Alustab mängu loop-i teises threadis
        Thread manguThread = new Thread(new GameLoop(onValge, in, out, malelaud, lauaVaade, kaigud));
        manguThread.setDaemon(true);
        manguThread.start();

    }
}