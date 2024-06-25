package hw.app.core.main;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Mediator {

    public static ObservableList<String> clientNames;
    public static TextArea messageTextArea;
    public static TextArea systemConsoleTextArea;
    public static final PriorityQueue<String> messages = new PriorityQueue<>();
    public static Dictionary<String, ArrayList<String>> messagesDictionary = new Hashtable<>();
    public static ArrayList<String> groupMessages = new ArrayList<>();
    public static String currentReceiver = "group";
    public static ObservableList<Client> connectedClientNames;
    public static Label clientNameLabel;
    public static ListView<Client> clientListView;
    public static Button switchToGroupChatButton;
    private static String _clientName;

    public static void log(String message) {
        System.out.println(message);
        systemConsoleTextArea.appendText(message + "\n");
    }

    public static void populateMessages(){
        messageTextArea.clear();
        if (Objects.equals(currentReceiver, "group")){

            Platform.runLater(() -> {
                for (var message : groupMessages) {
                    messageTextArea.appendText(message + "\n");
                }
            });
        }
        else {
            var messages = messagesDictionary.get(currentReceiver);
            if (messages != null){
                Platform.runLater(() -> {
                    for (var message : messages) {
                        messageTextArea.appendText(message + "\n");
                    }
                });
            }
        }
    }

    public static void savePrivateMessage(String sender, String payload, boolean isMyMessage) {
        var messages = messagesDictionary.get(sender);

        var m = sender + " [" + getCurrentTime()  + "]" + ":\n" + payload;

        if (isMyMessage){
            m = "**" + _clientName + "**"  + " [" + getCurrentTime()  + "]" + ":\n" + payload;
        }

        if (!isMyMessage && !currentReceiver.equals(sender)){


            connectedClientNames.stream()
                    .filter(x-> x.name.equals(sender))
                    .findFirst()
                    .ifPresent(x-> {
                        x.hasNewMessage = true;
                        Platform.runLater(() -> clientListView.refresh());
                    });
        }
        if (messages == null) {
            messages = new ArrayList<>();
            messagesDictionary.put(sender, messages);
            messages.add(m);
        }
        else {
            messages.add(m);
        }

        if (Objects.equals(currentReceiver, sender)){
            populateMessages();
        }
    }

    public static void saveGroupMessage(String sender, String payload, boolean isMyMessage) {
        var m = sender + " [" + getCurrentTime()  + "]" + ":\n" + payload;

        if (isMyMessage){
            m = "**" + _clientName + "**"  + " [" + getCurrentTime()  + "]" + ":\n" + payload;
        }
        
        if (!isMyMessage && !currentReceiver.equals("group")){
            Platform.runLater(() -> switchToGroupChatButton.setStyle("-fx-font-weight: bold;"));
        }

        groupMessages.add(m);

        if (Objects.equals(currentReceiver, "group")){
            populateMessages();
        }
    }

    public static String getCurrentTime(){
        var currentTime = LocalTime.now();
        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    public static void removeClient(String clientName) {
        Platform.runLater(()-> {
            connectedClientNames.stream().filter(x-> x.name.equals(clientName))
                    .findFirst()
                    .ifPresent(x-> connectedClientNames.remove(x));
        });
    }

    public static void addClient(String clientName) {
        Platform.runLater(()-> {
            var client = new Client();
            client.name = clientName;
            connectedClientNames.add(client);
        });
    }

    public static void setClientName(String clientName) {
        _clientName = clientName;
        Platform.runLater(()->clientNameLabel.setText("YOUR NAME: [" + clientName + "]"));
    }
}
