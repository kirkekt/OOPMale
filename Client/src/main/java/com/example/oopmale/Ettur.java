package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Ettur extends maleNupp {
    List<List<Integer>> kaigud;

    public Ettur(int x, int y, boolean onValge) {
        super(x, y, onValge);
    }

    @Override
    List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
