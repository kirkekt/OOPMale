package org.server;

import java.util.ArrayList;
import java.util.List;

public class Malelaud {
    private List<Malenupp> koikNupud = new ArrayList<>();
    private List<Malenupp> valgedNupud = new ArrayList<>();
    private List<Malenupp> mustadNupud = new ArrayList<>();

    public Malelaud() {
        for (int x = 0; x < 8; x++) {
            valgedNupud.add(new Ettur(x, 1, true));
            mustadNupud.add(new Ettur(x, 6, false));
        }
        for (int i = 0; i <2; i++) {
            valgedNupud.add(new Vanker(0 + 7*i, 0, true));
            valgedNupud.add(new Ratsu(1 + 5*i, 0, true));
            valgedNupud.add(new Oda(2 + 3*i, 0, true));

            mustadNupud.add(new Vanker(0 + 7*i, 7, false));
            mustadNupud.add(new Ratsu(1 + 5*i, 7, false));
            mustadNupud.add(new Oda(2 + 3*i, 7, false));
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
     * @param kaik
     * @return
     */
    public boolean kasLubatudKaik(boolean valgeKaik, int[] kaik){
        Asukoht vanaAsukoht = new Asukoht(kaik[0], kaik[1]);
        Asukoht deltaKaik = new Asukoht(kaik[2]-kaik[0], kaik[3]-kaik[1]);
        Asukoht uusAsukoht = new Asukoht(kaik[2], kaik[3]);

        Malenupp liigutatavNupp = misNuppRuudul(vanaAsukoht);
        Malenupp sihtNupp = misNuppRuudul(uusAsukoht);

        if (liigutatavNupp == null
                || liigutatavNupp.onValge() != valgeKaik
                || !liigutatavNupp.kaiguDeltad().stream().anyMatch(elt -> elt.contains(deltaKaik))
                || sihtNupp != null && sihtNupp.onValge() == valgeKaik
        )  return false;

        // kas teekond on vaba?
        if (!kasTeekondVaba(vanaAsukoht, uusAsukoht)) return false;

        return true;
    }

    private Malenupp misNuppRuudul(Asukoht asukoht) {
        for (Malenupp malenupp : koikNupud) {
            if (malenupp.kasAsubSiin(asukoht)) {
                return malenupp;
            }
        }
        return null;
    }

    private boolean kasTeekondVaba(Asukoht a, Asukoht b) {
        Asukoht algus, lõpp;
        if (a.getX() < b.getX()) {
            algus = a;
            lõpp = b;
        } else if (b.getX() < a.getX()) {
            algus = b;
            lõpp = a;
        } else {
            if (a.getY() > b.getY()) {
                algus = b;
                lõpp = a;
            } else {
                algus = a;
                lõpp = b;
            }
        }

        // horisontaalne
        if (algus.getY() == lõpp.getY()) {
            for (int x = algus.getX() + 1; x < lõpp.getX(); x++) {
                if (misNuppRuudul(new Asukoht(x,lõpp.getY())) != null) {
                    return false;
                }
            }
            return true;
        }

        // vertikaalne
        if (algus.getX() == lõpp.getX()) {
            for (int y = algus.getY() + 1; y < lõpp.getY(); y++) {
                if (misNuppRuudul(new Asukoht(lõpp.getX(),y)) != null) {
                    return false;
                }
            }
            return true;
        }

        // diagonaalne
        for (int x = algus.getX() + 1; x < lõpp.getX(); x++) {
            int muut = x - algus.getX();

            if (algus.getY() > lõpp.getY()) {
                muut = -muut;
            }

            if (misNuppRuudul(new Asukoht(x, algus.getY() + muut)) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Liigutab nupu algruudult lõppruutu. Kui lõppruudul on nupp, siis kustutab ta ära. Ei tee ühtegi kontrolli.
     * @param kaik
     */
    public void teeKaik(int[] kaik){
        Asukoht algneAsukoht = new Asukoht(kaik[0], kaik[1]);
        Asukoht deltaKaik = new Asukoht(kaik[2]-kaik[0], kaik[3]-kaik[1]);
        Asukoht uusAsukoht = new Asukoht(kaik[2], kaik[3]);
        for (Malenupp malenupp : koikNupud) {
            if (malenupp.kasAsubSiin(uusAsukoht)){
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
            if (malenupp.kasAsubSiin(algneAsukoht)){
                malenupp.liiguta(deltaKaik);
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

    public List<Malenupp> getKoikNupud() {
        return koikNupud;
    }
}
