module com.homework.chatclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens hw.app.chatclient to javafx.fxml;
    exports hw.app.chatclient;
}