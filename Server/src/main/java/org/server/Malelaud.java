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
    private Asukoht enPassantVoimalus = null; // kui liiguti eelmine käik käis ettur kaks sammu edasi, siis tema asukoht, muidu null;
    private Map<BigInteger, Integer> seisudKordsusega = new HashMap<>(Map.of(new BigInteger("1712277703481438167266596593165792007111605184911878924740611116537301490"), 1)); //algseisu hash
    public Malenupp[][] tabelisEsitus = new Malenupp[8][8]; // malenupp[x][y] -> nupp mis asub ruudul (x,y)
    private Malenupp asendatavEttur = null;
    private int poolKaikudeLoendaja = 0;
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
        for (Malenupp malenupp : koikNupud) {
            tabelisEsitus[malenupp.getAsukoht().getX()][malenupp.getAsukoht().getY()] = malenupp;
        }
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
        // NB: vangerdamist siin eraldi käsitlema ei pea, sest kasVangerdusVoimalik juba kontrollib, et kuningas ei liigu läbi tule jne.
        // kui vangerdada, siis vanker on juba laua servas, seega tema nö tagant ei saa uut tuld välja tulla, seega siin liigutamata jääv vanker ei saa tuld varjata.
        return !kutsuFunktsioonPealeSimuleerimist(vanaAsukoht, uusAsukoht, a -> onTuli(valgeKaik));

    }

    @FunctionalInterface
    interface BoardFunction<T> {
        T apply(Malelaud malelaud);
    }
    public <T> T kutsuFunktsioonPealeSimuleerimist(Asukoht vanaAsukoht, Asukoht uusAsukoht, BoardFunction<T> f){
        Malenupp liigutatavNupp = misNuppRuudul(vanaAsukoht);
        Malenupp araVoetavNupp = misNuppRuudul(uusAsukoht);
        Malenupp enPassantMalu = null;
        if (enPassantKatse(liigutatavNupp, uusAsukoht)){
            enPassantMalu = misNuppRuudul(enPassantVoimalus);
            enPassantMalu.setElus(false);
            tabelisEsitus[enPassantMalu.getX()][enPassantMalu.getY()] = null;
        }
        tabelisEsitus[uusAsukoht.getX()][uusAsukoht.getY()] = tabelisEsitus[vanaAsukoht.getX()][vanaAsukoht.getY()];
        tabelisEsitus[vanaAsukoht.getX()][vanaAsukoht.getY()] = null;
        boolean oliLiikunud = liigutatavNupp.isOnLiikunud();
        liigutatavNupp.liiguta(uusAsukoht.lahuta(vanaAsukoht));
        if (araVoetavNupp!=null) araVoetavNupp.setElus(false);
        Malenupp vanker = null;
        if (kasProovitakseVangerdada(liigutatavNupp, uusAsukoht)){
            vanker = leiaVangerduseVanker((Kuningas) liigutatavNupp, uusAsukoht);
            tabelisEsitus[vanker.getX()][vanker.getY()] = null;
            if (vanker.getAsukoht().getX() == 7) {
                // lühike vangerdus
                vanker.liiguta(-2, 0);
            } else {
                // pikk vangerdus
                vanker.liiguta(3, 0);
            }
            tabelisEsitus[vanker.getX()][vanker.getY()] = vanker;
        }
        T vastus = f.apply(this);
        if (kasProovitakseVangerdada(liigutatavNupp, uusAsukoht)){
            tabelisEsitus[vanker.getX()][vanker.getY()] = null;
            if (vanker.getAsukoht().getX() == 5) {
                // lühike vangerdus
                vanker.liiguta(2, 0);
            } else {
                // pikk vangerdus
                vanker.liiguta(-3, 0);
            }
            tabelisEsitus[vanker.getX()][vanker.getY()] = vanker;
            vanker.setOnLiikunud(false);
        }
            if (araVoetavNupp!=null) araVoetavNupp.setElus(true);
        liigutatavNupp.liiguta(vanaAsukoht.lahuta(uusAsukoht));
        liigutatavNupp.setOnLiikunud(oliLiikunud);
        tabelisEsitus[vanaAsukoht.getX()][vanaAsukoht.getY()] = tabelisEsitus[uusAsukoht.getX()][uusAsukoht.getY()];
        tabelisEsitus[uusAsukoht.getX()][uusAsukoht.getY()] = araVoetavNupp;
        if (enPassantKatse(liigutatavNupp, uusAsukoht)){
            enPassantMalu.setElus(true);
            tabelisEsitus[enPassantMalu.getX()][enPassantMalu.getY()] = enPassantMalu;
        }
        return vastus;

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
        Asukoht deltaKaik = sihtAsukoht.lahuta(algus);

        List<Asukoht> oigeSuunaDeltad = null;
        for (List<Asukoht> suunaDeltad : nupp.voimalikudKaigud()) {
            if (suunaDeltad.contains(deltaKaik)) {
                oigeSuunaDeltad = suunaDeltad;
                break;
            }
        }

        if (oigeSuunaDeltad == null)
            return false; // Nupul võimatu sellist käiku teha

        if (enPassantKatse(nupp, sihtAsukoht) && !sobivEnPassantKatse(nupp, sihtAsukoht)){
            return false;
        }
        // Kui ettur edasi üritab liikuda, siis peab ruut tühi olema.
        if (nupp instanceof Ettur && nupp.getX() == sihtAsukoht.getX() && misNuppRuudul(sihtAsukoht) != null){
            return false;
        }

        for (Asukoht vaadeldavDelta : oigeSuunaDeltad) {
            Asukoht vaadeldavAsukoht = algus.liida(vaadeldavDelta);
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
        if (kuningas.isOnLiikunud()) {
            return false;
        }

        Malenupp vanker = leiaVangerduseVanker(kuningas, sihtRuut);

        if (vanker == null) {
            return false;
        }
        if (!(vanker instanceof Vanker)) {
            return false;
        }
        if (vanker.isOnLiikunud()) {
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
     * tagastab, kas antud käik saab üldse olla en passant.
     * (en passant kui etturikäik, mis liigub diagonaalselt tühjale ruudule)
     * @return
     */
    public boolean enPassantKatse(Malenupp nupp, Asukoht sihtAsukoht){
        if (nupp instanceof Ettur){
            return nupp.getX() != sihtAsukoht.getX() && misNuppRuudul(sihtAsukoht) == null;
        }
        return false;
    }

    /**
     * Eeldab, et tegemist on en passant katsega. tagastab true, kui tegemist on sobiva käiguga. NB: funktsioon eeldab siiski, et deltakaik sisaldub etturi kaikudes
     * @param nupp
     * @param sihtAsukoht
     * @return
     */
    public boolean sobivEnPassantKatse(Malenupp nupp, Asukoht sihtAsukoht){
        // vastane pidi etturit kaks sammu edasi liigutama
        if (enPassantVoimalus==null){
            return false;
        }
        // Kui ettur üritab en passant võtta, siis peab ta olema kõrvuti kaks sammu edasi liikunud nupuga ja liikuma samasse tulpa
        return (nupp.getY() == enPassantVoimalus.getY() && sihtAsukoht.getX() == enPassantVoimalus.getX());

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
            if (nupp.isElus() && voimalikLiigutada(nupp, ruut)) {
                return true;
            }
        }
        return false;
    }

    private Malenupp misNuppRuudul(Asukoht asukoht) {
        if (0 <= asukoht.getX() && asukoht.getX() < 8 && 0 <= asukoht.getY() && asukoht.getY()<8)
            return tabelisEsitus[asukoht.getX()][asukoht.getY()];
        return null;
    }

    public void teeKaik(int[] kaik) {
        Asukoht algneAsukoht = new Asukoht(kaik[0], kaik[1]);
        Asukoht uusAsukoht = new Asukoht(kaik[2], kaik[3]);
        teeKaik(algneAsukoht, uusAsukoht);
    }


    public void teeKaik(Asukoht algneAsukoht, Asukoht uusAsukoht) {
        Asukoht deltaKaik = uusAsukoht.lahuta(algneAsukoht);
        Malenupp liigutatavNupp = misNuppRuudul(algneAsukoht);
        Malenupp voetavNupp = misNuppRuudul(uusAsukoht);
        if (liigutatavNupp instanceof Ettur || voetavNupp!=null) poolKaikudeLoendaja = 0;
        poolKaikudeLoendaja ++;
        if (voetavNupp != null) voetavNupp.setElus(false);
        if (kasProovitakseVangerdada(liigutatavNupp, uusAsukoht)){
            Malenupp vanker = leiaVangerduseVanker((Kuningas) liigutatavNupp, uusAsukoht);
            if (vanker == null)
                throw new RuntimeException("Vangerduse vankrit ei leitud");
            tabelisEsitus[vanker.getX()][vanker.getY()] = null;
            if (vanker.getAsukoht().getX() == 7) {
                // lühike vangerdus
                vanker.liiguta(-2, 0);
            } else {
                // pikk vangerdus
                vanker.liiguta(3, 0);
            }
            tabelisEsitus[vanker.getX()][vanker.getY()] = vanker;
        }

        if (enPassantKatse(liigutatavNupp, uusAsukoht)){
            misNuppRuudul(enPassantVoimalus).setElus(false);
            tabelisEsitus[enPassantVoimalus.getX()][enPassantVoimalus.getY()] = null;
        }
        enPassantVoimalus = null;
        if (liigutatavNupp instanceof Ettur && (deltaKaik.getY() == 2 || deltaKaik.getY() == -2)){
            enPassantVoimalus = liigutatavNupp.getAsukoht();
        }
        tabelisEsitus[liigutatavNupp.getX()][liigutatavNupp.getY()] = null;
        liigutatavNupp.liiguta(deltaKaik);
        tabelisEsitus[liigutatavNupp.getX()][liigutatavNupp.getY()] = liigutatavNupp;
        // kui ettur on esimesel või viimasel real (ehk 7|y), siis küsi hiljem, keda asendada tahad
        asendatavEttur = null;
        if (uusAsukoht.getY()%7==0){
            Malenupp potensiaalneEttur = misNuppRuudul(uusAsukoht);
            if (potensiaalneEttur.getClass()==Ettur.class){
                asendatavEttur = potensiaalneEttur;
                asendatavEttur.setElus(false);
            }
        }


    }
    public Asukoht asendatavaEtturiAsukoht(){
        if (asendatavEttur == null){
            return null;
        }
        return asendatavEttur.getAsukoht();
    }
    /**
     *
     * @param asendus
     */
    public void asendaEttur(String asendus){
        switch (asendus){
            case "Oda" :
                tabelisEsitus[asendatavEttur.getX()][asendatavEttur.getY()] = new Oda(asendatavEttur.getX(), asendatavEttur.getY(), asendatavEttur.onValge());
                break;
            case "Ratsu" :
                tabelisEsitus[asendatavEttur.getX()][asendatavEttur.getY()] = new Ratsu(asendatavEttur.getX(), asendatavEttur.getY(), asendatavEttur.onValge());
                break;
            case "Lipp" :
                tabelisEsitus[asendatavEttur.getX()][asendatavEttur.getY()] = new Lipp(asendatavEttur.getX(), asendatavEttur.getY(), asendatavEttur.onValge());
                break;
            case "Vanker" :
                tabelisEsitus[asendatavEttur.getX()][asendatavEttur.getY()] = new Vanker(asendatavEttur.getX(), asendatavEttur.getY(), asendatavEttur.onValge());
                break;
        }
        koikNupud.add(tabelisEsitus[asendatavEttur.getX()][asendatavEttur.getY()]);
        List<Malenupp> varvigaList = asendatavEttur.onValge() ? valgedNupud : mustadNupud;
        varvigaList.add(tabelisEsitus[asendatavEttur.getX()][asendatavEttur.getY()]);
    }
    /**
     * Kutsutakse välja käigu alguses (okei tehniliselt käigu lõpus aga vastase värviga, sama asi)
     * Hetkel tagastab 0 kui mäng jätkub, 1 kui valge võitis, -1 kui must võitis ja 67 kui seis on viik.
     * @param valgeKaik
     * @return
     */
    int mangLabi(boolean valgeKaik){
        if (seisudKordsusega.containsValue(3) || poolKaikudeLoendaja == 100)
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
                        System.out.println("Kaik mida teha saab on: " + iMalenupp.getAsukoht() + iMalenupp.getAsukoht().liida(iKaik));
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
        // on 9 erinevat võimalust, üks pool saab vangerdada nullil, ühel või kahel erineval moel mingist seisust alates, kokku 3*3
        int vangerduseVaartus = 0;
        int kuningasSaabVangerdada = 0;
        int liigutamataVankreid = 0;
        for (Malenupp malenupp : mustadNupud) {
            if (malenupp instanceof Kuningas && !malenupp.isOnLiikunud()){
                kuningasSaabVangerdada = 1;
            }
            if (malenupp instanceof Vanker && !malenupp.isOnLiikunud()){
                liigutamataVankreid += 1;
            }
            vangerduseVaartus += kuningasSaabVangerdada * liigutamataVankreid;
        }
        kuningasSaabVangerdada = 0;
        liigutamataVankreid = 0;

        for (Malenupp malenupp : valgedNupud) {
            if (malenupp instanceof Kuningas && !malenupp.isOnLiikunud()){
                kuningasSaabVangerdada = 1;
            }
            if (malenupp instanceof Vanker && !malenupp.isOnLiikunud()){
                liigutamataVankreid += 1;
            }
            vangerduseVaartus += 3 * kuningasSaabVangerdada * liigutamataVankreid;
        }
        summa = summa.add(BigInteger.valueOf(vangerduseVaartus).multiply(BigInteger.valueOf(13).pow(64)));
        if (enPassantVoimalus!= null){
            Malenupp kandidaat1 = misNuppRuudul(enPassantVoimalus.liida(new Asukoht(1, 0)));
            Malenupp kandidaat2 = misNuppRuudul(enPassantVoimalus.liida(new Asukoht(-1, 0)));
            if (kandidaat1 instanceof Ettur && kandidaat1.onValge() != misNuppRuudul(enPassantVoimalus).onValge()
                || kandidaat2 instanceof Ettur && kandidaat2.onValge() != misNuppRuudul(enPassantVoimalus).onValge())
                    summa = summa.add(BigInteger.valueOf(13).pow(65));
        }
        return summa;
    }

    public void uuendaSeisuLoendur(){
        BigInteger seisuSumma = seisuSumma();
        if (!seisudKordsusega.containsKey(seisuSumma)){
            seisudKordsusega.put(seisuSumma, 0);
        }
        seisudKordsusega.replace(seisuSumma, 1 + seisudKordsusega.get(seisuSumma));
    }


    public List<Asukoht> nupuVoimalikudKaigud(Malenupp malenupp) {
        List<Asukoht> voimalikudKaigud = new ArrayList<>();
        for (List<Asukoht> iSuund : malenupp.voimalikudKaigud()) {
            for (Asukoht iDelta : iSuund) {
                if (kasLubatudKaik(malenupp.onValge(), new int[]{
                        malenupp.getX(),
                        malenupp.getY(),
                        malenupp.getX() + iDelta.getX(),
                        malenupp.getY() + iDelta.getY()
                })) voimalikudKaigud.add(malenupp.getAsukoht().liida(iDelta));
            }
        }
        return voimalikudKaigud;
    }

    public List<List<Asukoht>> koikVoimalikudKaigud(Boolean onValge){
        List<List<Asukoht>> koikKaigud = new ArrayList<>();
        List<Malenupp> nupud = onValge?valgedNupud:mustadNupud;
        for (Malenupp malenupp : nupud) {
            if (malenupp.isElus()){
                for (Asukoht asukoht : nupuVoimalikudKaigud(malenupp)) {
                    koikKaigud.add(List.of(malenupp.getAsukoht(), asukoht));
                }
            }
        }
        return koikKaigud;
    }

    public List<Malenupp> getKoikNupud() {
        return koikNupud;
    }

    public List<Malenupp> getValgedNupud() {
        return valgedNupud;
    }

    public List<Malenupp> getMustadNupud() {
        return mustadNupud;
    }
}
