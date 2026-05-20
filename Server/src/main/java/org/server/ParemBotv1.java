package org.server;

import java.util.List;
import java.util.Map;

public class ParemBotv1 extends Bot{
    private int maxSugavus;
    private Kuningas valgeKuningas;
    private Kuningas mustKuningas;
    public ParemBotv1(int maxSugavus) {
        this.maxSugavus = maxSugavus;
        for (Malenupp malenupp : malelaud.getKoikNupud()) {
            if (malenupp instanceof Kuningas){
                if (malenupp.onValge()) valgeKuningas = (Kuningas) malenupp;
                else mustKuningas = (Kuningas) malenupp;
            }
        }
    }

    record Tulemus(Asukoht algKoht, Asukoht sihtKoht, Double tulemus){};

    private double heuristic(){
        int summa = 0;
        Map<Class, Integer> nupuVaartus = Map.of(Ettur.class,1,
                Ratsu.class, 3,
                Oda.class, 3,
                Vanker.class, 5,
                Lipp.class, 9,
                Kuningas.class, 0);

        for (Malenupp malenupp : malelaud.getKoikNupud()) {
            if (malenupp.isElus()) {
                summa += 10 * nupuVaartus.get(malenupp.getClass()) * (malenupp.onValge() ? 1 : -1);
                // mdea ütleme, et tahame üldiselt enda nuppe vastase kuningale võimalikult lähedale
                // sest miks mitte
                Kuningas vastasKuningas = malenupp.onValge() ? mustKuningas : valgeKuningas;
                Asukoht vahe = vastasKuningas.getAsukoht().lahuta(malenupp.getAsukoht());
                summa += Math.pow(vahe.getX()*vahe.getX()+ vahe.getY()* vahe.getY(), 0.5) * (malenupp.onValge()? -1 : 1);
            }
        }

        int valgeLabi = malelaud.mangLabi(true);
        int mustLabi = malelaud.mangLabi(false);
        if (valgeLabi == 0 && mustLabi == 0) return summa;
        if (valgeLabi == 67 || mustLabi == 67) return 0;
        if (valgeLabi == -1 || mustLabi == -1) return Double.NEGATIVE_INFINITY;
        if (valgeLabi == 1 || mustLabi == 1) return Double.POSITIVE_INFINITY;
        return 1000000; // siia ei tohiks jõuda tglt
    }

    private Tulemus dfs(int sugavus){
        double heuristicTulemus = heuristic();
        if (sugavus == maxSugavus || heuristicTulemus == Double.NEGATIVE_INFINITY || heuristicTulemus == Double.POSITIVE_INFINITY){
            return new Tulemus(null, null, heuristicTulemus);
        }
        boolean olenValge = onValge ^ (sugavus%2==0);
        Tulemus parim = new Tulemus(null, null, olenValge ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        List<List<Asukoht>> originaalKaigud = malelaud.koikVoimalikudKaigud(olenValge);
        List<List<Asukoht>> kaigudKoopia = originaalKaigud.stream().map(list -> list.stream().map(a -> Asukoht.koopia(a)).toList()).toList();
        for (List<Asukoht> iKaik : kaigudKoopia) {
            Tulemus praeguneTulemus = malelaud.kutsuFunktsioonPealeSimuleerimist(iKaik.get(0), iKaik.get(1), t -> dfs(sugavus + 1));
            if (praeguneTulemus.tulemus > parim.tulemus && olenValge){
                parim = new Tulemus(iKaik.get(0), iKaik.get(1), praeguneTulemus.tulemus);
            }
            if (praeguneTulemus.tulemus < parim.tulemus && !olenValge){
                parim = new Tulemus(iKaik.get(0), iKaik.get(1), praeguneTulemus.tulemus);
            }
        }
        return parim;
    }
    @Override
    public int[] annaKaik() {
        Tulemus vastus = dfs(1);
        System.out.println("Bot arvab, et parim võimalik on: " + vastus.algKoht + vastus.sihtKoht + " " + vastus.tulemus);
        return new int[] {
                vastus.algKoht.getX(),
                vastus.algKoht.getY(),
                vastus.sihtKoht.getX(),
                vastus.sihtKoht.getY()
        };
    }
}
