package com.example.oopmale;

import java.util.List;

public class Vanker extends maleNupp{
    List<List<Integer>> kaigud;

    public Vanker(int x, int y, boolean onValge, List<List<Integer>> kaigud) {
        super(x, y, onValge);
        for (int i = 1; i < 8; i++) {
            kaigud.add(List.of(0, i));
            kaigud.add(List.of(0,-i));
            kaigud.add(List.of(i,0));
            kaigud.add(List.of(-i,0));
        }

    }
    @Override
    List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
