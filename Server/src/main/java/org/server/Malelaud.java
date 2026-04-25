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
        Asukoht uusAsukoht = new Asukoht(kaik[2], kaik[3]);

        Malenupp liigutatavNupp = misNuppRuudul(vanaAsukoht);
        if (liigutatavNupp == null)
            return false;

        return voimalikLiigutada(liigutatavNupp, uusAsukoht);
    }

    /**
     * Vaatab, kas nuppu on võimalik liigutada sihtruudule. Ei kontrolli, kas nupp on tule all või
     * kas liigutuse tulemus paneb kuninga tule alla
     * @param nupp
     * @param sihtAsukoht
     * @return
     */
    public boolean voimalikLiigutada(Malenupp nupp, Asukoht sihtAsukoht) {
        Asukoht algus = nupp.getAsukoht();
        Asukoht deltaKaik = new Asukoht(sihtAsukoht.getX() - algus.getX(), sihtAsukoht.getY() - algus.getY());

        List<Asukoht> oigeSuunaDeltad = null;
        for (List<Asukoht> suunaDeltad : nupp.kaiguDeltad()) {
            if (suunaDeltad.contains(deltaKaik)) {
                oigeSuunaDeltad = suunaDeltad;
                break;
            }
        }

        if (oigeSuunaDeltad == null)
            return false; // Nupul võimatu sellist käiku teha

        for (Asukoht vaadeldavDelta : oigeSuunaDeltad) {
            Asukoht vaadeldavAsukoht = new Asukoht(algus.getX() + vaadeldavDelta.getX(), algus.getY() + vaadeldavDelta.getY());
            if (vaadeldavAsukoht.equals(algus))
                continue;

            Malenupp nuppRuudul = misNuppRuudul(vaadeldavAsukoht);
            if (nuppRuudul != null) {
                if (vaadeldavAsukoht.equals(sihtAsukoht)) {
                    return nuppRuudul.onValge() != nupp.onValge(); // Lõppruudul peab olema teist värvi nupp
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Kontrollib, kas vaadeldav mängija on tule all
     * @param valgeKaik
     * @return
     */
    public boolean onTuli(boolean valgeKaik) {
        Malenupp kuningas = null;
        List<Malenupp> mangijaNupud = valgeKaik ? valgedNupud : mustadNupud;
        List<Malenupp> vastaseNupud = valgeKaik ? mustadNupud : valgedNupud;

        for (Malenupp nupp: mangijaNupud) {
            if (nupp.getClass() == Kuningas.class) {
                kuningas = nupp;
            }
        }

        if (kuningas == null)
            throw new RuntimeException("Laual ei ole kuningat");

        for (Malenupp nupp : vastaseNupud) {
            if (voimalikLiigutada(nupp, kuningas.getAsukoht())) {
                return true;
            }
        }
        return false;
    }

    private Malenupp misNuppRuudul(Asukoht asukoht) {
        for (Malenupp malenupp : koikNupud) {
            if (malenupp.kasAsubSiin(asukoht)) {
                return malenupp;
            }
        }
        return null;
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
