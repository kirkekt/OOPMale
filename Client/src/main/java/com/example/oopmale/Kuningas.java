package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Kuningas extends maleNupp{
    List<List<Integer>> kaigud = new ArrayList<>();
    public Kuningas(int x, int y, boolean onValge) {
        super(x, y, onValge);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i * j != 0) kaigud.add(List.of(i,j));
            }
        }
    }


    @Override
    List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
