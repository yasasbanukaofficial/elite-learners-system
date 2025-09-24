package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.SchedulingBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.LessonDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditEnrolledCoursesToLsnController implements Initializable {
    @FXML
    private AnchorPane ancChooseCourseForm;
    @FXML
    private ListView<String> selectedCourses;
    @FXML
    private ListView<String> courseList;
    @FXML
    private ImageView btnCloseStdForm1;

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private final CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    private final SchedulingBO schedulingBO = (SchedulingBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SCHEDULE);

    private final List<String> selectedCourseList = new ArrayList<>();
    private final List<String> alreadyEnrolledCourseList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshSelectedCourses();
        selectedCourses.setOnMouseClicked(this::handleCourseRemoveClick);

        List<String> courseNames = fetchAllCourseNames();
        courseList.getItems().addAll(courseNames);
        courseList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !alreadyEnrolledCourseList.contains(newVal)) {
                selectedCourseList.clear();
                selectedCourses.getItems().clear();
                selectedCourseList.add(newVal);
                selectedCourses.getItems().add(newVal);
                selectedCourses.refresh();
            }
        });
    }

    private void refreshSelectedCourses() {
        alreadyEnrolledCourseList.clear();
        selectedCourses.getItems().clear();
        if (enrollmentContext.getCourseDTO() != null) {
            CourseDTO course = enrollmentContext.getCourseDTO();
            alreadyEnrolledCourseList.add(course.getName());
            selectedCourses.getItems().addAll(alreadyEnrolledCourseList);
        }
    }

    private void handleCourseRemoveClick(MouseEvent event) {
        AlertUtil.setErrorAlert("Can't remove a course from a lesson, you should try replacing by clicking another course!");
    }

    @FXML
    private void closeForm(ActionEvent event) {
        WindowManagerUtil.closeForm(ancChooseCourseForm);
    }

    @FXML
    public void editCourses(ActionEvent event) {
        try {
            if (selectedCourseList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select a course");
                return;
            }

            String selectedCourseName = selectedCourseList.getFirst();
            List<CourseDTO> courses = courseBO.getAll();
            Optional<CourseDTO> courseOpt = courses.stream()
                    .filter(c -> c.getName().equals(selectedCourseName))
                    .findFirst();

            if (courseOpt.isEmpty()) {
                AlertUtil.setErrorAlert("Course " + selectedCourseName + " not found");
                return;
            }

            LessonDTO lessonDTO = enrollmentContext.getLessonDTO();
            CourseDTO courseDTO = courseOpt.get();
            lessonDTO.setCourseId(courseDTO.getCourseId());

            if (schedulingBO.updateScheduleLesson(lessonDTO)) {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST, true);
                enrollmentContext.clear();
                WindowManagerUtil.closeForm(ancChooseCourseForm);
                AlertUtil.setInfoAlert("Successfully enrolled course to lesson!");
            } else {
                AlertUtil.setErrorAlert("Failed to enroll course to lesson");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error while enrolling course to lesson");
            if (Boolean.getBoolean("debug")) {
                System.err.println("Error in editCourses: " + e.getMessage());
            }
        }
    }

    private List<String> fetchAllCourseNames() {
        try {
            List<CourseDTO> courseDTOList = courseBO.getAll();
            List<String> courseNames = new ArrayList<>();
            for (CourseDTO course : courseDTOList) {
                courseNames.add(course.getName());
            }
            return courseNames;
        } catch (Exception e) {
            System.err.println("Error fetching course names: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @FXML
    private void closeCourseForm(MouseEvent ignored) {
        WindowManagerUtil.closeForm(ancChooseCourseForm);
    }
}
