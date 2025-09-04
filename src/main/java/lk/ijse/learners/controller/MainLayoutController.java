package lk.ijse.learners.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {
    public AnchorPane ancMain;
    public AnchorPane ancPages;

    public HBox btnStudentMgmt;
    public HBox btnLessonMgmt;
    public HBox btnPaymentMgmt;
    public HBox btnCoursesMgmt;
    public HBox btnUserMgmt;
    public HBox btnInstructorMgmt;
    public HBox btnSettingsPage;
    public ImageView btnLogout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/StudentMgmtPage.fxml"));
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
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/StudentMgmtPage.fxml"));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitUserMgmtPgOnAction(MouseEvent mouseEvent) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/UserMgmtPage.fxml"));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitInstructorsPgOnAction(MouseEvent mouseEvent) {
        try {
            ancPages.getChildren().clear();
            AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/InstructorMgmtPage.fxml"));
            anchorPane.prefWidthProperty().bind(ancPages.widthProperty());
            anchorPane.prefHeightProperty().bind(ancPages.heightProperty());
            ancPages.getChildren().add(anchorPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
