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

        VBox layout = new VBox(10, new Label("IP:"), ipVäli, new Label("port:"),portVäli, bott, inimene, ruumikoodLabel, ruumikoodiVäli, veateade, ühendaNupp);
        layout.setPadding(new Insets(30));
        stage.setScene(new Scene(layout, 400, 350));
        stage.setTitle("Ühenda serveriga");
        stage.show();

        ühendaNupp.setOnAction(e -> new Thread(() -> {
            try {
                Platform.runLater(() -> veateade.setText("Ootan ühendust."));
                SSLSocket server = (SSLSocket) ctx.getSocketFactory().createSocket(ipVäli.getText().trim(), Integer.parseInt(portVäli.getText().trim()));
                server.startHandshake();
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                DataInputStream in = new DataInputStream(server.getInputStream());

                boolean botiVastu = bott.isSelected();
                out.writeBoolean(botiVastu);
                System.out.println(botiVastu);
                if (!botiVastu) {
                    out.writeUTF(ruumikoodiVäli.getText());
                    System.out.println(ruumikoodiVäli.getText());
                }

                // Muutujate ette valmistamine
                final boolean onValge = Suhtlus.kasValge(in, out);
                GameLoop gl = new GameLoop(onValge, in, out);
                LauaVaade lauaVaade = new LauaVaade(onValge, gl);
                gl.setLauaVaade(lauaVaade);

                // Malelaua ette valmistamine
                lauaVaade.uuendaLaud(Suhtlus.loeLaud(in, out));
                Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);

                Platform.runLater(() -> {
                    if (onValge) stage.setTitle("Male, Valge");
                    else stage.setTitle("Male, Must");
                    stage.setScene(scene);

                    stage.setOnCloseRequest(ev -> {
                        try { server.close(); } catch (Exception e1) { throw new RuntimeException(e1); }
                    });
                });

                Thread thread = new Thread(gl);
                thread.setDaemon(true);
                thread.start();

            } catch (Exception ex) {
                Platform.runLater(() -> veateade.setText("Viga: " + ex.getMessage()));
            }
        }).start());
    }
}