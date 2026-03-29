module com.example.oopmale {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens com.example.oopmale to javafx.fxml;
    exports com.example.oopmale;
}