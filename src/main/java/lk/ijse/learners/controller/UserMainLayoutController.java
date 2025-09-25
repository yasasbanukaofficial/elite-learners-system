package lk.ijse.learners.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lk.ijse.learners.controller.util.ViewPath;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMainLayoutController implements Initializable {
    public AnchorPane ancMain;
    public AnchorPane ancPages;

    public StackPane btnStudentMgmt;
    public StackPane btnLessonMgmt;
    public StackPane btnPaymentMgmt;
    public StackPane btnCoursesMgmt;
    public StackPane btnUserMgmt;
    public StackPane btnInstructorMgmt;
    public StackPane btnSettingsPage;
    public StackPane btnLogout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(ViewPath.STUDENT.getPath()));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitStudentPgOnAction(MouseEvent mouseEvent) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(ViewPath.STUDENT.getPath()));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitPaymentPgOnAction(MouseEvent mouseEvent) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(ViewPath.PAYMENT.getPath()));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitLessonsPgOnAction(MouseEvent mouseEvent) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(ViewPath.LESSON.getPath()));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitSettingsOnAction(MouseEvent mouseEvent) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(ViewPath.SETTINGS.getPath()));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
