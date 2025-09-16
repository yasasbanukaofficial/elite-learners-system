package lk.ijse.learners.controller.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {
    private AlertUtil() {}
    public static void setErrorAlert(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(message);
        errorAlert.show();
    }

    public static void setInfoAlert(String message) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setHeaderText(message);
        infoAlert.show();
    }

    public static Optional<ButtonType> setConfirmationAlert(String message, String subMessage) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setHeaderText(message);
        confirmationAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        confirmationAlert.setContentText(subMessage);
        return confirmationAlert.showAndWait();
    }
}
