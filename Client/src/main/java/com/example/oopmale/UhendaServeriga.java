package com.example.oopmale;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;



public class UhendaServeriga implements Runnable {

    private static boolean elus = false;

    public static synchronized UhendaServeriga create(Label veateade, Stage stage, SSLContext ctx, String ip, int port, String ruumikood, boolean botiVastu) {
        if (elus) {
            return null;
        }
        elus = true;
        return new UhendaServeriga(veateade, stage, ctx, ip, port, ruumikood, botiVastu);
    }

    public static synchronized void destroy() {
        elus = false;
    }

    private Label veateade;
    private Stage stage;
    private SSLContext ctx;
    private String ip;
    private int port;
    private String ruumikood;
    private boolean botiVastu;

    private UhendaServeriga(Label veateade, Stage stage, SSLContext ctx, String ip, int port, String ruumikood, boolean botiVastu) {
        this.veateade = veateade;
        this.stage = stage;
        this.ctx = ctx;
        this.ip = ip;
        this.port = port;
        this.ruumikood = ruumikood;
        this.botiVastu = botiVastu;
    }

    @Override
    public void run() {

        try {
            Platform.runLater(() -> veateade.setText("Ootan ühendust."));
            SSLSocket server = (SSLSocket) ctx.getSocketFactory().createSocket(ip, port);
            server.startHandshake();
            Platform.runLater(() -> {
                stage.setOnCloseRequest(ev -> {
                    try {
                        server.close();
                    } catch (Exception e1) {
                        throw new RuntimeException(e1);
                    }
                });
            });

            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            DataInputStream in = new DataInputStream(server.getInputStream());

            out.writeBoolean(botiVastu);
            System.out.println(botiVastu);
            if (!botiVastu) {
                out.writeUTF(ruumikood);
                in.readInt();
                out.writeInt(1);
                System.out.println(ruumikood);
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
            });

            Thread thread = new Thread(gl);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception ex) {
            UhendaServeriga.destroy();
            Platform.runLater(() -> veateade.setText("Viga: " + ex.getMessage()));
        }
    }
}
