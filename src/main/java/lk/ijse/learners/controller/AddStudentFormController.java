package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
        txtFName.setText("Student");
        txtLName.setText("Name");
        txtEmail.setText("y@mail.com");
        txtContact.setText("0123456789");
        txtAddress.setText("Address");
        dobPicker.setValue(LocalDate.now());
        lblPayId.setText("Pay ID: ");
    }

    @FXML
    public void closeStudentForm(Event onClick) {
        Stage window = (Stage) ancAddStudentForm.getScene().getWindow();
        window.close();
    }

    @FXML
    public void nextForm(ActionEvent actionEvent) {
        if (addStudent()) {
            closeStudentForm(actionEvent);
            openForms(ViewPath.CHOOSE_COURSE_FORM.getPath());
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

        String emailPattern = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";
        String contactPattern = " ^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$ ";

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        if (!Auth.areRequiredFieldsFilled(fName, lName, email, contact, address, dob)){
            errorMsg.append("Required fields are empty!\n");
            isValid = false;
        }

        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18 || age > 60){
            dobPicker.setStyle(errorStyle);
            errorMsg.append("Student age must be between 18 and 60 years.\n");
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
            return studentBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }

}
