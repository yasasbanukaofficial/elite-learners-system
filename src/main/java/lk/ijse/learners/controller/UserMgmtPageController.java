package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMgmtPageController implements Initializable {

    public AnchorPane ancUser;
    public StackPane btnOpenStdForm;
    public TextField txtUserName;
    public TextField txtRole;
    public TextField txtEmail;
    public TextField txtAge;
    public TextField txtContact;
    public DatePicker userDob;
    public Label btnShowPassword;
    public PasswordField userPassword;
    public Button btnDeleteUser;
    public Button btnEdit;
    public ListView listUsers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void deleteUser(ActionEvent event) {
    }

    public void editUser(ActionEvent event) {
    }
}
