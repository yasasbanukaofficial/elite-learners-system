package lk.ijse.learners.controller.util;

public enum ViewPath {
    LOGIN("LoginPage.fxml"),
    MAIN("MainLayout.fxml"),
    STUDENT("StudentMgmtPage.fxml"),
    INSTRUCTOR("InstructorMgmtPage.fxml"),
    USER("UserMgmtPage.fxml");

    private final String fileName;
    ViewPath(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return "/view/" + fileName;
    }
}
