package org.server;

import java.util.ArrayList;
import java.util.List;

public class Vanker extends Malenupp {
    private final List<List<Asukoht>> kaigud = new ArrayList<>();

    public Vanker(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = 0; i < 4; i++) {
            kaigud.add(new ArrayList<>());
        }
        for (int i = 1; i < 8; i++) {
            kaigud.get(0).add(new Asukoht(0, i));
            kaigud.get(1).add(new Asukoht(0,-i));
            kaigud.get(2).add(new Asukoht(i,0));
            kaigud.get(3).add(new Asukoht(-i,0));
        }

    }
    @Override
    protected List<List<Asukoht>> kaiguDeltad() {
        return kaigud;
    }

    @Override
    public int getNupuKood() {
        return super.onValge() ? Suhtlus.vVanker : Suhtlus.mVanker;
    }
}
