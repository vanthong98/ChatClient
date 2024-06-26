package hw.app.chatclient;

import hw.app.core.common.MessageType;
import hw.app.core.main.Client;
import hw.app.core.main.Mediator;
import hw.app.core.socket.ClientSocketInputStreamHandler;
import hw.app.core.socket.ClientSocketOutputStreamHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ChatClientController {

    public TextField hostTextbox;
    public TextField portTextBox;
    public Button connectButton;
    public ListView<Client> clientListView;
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
        Mediator.switchToGroupChatButton = switchToGroupChatButton;

        var host = hostTextbox.getText();
        var port = portTextBox.getText();

        if (host.isEmpty() || port.isEmpty()) {
            return;
        }

        ObservableList<Client> connectedClientNames = FXCollections.observableArrayList();

        Mediator.connectedClientNames = connectedClientNames;

        clientListView.setItems(connectedClientNames);

        SetListViewStyle();
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
        var selectedClient = clientListView.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            selectedClient.hasNewMessage = false;
            Mediator.currentReceiver = selectedClient.name;
            currentReceiverLabel.setText("PRIVATE CHAT [" + selectedClient.name +"]");
            currentReceiverLabel.setTextFill(Paint.valueOf("#0000FF"));
            Mediator.populateMessages();
            switchToGroupChatButton.setDisable(false);
            clientListView.refresh();
        }
    }

    public void onSwitchToGroupChatButtonClick(ActionEvent actionEvent) {
        if (!Objects.equals(Mediator.currentReceiver, "group")) {
            Mediator.currentReceiver = "group";
            Mediator.populateMessages();
            currentReceiverLabel.setText("GROUP CHAT");
            currentReceiverLabel.setTextFill(Paint.valueOf("#FF0000"));
            switchToGroupChatButton.setDisable(true);
            switchToGroupChatButton.setStyle("-fx-font-weight: normal;");
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

    private void SetListViewStyle(){
        clientListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Client> call(ListView<Client> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Client client, boolean empty) {
                        super.updateItem(client, empty);
                        if (empty || client == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(client.name);
                            setStyle(client.hasNewMessage ? "-fx-font-weight: bold;" : "-fx-font-weight: normal;");
                        }
                    }
                };
            }
        });
    }
}