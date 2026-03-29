package com.example.oopmale;

import java.util.List;

public class Oda extends maleNupp{
    List<List<Integer>> kaigud;

    public Oda(int x, int y, boolean onValge, List<List<Integer>> kaigud) {
        super(x, y, onValge);
        for (int i = 1; i < 8; i++) {
            kaigud.add(List.of(i, i));
            kaigud.add(List.of(i,-i));
            kaigud.add(List.of(-i,i));
            kaigud.add(List.of(-i,-i));
        }

    }
    @Override
    List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
