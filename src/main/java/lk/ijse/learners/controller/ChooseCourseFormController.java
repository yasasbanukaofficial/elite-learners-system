package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentUnitOfWork;
import lk.ijse.learners.bo.custom.EnrollmentBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.CourseDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseCourseFormController implements Initializable {
    public AnchorPane ancChooseCourseForm;

    public Button btnAddCourses;
    public Button btnCancel;

    public ImageView btnCloseStdForm;

    public ChoiceBox<String> cmbCourseOne;
    public ChoiceBox<String> cmbCourseTwo;
    public ChoiceBox<String> cmbCourseThree;
    public ChoiceBox<String> cmbCourseFour;
    public ChoiceBox<String> cmbCourseFive;

    private final EnrollmentUnitOfWork enrollmentUnitOfWork = EnrollmentUnitOfWork.getInstance();
    EnrollmentBO enrollmentBO = (EnrollmentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ENROLLMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> courseList1 = Arrays.asList("Mathematics", "Physics", "Chemistry");
        List<String> courseList2 = Arrays.asList("Biology", "Accounting", "Economics");
        List<String> courseList3 = Arrays.asList("English", "History", "ICT");
        List<String> courseList4 = Arrays.asList("Geography", "Business Studies", "Art");
        List<String> courseList5 = Arrays.asList("Music", "Drama", "Sports Science");

        cmbCourseOne.getItems().addAll(courseList1);
        cmbCourseTwo.getItems().addAll(courseList2);
        cmbCourseThree.getItems().addAll(courseList3);
        cmbCourseFour.getItems().addAll(courseList4);
        cmbCourseFive.getItems().addAll(courseList5);
    }

    public void closeForm(Event onClick) {
        enrollmentUnitOfWork.clear();
        Stage window = (Stage) ancChooseCourseForm.getScene().getWindow();
        window.close();
    }

    public void addCourses(Event onClick) {
        enrollmentUnitOfWork.setCourseDTO(
                new CourseDTO(
                        "COS-001",
                        "INS-001",
                        "Basic Drive",
                        "BAsic",
                        "JIdiao",
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        if (!enrollmentBO.enrollStudent()) {
            AlertUtil.setErrorAlert("Failed to enroll student");
            return;
        }
        closeForm(onClick);
    }

    public void closeStudentForm(MouseEvent mouseEvent) {
        closeForm(new ActionEvent());
    }
}
