package org.server;

/**
 * Praktikas 2d vektor
 */
public class Asukoht {
    private int x;
    private int y;

    public Asukoht(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void liiguta(Asukoht delta){
        this.x += delta.x;
        this.y += delta.y;
    }

    /**
     * Kui on vaja liikuda A -> B, siis kasutusviisiga B.lahuta(A) saab vektori, mida B-le liita.
     * @param algus
     * @return
     */
    public Asukoht lahuta(Asukoht algus){
        return new Asukoht(this.getX()-algus.getX(), this.getY()- algus.getY());
    }
    public Asukoht liida(Asukoht delta){
        return new Asukoht(this.getX()+delta.getX(), this.getY() + delta.getY());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Asukoht asukoht = (Asukoht) o;
        return x == asukoht.x && y == asukoht.y;
    }
    public static Asukoht koopia(Asukoht other){
        if (other == null) return null;
        return new Asukoht(other.getX(), other.getY());
    }
    @Override
    public String toString() {
        return "Asukoht("+x+", "+y+")";
    }
}
