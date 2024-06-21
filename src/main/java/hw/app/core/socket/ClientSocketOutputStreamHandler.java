package hw.app.core.socket;

import hw.app.core.main.Mediator;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ClientSocketOutputStreamHandler extends Thread {
    private final OutputStream _socketOutputStream;

    public ClientSocketOutputStreamHandler(OutputStream socketOutputStream) {
        _socketOutputStream = socketOutputStream;
    }

    public void run() {
        PrintWriter writer = new PrintWriter(_socketOutputStream, true);

        String message;
        //noinspection InfiniteLoopStatement
        do {
            synchronized(Mediator.messages) {
                message = Mediator.messages.poll();
            }

            if (message == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            message = message + "\n";

            writer.println(message);

        } while (true);
    }
}
