package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.LessonBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

import java.net.URL;
import java.util.ResourceBundle;

public class LessonMgmtPageController implements Initializable {
    private LessonDTO lessonDTO;
    public AnchorPane ancLesson;
    public StackPane btnOpenLessonForm;
    public TextField txtLsnName;
    public TextField txtStatus;
    public TextField txtDuration;
    public ListView <StudentDTO> listStdEnrolled;
    public ListView <InstructorDTO> listInsAssigned;
    public Button btnDeleteLesson;
    public Button btnReschedule;
    public ListView <LessonDTO> listLessons;
    public ListView <CourseDTO> listCoursesAssigned;

    LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.LESSON);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLists();
    }

    public void setupLists() {
        try {
            listLessons.getItems().setAll(lessonBO.getAll());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load lesson list", e);
        }
        listLessons.setSelectionModel(null);
        listLessons.setCellFactory(lv -> new ListCell<LessonDTO>() {
            @Override
            protected void updateItem(LessonDTO lesson, boolean empty) {
                super.updateItem(lesson, empty);
                if (empty || lesson == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle(
                            "-fx-background-color: white; " +
                                    "-fx-border-color: #ccc; " +
                                    "-fx-border-radius: 8; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-padding: 10; "
                    );

                    Label lblName = new Label(lesson.getName());
                    lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label lblDuration = new Label("Duration: " + lesson.getStart_time().toString() + " - " + lesson.getEnd_time().toString());
                    Label lblStatus = new Label("Status: " + lesson.getStatus());

                    card.getChildren().addAll(lblName, lblDuration, lblStatus);
                    card.setOnMouseClicked(event -> setupForm(lesson));

                    setGraphic(card);
                }
            }
        });

        listCoursesAssigned.setCellFactory(lv -> new ListCell<CourseDTO>() {
            @Override
            protected void updateItem(CourseDTO course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle(
                            "-fx-background-color: white; " +
                                    "-fx-border-color: #ccc; " +
                                    "-fx-border-radius: 8; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-padding: 10; "
                    );

                    Label lblName = new Label(course.getName());
                    lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label lblDuration = new Label("Duration: " + course.getDuration());
                    Label lblFees = new Label("Fees: " + course.getFees());

                    card.getChildren().addAll(lblName, lblDuration, lblFees);
                    setGraphic(card);
                }
            }
        });

        listInsAssigned.setCellFactory(lv -> new ListCell<InstructorDTO>() {
            @Override
            protected void updateItem(InstructorDTO instructor, boolean empty) {
                super.updateItem(instructor, empty);
                if (empty || instructor == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(8);
                    card.setStyle(
                            "-fx-background-color: white; " +
                                    "-fx-border-color: #ddd; " +
                                    "-fx-border-radius: 10; " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-padding: 12; "
                    );

                    Label lblName = new Label(instructor.getName());
                    lblName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                    Label lblEmail = new Label("📧 " + instructor.getEmail());
                    Label lblContact = new Label("📞 " + instructor.getContact());
                    Label lblAvailability = new Label("🟢 " + instructor.getAvailability());

                    card.getChildren().addAll(lblName, lblEmail, lblContact, lblAvailability);
                    setGraphic(card);
                }
            }
        });

        listStdEnrolled.setCellFactory(lv -> new ListCell<StudentDTO>() {
            @Override
            protected void updateItem(StudentDTO student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(6);
                    card.setStyle(
                            "-fx-background-color: #f9f9f9; " +
                                    "-fx-border-color: #ccc; " +
                                    "-fx-border-radius: 8; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-padding: 8; "
                    );

                    Label lblName = new Label(student.getFirstName() + " " + student.getLastName());
                    lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label lblEmail = new Label("📧 " + student.getEmail());
                    Label lblContact = new Label("📞 " + student.getContactNumber());

                    card.getChildren().addAll(lblName, lblEmail, lblContact);
                    setGraphic(card);
                }
            }
        });
    }

    private void setupForm(LessonDTO lessonDTO) {
        if (lessonDTO != null) {
            try {
                Optional<LessonDTO> lesson = lessonBO.findById(lessonDTO.getLessonId());
                if (lesson.isEmpty()) {
                    AlertUtil.setErrorAlert("Lesson is not present in the database");
                    return;
                }

                this.lessonDTO = lesson.get();

                txtLsnName.setText(this.lessonDTO.getName());
                txtStatus.setText(this.lessonDTO.getStatus());
                txtDuration.setText(this.lessonDTO.getStart_time().toString() + " - " + this.lessonDTO.getEnd_time().toString());

                StudentDTO enrolledStudents = lessonBO.getAllStudentsByLessonId(this.lessonDTO.getLessonId());
                listStdEnrolled.getItems().setAll(enrolledStudents);

                InstructorDTO assignedInstructor = lessonBO.getAllInstructorsByLessonId(this.lessonDTO.getLessonId());
                listInsAssigned.getItems().setAll(assignedInstructor);

                CourseDTO assignedCourse = lessonBO.getAllCoursesByLessonId(this.lessonDTO.getLessonId());
                listCoursesAssigned.getItems().setAll(assignedCourse);

            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load lesson details");
                throw new RuntimeException(e);
            }
        }
    }

    public void openLessonForm(MouseEvent mouseEvent) {
        WindowManagerUtil.openForm(ViewPath.ADD_LESSON_FORM.getPath(), false);
    }

    public void editStdList(MouseEvent mouseEvent) {
    }

    public void editInsList(MouseEvent mouseEvent) {
    }

    public void deleteLesson(ActionEvent actionEvent) {
    }

    public void editSchedule(ActionEvent actionEvent) {
    }

    public void editCourseList(MouseEvent mouseEvent) {
    }
}
