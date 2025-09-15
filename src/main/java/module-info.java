module lk.ijse.learners {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
//    requires lk.ijse.learners;


    opens lk.ijse.learners.config to jakarta.persistence;
    opens lk.ijse.learners.entity to org.hibernate.orm.core;

    opens lk.ijse.learners.controller to javafx.fxml;
    opens lk.ijse.learners.dto to javafx.base;
    exports lk.ijse.learners;
}