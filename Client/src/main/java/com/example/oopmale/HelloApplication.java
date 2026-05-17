package com.example.oopmale;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class HelloApplication extends Application {

    private String ipAdress = "localhost";

    @Override
    public void start(Stage stage) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Serveri ühenduse loomine
        File storeFile = new File("truststore.p12");
        String storePass = "secret";

        KeyStore store = KeyStore.getInstance(storeFile, storePass.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(store);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustManagers, null);


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
        valik.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
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
            new Thread(UhendaServeriga.create(veateade, stage, ctx, ip, port, ruumiKood, botiVastu, algStseen)).start();
        });
    }
}