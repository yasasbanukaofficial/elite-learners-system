package lk.ijse.learners.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.controller.util.ViewPath;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminMainLayoutController implements Initializable {
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

    private final Map<ViewPath, AnchorPane> pageCache = new HashMap<>();

    private AnchorPane getPage(ViewPath path) {
        return pageCache.computeIfAbsent(path, p -> {
            try {
                AnchorPane pane = FXMLLoader.load(getClass().getResource(p.getPath()));
                pane.prefWidthProperty().bind(ancPages.widthProperty());
                pane.prefHeightProperty().bind(ancPages.heightProperty());
                return pane;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private void showPage(ViewPath path) {
        ancPages.getChildren().setAll(getPage(path));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       showPage(ViewPath.DASHBOARD);
    }

    public void visitStudentPgOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.STUDENT);
    }

    public void visitUserMgmtPgOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.USER);
    }

    public void visitInstructorsPgOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.INSTRUCTOR);
    }

    public void visitCoursesPgOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.COURSE);
    }

    public void visitPaymentPgOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.PAYMENT);
    }

    public void visitLessonsPgOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.LESSON);
    }

    public void visitSettingsOnAction(MouseEvent mouseEvent) {
        showPage(ViewPath.SETTINGS);
    }

    public void visitDashboard(MouseEvent mouseEvent) {
        showPage(ViewPath.DASHBOARD);
    }

    public void logout(MouseEvent mouseEvent) {
        try {
            Stage stage = (Stage) ancMain.getScene().getWindow();

            Parent root = FXMLLoader.load(getClass().getResource(ViewPath.LOGIN.getPath()));
            Scene loginScene = new Scene(root, 1000, 690);

            stage.setScene(loginScene);

            stage.setResizable(false);
            stage.setWidth(1000);
            stage.setHeight(690);

            stage.centerOnScreen();
            stage.show();
            stage.toFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
