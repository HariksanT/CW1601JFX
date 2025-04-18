module com.iit.tutorials.cw1601jfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cw1601 to javafx.fxml;
    exports com.cw1601;
}