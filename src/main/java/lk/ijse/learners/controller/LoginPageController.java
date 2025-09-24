package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {
    public AnchorPane ancLogin;

    public TextField usernameField;
    public PasswordField passwordField;
    public Button btnLogin;
    public TextField textField;
    public CheckBox showPassword;

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
        if (usernameField.getText().equals("root") && passwordField.getText().equals("root")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ViewPath.MAIN.getPath()));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setMaximized(true);
            stage.show();

            Stage window = (Stage) ancLogin.getScene().getWindow();
            window.close();
        } else {
            AlertUtil.setErrorAlert("Invalid username or password");
        }
    }
}
