package hw.app.core.socket;

import hw.app.core.common.MessageType;
import hw.app.core.main.Mediator;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClientSocketInputStreamHandler extends Thread {
    private final InputStream _socketInputStream;

    public ClientSocketInputStreamHandler(InputStream socketInputStream) {
        _socketInputStream = socketInputStream;
    }

    public void run() {
        try {

            var reader = new BufferedReader(new InputStreamReader(_socketInputStream));

            String message;

            do {

                message = reader.readLine();

                if (message == null) {
                    Mediator.log("Socket disconnected");
                    Thread.sleep(500);
                    Mediator.log("Exiting...");
                    Thread.sleep(2000);
                    Platform.exit();
                    break;
                }

                if (message.isEmpty()){
                    Thread.sleep(100);
                    continue;
                }

                var parts = message.split(";");

                var type = parts[0];

                if (type.equals(MessageType.PrivateMessage.toString())) {
                    var sender = parts[1];
                    var receiver = parts[2];
                    var payload = parts[3];

                    Mediator.savePrivateMessage(sender, payload, false);
                }

                if (type.equals(MessageType.GroupMessage.toString())) {
                    var sender = parts[1];
                    var receiver = parts[2];
                    var payload = parts[3];

                    Mediator.saveGroupMessage(sender, payload, false);
                }

                if (type.equals(MessageType.ClientConnected.toString())) {
                    var clientName = parts[1];
                    Mediator.addClient(clientName);
                }

                if (type.equals(MessageType.ClientDisconnected.toString())) {
                    var clientName = parts[1];
                    Mediator.removeClient(clientName);
                }

                if (type.equals(MessageType.ClientNameGranted.toString())) {
                    var clientName = parts[1];
                    Mediator.setClientName(clientName);
                }

            } while (true);

        } catch (Exception ex) {
            Mediator.log("Error: " + ex.getMessage());
        }
    }
}
