package com.example.oopmale;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {

        // Mängu alguse stseen

        TextField ipVäli = new TextField("localhost");
        TextField portVäli = new TextField("1337");
        TextField ruumikoodiVäli = new TextField("ABCD");
        Label veateade = new Label();
        veateade.setStyle("-fx-text-fill: red;");
        Button ühendaNupp = new Button("Ühenda");
        ühendaNupp.setDefaultButton(true);

        ToggleGroup valik = new ToggleGroup();
        RadioButton bott = new RadioButton("Boti vastu");
        RadioButton inimene = new RadioButton("Inimese vastu");
        bott.setToggleGroup(valik);
        inimene.setToggleGroup(valik);
        bott.setSelected(true);

        Label ruumikoodLabel = new Label("Ruumikood:");
        ruumikoodiVäli.setDisable(true);
        valik.selectedToggleProperty().addListener((_, _, newVal) -> {
            boolean onInimene = newVal == inimene;
            ruumikoodiVäli.setDisable(!onInimene);
        });

        VBox layout = new VBox(10, new Label("IP:"), ipVäli, new Label("port:"), portVäli, bott, inimene, ruumikoodLabel, ruumikoodiVäli, veateade, ühendaNupp);
        layout.setPadding(new Insets(30));
        Scene algStseen = new Scene(layout, 400, 350);
        stage.setScene(algStseen);
        stage.setTitle("Ühenda serveriga");
        stage.show();

        ühendaNupp.setOnAction(_ -> {
            boolean botiVastu = bott.isSelected();
            String ruumiKood = ruumikoodiVäli.getText().trim();
            int port = Integer.parseInt(portVäli.getText().trim());
            String ip = ipVäli.getText().trim();
            new Thread(UhendaServeriga.create(veateade, stage, ip, port, ruumiKood, botiVastu, algStseen)).start();
        });
    }
}