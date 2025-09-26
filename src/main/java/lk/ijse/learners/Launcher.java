package lk.ijse.learners;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.controller.util.ViewPath;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
       Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADMIN_MAIN.getPath()));
       stage.setScene(new Scene(parent));
       stage.initStyle(StageStyle.TRANSPARENT);
       stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
