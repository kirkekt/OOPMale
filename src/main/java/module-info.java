module com.example.oopmidagi {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens com.example.oopmidagi to javafx.fxml;
    exports com.example.oopmidagi;
}