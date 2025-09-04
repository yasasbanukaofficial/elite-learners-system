module lk.ijse.learners {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.learners.controller to javafx.fxml;
    exports lk.ijse.learners;
}