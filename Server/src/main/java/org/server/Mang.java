package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Mang implements Runnable {

    private final Socket valgeSocket;
    private final Socket mustSocket;


    public Mang(Socket valge, Socket must) {
        this.valgeSocket = valge;
        this.mustSocket = must;
    }

    @Override
    public void run() {
        // Tekitab malelaua
        Malelaud malelaud = new Malelaud();

        // Avab suhtlus-Streamid
        try (DataInputStream mustIn = new DataInputStream(mustSocket.getInputStream());
             DataOutputStream mustOut = new DataOutputStream(mustSocket.getOutputStream());
             DataInputStream valgeIn = new DataInputStream(valgeSocket.getInputStream());
             DataOutputStream valgeOut = new DataOutputStream(valgeSocket.getOutputStream())) {

            // Teavitab mõlemat mängijat
            Suhtlus.init(valgeOut, valgeIn, mustOut, mustIn);

            // Valmistab ette muutujad
            boolean valgeKord = true;
            DataInputStream kaiguTegijaIn;
            DataOutputStream kaiguTegijaOut;
            DataInputStream vastaneIn;
            DataOutputStream vastaneOut;

            // Algatab mängu loop-i
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

                int[] kaik = Suhtlus.loeKaik(kaiguTegijaIn, kaiguTegijaOut, vastaneOut);

                if (kaik[0] == Suhtlus.manguLopp) {
                    Suhtlus.teavitaEtManguLopp(kaik[1], vastaneOut);
                    break;
                }

                if (malelaud.kasLubatudKaik(valgeKord, kaik)) {
                    malelaud.teeKaik(kaik);
                    Suhtlus.saadaKaiguInfo(kaiguTegijaOut, vastaneOut, vastaneIn, kaik);
                    valgeKord = !valgeKord;
                }
                else {
                    System.out.println("keelatud käik");
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
