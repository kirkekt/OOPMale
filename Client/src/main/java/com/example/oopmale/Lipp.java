package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Lipp extends Malenupp {
    private final List<List<Integer>> kaigud = new ArrayList<>();

    public Lipp(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = 1; i < 8; i++) {
            kaigud.add(List.of(0, i));
            kaigud.add(List.of(0,-i));
            kaigud.add(List.of(i,0));
            kaigud.add(List.of(-i,0));
            kaigud.add(List.of(i, i));
            kaigud.add(List.of(i,-i));
            kaigud.add(List.of(-i,i));
            kaigud.add(List.of(-i,-i));
        }
    }
    @Override
    protected List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}

