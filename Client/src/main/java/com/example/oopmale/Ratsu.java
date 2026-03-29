package com.example.oopmale;

import java.util.List;

public class Ratsu extends maleNupp{
    List<List<Integer>> kaigud;
    public Ratsu(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = -1; i < 2; i+=2) {
            for (int j = -1; j < 2; j+=2){
                kaigud.add(List.of(2*i, j));
                kaigud.add(List.of(2*i, -j));
            }
        }
    }

    @Override
    List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
