package org.server;

import java.util.Objects;

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

    // getterid ja setterid on erandjuhtudeks (nt vangerdamise jaoks ja katsetamise jaoks
    // ideaalis ei tohi neid kasutada

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Asukoht asukoht = (Asukoht) o;
        return x == asukoht.x && y == asukoht.y;
    }

}
