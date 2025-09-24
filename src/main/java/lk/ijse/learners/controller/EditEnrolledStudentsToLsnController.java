package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.*;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.StudentDTO;
import lk.ijse.learners.dto.LessonDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditEnrolledStudentsToLsnController implements Initializable {
    @FXML
    private AnchorPane ancChooseStudentsForm;
    @FXML
    private Button btnCancel;
    @FXML
    private ListView<String> selectedStudents;
    @FXML
    private ListView<String> stdList;
    @FXML
    private ImageView btnCloseForm;
    @FXML
    private Button btnEditStudents;

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    private final SchedulingBO schedulingBO = (SchedulingBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SCHEDULE);

    private final List<String> selectedStdList = new ArrayList<>();
    private final List<String> alreadyEnrolledStdList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshSelectedStudents();
        selectedStudents.setOnMouseClicked(this::handleStdRemoveClick);

        List<String> studentNames = fetchAllStudentNames();
        stdList.getItems().addAll(studentNames);
        stdList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !alreadyEnrolledStdList.contains(newVal)) {
                selectedStdList.clear();
                selectedStudents.getItems().clear();
                selectedStdList.add(newVal);
                selectedStudents.getItems().add(newVal);
                selectedStudents.refresh();
            }
        });
    }

    private void refreshSelectedStudents() {
        alreadyEnrolledStdList.clear();
        selectedStudents.getItems().clear();
        if (enrollmentContext.getStudentDTO() != null) {
            StudentDTO student = enrollmentContext.getStudentDTO();
            alreadyEnrolledStdList.add(student.getFirstName() + " " + student.getLastName());
            selectedStudents.getItems().addAll(alreadyEnrolledStdList);
        }
    }

    private void handleStdRemoveClick(MouseEvent event) {
        AlertUtil.setErrorAlert("Can't remove a student from a lesson, you should try replacing by clicking another student!");
    }

    @FXML
    private void closeForm(ActionEvent event) {
        WindowManagerUtil.closeForm(ancChooseStudentsForm);
    }

    @FXML
    public void editStd(ActionEvent event) {
        try {
            if (selectedStdList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select a student");
                return;
            }

            String selectedStdName = selectedStdList.get(0);
            String[] nameParts = selectedStdName.split(" ", 2);
            if (nameParts.length != 2) {
                AlertUtil.setErrorAlert("Invalid student name format");
                return;
            }

            List<StudentDTO> students = studentBO.getAll();
            Optional<StudentDTO> studentOpt = students.stream()
                    .filter(s -> (s.getFirstName() + " " + s.getLastName()).equals(selectedStdName))
                    .findFirst();

            if (studentOpt.isEmpty()) {
                AlertUtil.setErrorAlert("Student " + selectedStdName + " not found");
                return;
            }

            LessonDTO lessonDTO = enrollmentContext.getLessonDTO();
            StudentDTO studentDTO = studentOpt.get();

            if (schedulingBO.editEnrolledStudent(lessonDTO, studentDTO.getStudentId())) {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST, true);
                enrollmentContext.clear();
                WindowManagerUtil.closeForm(ancChooseStudentsForm);
                AlertUtil.setInfoAlert("Successfully enrolled student to lesson!");
            } else {
                AlertUtil.setErrorAlert("Failed to enroll student to lesson");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error while enrolling student to lesson");
            if (Boolean.getBoolean("debug")) {
                System.err.println("Error in editStd: " + e.getMessage());
            }
        }
    }

    private List<String> fetchAllStudentNames() {
        try {
            List<StudentDTO> stdDTOList = studentBO.getAll();
            List<String> stdNames = new ArrayList<>();
            for (StudentDTO std : stdDTOList) {
                stdNames.add(std.getFirstName() + " " + std.getLastName());
            }
            return stdNames;
        } catch (Exception e) {
            System.err.println("Error fetching student names: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @FXML
    private void closeStudentForm(MouseEvent ignored) {
        WindowManagerUtil.closeForm(ancChooseStudentsForm);
    }
}
