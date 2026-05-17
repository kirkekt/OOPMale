package org.server;

import java.io.*;
import java.net.Socket;

/**
 * A fake Socket backed by PipedStreams.
 * The server sees a normal Socket; the bot talks via getBotSideIn/Out.
 */
public class BotSocket extends Socket {

    private final PipedInputStream  serverIn;
    private final PipedOutputStream serverOut;

    private final DataInputStream  botIn;
    private final DataOutputStream botOut;

    public BotSocket() throws IOException {
        PipedOutputStream botWritesPipe = new PipedOutputStream();
        this.serverIn  = new PipedInputStream(botWritesPipe);
        this.botOut    = new DataOutputStream(botWritesPipe);

        PipedInputStream botReadsPipe = new PipedInputStream();
        this.serverOut = new PipedOutputStream(botReadsPipe);
        this.botIn     = new DataInputStream(botReadsPipe);
    }

    @Override public InputStream  getInputStream()  { return serverIn;  }
    @Override public OutputStream getOutputStream() { return serverOut; }

    public DataInputStream  getBotIn()  { return botIn;  }
    public DataOutputStream getBotOut() { return botOut; }
}