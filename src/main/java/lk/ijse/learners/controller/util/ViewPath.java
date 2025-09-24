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

    ADD_STUDENT_FORM("AddStudentForm.fxml"),
    ADD_PAYMENT_FORM("AddPaymentForm.fxml"),
    ADD_COURSE_FORM("AddCourseForm.fxml"),
    ADD_INSTRUCTOR_FORM("AddInstructorForm.fxml"),
    ADD_LESSON_FORM("AddLessonForm.fxml"),
    ADD_QUERY_FORM("AddQueryForm.fxml"),
    ADD_USER_FORM("AddUserForm.fxml"),

    CHOOSE_COURSE_FORM("ChooseCourseForm.fxml"),
    EDIT_ENROLLED_COURSES("EditEnrolledCourses.fxml"),
    EDIT_ASSIGNED_COURSES("EditAssignedCourses.fxml"),
    EDIT_ASSIGNED_INSTRUCTORS("EditAssignedInstructors.fxml"),
    EDIT_ASSIGNED_INSTRUCTORS_TO_LSN("EditAssignedInstructorsToLessons.fxml"),
    EDIT_ASSIGNED_STUDENTS_TO_LSN("EditEnrolledStudentsToLessons.fxml"),
    EDIT_ENROLLED_COURSES_TO_LSN("EditEnrolledCoursesToLessons.fxml"),
    EDIT_ENROLLED_STUDENTS("EditEnrolledStudents.fxml");

    private final String fileName;
    ViewPath(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return "/view/" + fileName;
    }
}
