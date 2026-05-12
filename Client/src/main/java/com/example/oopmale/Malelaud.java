package com.example.oopmale;

import javafx.application.Platform;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Malelaud {
    private List<Nupp> koikNupud = new ArrayList<>();
    private List<Nupp> valgedNupud = new ArrayList<>();
    private List<Nupp> mustadNupud = new ArrayList<>();
    public Malelaud() {
        for (int x = 0; x < 8; x++) {
            valgedNupud.add(new Nupp(x, 1, true, "ettur"));
            mustadNupud.add(new Nupp(x, 6, false, "ettur"));
        }
        for (int i = 0; i <2; i++) {
            valgedNupud.add(new Nupp(0 + 7*i, 0, true, "vanker"));
            valgedNupud.add(new Nupp(1 + 5*i, 0, true, "ratsu"));
            valgedNupud.add(new Nupp(2 + 3*i, 0, true, "oda"));

            mustadNupud.add(new Nupp(0 + 7*i, 7, false, "vanker"));
            mustadNupud.add(new Nupp(1 + 5*i, 7, false, "ratsu"));
            mustadNupud.add(new Nupp(2 + 3*i, 7, false, "oda"));
        }
        valgedNupud.add(new Nupp(3,0, true, "lipp"));
        valgedNupud.add(new Nupp(4, 0, true, "kuningas"));
        mustadNupud.add(new Nupp(3, 7, false, "lipp"));
        mustadNupud.add(new Nupp(4, 7, false, "kuningas"));
        koikNupud.addAll(valgedNupud);
        koikNupud.addAll(mustadNupud);
    }

    private Nupp misNuppRuudul(int x, int y) {
        for (Nupp malenupp : koikNupud) {
            if (malenupp.kasAsubSiin(x, y)) {
                return malenupp;
            }
        }
        return null;
    }

    public boolean kasProovitakseVangerdada(Nupp liigutatavNupp, int uusX, int uusY) {
        int nupuX = liigutatavNupp.getX();
        int nupuY = liigutatavNupp.getY();

        if (!(liigutatavNupp.getMalend().equals("kuningas"))) {
            return false;
        }
        if (!(uusX == 2 || uusX == 6)) {
            return false;
        }
        if (Math.abs(nupuX - uusX) != 2) {
            return false;
        }
        return true;
    }

    public Nupp leiaVangerduseVanker(Nupp kuningas, int uusX) {
        if (uusX == 2) {
            if (kuningas.onValge()) {
                return misNuppRuudul(0, 0);
            } else {
                return misNuppRuudul(0, 7);
            }
        } else if (uusX == 6) {
            if (kuningas.onValge()) {
                return misNuppRuudul(7, 0);
            } else {
                return misNuppRuudul(7, 7);
            }
        }
        return null;
    }

    /**
     * Liigutab nupu algruudult lõppruutu. Kui lõppruudul on nupp, siis kustutab ta ära. Ei tee ühtegi kontrolli.
     * @param kaik
     */
    public void teeKaik(int[] kaik, LauaVaade lauaVaade) {
        int vanaX = kaik[0];
        int vanaY = kaik[1];
        int uusX = kaik[2];
        int uusY = kaik[3];
        for (Nupp malenupp : koikNupud) {
            if (malenupp.kasAsubSiin(uusX, uusY)){
                if (malenupp.onValge()) {
                    valgedNupud.remove(malenupp);
                } else {
                    mustadNupud.remove(malenupp);
                }
                koikNupud.remove(malenupp);
                break;
            }
        }

        for (Nupp malenupp : koikNupud){
            if (malenupp.kasAsubSiin(vanaX, vanaY)){
                if (kasProovitakseVangerdada(malenupp, uusX, uusY)) {

                    Nupp vanker = leiaVangerduseVanker(malenupp, uusX);
                    if (vanker == null)
                        throw new RuntimeException("Vangerduse vankrit ei leitud");
                    if (vanker.getX() == 7) {
                        // lühike vangerdus
                        vanker.liiguta(5, vanker.getY());
                    } else {
                        // pikk vangerdus
                        vanker.liiguta(3, vanker.getY());
                    }
                }
                malenupp.liiguta(uusX, uusY);
            }
        }
        Platform.runLater(lauaVaade::uuendaLaud);
    }

    public List<Nupp> getKoikNupud() {
        return koikNupud;
    }
}
