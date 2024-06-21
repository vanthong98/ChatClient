package hw.app.chatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatClientApplication.class.getResource("application.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chat Client Application");
        stage.setScene(scene);

        stage.setResizable(false);

        stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            stage.setIconified(false);
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}