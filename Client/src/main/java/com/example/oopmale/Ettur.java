package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Ettur extends MaleNupp {
    private final List<List<Integer>> kaigud = new ArrayList<>();
    private final int suund;

    public Ettur(int x, int y, boolean onValge) {
        super(x, y, onValge);
        if (onValge) {suund = 1;}
        else {suund = -1;}
        kaigud.add(List.of(suund, 0));
        kaigud.add(List.of(2* suund, 0));
        kaigud.add(List.of(suund, 1));
        kaigud.add(List.of(suund, -1));
    }

    @Override
    protected List<List<Integer>> kaiguDeltad() {
        return kaigud;
    }
}
