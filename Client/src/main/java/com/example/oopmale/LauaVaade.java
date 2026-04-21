package com.example.oopmale;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

public class LauaVaade {
    private GridPane ruudustik = new GridPane();
    private Malelaud malelaud;
    private BlockingQueue<int[]> kaigud;

    private Integer esimeneX = null;
    private Integer esimeneY = null;

    private boolean onValge;

    public LauaVaade(Malelaud laud, boolean onValge, BlockingQueue<int[]> kaigud) {
        this.onValge = onValge;
        this.kaigud = kaigud;
        malelaud = laud;
        uuendaLaud();
    }

    private void nuppKlikiti(Nupp nupp) {
        int x = nupp.getX();
        int y = nupp.getY();

        if (esimeneX == null) {
            esimeneX = x;
            esimeneY = y;
            System.out.println("Esimene klikk: " + x + ", " + y);
        } else {
            teeTeineKlikk(x, y);
        }
    }

    private void ruutKlikiti(int x, int y) {
        if (esimeneX != null) {
            teeTeineKlikk(x, y);
        }
    }

    private void teeTeineKlikk(int uusX, int uusY) {

        try {
            kaigud.put(new int[]{esimeneX, esimeneY, uusX, uusY});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Teine klikk: " + uusX + ", " + uusY);
    }

    private void ehitaLaud() {
        for (int rida = 0; rida < 9; rida++) {
            for (int veerg = 0; veerg < 9; veerg++) {

                int reaNumber;
                if (onValge) {
                    reaNumber = 9 - rida;
                } else {
                    reaNumber = rida;
                }

                if (veerg == 0 && rida == 0) {
                    // top-left empty corner
                } else if (rida == 0) {
                    Label täht = new Label(Character.toString((char) ('A' + veerg - 1)));
                    täht.setMaxWidth(Double.MAX_VALUE);
                    täht.setAlignment(Pos.CENTER);
                    ruudustik.add(täht, veerg, rida);
                } else if (veerg == 0) {
                    Label number = new Label(Integer.toString(reaNumber));
                    number.setMinWidth(30);
                    number.setMaxWidth(Double.MAX_VALUE);
                    number.setAlignment(Pos.CENTER);
                    ruudustik.add(number, veerg, rida);
                } else {
                    Rectangle ruut = new Rectangle(80, 80);

                    if ((rida + veerg) % 2 == 0) {
                        ruut.setFill(Color.WHITE);
                    } else {
                        ruut.setFill(Color.GREEN);
                    }

                    int x = veerg - 1;
                    int y = reaNumber - 1;


                    ruut.setOnMouseClicked(e -> ruutKlikiti(x, y));

                    ruudustik.add(ruut, veerg, rida);
                }
            }
        }
    }

    public GridPane getVaade() {
        return ruudustik;
    }

    private ImageView getPilt(Nupp nupp) {
        String värv = nupp.onValge() ? "valge" : "must";
        String failiNimi = nupp.getMalend();

        Image pilt = new Image(getClass().getResourceAsStream("/pildid/" + värv + "/" + failiNimi + ".png"));
        ImageView vaade = new ImageView(pilt);
        GridPane.setHalignment(vaade, HPos.CENTER);
        GridPane.setValignment(vaade, VPos.CENTER);
        vaade.setOnMouseClicked(e -> nuppKlikiti(nupp));
        return vaade;
    }

    public void uuendaLaud() {
        ruudustik.getChildren().clear();
        ehitaLaud();

        malelaud.getKoikNupud().forEach(
                malend -> ruudustik.add(getPilt(malend), malend.getX() + 1, onValge ? 8 - malend.getY() : malend.getY() + 1)
        );
    }

    public void klikidReset() {
        esimeneX = null;
        esimeneY = null;
    }
}