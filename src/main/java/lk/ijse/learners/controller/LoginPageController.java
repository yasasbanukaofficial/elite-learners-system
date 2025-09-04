package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPageController {
    public AnchorPane ancLogin;

    public TextField usernameField;
    public PasswordField passwordField;

    public Label btnShowPassword;
    public Button btnLogin;

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setMaximized(true);
        stage.show();

        Stage window = (Stage) ancLogin.getScene().getWindow();
        window.close();
    }
}
