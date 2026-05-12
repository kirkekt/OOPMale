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

    private void ruutKlikiti(int x, int y) {
        System.out.println("Vajutasid ruutu (x,y): " + x +", "+ y);
        if (esimeneX != null) {
            teeTeineKlikk(x, y);
        }
        esimeneX = x;
        esimeneY = y;

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

                int reaNumber, veeruNumber;
                if (onValge) {
                    reaNumber = 9 - rida;
                    veeruNumber = veerg;
                } else {
                    reaNumber = rida;
                    veeruNumber = 9-veerg;
                }

                if (veerg == 0 && rida == 0) {
                    // top-left empty corner
                } else if (rida == 0) {
                    Label täht = new Label(Character.toString((char) (onValge? 'A' + veerg - 1 : 'A' - veerg + 8)));
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

                    int x = veeruNumber - 1;
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
        vaade.setMouseTransparent(true);
        return vaade;
    }

    public void uuendaLaud() {
        ruudustik.getChildren().clear();
        ehitaLaud();

        malelaud.getKoikNupud().forEach(
                malend -> ruudustik.add(getPilt(malend), onValge ? malend.getX() + 1 : 8 - malend.getX(), onValge ? 8 - malend.getY() : malend.getY() + 1)
        );
    }

    public void klikidReset() {
        esimeneX = null;
        esimeneY = null;
    }
}