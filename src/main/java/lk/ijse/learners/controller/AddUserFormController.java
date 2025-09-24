package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.UserBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.UserDTO;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserFormController implements Initializable {
    public AnchorPane ancAddUserForm;
    public Label lblUserId;
    public ImageView btnCloseInsForm;
    public TextField txtUserName;
    public TextField txtAge;
    public TextField txtEmail;
    public TextField txtContactNumber;
    public Button btnCancel;
    public Button btnAddUser;
    public PasswordField passwordField;
    public TextField textField;   
    public CheckBox showPassword; 

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    public PasswordField confirmpasswordfield;
    public TextField confirmTxtField;
    public CheckBox showConfirmPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserId.setText(loadNextId());

        textField.managedProperty().bind(showPassword.selectedProperty());
        textField.visibleProperty().bind(showPassword.selectedProperty());
        confirmTxtField.managedProperty().bind(showConfirmPassword.selectedProperty());
        confirmTxtField.visibleProperty().bind(showConfirmPassword.selectedProperty());

        passwordField.managedProperty().bind(showPassword.selectedProperty().not());
        passwordField.visibleProperty().bind(showPassword.selectedProperty().not());
        confirmpasswordfield.managedProperty().bind(showConfirmPassword.selectedProperty().not());
        confirmpasswordfield.visibleProperty().bind(showConfirmPassword.selectedProperty().not());

        textField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmTxtField.textProperty().bindBidirectional(confirmpasswordfield.textProperty());
    }

    public void closeInsForm(MouseEvent mouseEvent) {
        WindowManagerUtil.closeForm(ancAddUserForm);
    }

    public void closeInsFormOnAction(ActionEvent event) {
        WindowManagerUtil.closeForm(ancAddUserForm);
    }

    public void addUser(ActionEvent event) {
        String name = txtUserName.getText();
        String age = txtAge.getText();
        String email = txtEmail.getText();
        String contact = txtContactNumber.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : textField.getText();
        String confirmPassword = confirmpasswordfield.isVisible() ? confirmpasswordfield.getText() : confirmTxtField.getText();

        if (validateUserDetails(name, age, email, contact, password, confirmPassword)) {
            try {
                if (!userBO.save(new UserDTO(
                        lblUserId.getText(),
                        name,
                        age,
                        email,
                        password,
                        contact,
                        "USER"
                ))) {
                    AlertUtil.setErrorAlert("Failed to add user!");
                }
                AlertUtil.setInfoAlert("Successfully added user!");
                WindowManagerUtil.closeForm(ancAddUserForm);
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.USERS, true);
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to add user!");
                e.printStackTrace();
            }
        }
    }

    private boolean validateUserDetails(String name, String age, String email, String contact, String password, String confirmPassword) {
        if (!Auth.areRequiredFieldsFilled(name, age, email, contact, password, confirmPassword)) {
            AlertUtil.setErrorAlert("All fields must be filled!");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtil.setErrorAlert("Invalid email format!");
            return false;
        }

        if (!contact.matches("^\\d{10}$")) {
            AlertUtil.setErrorAlert("Contact number should be 10 digits!");
            return false;
        }

        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 18 || ageValue > 65) {
                AlertUtil.setErrorAlert("Age must be between 18 and 65!");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.setErrorAlert("Invalid age format!");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtil.setErrorAlert("Passwords do not match!");
            return false;
        }

        return true;
    }

    private String loadNextId() {
        try {
            return userBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }
}
