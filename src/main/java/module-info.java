module com.example.t10 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.t10 to javafx.fxml;
    exports com.example.t10;
}