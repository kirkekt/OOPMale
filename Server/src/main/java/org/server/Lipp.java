package org.server;

import java.util.ArrayList;
import java.util.List;

public class Lipp extends Malenupp {
    private final List<List<Asukoht>> kaigud = new ArrayList<>();

    public Lipp(Asukoht asukoht, boolean onValge) {
        super(asukoht, onValge);
        for (int i = 0; i < 8; i++) {
            kaigud.add(new ArrayList<>());
        }
        for (int i = 1; i < 8; i++) {
            kaigud.get(0).add(new Asukoht(0, i));
            kaigud.get(1).add(new Asukoht(0,-i));
            kaigud.get(2).add(new Asukoht(i,0));
            kaigud.get(3).add(new Asukoht(-i,0));
            kaigud.get(4).add(new Asukoht(i, i));
            kaigud.get(5).add(new Asukoht(i,-i));
            kaigud.get(6).add(new Asukoht(-i,i));
            kaigud.get(7).add(new Asukoht(-i,-i));
        }
    }
    public Lipp(int x, int y, boolean onValge){
        this(new Asukoht(x, y), onValge);
    }
    @Override
    protected List<List<Asukoht>> kaiguDeltad() {
        return kaigud;
    }
}

