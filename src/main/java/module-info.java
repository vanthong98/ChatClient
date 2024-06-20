module com.homework.chatclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.homework.chatclient to javafx.fxml;
    exports com.homework.chatclient;
}