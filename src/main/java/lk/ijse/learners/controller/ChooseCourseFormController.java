package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.StudentDTO;

import java.net.URL;
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
}
