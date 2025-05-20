module com.example.t10 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql.rowset;


    opens com.example.t10 to javafx.fxml;
    exports com.example.t10;
}