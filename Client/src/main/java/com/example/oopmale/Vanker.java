package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Vanker extends MaleNupp {
    private final List<List<Integer>> kaigud = new ArrayList<>();

    public Vanker(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = 1; i < 8; i++) {
            kaigud.add(List.of(0, i));
            kaigud.add(List.of(0,-i));
            kaigud.add(List.of(i,0));
            kaigud.add(List.of(-i,0));
        }

    }
    @Override
    protected List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
