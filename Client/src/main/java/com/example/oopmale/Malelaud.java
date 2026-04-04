package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Malelaud {
    private List<Malenupp> koikNupud = new ArrayList<>();
    private List<Malenupp> valgedNupud = new ArrayList<>();
    private List<Malenupp> mustadNupud = new ArrayList<>();

    public Malelaud() {
        for (int xi = 0; xi < 8; xi++) {
            valgedNupud.add(new Ettur(xi, 1, true));
            mustadNupud.add(new Ettur(xi, 6, false));
        }
        for (int dxi = 0; dxi <2; dxi++) {
            valgedNupud.add(new Vanker(0 + 7*dxi, 0, true));
            valgedNupud.add(new Ratsu(1 + 5*dxi, 0, true));
            valgedNupud.add(new Oda(2 + 3*dxi, 0, true));

            mustadNupud.add(new Vanker(0 + 7*dxi, 7, false));
            mustadNupud.add(new Ratsu(1 + 5*dxi, 7, false));
            mustadNupud.add(new Oda(2 + 3*dxi, 7, false));
        }
        valgedNupud.add(new Lipp(3,0, true));
        valgedNupud.add(new Kuningas(4, 0, true));
        mustadNupud.add(new Lipp(3, 7, false));
        mustadNupud.add(new Kuningas(4, 7, false));
        koikNupud.addAll(valgedNupud);
        koikNupud.addAll(mustadNupud);
    }

    /**
     * vaatab, kas käik, mida üritatakse teha on võimalik. Hetkel ta lihtsalt vaatab,
     * kas üritatakse liigutada mingit nuppu ja et liigutatav nupp sama värvi nupu ära ei võtaks.
     * @param valgeKaik
     * @param vanaX
     * @param vanaY
     * @param uusX
     * @param uusY
     * @return
     */
    public boolean kaiguKatse(boolean valgeKaik, int vanaX, int vanaY, int uusX, int uusY){
        for (Malenupp malenupp : koikNupud) {
            if (!(malenupp.kasAsubSiin(vanaX, vanaY) && malenupp.onValge() == valgeKaik)){
                continue;
            }
            if (!malenupp.kaiguDeltad().contains(List.of(uusX-vanaX, uusY-vanaY))){
                continue;
            }
            if (malenupp.kasAsubSiin(uusX, uusY) && malenupp.onValge() == valgeKaik){
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Liigutab nupu algruudult lõppruutu. Kui lõppruudul on nupp, siis kustutab ta ära. Ei tee ühtegi kontrolli.
     * @param vanaX
     * @param vanaY
     * @param uusX
     * @param uusY
     */
    public void teeKaik(int vanaX, int vanaY, int uusX, int uusY){
        for (Malenupp malenupp : koikNupud) {
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
        for (Malenupp malenupp : koikNupud){
            if (malenupp.kasAsubSiin(vanaX, vanaY)){
                malenupp.liiguta(uusX, uusY);
            }
        }
    }

    /**
     * Hetkel tagastab 0, kui mäng ei ole läbi, 1 kui laua värv võtis ja -1 kui laua värv kaotas. Viiki veel ei eksisteeri.
     * @param valgeKaik
     * @return
     */
    int mangLabi(boolean valgeKaik){
        boolean valgeKuningas = false;
        boolean mustKuningas = false;
        for (Malenupp malenupp : koikNupud) {
            if (malenupp.getClass() == Kuningas.class){
                if (malenupp.onValge()) valgeKuningas = true;
                else mustKuningas = true;
            }
        }
        if (valgeKuningas && mustKuningas) return 0;
        if (valgeKuningas && valgeKaik || mustKuningas && !valgeKaik) return 1;
        return 0;
    }
}
