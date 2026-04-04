package com.example.oopmale;

import java.util.List;

public abstract class Malenupp {
    private int x;
    private int y;
    private boolean onValge;
    /*

     */
    public Malenupp(int x, int y, boolean onValge) {
        this.x = x;
        this.y = y;
        this.onValge = onValge;
    }

    public void liiguta(int uusx, int uusy){
        this.x = uusx;
        this.y = uusy;
    }
    abstract List<List<Integer>> kaiguDeltad();

    List<List<Integer>> voimalikudKaigud(){
        List<List<Integer>> kaigud =  kaiguDeltad();
        for (List<Integer> deltad : kaigud) {
             deltad.set(0,deltad.get(0)+x);
             deltad.set(1,deltad.get(1)+y);
        }
        return kaigud;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOnValge() {
        return onValge;
    }
}