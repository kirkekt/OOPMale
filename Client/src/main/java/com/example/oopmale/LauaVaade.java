package com.example.oopmale;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class LauaVaade {
    private GridPane ruudustik = new GridPane();

    public LauaVaade() {
        ehitaLaud();
    }

    private void ehitaLaud() {
        for (int rida = 0; rida < 9; rida++) {
            for (int veerg = 0; veerg < 9; veerg++) {
                if (veerg == 0 && rida == 0) {
                } else if (rida == 0) {
                    Label täht = new Label(Character.toString((char) ('A' + veerg-1)));
                    täht.setMinWidth(30);
                    GridPane.setHalignment(täht, HPos.CENTER);
                    ruudustik.add(täht, veerg, rida);
                } else if (veerg == 0) {
                    Label number = new Label(Integer.toString(9-rida));
                    GridPane.setHalignment(number, HPos.CENTER);
                    ruudustik.add(number, veerg, rida);
                } else {
                    Rectangle ruut = new Rectangle(80, 80);
                    if ((rida + veerg) % 2 == 0) {
                        ruut.setFill(Color.WHITE);
                    } else {
                        ruut.setFill(Color.BLACK);
                    }
                    ruudustik.add(ruut, veerg, rida);
                }
            }
        }
    }

    public GridPane getVaade() {
        return ruudustik;
    }
}
