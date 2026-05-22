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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LauaVaade {

    private GridPane ruudustik = new GridPane();
    private GridPane nupud = new GridPane();
    private GridPane voimalikud = new GridPane();
    private GridPane valitudNupp = new GridPane();
    private StackPane laud = new StackPane(ruudustik, valitudNupp, nupud, voimalikud);
    private BorderPane lauaHoidja = new BorderPane();
    private Set<Nupp> lauaNupud;

    private static final Color lauaVarv1 = Color.web("#3A3A3A"/*"#A9A9A9"*/);
    private static final Color lauaVarv2 = Color.web("#2A2A2A"/*"#36454F"*/);


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
        ruudustik.setBackground(new Background(new BackgroundFill(lauaVarv1, null, null)));
        laud.setAlignment(Pos.CENTER);
        ruudustik.setAlignment(Pos.CENTER);
        voimalikud.setAlignment(Pos.CENTER);
        valitudNupp.setAlignment(Pos.CENTER);
        nupud.setAlignment(Pos.CENTER);
        lauaHoidja.setCenter(laud);
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
                    täht.setStyle("-fx-font-weight: bold; -fx-text-fill: #7A7A7A");
                    täht.setMaxWidth(Double.MAX_VALUE);
                    täht.setAlignment(Pos.CENTER);
                    ruudustik.add(täht, veerg, rida);
                } else if (veerg == 0) {
                    Label number = new Label(Integer.toString(reaNumber));
                    number.setStyle("-fx-font-weight: bold; -fx-text-fill: #7A7A7A");
                    number.setMinWidth(30);
                    number.setMaxWidth(Double.MAX_VALUE);
                    number.setAlignment(Pos.CENTER);
                    ruudustik.add(number, veerg, rida);
                } else {
                    Rectangle ruut = new Rectangle(80, 80);

                    boolean kasTulebValgeRuut = (rida + veerg) % 2 == 0;
                    if (onValge) {
                        ruut.setFill(kasTulebValgeRuut ? lauaVarv1 : lauaVarv2);
                    } else {
                        ruut.setFill(!kasTulebValgeRuut ? lauaVarv1 : lauaVarv2);
                    }

                    int x = veerg - 1;
                    int y = reaNumber - 1;
                    ruut.setOnMouseClicked(_ -> ruutKlikiti(x, y));

                    ruudustik.add(ruut, veerg, rida);
                }
            }
        }
        for (GridPane grid : List.of(ruudustik, nupud, voimalikud, valitudNupp)) {
            if (!grid.equals(ruudustik)) grid.setMouseTransparent(true);
            grid.setAlignment(Pos.CENTER);
            grid.getColumnConstraints().add(new ColumnConstraints(40));
            grid.getRowConstraints().add(new RowConstraints(40));
            for (int i = 1; i < 9; i++) {
                ColumnConstraints col = new ColumnConstraints(80);
                RowConstraints row = new RowConstraints(80);
                col.setHalignment(HPos.CENTER);
                grid.getColumnConstraints().add(col);
                grid.getRowConstraints().add(row);
            }
        }
    }

    public BorderPane getVaade() {
        return lauaHoidja;
    }

    private ImageView getPilt(Nupp nupp) {
        String värv = nupp.onValge() ? "valge" : "must";
        String failiNimi = nupp.getMalend();

        Image pilt = new Image(getClass().getResourceAsStream("/pildid/" + värv + "/" + failiNimi + ".png"));
        ImageView vaade = new ImageView(pilt);
        return vaade;
    }

    public void uuendaLaud(Set<Nupp> lauaOlek) {
        nupud.getChildren().clear();
        voimalikud.getChildren().clear();

        if (lauaOlek == null) return;
        lauaNupud = lauaOlek;

        // Malendite pildid:
        // https://x.com/dr_smey
        // https://www.instagram.com/dr.smey
        // https://www.reddit.com/r/PixelArt/comments/pmfegd/sets_of_chess_pieces/
        lauaOlek.forEach(
                malend -> nupud.add(getPilt(malend), malend.getX()+1, onValge ? 8-malend.getY() : malend.getY()+1)
        );
    }

    public void uuendaVoimalikud(int nupuX, int nupuY, int[][] voimalikudKaigud) {
        int x = nupuX+1;
        int y = onValge ? 8-nupuY : nupuY+1;
        Set<Nupp> nu = new HashSet<>(lauaNupud);
        nu.removeIf(n -> n.getX() != nupuX || n.getY() != nupuY);

        Nupp nupp = new Nupp(Suhtlus.vEttur, 0, 0);
        Optional<Nupp> vbNupp = nu.stream().findAny();
        if (vbNupp.isPresent()) {
            nupp = vbNupp.get();
        }

        Image pilt = new Image(getClass().getResourceAsStream("/pildid/outline-vari/" + nupp.getMalend() + ".png"));

        valitudNupp.add(new ImageView(pilt), x, y);

        for (int[] i : voimalikudKaigud) {
            Rectangle ruut = new Rectangle(14, 14, onValge ? Color.WHITE : Color.BLACK);
            ruut.setOpacity(0.4);
            x = i[0]+1;
            y = onValge ? 8-i[1] : i[1]+1;
            voimalikud.add(ruut, x, y);
        }
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

    public void clearVoimalikud() {
        voimalikud.getChildren().clear();
        valitudNupp.getChildren().clear();
    }
}