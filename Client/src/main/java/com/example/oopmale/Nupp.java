package com.example.oopmale;

public class Nupp {
    private int x;
    private int y;

    private boolean valge;
    private String malend;

    public Nupp(int x, int y, byte kood) {
        this.x = x;
        this.y = y;
        switch (kood) {
            case Suhtlus.vEttur:
                valge = true;
                malend = "ettur";
                break;
            case Suhtlus.mEttur:
                valge = false;
                malend = "ettur";
                break;
            case Suhtlus.vVanker:
                valge = true;
                malend = "vanker";
                break;
            case Suhtlus.mVanker:
                valge = false;
                malend = "vanker";
                break;
            case Suhtlus.vRatsu:
                valge = true;
                malend = "ratsu";
                break;
            case Suhtlus.mRatsu:
                valge = false;
                malend = "ratsu";
                break;
            case Suhtlus.vOda:
                valge = true;
                malend = "oda";
                break;
            case Suhtlus.mOda:
                valge = false;
                malend = "oda";
                break;
            case Suhtlus.vLipp:
                valge = true;
                malend = "lipp";
                break;
            case Suhtlus.mLipp:
                valge = false;
                malend = "lipp";
                break;
            case Suhtlus.vKuningas:
                valge = true;
                malend = "kuningas";
                break;
            case Suhtlus.mKuningas:
                valge = false;
                malend = "kuningas";
                break;
        }
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
