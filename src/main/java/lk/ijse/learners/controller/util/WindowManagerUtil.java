package lk.ijse.learners.controller.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    public static void openForm(String path, boolean cache) {
        try {
            Parent parent = getView(path, cache);
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load form!");
            e.printStackTrace();
        }
    }

    private static Parent getView(String path, boolean cache) throws Exception {
        if (cache) {
            if (!viewCache.containsKey(path)) {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WindowManagerUtil.class.getResource(path)));
                Parent parent = loader.load();
                viewCache.put(path, parent);
            }
            return viewCache.get(path);
        } else {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WindowManagerUtil.class.getResource(path)));
            return loader.load();
        }
    }

    public static void closeForm(Node node) {
        Stage window = (Stage) node.getScene().getWindow();
        if (window != null) {
            window.close();
        }
    }
}
