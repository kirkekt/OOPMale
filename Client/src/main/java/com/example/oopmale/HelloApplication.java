package com.example.oopmale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Laud laud = new Laud();
        LauaVaade lauaVaade = new LauaVaade(laud);
        Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);
        stage.setTitle("Male");
        stage.setScene(scene);
        stage.show();
    }

}
