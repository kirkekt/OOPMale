package com.example.oopmale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Malelaud malelaud = new Malelaud();
        LauaVaade lauaVaade = new LauaVaade(malelaud);
        Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);
        stage.setTitle("Male");
        stage.setScene(scene);
        stage.show();

    }
}
