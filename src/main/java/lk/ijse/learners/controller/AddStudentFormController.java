package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.custom.PaymentBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
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

    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();

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
        WindowManagerUtil.closeForm(ancAddStudentForm);
    }

    @FXML
    public void closeStudentFormOnAction(ActionEvent actionEvent) {
        WindowManagerUtil.closeForm(ancAddStudentForm);
    }

    @FXML
    public void nextForm(ActionEvent actionEvent) {
        try {
            if (addStudent()) {
                WindowManagerUtil.closeForm(ancAddStudentForm);
                if (enrollmentContext.getPaymentDTO() == null) {
                    AlertUtil.setErrorAlert("Please add payment details before proceeding");
                    return;
                }
                if(enrollmentContext.getPaymentDTO().getPaymentId() == null || !enrollmentContext.getPaymentDTO().getPaymentId().equals(paymentBO.loadNextId())){
                    AlertUtil.setErrorAlert("Please add payment before proceeding");
                    return;
                }
                WindowManagerUtil.openForm(ViewPath.CHOOSE_COURSE_FORM.getPath(), false);
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
        WindowManagerUtil.openForm(ViewPath.ADD_PAYMENT_FORM.getPath(), false);
    }

    private boolean addStudent() {
        String sid = lblStdId.getText();
        String firstName = txtFName.getText();
        String lastName = txtLName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();

        if (validateStudentDetails(firstName, lastName, email, contact, address)){
            enrollmentContext.setStudentDTO(new StudentDTO(
                    sid,
                    firstName,
                    lastName,
                    Date.valueOf(dobPicker.getValue()),
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
    private boolean validateStudentDetails(String fName, String lName, String email, String contact, String address) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        String contactPattern = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        // Initial State of UI Components
        txtFName.setStyle(normalStyle);
        txtLName.setStyle(normalStyle);
        dobPicker.setStyle(normalStyle);
        txtEmail.setStyle(normalStyle);
        txtContact.setStyle(normalStyle);
        txtAddress.setStyle(normalStyle);

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

        if (dobPicker.getValue() == null){
            errorMsg.append("* You must include student date of birth!\n");
            dobPicker.setStyle(errorStyle);
            isValid = false;
        } else {
            LocalDate birthDate = dobPicker.getValue();
            int age = LocalDate.now().getYear() - birthDate.getYear();
            if (age < 18 || age > 60){
                errorMsg.append("* Student age must be between 18 and 60 years.\n");
                dobPicker.setStyle(errorStyle);
                isValid = false;
            }
        }
        if (!Auth.areRequiredFieldsFilled(email)){
            errorMsg.append("* Must include instructor's email!\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        } else if (!email.matches(emailPattern)){
            errorMsg.append("* Email should be a valid one (ex: john@mail.com) !\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(contact)){
            errorMsg.append("* You must include student's contact!\n");
            txtContact.setStyle(errorStyle);
            isValid = false;
        } else if (!contact.matches(contactPattern)){
            errorMsg.append("* Contact should be a valid one (ex: 0721231231 (LK), 4615555679 (US))!\n");
            txtContact.setStyle(errorStyle);
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(address)){
            errorMsg.append("* You must include students address!\n");
            txtAddress.setStyle(errorStyle);
            isValid = false;
        }

        if (!isValid){
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }
        return isValid;
    }

    private String loadNextId() {
        try {
            return studentBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }

}
