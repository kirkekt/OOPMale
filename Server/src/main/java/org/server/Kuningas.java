package org.server;

import java.util.ArrayList;
import java.util.List;

public class Kuningas extends Malenupp {
    private final List<List<Asukoht>> kaigud = new ArrayList<>();

    public Kuningas(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0) kaigud.add(List.of(new Asukoht(i,j)));
            }
        }
        kaigud.add(List.of(new Asukoht(2, 0)));
        kaigud.add(List.of(new Asukoht(-2, 0)));
    }

    @Override
    protected List<List<Asukoht>> kaiguDeltad() {
        return kaigud;
    }

    @Override
    public int getNupuKood() {
        return super.onValge() ? Suhtlus.vKuningas : Suhtlus.mKuningas;
    }
}
