package org.server;

import java.util.List;

public class RandoBot extends Bot{
    public RandoBot() {
        super();
    }

    @Override
    public int[] annaKaik() {
        System.out.println("Ma arvan, et olen valge" + onValge);
        List<List<Asukoht>> kaigud =  malelaud.koikVoimalikudKaigud(onValge);
        List<Asukoht> valitudKaik = kaigud.get((int) (Math.random() * kaigud.size()));
        return new int[] {valitudKaik.get(0).getX(),
                          valitudKaik.get(0).getY(),
                          valitudKaik.get(1).getX(),
                          valitudKaik.get(1).getY()};
    }
}
