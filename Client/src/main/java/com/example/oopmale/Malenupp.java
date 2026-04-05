package com.example.oopmale;

import java.util.List;

public abstract class Malenupp {
    private int x;
    private int y;
    private final boolean onValge;

    public Malenupp(int x, int y, boolean onValge) {
        this.x = x;
        this.y = y;
        this.onValge = onValge;
    }

    public void liiguta(int uusx, int uusy){
        this.x = uusx-1;
        this.y = 9-uusy;
    }
    protected abstract List<List<Integer>> kaiguDeltad();

    public List<List<Integer>> voimalikudKaigud(){
        List<List<Integer>> kaigud =  kaiguDeltad();
        for (List<Integer> deltad : kaigud) {
            deltad.set(0,deltad.get(0)+x);
            deltad.set(1,deltad.get(1)+y);
        }

        kaigud.removeIf(deltad ->  deltad.getFirst() < 0  // eemaldab kõik käigud, mis läheksid lauast välja
                || deltad.getFirst() > 7
                || deltad.getLast() < 0
                || deltad.getLast() > 7);

        return kaigud;
    }

    public boolean onValge() {
        return onValge;
    }
    public boolean kasAsubSiin(int x, int y) {
        return this.x == x && this.y == y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean OnValge() {
        return onValge;
    }
}