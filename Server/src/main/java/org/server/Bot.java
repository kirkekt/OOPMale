package org.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public abstract class Bot implements Runnable{
    public final Malelaud malelaud;
    public final Boolean onValge;
    private DataOutputStream out;
    private DataInputStream in;

    public Bot(boolean onValge) {
        this.onValge = onValge;
        this.malelaud = new Malelaud();

    }

    /**
     * Tagastab käigu, mida teha üritab. Enda laual seda päriselt ei tee
     * @return
     */
    abstract public int[] annaKaik();

    /**
     * Ootab alati, et server annaks talle käigu
     * @param kaik
     */
    public void teeKaik(int[] kaik){
        malelaud.teeKaik(kaik);
    }

    public BotSocket createSocket() throws IOException {
        BotSocket socket = new BotSocket();
        this.in  = socket.getBotIn();
        this.out = socket.getBotOut();
        Thread t = new Thread(this, "Bot-" + (onValge ? "white" : "black"));
        t.setDaemon(true);
        t.start();
        return socket;
    }

    @Override
    public void run() {
        try {
            handleInit();
            boolean myTurn = onValge;
            while (true) {
                if (myTurn) {
                    myTurn = !handleMyTurn();
                } else {
                    if (!handleOpponentTurn()) break;
                    myTurn = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Bot: connection closed — " + e.getMessage());
        }
    }

    private void handleInit() throws IOException {
        in.readInt(); // length
        in.readInt(); // manguAlgus
        in.readInt(); // color
        out.writeInt(1);
        out.flush();
    }

    private boolean handleMyTurn() throws IOException {
        int[] kaik = annaKaik();
        out.writeInt(kaik.length + 1);
        out.writeInt(Suhtlus.kaiguKood);
        for (int v : kaik) out.writeInt(v);
        out.flush();

        in.readInt();                    // consume the loeKoik confirmation (always 1)
        int response = in.readInt();     // now read the actual kaikOk / illegaalneKaik

        if (response == Suhtlus.kaikOk) {
            teeKaik(kaik);
            return true;
        }
        return false;
    }
    private boolean handleOpponentTurn() throws IOException {
        int length = in.readInt();
        int[] msg  = new int[length];
        for (int i = 0; i < length; i++) msg[i] = in.readInt();

        if (msg[0] == Suhtlus.manguLopp)  return false;

        if (msg[0] == Suhtlus.kaiguKood) {
            out.writeInt(1);
            out.flush();
            teeKaik(Arrays.copyOfRange(msg, 1, msg.length));
        }
        return true;
    }
}
