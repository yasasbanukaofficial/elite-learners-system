package lk.ijse.learners.controller.util;

public enum ViewPath {
    LOGIN("LoginPage.fxml"),
    MAIN("MainLayout.fxml"),
    STUDENT("StudentMgmtPage.fxml"),
    INSTRUCTOR("InstructorMgmtPage.fxml"),
    USER("UserMgmtPage.fxml"),
    COURSE("CourseMgmtPage.fxml"),
    PAYMENT("PaymentMgmtPage.fxml"),
    LESSON("LessonMgmtPage.fxml"),
    STUDENT_COURSE_DETAILS("StudentCourseDetailsMgmtPage.fxml"),
    ENROLLMENT("EnrollmentMgmtPage.fxml"),
    QUERY("QueryMgmtPage.fxml"),

    ADD_STUDENT_FORM("AddStudentForm.fxml"),
    ADD_PAYMENT_FORM("AddPaymentForm.fxml"),
    ADD_COURSE_FORM("AddCourseForm.fxml"),
    ADD_INSTRUCTOR_FORM("AddInstructorForm.fxml"),
    ADD_QUERY_FORM("AddQueryForm.fxml"),

    CHOOSE_COURSE_FORM("ChooseCourseForm.fxml");

    private final String fileName;
    ViewPath(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return "/view/" + fileName;
    }
}
