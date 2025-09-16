package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentUnitOfWork;
import lk.ijse.learners.bo.custom.PaymentBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.StudentDTO;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddStudentFormController implements Initializable {
    public AnchorPane ancAddStudentForm;

    public ImageView btnCloseStdForm;
    public Button btnCancel;
    public Button btnAddPay;
    public Button btnNext;

    public TextField txtFName;
    public TextField txtLName;
    public TextField txtEmail;
    public TextField txtContact;
    public TextField txtAddress;

    public Label lblPayId;
    public Label lblStdId;

    public DatePicker dobPicker;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);

    private final EnrollmentUnitOfWork enrollmentUnitOfWork = EnrollmentUnitOfWork.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblStdId.setText(loadNextId());
//        txtFName.setText("DDDD");
//        txtLName.setText("Name");
//        txtEmail.setText("y@msaail.com");
//        txtContact.setText("1023456789");
//        txtAddress.setText("Address");
//        dobPicker.setValue(LocalDate.now());
        lblPayId.setText("Pay ID: ");
    }

    @FXML
    public void closeStudentForm(MouseEvent actionEvent) {
        closeForm();
    }

    @FXML
    public void closeStudentFormOnAction(ActionEvent actionEvent) {
        closeForm();
    }

    @FXML
    public void nextForm(ActionEvent actionEvent) {
        try {
            if (addStudent()) {
                closeForm();
                if (enrollmentUnitOfWork.getPaymentDTO() == null) {
                    AlertUtil.setErrorAlert("Please add payment details before proceeding");
                    return;
                }
                if(enrollmentUnitOfWork.getPaymentDTO().getPaymentId() == null || !enrollmentUnitOfWork.getPaymentDTO().getPaymentId().equals(paymentBO.loadNextId())){
                    AlertUtil.setErrorAlert("Please add payment before proceeding");
                    return;
                }
                openForms(ViewPath.CHOOSE_COURSE_FORM.getPath());
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to check for paymentDto");
            throw new RuntimeException("Error when checking for paymentDto", e);
        }
    }

    @FXML
    public void openPayForm(ActionEvent actionEvent) {
        try {
            lblPayId.setText("Pay ID: " + paymentBO.loadNextId());
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to set pay id");
            e.printStackTrace();
        }
        openForms(ViewPath.ADD_PAYMENT_FORM.getPath());
    }

    private boolean addStudent() {
        String sid = lblStdId.getText();
        String firstName = txtFName.getText();
        String lastName = txtLName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();

        if (dobPicker.getValue() == null){
            AlertUtil.setErrorAlert("Student date of birth cannot be empty!");
            return false;
        }

        Date dob = Date.valueOf(dobPicker.getValue());

        if (validateStudentDetails(firstName, lastName, email, contact, address, dob.toString())){
            enrollmentUnitOfWork.setStudentDTO(new StudentDTO(
                    sid,
                    firstName,
                    lastName,
                    dob,
                    email,
                    contact,
                    address,
                    new ArrayList<>(),
                    new ArrayList<>()
            ));
            return true;
        }
        return false;
    }


    // Utility methods
    private boolean validateStudentDetails(String fName, String lName, String email, String contact, String address, String dob) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        String contactPattern = "^(\\d{3}[- .]?){2}\\d{4}$";

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        // Initial State of UI Components
        txtFName.setStyle(normalStyle);
        txtLName.setStyle(normalStyle);
        dobPicker.setStyle(normalStyle);
        txtEmail.setStyle(normalStyle);
        txtContact.setStyle(normalStyle);
        txtAddress.setStyle(normalStyle);

        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (!Auth.areRequiredFieldsFilled(fName)){
            errorMsg.append("* First Name must not be empty\n");
            txtFName.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(lName)){
            errorMsg.append("* Last Name must not be empty\n");
            txtLName.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(dob)){
            errorMsg.append("* You must include student age!\n");
            dobPicker.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(email) || !email.matches(emailPattern)){
            errorMsg.append("* Email must not be empty and it should be a valid one!\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(contact) || !contact.matches(contactPattern)){
            errorMsg.append("* You must provide student contact and it should be in correct format!\n");
            txtContact.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(address)){
            errorMsg.append("* You must include students address!\n");
            txtAddress.setStyle(errorStyle);
            isValid = false;
        }

        if (age < 18 || age > 60){
            dobPicker.setStyle(errorStyle);
            errorMsg.append("Student age must be between 18 and 60 years.\n");
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
            return studentBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }

    private void closeForm() {
        Stage window = (Stage) ancAddStudentForm.getScene().getWindow();
        window.close();
    }
}
