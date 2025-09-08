package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.StudentDTO;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ChooseCourseFormController implements Initializable {
    public AnchorPane ancChooseCourseForm;
    public Button btnAddStudent;

    public ImageView btnCloseStdForm;

    private StudentDTO studentDTO;
    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void initializeStudentForm(StudentDTO studentDTO) {
        this.studentDTO = studentDTO;
    }

    public void closeStudentForm(Event onClick) {
        Stage window = (Stage) ancChooseCourseForm.getScene().getWindow();
        window.close();
    }

    public void addStudent(ActionEvent actionEvent) {
        try {
             if (studentBO.save(studentDTO)){
                 AlertUtil.setInfoAlert("Student saved successfully!");
                 closeStudentForm(new ActionEvent());
             }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed when saving student");
            e.printStackTrace();
        }
    }

    public void viewCourses(ActionEvent actionEvent) throws IOException {
        System.out.println("Hi");
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ChooseCourseForm.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
