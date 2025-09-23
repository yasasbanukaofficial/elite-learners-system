package lk.ijse.learners.controller;

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
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditAssignedInstructorsController implements Initializable {
    public AnchorPane ancChooseCourseForm;
    public Button btnCancel;
    public ListView<String> selectedInstructors;
    public ListView<String> insList;
    public Button btnEditInstructors;
    public ImageView btnCloseForm;

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    public Button btnEditIns;
    EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    EnrollmentBO enrollmentBO = (EnrollmentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ENROLLMENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);

    private final List<String> selectedInsList = new ArrayList<>();
    private List<String> alreadyAssignedInsList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshSelectedInstructors();
        selectedInstructors.setOnMouseClicked(this::handleInsRemoveClick);

        insList.getItems().addAll(fetchAllInsNames());
        insList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !alreadyAssignedInsList.contains(newVal)) {
                selectedInsList.add(newVal);
                selectedInstructors.getItems().add(newVal);
                selectedInstructors.refresh();
            }
        });
    }

    private void refreshSelectedInstructors() {
        alreadyAssignedInsList.clear();
        selectedInstructors.getItems().clear();
        enrollmentContext.getCourseDTO().getInstructors().forEach(instructorDTO ->
            alreadyAssignedInsList.add(instructorDTO.getName())
        );
        selectedInstructors.getItems().addAll(alreadyAssignedInsList);
    }

    private void handleInsRemoveClick(MouseEvent event) {
        String selectedIns = selectedInstructors.getSelectionModel().getSelectedItem();
        if (selectedIns != null) {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to remove instructor " + selectedIns + " from the course?")) {
                removeInstructor(selectedIns);
                refreshSelectedInstructors();
                selectedInstructors.getSelectionModel().clearSelection();
            }
        }
    }

    private void removeInstructor(String name) {
        try {
            CourseDTO course = enrollmentContext.getCourseDTO();
            Optional<InstructorDTO> instructorOpt = course.getInstructors().stream()
                    .filter(ins -> ins.getName().equals(name))
                    .findFirst();
            if (instructorOpt.isPresent()) {
                String instructorId = instructorOpt.get().getInstructorId();
                course.getInstructors().removeIf(ins -> ins.getInstructorId().equals(instructorId));
                courseBO.update(course);
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST, true);
            } else {
                AlertUtil.setErrorAlert("Instructor " + name + " not found in course list");
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
    public void editIns(Event onClick) {
        try {
            if (selectedInsList.isEmpty() && alreadyAssignedInsList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select at least one instructor");
                return;
            }
            List<InstructorDTO> newInstructors = new ArrayList<>(enrollmentContext.getCourseDTO().getInstructors());
            for (String name : selectedInsList) {
                instructorBO.findByName(name).ifPresent(newInstructors::add);
            }
            CourseDTO course = enrollmentContext.getCourseDTO();
            course.setInstructors(newInstructors);
            courseBO.update(course);
            if (!enrollmentBO.updateEnrolledInstructors()) {
                AlertUtil.setErrorAlert("Failed to update assigned instructors");
            } else {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST, true);
                enrollmentContext.clear();
                WindowManagerUtil.closeForm(ancChooseCourseForm);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> fetchAllInsNames() {
        try {
            List<InstructorDTO> insDTOList = instructorBO.getAll();
            List<String> insNames = new ArrayList<>();
            insDTOList.forEach(ins -> insNames.add(ins.getName()));
            return insNames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeStudentForm(MouseEvent mouseEvent) {
    }
}
