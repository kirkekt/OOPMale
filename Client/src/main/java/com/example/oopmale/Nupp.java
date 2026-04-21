package com.example.oopmale;

public class Nupp {
    private int x;
    private int y;

    private boolean valge;
    private String malend;

    public Nupp(int x, int y, boolean valge, String malend) {
        this.x = x;
        this.y = y;
        this.valge = valge;
        this.malend = malend;
    }

    public boolean onValge() {
        return valge;
    }

    public boolean kasAsubSiin(int x, int y) {
        return this.x == x && this.y == y;
    }

    public void liiguta(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getMalend() {
        return malend;
    }
}
