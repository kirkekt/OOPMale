package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Mang implements Runnable {

    private final Socket valgeSocket;
    private final Socket mustSocket;


    public Mang(Socket valge, Socket must) {
        this.valgeSocket = valge;
        this.mustSocket = must;
    }


    @Override
    public void run() {
        Malelaud malelaud = new Malelaud();

        try (DataInputStream mustIn = new DataInputStream(mustSocket.getInputStream());
             DataOutputStream mustOut = new DataOutputStream(mustSocket.getOutputStream());
             DataInputStream valgeIn = new DataInputStream(valgeSocket.getInputStream());
             DataOutputStream valgeOut = new DataOutputStream(valgeSocket.getOutputStream())) {

            Suhtlus.init(valgeOut, valgeIn, mustOut, mustIn);

            boolean valgeKord = true;
            DataInputStream kaiguTegijaIn;
            DataOutputStream kaiguTegijaOut;
            DataInputStream vastaneIn;
            DataOutputStream vastaneOut;

            while (true) {
                if (valgeKord) {
                    kaiguTegijaIn = valgeIn;
                    kaiguTegijaOut = valgeOut;
                    vastaneIn = mustIn;
                    vastaneOut = mustOut;
                }
                else {
                    kaiguTegijaIn = mustIn;
                    kaiguTegijaOut = mustOut;
                    vastaneIn = valgeIn;
                    vastaneOut = valgeOut;
                }

                int[] kaik = Suhtlus.loeKoik(kaiguTegijaIn);

                if (kaik[0] == Suhtlus.manguLopp) {
                    Suhtlus.teavitaEtManguLopp(kaik[1], vastaneOut);
                    break;
                }

                kaik = Suhtlus.eemaldaKood(kaik);
                if (malelaud.kasLubatudKaik(valgeKord, kaik)) {
                    malelaud.teeKaik(kaik);
                    Suhtlus.saadaInfo(vastaneOut, vastaneIn, Suhtlus.kaiguKood, kaik);

                    kaiguTegijaOut.writeInt(Suhtlus.kaikOk);
                    valgeKord = !valgeKord;
                }
                else {
                    kaiguTegijaOut.writeInt(Suhtlus.illegaalneKaik);
                }
            }
            System.out.println("Mäng läbi");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
