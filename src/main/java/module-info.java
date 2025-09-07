module lk.ijse.learners {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;


    opens lk.ijse.learners.controller to javafx.fxml;
    exports lk.ijse.learners;
}