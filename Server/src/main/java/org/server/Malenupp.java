package org.server;

import java.util.List;

public abstract class Malenupp {
    private Asukoht asukoht;
    private final boolean onValge;

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
    }
    public void liiguta(int deltaX, int deltaY){
        this.asukoht.liiguta(new Asukoht(deltaX, deltaY));
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
        List<List<Asukoht>> kaigud =  kaiguDeltad();
        // eemaldab kõik käigud, mis lähevad lauast välja
        for (List<Asukoht> listDelta : kaigud) {
            listDelta.removeIf(delta -> delta.getX() + asukoht.getX() < 0
            || delta.getX() + asukoht.getX() > 7
            || delta.getY() + asukoht.getY() < 0
            || delta.getY() + asukoht.getY() > 7);
        }
        return kaigud;
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
}