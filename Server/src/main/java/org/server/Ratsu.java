package org.server;

import java.util.ArrayList;
import java.util.List;

public class Ratsu extends Malenupp {
    private final List<List<Asukoht>> kaigud = new ArrayList<>();

    public Ratsu(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = -1; i < 2; i+=2) {
            for (int j = -1; j < 2; j+=2){
                kaigud.add(List.of(new Asukoht(2*i, j)));
                kaigud.add(List.of(new Asukoht(i, 2*j)));
            }
        }
    }

    @Override
    protected List<List<Asukoht>> kaiguDeltad() {
        return kaigud;
    }

    @Override
    public int getNupuKood() {
        return super.onValge() ? Suhtlus.vRatsu : Suhtlus.mRatsu;
    }
}
