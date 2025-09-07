package lk.ijse.learners.controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.PaymentDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class StudentMgmtPageController extends Application {
    public AnchorPane ancStudent;
    public AnchorPane ancAddStudentForm;

    public ImageView btnCloseStdForm;
    public Button btnAddStudent;
    public Button btnOpenStdForm;

    public TextField txtFName;
    public TextField txtLName;
    public TextField txtEmail;
    public TextField txtContact;
    public TextField txtAddress;

    public TableView tblStudents;
    public DatePicker dobPicker;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    public void openStdForm(ActionEvent actionEvent) throws Exception {
        start(new Stage());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/AddStudentForm.fxml"));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public void closeStudentForm(Event onClick) {
        Stage window = (Stage) ancAddStudentForm.getScene().getWindow();
        window.close();
    }

    public void addStudent(ActionEvent actionEvent) {
        String firstName = txtFName.getText();
        String lastName = txtLName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();
        Date dob = Date.valueOf(dobPicker.getValue());

        StudentDTO studentDTO = new StudentDTO(
                "STD-001",
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

        if (validateStudentDetails(firstName, lastName, email, contact, address, dob.toString())){
            try {
                if (studentBO.save(studentDTO)){
                    AlertUtil.setInfoAlert("Student saved successfully!");
                    txtFName.clear();
                    txtLName.clear();
                    txtEmail.clear();
                    txtContact.clear();
                    txtAddress.clear();
                    dobPicker.setValue(null);
                    closeStudentForm(new ActionEvent());
                }
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed when saving student");
                e.printStackTrace();
            }
        }

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
}
