package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.EnrollmentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditEnrolledCoursesController implements Initializable {
    public AnchorPane ancChooseCourseForm;

    public Button btnEditCourses;
    public Button btnCancel;

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    public ListView<String> selectedCourses;
    public ListView<String> courseList;

    EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    EnrollmentBO enrollmentBO = (EnrollmentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ENROLLMENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);

    private final List<String> selectedCourseList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> alreadyEnrolledCourseList = new ArrayList<>();

        enrollmentContext.getCourseDTOList().forEach(courseDTO -> {
            alreadyEnrolledCourseList.add(courseDTO.getName());
        });
        selectedCourses.getItems().addAll(alreadyEnrolledCourseList);
        courseList.getItems().addAll(fetchAllCourseNames());
        courseList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            String selected = courseList.getSelectionModel().getSelectedItem();
            if (selected != null && !selectedCourseList.contains(selected)) {
                selectedCourseList.add(selected);
                selectedCourses.getItems().add(selected);
                selectedCourses.refresh();
            }
        });
    }

    @FXML
    public void closeForm(Event onClick) {
        WindowManagerUtil.closeForm(ancChooseCourseForm);
    }

    @FXML
    public void editCourses(Event onClick) {
        try {
            if(selectedCourseList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select at least one course");
                return;
            }

            enrollmentContext.setCourseDTOList(
                    entityDTOConverter.toCourseDTOList(
                            courseBO.fetchCourseListByName(selectedCourseList)
                    )
            );
            if (!enrollmentBO.updateEnrolledStudent()) {
                AlertUtil.setErrorAlert("Failed to update enrolled courses");
            } else {
                enrollmentContext.clear();
                WindowManagerUtil.closeForm(ancChooseCourseForm);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void closeStudentForm(MouseEvent mouseEvent) {
        WindowManagerUtil.closeForm(ancChooseCourseForm);
    }

    private List<String> fetchAllCourseNames() {
        try {
            List<CourseDTO> courseDTOList = courseBO.getAll();
            List<String> courseNames = new ArrayList<>();
            courseDTOList.forEach(course -> courseNames.add(course.getName()));
            return courseNames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
