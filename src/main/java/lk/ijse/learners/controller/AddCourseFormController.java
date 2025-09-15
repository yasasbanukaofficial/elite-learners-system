package lk.ijse.learners.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.exception.NotAvailableException;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddCourseFormController implements Initializable {
    public AnchorPane ancAddCourseForm;

    public VBox instructorSection;

    public Label lblCourseId;

    public TextField txtCName;
    public TextField txtDescription;
    public TextField txtCDuration;
    public TextField txtCFee;

    public Button btnCancel;
    public Button btnAddInstructor;
    public Button btnAddCourse;
    public ImageView btnCloseStdForm;

    public ListView <String> availableInstructors;
    public ListView <String> chosenInstructors;

    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);

    private List<String> selectedInstructors = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblCourseId.setText(loadNextId());
        txtCName.setText("Basic Drive");
        txtDescription.setText("Basic Drive Description");
        txtCDuration.setText("10");
        txtCFee.setText("100");

        try {
            List<String> instructors = fetchAvailableInstructors();
            if (instructors.isEmpty()) {
                Platform.runLater(() -> {
                    AlertUtil.setErrorAlert("Please add some instructors first!!");
                });
                btnAddCourse.setDisable(true);
                btnAddInstructor.setDisable(true);
                return;
            }
            availableInstructors.getItems().addAll(instructors);
        } catch (Exception e) {
            btnAddCourse.setDisable(true);
            btnAddInstructor.setDisable(true);
            AlertUtil.setErrorAlert("No instructors available. Please add instructors first.");
            return;
        }

        availableInstructors.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String selected = availableInstructors.getSelectionModel().getSelectedItem();
                if (selected != null && !selectedInstructors.contains(selected)) {
                    selectedInstructors.add(selected);
                    chosenInstructors.getItems().add(selected);
                }
            }
        });
    }

    @FXML
    public void closeCourseFormOnAction(MouseEvent mouseEvent) {
        closeForm();
    }

    private void closeForm() {
        Stage window = (Stage) ancAddCourseForm.getScene().getWindow();
        window.close();
    }


    @FXML
    public boolean addCourse() {
        String cid = lblCourseId.getText();
        String cName = txtCName.getText();
        String cDescription = txtDescription.getText();
        String cDuration = txtCDuration.getText();
        String cFee = txtCFee.getText();

        if (validateCourseDetails(cName, cDescription, cDuration, cFee)) {
            try {
                courseBO.save(new CourseDTO(
                        cid,
                        cName,
                        cDescription,
                        cDuration,
                        cFee,
                        instructorBO.fetchInstructorListByName(selectedInstructors),
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
                AlertUtil.setInfoAlert("Successfully added course!");
                return true;
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to add course!");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    // Utility methods
    private boolean validateCourseDetails(String cName, String cDescription, String cDuration, String cFee) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        if (!Auth.areRequiredFieldsFilled(cName, cDescription, cDuration, cFee)){
            errorMsg.append("Required fields are empty!\n");
            isValid = false;
        }

        if (Integer.parseInt(cFee) < 0){
            txtCFee.setStyle(errorStyle);
            errorMsg.append("Course fee must not be a negative value\n");
            isValid = false;
        }

        if (!isValid){
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }
        return isValid;
    }

    private void openForms(String path) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(path));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to form!");
            e.printStackTrace();
            return;
        }
    }

    private String loadNextId() {
        try {
            return courseBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }

    private List<String> fetchAvailableInstructors() throws Exception {
        List<String> instructors = instructorBO.getAllAvailableInstructors();
        if (instructors == null || instructors.isEmpty()) {
            return new ArrayList<>();
        }
        return instructors;
    }


    public void showAvailableInstructors(ActionEvent event) {
    }

    public void closeStudentForm(MouseEvent mouseEvent) {
    }
}
