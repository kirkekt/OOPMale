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

public class LauaVaade {
    private GridPane ruudustik = new GridPane();
    private Malelaud malelaud;

    public LauaVaade(Malelaud laud) {
        malelaud = laud;
        uuendaLaud();
    }

    private void ehitaLaud() {
        for (int rida = 0; rida < 9; rida++) {
            for (int veerg = 0; veerg < 9; veerg++) {
                if (veerg == 0 && rida == 0) {
                } else if (rida == 0) {
                    Label täht = new Label(Character.toString((char) ('A' + veerg-1)));
                    täht.setMaxWidth(Double.MAX_VALUE);
                    täht.setAlignment(Pos.CENTER);
                    ruudustik.add(täht, veerg, rida);
                } else if (veerg == 0) {
                    Label number = new Label(Integer.toString(9-rida));
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
                    ruudustik.add(ruut, veerg, rida);
                }
            }
        }
    }

    public GridPane getVaade() {
        return ruudustik;
    }

    private ImageView getPilt(Malenupp nupp) {
        String värv = nupp.OnValge() ? "valge" : "must";
        String failiNimi = "";

        if (nupp instanceof Vanker) {
            failiNimi = "vanker";
        } else if (nupp instanceof Ratsu) {
            failiNimi = "ratsu";
        } else if (nupp instanceof Oda) {
            failiNimi = "oda";
        } else if (nupp instanceof Lipp) {
            failiNimi = "lipp";
        } else if (nupp instanceof Kuningas) {
            failiNimi = "kuningas";
        } else if (nupp instanceof Ettur) {
            failiNimi = "ettur";
        }

        Image pilt = new Image(getClass().getResourceAsStream("/pildid/" + värv + "/" + failiNimi + ".png"));
        ImageView vaade = new ImageView(pilt);
        GridPane.setHalignment(vaade, HPos.CENTER);
        GridPane.setValignment(vaade, VPos.CENTER);
        return vaade;
    }

    public void uuendaLaud() {
        ehitaLaud();
        malelaud.getKoikNupud().forEach(malend -> ruudustik.add(getPilt(malend), malend.getX()+1, 8-malend.getY()));
    }
}
