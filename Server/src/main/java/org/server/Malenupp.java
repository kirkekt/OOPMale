package org.server;

import java.util.ArrayList;
import java.util.List;

public abstract class Malenupp {
    private Asukoht asukoht;
    private final boolean onValge;
    private boolean onLiikunud = false;
    private boolean elus = true;


    public Malenupp(Asukoht asukoht, boolean onValge){
        this.asukoht = asukoht;
        this.onValge = onValge;
    }
    public Malenupp(int x, int y, boolean onValge) {
        this.asukoht = new Asukoht(x,y);
        this.onValge = onValge;
    }

    public void liiguta(Asukoht muutus){
        this.asukoht.liiguta(muutus);
        this.onLiikunud = true;
    }
    public void liiguta(int deltaX, int deltaY){
        this.asukoht.liiguta(new Asukoht(deltaX, deltaY));
        this.onLiikunud = true;
    }

    /**
     * tagastab listi, mille igas elemendis on list nö samas suunas käikudega.
     * St igas elemendis kui k-s käik ei sobi, sest mingi malend on ees, siis sealt edasi ei sobi kinldalt ükski käik.
     * @return
     */
    protected abstract List<List<Asukoht>> kaiguDeltad();

    /**
     * Tagastab
     * @return
     */
    public List<List<Asukoht>> voimalikudKaigud(){
        List<List<Asukoht>> kaigud = new ArrayList<>();

        for (List<Asukoht> suund : kaiguDeltad()) {
            ArrayList<Asukoht> lubatud = new ArrayList<>(suund);
            lubatud.removeIf(delta ->{
                int x = asukoht.getX() + delta.getX();
                int y = asukoht.getY() + delta.getY();
                return x < 0 || x > 7 || y < 0 || y > 7;
            });
            kaigud.add(lubatud);

        }        // eemaldab kõik käigud, mis lähevad lauast välja
        return kaigud;
    }
    public boolean isOnLiikunud(){
        return onLiikunud;
    }

    public void setOnLiikunud(boolean onLiikunud){
        this.onLiikunud = onLiikunud;
    }
    public boolean onValge() {
        return onValge;
    }

    public boolean kasAsubSiin(Asukoht asukoht){
        return this.asukoht.equals(asukoht);
    }

    public boolean kasAsubSiin(int x, int y) {
        return asukoht.equals(new Asukoht(x,y));
    }

    public Asukoht getAsukoht() {
        return asukoht;
    }

    public boolean OnValge() {
        return onValge;
    }

    public int getX() { return asukoht.getX(); }
    public int getY() { return asukoht.getY(); }

    public boolean isElus() {
        return elus;
    }

    public void setElus(boolean elus) {
        this.elus= elus;
    }

    @Override
    public String toString() {
        return "Malenupp{" +
                "Klass="+this.getClass()+
                "asukoht=" + asukoht +
                ", onValge=" + onValge +
                '}';
    }
}