package hw.app.chatclient;

import hw.app.core.common.MessageType;
import hw.app.core.main.Mediator;
import hw.app.core.socket.ClientSocketInputStreamHandler;
import hw.app.core.socket.ClientSocketOutputStreamHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ChatClientController {

    public TextField hostTextbox;
    public TextField portTextBox;
    public Button connectButton;
    public ListView<String> clientListView;
    public TextArea messageTextArea;
    public TextArea systemConsoleTextArea;
    public TextArea userMessageTextArea;
    public Button sendMessageButton;
    public Label currentReceiverLabel;
    public Button switchToGroupChatButton;
    public Label clientNameLabel;

    Socket socket = null;

    public void onConnectButtonClick(ActionEvent actionEvent) throws InterruptedException {

        Mediator.systemConsoleTextArea = systemConsoleTextArea;
        Mediator.messageTextArea = messageTextArea;
        Mediator.clientNameLabel = clientNameLabel;
        Mediator.clientListView = clientListView;

        var host = hostTextbox.getText();
        var port = portTextBox.getText();

        if (host.isEmpty() || port.isEmpty()) {
            return;
        }

        ObservableList<String> connectedClientNames = FXCollections.observableArrayList();

        Mediator.connectedClientNames = connectedClientNames;

        clientListView.setItems(connectedClientNames);

        try {
            Mediator.log("Connecting to " + host + ":" + port);
            socket = new Socket(host, Integer.parseInt(port));
            Mediator.log("Connected to " + host + ":" + port);

            var clientSocketInputHandler = new ClientSocketInputStreamHandler(socket.getInputStream());
            var clientSocketOutputHandler = new ClientSocketOutputStreamHandler(socket.getOutputStream());

            clientSocketInputHandler.start();
            clientSocketOutputHandler.start();

            hostTextbox.setDisable(true);
            portTextBox.setDisable(true);
            connectButton.setDisable(true);

        } catch (IOException _) {

        }
    }

    public void onClientListViewClick(MouseEvent mouseEvent) {
        var selectedClientName = clientListView.getSelectionModel().getSelectedItem();

        if (selectedClientName != null) {
            Mediator.currentReceiver = selectedClientName;
            currentReceiverLabel.setText("PRIVATE CHAT [" + selectedClientName +"]");
            currentReceiverLabel.setTextFill(Paint.valueOf("#0000FF"));
            Mediator.populateMessages();
            switchToGroupChatButton.setDisable(false);
        }
    }

    public void onSwitchToGroupChatButtonClick(ActionEvent actionEvent) {
        if (!Objects.equals(Mediator.currentReceiver, "group")) {
            Mediator.currentReceiver = "group";
            Mediator.populateMessages();
            currentReceiverLabel.setText("GROUP CHAT");
            currentReceiverLabel.setTextFill(Paint.valueOf("#FF0000"));
            switchToGroupChatButton.setDisable(true);
        }
    }

    public void onSendMessageButtonClick(ActionEvent actionEvent) {

        var message = userMessageTextArea.getText();

        if (message == null || message.isEmpty()){
            return;
        }

        userMessageTextArea.clear();

        if (Mediator.currentReceiver.equals("group")) {
            Mediator.saveGroupMessage(Mediator.currentReceiver, message, true);
            Mediator.messages.add(MessageType.GroupMessage + ";" + Mediator.currentReceiver + ";" + message);
        } else {
            Mediator.savePrivateMessage(Mediator.currentReceiver, message, true);
            Mediator.messages.add(MessageType.PrivateMessage + ";" + Mediator.currentReceiver + ";" + message);
        }
    }
}