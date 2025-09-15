package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.InstructorDTO;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddInstructorFormController implements Initializable {
    public AnchorPane ancAddInstructorForm;

    public Label lblInsId;

    public TextField txtFName;
    public TextField txtEmail;
    public TextField txtContact;
    public TextField txtSpeciality;

    public ImageView btnCloseInsForm;
    public Button btnCancel;
    public Button btnNext;

    public DatePicker dobPicker;

    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblInsId.setText(loadNextId());
    }

    @FXML
    public void closeInsForm(MouseEvent mouseEvent) {
        Stage window = (Stage) ancAddInstructorForm.getScene().getWindow();
        window.close();
    }

    @FXML
    public boolean addInstructor() {
        String insId = lblInsId.getText();
        String name = txtFName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String speciality = txtSpeciality.getText();

        if (dobPicker.getValue() == null){
            AlertUtil.setErrorAlert("Instructor date of birth cannot be empty!");
            return false;
        }

        Date dob = Date.valueOf(dobPicker.getValue());

        if (validateInstructorDetails(name, email, contact, speciality, dob.toString())){
            try {
                instructorBO.save(new InstructorDTO(
                        insId,
                        name,
                        dob,
                        email,
                        contact,
                        speciality,
                        "available",
                        new ArrayList<>()
                ));
                AlertUtil.setInfoAlert("Successfully added instructor!");
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    // Utility methods
    private boolean validateInstructorDetails(String name, String email, String contact, String speciality, String dob) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";
        String contactPattern = " ^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$ ";

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        if (!Auth.areRequiredFieldsFilled(name, email, contact, speciality, dob)){
            errorMsg.append("Required fields are empty!\n");
            isValid = false;
        }

        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18){
            dobPicker.setStyle(errorStyle);
            errorMsg.append("Instructor age must be greater than 18.\n");
            isValid = false;
        }

        if (!email.matches(emailPattern) && !contact.matches(contactPattern)){
            txtEmail.setStyle(errorStyle);
            txtContact.setStyle(errorStyle);
            errorMsg.append("Invalid email or contact number.\n");
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
            return instructorBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }
}
