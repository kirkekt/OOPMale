package org.server;

import java.util.ArrayList;
import java.util.List;

public class Ettur extends Malenupp {
    private final List<List<Asukoht>> esimeneKaik = new ArrayList<>();
    private final List<List<Asukoht>> hilisemKaik = new ArrayList<>();

    public Ettur(int x, int y, boolean onValge) {
        super(x, y, onValge);
        int suund = 1;
        if (!onValge) suund = -1;
        for (int i = -1; i < 2; i++) {
            esimeneKaik.add(new ArrayList<>(List.of(new Asukoht(i, suund))));
            hilisemKaik.add(new ArrayList<>(List.of(new Asukoht(i, suund))));        }
        esimeneKaik.get(1).add(new Asukoht(0, 2*suund));
    }

    @Override
    protected List<List<Asukoht>> kaiguDeltad() {
        if (onValge() && this.getAsukoht().getY()==1 || !onValge() && this.getAsukoht().getY() == 6){
            System.out.println(esimeneKaik);
            return esimeneKaik;

        }
        else{
            return hilisemKaik;
        }

    }
}
