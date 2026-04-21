package com.example.oopmale;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class Malelaud {
    private List<Nupp> koikNupud = new ArrayList<>();
    private List<Nupp> valgedNupud = new ArrayList<>();
    private List<Nupp> mustadNupud = new ArrayList<>();

    private boolean onValgeKäik = true;

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
                malenupp.liiguta(uusX, uusY);
            }
        }
        Platform.runLater(lauaVaade::uuendaLaud);
    }

    public List<Nupp> getKoikNupud() {
        return koikNupud;
    }
}
