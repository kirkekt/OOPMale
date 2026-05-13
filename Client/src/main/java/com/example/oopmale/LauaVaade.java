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
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class LauaVaade {
    private GridPane ruudustik = new GridPane();
    private boolean onValge;
    private boolean minuKaik;
    private DataInputStream in;
    private DataOutputStream out;

    public LauaVaade(boolean onValge) {
        this.onValge = onValge;
        this.minuKaik = onValge;
    }

    private void ruutKlikiti(int x, int y) {
        if (minuKaik) {
            try {
                int serveriVastus = Suhtlus.saadaKlikk(in, out, new int[]{x, y});
                if (serveriVastus == Suhtlus.kaiguLopp) minuKaik = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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


                    ruut.setOnMouseClicked(_ -> ruutKlikiti(x, y));

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
        vaade.setMouseTransparent(true);
        return vaade;
    }

    public void uuendaLaud() throws IOException {
        Set<Nupp> lauaOlek = Suhtlus.loeLaud(in, out);
        if (lauaOlek == null) mangLabi();
        ruudustik.getChildren().clear();
        ehitaLaud();

        if (lauaOlek == null) return;

        lauaOlek.forEach(
                malend -> ruudustik.add(getPilt(malend), malend.getX() + 1, onValge ? 8 - malend.getY() : malend.getY() + 1)
        );
        minuKaik = true;
    }

    public void mangLabi() throws IOException {
        int tulemus = Suhtlus.kusiManguTulemust(in, out);
        // tee midagi mõistlikku - näita mäng läbi teksti vms
    }
}