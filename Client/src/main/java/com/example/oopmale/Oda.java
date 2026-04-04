package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Oda extends MaleNupp {
    private final List<List<Integer>> kaigud = new ArrayList<>();

    public Oda(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = 1; i < 8; i++) {
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
