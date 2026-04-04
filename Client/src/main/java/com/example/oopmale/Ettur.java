package com.example.oopmale;

import java.util.List;

public class Ettur extends Malenupp {
    List<List<Integer>> kaigud;

    public Ettur(int x, int y, boolean onValge) {
        super(x, y, onValge);
        int suund = 1;
        if (!onValge) suund = -1;
        kaigud.add(List.of(0, 1 * suund));
        kaigud.add(List.of(0, 2 * suund));
        kaigud.add(List.of(1, 1*suund));
        kaigud.add(List.of(-1, 1*suund));
    }

    @Override
    List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
