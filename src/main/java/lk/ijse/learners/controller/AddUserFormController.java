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
import org.springframework.security.crypto.bcrypt.BCrypt;

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
    public CheckBox cbAdmin;
    public CheckBox cbUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserId.setText(loadNextId());

        // Show/hide password fields
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

        // Ensure only one role checkbox is selected
        cbAdmin.setOnAction(e -> {
            if (cbAdmin.isSelected()) cbUser.setSelected(false);
        });
        cbUser.setOnAction(e -> {
            if (cbUser.isSelected()) cbAdmin.setSelected(false);
        });
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

        if (!cbAdmin.isSelected() && !cbUser.isSelected()) {
            AlertUtil.setErrorAlert("Please select a role (Admin or User)!");
            return;
        }

        String role = cbAdmin.isSelected() ? "ADMIN" : "USER";
        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt(10));

        if (validateUserDetails(name, age, email, contact, password, confirmPassword)) {
            try {
                boolean success = userBO.save(new UserDTO(
                        lblUserId.getText(),
                        name,
                        age,
                        email,
                        hashedPw,
                        contact,
                        role
                ));
                if (!success) {
                    AlertUtil.setErrorAlert("Failed to add user!");
                    return;
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
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        String contactPattern = "^\\d{10}$";

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        if (!Auth.areRequiredFieldsFilled(name)) {
            errorMsg.append("* Name must not be empty\n");
            txtUserName.setStyle(errorStyle);
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(age)) {
            errorMsg.append("* Age must not be empty\n");
            txtAge.setStyle(errorStyle);
            isValid = false;
        } else {
            try {
                int ageValue = Integer.parseInt(age);
                if (ageValue < 18 || ageValue > 65) {
                    errorMsg.append("* Age must be between 18 and 65\n");
                    txtAge.setStyle(errorStyle);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMsg.append("* Invalid age format\n");
                txtAge.setStyle(errorStyle);
                isValid = false;
            }
        }

        if (!Auth.areRequiredFieldsFilled(email)) {
            errorMsg.append("* Email must not be empty\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        } else if (!email.matches(emailPattern)) {
            errorMsg.append("* Invalid email format\n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(contact)) {
            errorMsg.append("* Contact number must not be empty\n");
            txtContactNumber.setStyle(errorStyle);
            isValid = false;
        } else if (!contact.matches(contactPattern)) {
            errorMsg.append("* Contact number should be 10 digits\n");
            txtContactNumber.setStyle(errorStyle);
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(password, confirmPassword)) {
            errorMsg.append("* Password fields must not be empty\n");
            passwordField.setStyle(errorStyle);
            confirmpasswordfield.setStyle(errorStyle);
            textField.setStyle(errorStyle);
            confirmTxtField.setStyle(errorStyle);
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            errorMsg.append("* Passwords do not match\n");
            passwordField.setStyle(errorStyle);
            confirmpasswordfield.setStyle(errorStyle);
            textField.setStyle(errorStyle);
            confirmTxtField.setStyle(errorStyle);
            isValid = false;
        }

        if (!isValid) {
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }

        return isValid;
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
