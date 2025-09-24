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
import lk.ijse.learners.bo.custom.*;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditAssignedInstructorsToLsnController implements Initializable {
    public AnchorPane ancChooseIns;
    public Button btnCancel;
    public ListView<String> selectedInstructors;
    public ListView<String> insList;
    public ImageView btnCloseForm;

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    public Button btnEditIns;
    EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.LESSON);
    SchedulingBO schedulingBO = (SchedulingBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SCHEDULE);

    private final List<String> selectedInsList = new ArrayList<>();
    private List<String> alreadyAssignedInsList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshSelectedInstructors();
        selectedInstructors.setOnMouseClicked(this::handleInsRemoveClick);

        insList.getItems().addAll(fetchAllInsNames());
        insList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null && !alreadyAssignedInsList.contains(newVal)) {
                selectedInsList.clear();
                selectedInstructors.getItems().clear();
                selectedInsList.add(newVal);
                selectedInstructors.getItems().add(newVal);
                selectedInstructors.refresh();
            }
        });
    }

    private void refreshSelectedInstructors() {
        alreadyAssignedInsList.clear();
        selectedInstructors.getItems().clear();
        alreadyAssignedInsList.add(enrollmentContext.getInstructorDTO().getName());
        selectedInstructors.getItems().addAll(alreadyAssignedInsList);
    }

    private void handleInsRemoveClick(MouseEvent event) {
        AlertUtil.setErrorAlert("Cant remove an instructor from a lesson, you should try replacing by clicking another instructor!");
    }

    private void removeInstructor(String name) {
        try {
            LessonDTO lessonDTO = enrollmentContext.getLessonDTO();
            Optional<InstructorDTO> instructorOpt = instructorBO.findByName(name);

            if (instructorOpt.isPresent()) {
                if (schedulingBO.removeInstructorFromLesson(lessonDTO)) {
                    RefreshContext.getInstance().setRefreshFlag(
                            RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST, true
                    );
                } else {
                    AlertUtil.setErrorAlert("Failed to remove instructor");
                }
            } else {
                AlertUtil.setErrorAlert("Instructor " + name + " not found for this lesson");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update lesson when removing instructor");
            e.printStackTrace();
        }
    }

    @FXML
    public void closeForm(Event onClick) {
        WindowManagerUtil.closeForm(ancChooseIns);
    }

    @FXML
    public void editIns(Event onClick) {
        try {
            if (selectedInsList.isEmpty()) {
                AlertUtil.setErrorAlert("Please select an instructor");
                return;
            }

            String selectedInsName = selectedInsList.getFirst();

            Optional<InstructorDTO> instructorOpt = instructorBO.findByName(selectedInsName);
            if (instructorOpt.isEmpty()) {
                AlertUtil.setErrorAlert("Instructor " + selectedInsName + " not found");
                return;
            }

            LessonDTO lessonDTO = enrollmentContext.getLessonDTO();
            lessonDTO.setInstructorId(instructorOpt.get().getInstructorId());

            if (lessonBO.update(lessonDTO)) {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST, true);
                enrollmentContext.clear();
                WindowManagerUtil.closeForm(ancChooseIns);
                AlertUtil.setInfoAlert("Successfully updated lesson instructor!");
            } else {
                AlertUtil.setErrorAlert("Failed to update lesson instructor");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error while updating lesson instructor");
            e.printStackTrace();
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
