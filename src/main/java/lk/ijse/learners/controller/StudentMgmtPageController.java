package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.StudentDTO;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StudentMgmtPageController {
    public AnchorPane ancStudent;
    public Button btnOpenStdForm;

    public TableView tblStudents;

    public void openStdForm(ActionEvent actionEvent) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADD_STUDENT_FORM.getPath()));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
