package com.example.oopmale;

import java.util.ArrayList;
import java.util.List;

public class Laud {
    Malenupp[][] laud = new Malenupp[8][8];

    public Laud() {
        // Mustade algasend
        laud[0][0] = new Vanker(0,0,false);
        laud[0][1] = new Ratsu(0,1,false);
        laud[0][2] = new Oda(0,2,false);
        laud[0][3] = new Lipp(0,3,false);
        laud[0][4] = new Kuningas(0,4,false);
        laud[0][5] = new Oda(0,5,false);
        laud[0][6] = new Ratsu(0,6,false);
        laud[0][7] = new Vanker(0,7,false);

        for (int i = 0; i < 8; i++) {
            laud[1][i] = new Ettur(1,i,false);
        }

        // Valgete algasend
        laud[7][0] = new Vanker(7,0,true);
        laud[7][1] = new Ratsu(7,1,true);
        laud[7][2] = new Oda(7,2,true);
        laud[7][3] = new Lipp(7,3,true);
        laud[7][4] = new Kuningas(7,4,true);
        laud[7][5] = new Oda(7,5,true);
        laud[7][6] = new Ratsu(7,6,true);
        laud[7][7] = new Vanker(7,7,true);

        for (int i = 0; i < 8; i++) {
            laud[6][i] = new Ettur(6,i,true);
        }
    }

    public Malenupp[][] getLaud() {
        return laud;
    }
}
