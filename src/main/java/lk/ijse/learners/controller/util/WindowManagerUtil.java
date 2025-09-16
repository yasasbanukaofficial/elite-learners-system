package lk.ijse.learners.controller.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WindowManagerUtil {
    private static final Map<String, Parent> viewCache = new HashMap<>();

    private WindowManagerUtil() {}

    public static void openForm(String path) {
        try {
            Parent parent = getView(path);
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to form!");
            e.printStackTrace();
        }
    }

    private static Parent getView(String path) throws Exception{
        if(!viewCache.containsKey(path)) {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WindowManagerUtil.class.getResource(path)));
            Parent parent = loader.load();
            viewCache.put(path, parent);
        }
        return viewCache.get(path);
    }

    public static void closeForm(AnchorPane anchorPane) {
        Stage window = (Stage) anchorPane.getScene().getWindow();
        window.close();
    }
}
