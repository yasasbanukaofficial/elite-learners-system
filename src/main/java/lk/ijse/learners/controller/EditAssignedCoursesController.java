package lk.ijse.learners.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.EnrollmentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditAssignedCoursesController implements Initializable {
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
    private List<String> alreadyEnrolledCourseList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        enrollmentContext.getCourseDTOList().forEach(courseDTO -> {
            alreadyEnrolledCourseList.add(courseDTO.getName());
        });
        selectedCourses.getItems().addAll(alreadyEnrolledCourseList);
        selectedCourses.setOnMouseClicked(this::handleCourseRemoveClick);

        courseList.getItems().addAll(fetchAllCourseNames());
        courseList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            String selected = courseList.getSelectionModel().getSelectedItem();
            if (selected != null && !alreadyEnrolledCourseList.contains(selected)) {
                selectedCourseList.add(selected);
                selectedCourses.getItems().add(selected);
                selectedCourses.refresh();
            }
        });
    }

    private void handleCourseRemoveClick(MouseEvent event) {
        String selectedCourse = selectedCourses.getSelectionModel().getSelectedItem();

        if (selectedCourse != null) {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to remove instructor from " + selectedCourse + " course?")) {
                removeCourse(selectedCourse);
                selectedCourses.getItems().remove(selectedCourse);
                selectedCourses.getSelectionModel().clearSelection();
            }
        }
    }

    private void removeCourse(String courseName) {
        try {
            Optional<CourseDTO> courseDTO = courseBO.findByName(courseName);
            if (courseDTO.isPresent()) {
                CourseDTO course = courseDTO.get();
                course.getInstructors().removeIf(ins -> ins.getInstructorId().equals(enrollmentContext.getInstructorDTO().getInstructorId()));
                courseBO.update(course);
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST, true);
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update course when removing instructor");
            e.printStackTrace();
        }
    }

    @FXML
    public void closeForm(Event onClick) {
        WindowManagerUtil.closeForm(ancChooseCourseForm);
    }

    @FXML
    public void editCourses(Event onClick) {
        try {
            if (selectedCourseList.isEmpty() && alreadyEnrolledCourseList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select at least one course");
                return;
            }
            enrollmentContext.setCourseDTOList(courseBO.fetchCourseListByName(selectedCourseList));
            if (!enrollmentBO.updateEnrolledInstructors()) {
                AlertUtil.setErrorAlert("Failed to update enrolled courses");
            } else {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST, true);
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
