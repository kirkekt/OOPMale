package com.example.oopmale;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class LauaVaade {

    private GridPane ruudustik = new GridPane();
    private GridPane nupud = new GridPane();
    private GridPane voimalikud = new GridPane();
    private StackPane laud = new StackPane(voimalikud, nupud);

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
        nupud.setMouseTransparent(true);
        voimalikud.setMouseTransparent(true);
        ehitaLaud();
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

                int reaNumber = onValge ? 9 - rida : rida;

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

                    boolean kasTulebValgeRuut = (rida + veerg) % 2 == 0;
                    if (onValge) {
                        ruut.setFill(kasTulebValgeRuut ? Color.WHITE : Color.GREEN);
                    } else {
                        ruut.setFill(!kasTulebValgeRuut ? Color.WHITE : Color.GREEN);
                    }

                    int x = veerg - 1;
                    int y = reaNumber - 1;
                    ruut.setOnMouseClicked(_ -> ruutKlikiti(x, y));

                    ruudustik.add(ruut, veerg, rida);
                }
            }
        }
        /*for (GridPane grid : List.of(ruudustik, nupud, voimalikud)) {
            grid.getColumnConstraints().add(new ColumnConstraints(60));
            grid.getRowConstraints().add(new RowConstraints(60));
            for (int i = 1; i < 8; i++) {
                ColumnConstraints col = new ColumnConstraints(80);
                RowConstraints    row = new RowConstraints(80);
                grid.getColumnConstraints().add(col);
                grid.getRowConstraints().add(row);
            }
        }*/
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
        nupud.getChildren().clear();
        ruudustik.getChildren().clear();
        ehitaLaud();

        if (lauaOlek == null) return;

        lauaOlek.forEach(
                malend -> ruudustik.add(getPilt(malend), malend.getX()+1, onValge ? 8-malend.getY() : malend.getY()+1)
        );
    }

    public void uuendaVoimalikud(int nupuX, int nupuY, int[][] voimalikudKaigud) {
        //voimalikud.add(new Rectangle(80, 80, Color.BURLYWOOD), nupuX+1, nupuY+1);
        return;
    }

    public void mangLabi(int tulemus) throws IOException { // saad võtta koodid Suhtlus.voit, Suhtlus.viik ja Suhtlus.kaotus
        String tulemusTekst = switch (tulemus) {
            case Suhtlus.voit -> "Võitsid!";
            case Suhtlus.kaotus -> "Kaotasid!";
            default -> "Viik!";
        };

        Platform.runLater(() -> {
            Button tagasiNupp = new Button("Tagasi algekraanile");
            tagasiNupp.setDefaultButton(true);
            tagasiNupp.setOnAction(e -> {
                stage.setScene(algStseen);
                UhendaServeriga.destroy();
            });

            VBox lõppLayout = new VBox(15, new Label(tulemusTekst), tagasiNupp);
            lõppLayout.setAlignment(Pos.CENTER);
            lõppLayout.setPadding(new Insets(40));

            stage.setScene(new Scene(lõppLayout, 300, 200));
            stage.setTitle("Mäng läbi");
        });
    }
}