package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.controller.util.ViewPath;

import java.io.IOException;

public class InstructorMgmtPageController {
    public AnchorPane ancInstructor;
    public Button btnOpenInsForm;
    public TableView tblInstructor;

    public void openInsForm(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADD_INSTRUCTOR_FORM.getPath()));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
