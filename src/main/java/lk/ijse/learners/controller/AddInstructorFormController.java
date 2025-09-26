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
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
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
    public Button btnAddInstructor;

    public DatePicker dobPicker;

    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblInsId.setText(loadNextId());
    }

    @FXML
    public void closeInsForm(MouseEvent mouseEvent) {
        WindowManagerUtil.closeForm(ancAddInstructorForm);
    }

    @FXML
    public void closeInsFormOnAction(ActionEvent actionEvent) {
        WindowManagerUtil.closeForm(ancAddInstructorForm);
    }

    @FXML
    public boolean addInstructor() {
        String insId = lblInsId.getText();
        String name = txtFName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String speciality = txtSpeciality.getText();

        if (validateInstructorDetails(name, email, contact, speciality)){
            try {
                instructorBO.save(new InstructorDTO(
                        insId,
                        name,
                        Date.valueOf(dobPicker.getValue()),
                        email,
                        contact,
                        speciality,
                        "available",
                        new ArrayList<>()
                ));
                AlertUtil.setInfoAlert("Successfully added instructor!");
                WindowManagerUtil.closeForm(ancAddInstructorForm);
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTORS, true);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    // Utility methods
    private boolean validateInstructorDetails(String name, String email, String contact, String speciality) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        String contactPattern = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        // Initial State of UI Components
        txtFName.setStyle(normalStyle);
        txtEmail.setStyle(normalStyle);
        txtContact.setStyle(normalStyle);
        txtSpeciality.setStyle(normalStyle);
        dobPicker.setStyle(normalStyle);

        if (!Auth.areRequiredFieldsFilled(name)){
            errorMsg.append("* Instructor's Name must not be empty\n");
            txtFName.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(email)){
            errorMsg.append("* Email must not be empty!\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        } else if (!email.matches(emailPattern)){
            errorMsg.append("* Email should be a valid one (ex: john@mail.com) !\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(contact)){
            errorMsg.append("* You must include instructor's contact!\n");
            txtContact.setStyle(errorStyle);
            isValid = false;
        } else if (!contact.matches(contactPattern)){
            errorMsg.append("* Contact should be a valid one (ex: 0721231231 (LK), 4615555679 (US))!\n");
            txtContact.setStyle(errorStyle);
            isValid = false;
        }

        if (dobPicker.getValue() == null){
            errorMsg.append("* You must include student date of birth!\n");
            dobPicker.setStyle(errorStyle);
            isValid = false;
        } else {
            LocalDate birthDate = LocalDate.parse(dobPicker.getValue().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            if (age < 18 || age > 60){
                errorMsg.append("* Instructor's age must be between 18 and 60 years.\n");
                dobPicker.setStyle(errorStyle);
                isValid = false;
            }
        }

        if (!Auth.areRequiredFieldsFilled(speciality)){
            errorMsg.append("* You must include instructor's speciality!\n");
            txtSpeciality.setStyle(errorStyle);
            isValid = false;
        }

        if (!isValid){
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }
        return isValid;
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
