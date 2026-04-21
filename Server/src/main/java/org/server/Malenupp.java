package org.server;

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
        this.x = uusx;
        this.y = uusy;
    }
    protected abstract List<List<Integer>> kaiguDeltad();

    public List<List<Integer>> voimalikudKaigud(){
        List<List<Integer>> kaigud =  kaiguDeltad();
        for (List<Integer> deltad : kaigud) {
            deltad.set(0,deltad.get(0)+x);
            deltad.set(1,deltad.get(1)+y);
        }

        kaigud.removeIf(deltad ->  deltad.get(1) < 0  // eemaldab kõik käigud, mis läheksid lauast välja
                || deltad.get(1) > 7
                || deltad.get(deltad.size()-1) < 0
                || deltad.get(deltad.size()-1) > 7);

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