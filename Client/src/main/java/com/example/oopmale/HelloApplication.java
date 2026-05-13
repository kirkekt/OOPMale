package com.example.oopmale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.net.ssl.SSLContext;
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
        Socket server = ctx.getSocketFactory().createSocket("localhost", 1337);
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        DataInputStream in = new DataInputStream(server.getInputStream());

        // Muutujate ette valmistamine
        final boolean onValge = Suhtlus.kasValge(in, out);
        LauaVaade lauaVaade = new LauaVaade(onValge);

        // Malelaua ette valmistamine
        lauaVaade.uuendaLaud();
        Scene scene = new Scene(lauaVaade.getVaade(), 700, 700);
        if (onValge) stage.setTitle("Male, Valge");
        else stage.setTitle("Male, Must");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> {
            try {server.close();} catch (Exception e1) {throw new RuntimeException(e1);}
        });

        while (true) {
            lauaVaade.uuendaLaud();
        }

    }
}