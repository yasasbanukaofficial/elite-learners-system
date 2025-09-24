package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.UserBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.entity.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {
    public AnchorPane ancLogin;

    public TextField usernameField;
    public PasswordField passwordField;
    public Button btnLogin;
    public TextField textField;
    public CheckBox showPassword;

    private UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textField.managedProperty().bind(showPassword.selectedProperty());
        textField.visibleProperty().bind(showPassword.selectedProperty());
        passwordField.managedProperty().bind(showPassword.selectedProperty().not());
        passwordField.visibleProperty().bind(showPassword.selectedProperty().not());
        textField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        if (validateLoginDetails(usernameField.getText(), passwordField.getText())) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ViewPath.MAIN.getPath()));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setMaximized(true);
            stage.show();

            Stage window = (Stage) ancLogin.getScene().getWindow();
            window.close();
        }
    }

    private boolean validateLoginDetails(String username, String password) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        if (username.isEmpty() || password.isEmpty()) {
            errorMsg.append("Username and password must be filled");
            usernameField.setStyle(errorStyle);
            passwordField.setStyle(errorStyle);
            textField.setStyle(errorStyle);
            isValid = false;
        } else if (!userBO.existsByUsername(username.trim())) {
            errorMsg.append("Username does not exist, Please try again");
            usernameField.setStyle(errorStyle);
            isValid = false;
        } else if (!BCrypt.checkpw(password, userBO.findByName(username.trim()).get().getPassword())) {
            errorMsg.append("Invalid password, Please try again");
            passwordField.setStyle(errorStyle);
            textField.setStyle(errorStyle);
            isValid = false;
        }

        if (!isValid) {
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }
    
        return isValid;
    }
}
