package org.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Malelaud {
    private List<Malenupp> koikNupud = new ArrayList<>();
    private List<Malenupp> valgedNupud = new ArrayList<>();
    private List<Malenupp> mustadNupud = new ArrayList<>();
    private Map<BigInteger, Integer> seisudKordsusega = new HashMap<>();
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
     * Tagastab true, kui käiku saab teha, muidu false.
     * @param valgeKaik
     * @param kaik
     * @return
     */
    public boolean kasLubatudKaik(boolean valgeKaik, int[] kaik){
        Asukoht vanaAsukoht = new Asukoht(kaik[0], kaik[1]);
        Asukoht uusAsukoht = new Asukoht(kaik[2], kaik[3]);

        Malenupp liigutatavNupp = misNuppRuudul(vanaAsukoht);
        if (liigutatavNupp == null) // kui algsel ruudul pole nuppu, siis käik on võimatu
            return false;
        if (liigutatavNupp.onValge()!=valgeKaik)
            return false; // nupp peab õiget värvi olema

        if (kasProovitakseVangerdada(liigutatavNupp, uusAsukoht)) {
            return kasVangerdusVoimalik((Kuningas) liigutatavNupp, uusAsukoht);
        }
        if (!voimalikLiigutada(liigutatavNupp, uusAsukoht)){
            return false; // kui nupp ei saa käiku teha, siis käik on võimatu
        }

        // kontrollime ega tuld ei teki peale käiku:
        Malenupp araVoetavNupp = misNuppRuudul(uusAsukoht);
        liigutatavNupp.liiguta(uusAsukoht.getX() - vanaAsukoht.getX(), uusAsukoht.getY()-vanaAsukoht.getY());
        List<Malenupp> vastasNuppud = valgeKaik ? mustadNupud : valgedNupud;
        if (vastasNuppud.contains(araVoetavNupp)) vastasNuppud.remove(araVoetavNupp);
        boolean vastus = onTuli(valgeKaik);
        if (araVoetavNupp != null) vastasNuppud.add(araVoetavNupp);
        liigutatavNupp.liiguta(vanaAsukoht.getX()- uusAsukoht.getX(), vanaAsukoht.getY()-uusAsukoht.getY());
        return !vastus;


    }

    /**
     * Vaatab, kas nuppu on võimalik liigutada sihtruudule. Ei kontrolli,
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
            if (vaadeldavAsukoht.equals(sihtAsukoht)){
                return true;
            }
        }

        return true;
    }

    /**
     * Ei kontrolli, kas kuningat on juba liigutatud ehk võib tagastada true, kui kuningas on juba liikunud
     * @param liigutatavNupp
     * @param sihtRuut
     * @return
     */
    public boolean kasProovitakseVangerdada(Malenupp liigutatavNupp, Asukoht sihtRuut) {
        int nupuX = liigutatavNupp.getAsukoht().getX();
        int nupuY = liigutatavNupp.getAsukoht().getY();
        int sihtRuuduX = sihtRuut.getX();

        if (!(liigutatavNupp instanceof Kuningas)) {
            return false;
        }
        if (!(sihtRuuduX == 2 || sihtRuuduX == 6)) {
            return false;
        }
        if (Math.abs(nupuX - sihtRuuduX) != 2) {
            return false;
        }
        return sihtRuut.getY() == nupuY;
    }


    public boolean kasVangerdusVoimalik(Kuningas kuningas, Asukoht sihtRuut) {
        if (kuningas.KasOnLiikunud()) {
            return false;
        }

        Malenupp vanker = leiaVangerduseVanker(kuningas, sihtRuut);

        if (vanker == null) {
            return false;
        }
        if (!(vanker instanceof Vanker)) {
            return false;
        }
        if (vanker.KasOnLiikunud()) {
            return false;
        }

        int kuningaX = kuningas.getAsukoht().getX();
        int kuningaY = kuningas.getAsukoht().getY();
        int vankriX = vanker.getAsukoht().getX();
        int algus = Math.min(kuningaX, vankriX);
        int lõpp = Math.max(kuningaX, vankriX);

        for (int x = algus + 1; x < lõpp; x++) {
            if (misNuppRuudul(new Asukoht(x,kuningaY)) != null) {
                return false;
            }
        }

        int sihtRuuduX = sihtRuut.getX();
        algus = Math.min(sihtRuuduX, kuningaX);
        lõpp = Math.max(sihtRuuduX, kuningaX);
        List<Malenupp> vastaseNupud = kuningas.onValge() ? mustadNupud : valgedNupud;

        for (int x = algus; x < lõpp + 1; x++) {
            if (kasRuudulTuli(new Asukoht(x,kuningaY), vastaseNupud)) {
                return false;
            }
        }
        return true;
    }

    public Malenupp leiaVangerduseVanker(Kuningas kuningas, Asukoht sihtRuut) {
        int sihtRuuduX = sihtRuut.getX();

        if (sihtRuuduX == 2) {
            if (kuningas.onValge()) {
                return misNuppRuudul(new Asukoht(0,0));
            } else {
                return misNuppRuudul(new Asukoht(0,7));
            }
        } else if (sihtRuuduX == 6) {
            if (kuningas.onValge()) {
                return misNuppRuudul(new Asukoht(7,0));
            } else {
                return misNuppRuudul(new Asukoht(7,7));
            }
        }
        return null;
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

        return kasRuudulTuli(kuningas.getAsukoht(), vastaseNupud);
    }

    public boolean kasRuudulTuli(Asukoht ruut, List<Malenupp> vastaseNupud) {
        for (Malenupp nupp : vastaseNupud) {
            if (voimalikLiigutada(nupp, ruut)) {
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
                if (kasProovitakseVangerdada(malenupp, uusAsukoht)) {
                    Malenupp vanker = leiaVangerduseVanker((Kuningas) malenupp, uusAsukoht);
                    if (vanker == null)
                        throw new RuntimeException("Vangerduse vankrit ei leitud");
                    if (vanker.getAsukoht().getX() == 7) {
                        // lühike vangerdus
                        vanker.liiguta(-2, 0);
                    } else {
                        // pikk vangerdus
                        vanker.liiguta(3, 0);
                    }
                }
                malenupp.liiguta(deltaKaik);
            }
        }
        // kui ettur on esimesel või viimasel real (ehk 7|y), siis lipp asemele
        if (uusAsukoht.getY()%7==0){
            Malenupp potensiaalneEttur = misNuppRuudul(uusAsukoht);
            if (potensiaalneEttur.getClass()==Ettur.class){
                Malenupp asendusLipp = new Lipp(potensiaalneEttur.getAsukoht(), potensiaalneEttur.onValge());
                List<Malenupp> kusAsendada = asendusLipp.onValge() ? valgedNupud : mustadNupud;
                valgedNupud.remove(potensiaalneEttur);
                valgedNupud.add(asendusLipp);
                koikNupud.remove(potensiaalneEttur);
                koikNupud.add(asendusLipp);
            }
        }
        BigInteger seisuSumma = seisuSumma();
        if (!seisudKordsusega.containsKey(seisuSumma)){
            seisudKordsusega.put(seisuSumma, 0);
        }
        seisudKordsusega.replace(seisuSumma, 1 + seisudKordsusega.get(seisuSumma));
    }

    /**
     * Kutsutakse välja käigu alguses (okei tehniliselt käigu lõpus aga vastase värviga, sama asi)
     * Hetkel tagastab 0 kui mäng jätkub, 1 kui valge võitis, -1 kui must võitis ja 67 kui seis on viik.
     * @param valgeKaik
     * @return
     */
    int mangLabi(boolean valgeKaik){
        if (seisudKordsusega.containsValue(3))
            return 67;
        List<Malenupp> vaadeldavadNupud = valgeKaik? valgedNupud : mustadNupud;
        for (Malenupp iMalenupp : vaadeldavadNupud) {
            for (List<Asukoht> iKaigud : iMalenupp.voimalikudKaigud()) {
                for (Asukoht iKaik : iKaigud) {
                    if (kasLubatudKaik(
                            valgeKaik,
                            new int[] {
                                    iMalenupp.getAsukoht().getX(),
                                    iMalenupp.getAsukoht().getY(),
                                    iMalenupp.getAsukoht().getX() + iKaik.getX(),
                                    iMalenupp.getAsukoht().getY() + iKaik.getY()
                            }
                    )){
                        //System.out.println(iMalenupp);
                        System.out.println(iMalenupp.getAsukoht().getX() + iKaik.getX() + " " + iMalenupp.getAsukoht().getY() + iKaik.getY());
                        return 0; // mängija saab mingi käigu teha, mäng ei ole läbi
                    }
                }
            }
        }
        //teame, et ühtegi käiku ei saa teha. Kui on tuli, siis on kaotus, muidu viik
        // kui valge peaks käigu tegema aga ei saa ja on tuli, siis must võitis. (ja vastupidi).
        System.out.println("Tulemus on:");
        System.out.println(onTuli(valgeKaik)?(valgeKaik ? -1 : 1) : 67);
        return onTuli(valgeKaik)?(valgeKaik ? -1 : 1) : 67;
    }

    private int nupuVaartusSeisuks(Malenupp malenupp){
        if (malenupp == null){
            return 0;
        }
        int vaartus = malenupp.onValge()? 0 : 6;
        switch (malenupp.getClass().getSimpleName()){
            case ("Ettur"): vaartus += 1;
                break;
            case ("Ratsu"): vaartus += 2;
                break;
            case ("Vanker"): vaartus += 3;
                break;
            case ("Lipp"): vaartus += 4;
                break;
            case ("Kuningas"): vaartus += 5;
                break;
            case ("Oda"): vaartus += 6;
        }
        return vaartus;
    }
    private BigInteger seisuSumma(){
        BigInteger summa = BigInteger.ZERO;
        for (int iPos = 0; iPos < 64; iPos++) {
            int nupuVaartus = nupuVaartusSeisuks(misNuppRuudul(new Asukoht(iPos/8, iPos%8)));
            summa = summa.add(BigInteger.valueOf(nupuVaartus).multiply(BigInteger.valueOf(13).pow(iPos)));
        }
        return summa;
    }


    public List<Malenupp> getKoikNupud() {
        return koikNupud;
    }
}
