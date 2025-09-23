package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.EnrollmentBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditEnrolledStudentsController implements Initializable {
    public AnchorPane ancChooseCourseForm;
    public Button btnCancel;
    public ListView<String> selectedStudents;
    public ListView<String> stdList;
    public Button btnEditStudents;

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    public ImageView btnCloseForm;
    EnrollmentBO enrollmentBO = (EnrollmentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ENROLLMENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    private final List<String> selectedStdList = new ArrayList<>();
    private List<String> alreadyEnrolledStdList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshSelectedStudents();
        selectedStudents.setOnMouseClicked(this::handleStdRemoveClick);

        stdList.getItems().addAll(fetchAllStdNames());
        stdList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !alreadyEnrolledStdList.contains(newVal)) {
                selectedStdList.add(newVal);
                selectedStudents.getItems().add(newVal);
                selectedStudents.refresh();
            }
        });
    }

    private void refreshSelectedStudents() {
        alreadyEnrolledStdList.clear();
        selectedStudents.getItems().clear();

        enrollmentContext.getCourseDTO().getStudents().forEach(studentDTO -> {
            if (studentDTO != null) {
                alreadyEnrolledStdList.add(studentDTO.getFirstName() + " " + studentDTO.getLastName());
            }
        });

        selectedStudents.getItems().addAll(alreadyEnrolledStdList);
    }


    private void handleStdRemoveClick(MouseEvent event) {
        String selectStd = selectedStudents.getSelectionModel().getSelectedItem();
        if (selectStd != null) {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to remove student " + selectStd + " from the course?")) {
                String[] parts = selectStd.split(" ", 2);
                if (parts.length == 2) {
                    removeStd(parts[0], parts[1]);
                    refreshSelectedStudents();
                } else {
                    AlertUtil.setErrorAlert("Invalid student name format: " + selectStd);
                }
                selectedStudents.getSelectionModel().clearSelection();
            }
        }
    }

    private void removeStd(String fName, String lName) {
        try {
            CourseDTO course = enrollmentContext.getCourseDTO();
            Optional<StudentDTO> studentOpt = course.getStudents().stream()
                    .filter(std -> std.getFirstName().trim().equalsIgnoreCase(fName.trim())
                            && std.getLastName().trim().equalsIgnoreCase(lName.trim()))
                    .findFirst();
            if (studentOpt.isPresent()) {
                String studentId = studentOpt.get().getStudentId();
                course.getStudents().removeIf(std -> std.getStudentId().equals(studentId));
                courseBO.update(course);
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST, true);
            } else {
                AlertUtil.setErrorAlert("Student " + fName + " " + lName + " not found in course list");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update course when removing student");
            e.printStackTrace();
        }
    }

    @FXML
    public void closeForm(Event onClick) {
        WindowManagerUtil.closeForm(ancChooseCourseForm);
    }

    @FXML
    public void editStd(ActionEvent actionEvent) {
        try {
            if (selectedStdList.isEmpty() && alreadyEnrolledStdList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select at least one student");
                return;
            }

            List<StudentDTO> newStudents = new ArrayList<>(enrollmentContext.getCourseDTO().getStudents());
            for (String name : selectedStdList) {
                String[] parts = name.trim().split("\\s+", 2); // <-- safe split
                if (parts.length == 2) {
                    studentBO.findByStudentName(parts[0], parts[1])
                            .ifPresent(newStudents::add);
                }
            }

            CourseDTO course = enrollmentContext.getCourseDTO();
            course.setStudents(newStudents);
            courseBO.update(course);

            if (!enrollmentBO.updateEnrolledStd()) {
                AlertUtil.setErrorAlert("Failed to update enrolled students");
            } else {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST, true);
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

    private List<String> fetchAllStdNames() {
        try {
            List<StudentDTO> stdDTOList = studentBO.getAll();
            List<String> stdNames = new ArrayList<>();
            stdDTOList.forEach(std -> stdNames.add(std.getFirstName() + " " + std.getLastName()));
            return stdNames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
