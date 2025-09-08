package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.StudentDTO;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
    public TextField txtPayID;

    public Label lblStdId;

    public DatePicker dobPicker;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblStdId.setText(loadNextId());
        txtFName.clear();
        txtLName.clear();
        txtEmail.clear();
        txtContact.clear();
        txtAddress.clear();
        dobPicker.setValue(null);
        txtPayID.clear();
    }

    public void closeStudentForm(Event onClick) {
        Stage window = (Stage) ancAddStudentForm.getScene().getWindow();
        window.close();
    }

    private boolean validateStudentDetails(String fName, String lName, String email, String contact, String address, String dob) {
        String emailPattern = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";
        String contactPattern = " ^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$ ";
        if (!Auth.areRequiredFieldsFilled(fName, lName, email, contact, address, dob)){
            AlertUtil.setErrorAlert("You must fill required fields (*)!");
            return false;
        }

        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18 || age > 60){
            AlertUtil.setErrorAlert("Student age must be between 18 and 60 years.");
            return false;
        }

        if (!email.matches(emailPattern) && contact.matches(contactPattern)){
            AlertUtil.setErrorAlert("Cannot mark a future assignment as overdue.");
            return false;
        }
        return true;
    }

    private String loadNextId() {
        try {
            return studentBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }

    public void viewCourses(ActionEvent actionEvent) throws IOException {
        String sid = lblStdId.getText();
        String firstName = txtFName.getText();
        String lastName = txtLName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();
        if (dobPicker.getValue() == null){
            AlertUtil.setErrorAlert("Student date of birth cannot be empty!");
            return;
        }
        Date dob = Date.valueOf(dobPicker.getValue());

        if (validateStudentDetails(firstName, lastName, email, contact, address, dob.toString())){
            closeStudentForm(actionEvent);
            return;
        }

        StudentDTO studentDTO = new StudentDTO(
                sid,
                firstName,
                lastName,
                dob,
                email,
                contact,
                address,
                null,
                null,
                null
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChooseCourseForm.fxml"));
        Parent parent = loader.load();

        ChooseCourseFormController controller = loader.getController();
        controller.initializeStudentForm(studentDTO);
        
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public void openPayForm(ActionEvent actionEvent) {
    }
}
