package com.example.oopmale;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class LauaVaade {
    private GridPane ruudustik = new GridPane();
    private boolean onValge;
    private boolean minuKaik;
    private GameLoop gl;
    private Stage stage;
    private Scene algStseen;

    public LauaVaade(boolean onValge, GameLoop gl, Stage stage, Scene algStseen) {
        this.onValge = onValge;
        this.minuKaik = onValge;
        this.gl = gl;
        this.stage = stage;
        this.algStseen = algStseen;
    }

    public void setMinuKaik(boolean minuKaik) {
        this.minuKaik = minuKaik;
    }

    private void ruutKlikiti(int x, int y) {
        if (minuKaik) {
            gl.lisaKlikk(x, y);
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

                    if (onValge) {
                        if ((rida + veerg) % 2 == 0) {
                            ruut.setFill(Color.WHITE);
                        } else {
                            ruut.setFill(Color.GREEN);
                        }
                    } else {
                        if ((rida + veerg) % 2 != 0) {
                            ruut.setFill(Color.WHITE);
                        } else {
                            ruut.setFill(Color.GREEN);
                        }
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

    public void uuendaLaud(Set<Nupp> lauaOlek) {
        ruudustik.getChildren().clear();
        ehitaLaud();

        if (lauaOlek == null) return;

        lauaOlek.forEach(
                malend -> ruudustik.add(getPilt(malend), malend.getX() + 1, onValge ? 8 - malend.getY() : malend.getY() + 1)
        );
    }

    public void uuendaVoimalikud(int nupuX, int nupuY, int[][] voimalikud) {
        return;
    }

    public void mangLabi(int tulemus) throws IOException { // saad võtta koodid Suhtlus.voit, Suhtlus.viik ja Suhtlus.kaotus
        String tulemusTekst = switch (tulemus) {
            case Suhtlus.voit   -> "Võitsid!";
            case Suhtlus.kaotus -> "Kaotasid!";
            default             -> "Viik!";
        };

        Platform.runLater(() -> {
            Button tagasiNupp = new Button("Tagasi algekraanile");
            tagasiNupp.setDefaultButton(true);
            tagasiNupp.setOnAction(e -> stage.setScene(algStseen));

            VBox lõppLayout = new VBox(15, new Label(tulemusTekst), tagasiNupp);
            lõppLayout.setAlignment(Pos.CENTER);
            lõppLayout.setPadding(new Insets(40));
            UhendaServeriga.destroy();

            stage.setScene(new Scene(lõppLayout, 300, 200));
            stage.setTitle("Mäng läbi");
        });
    }
}