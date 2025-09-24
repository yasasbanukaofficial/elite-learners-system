package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.LessonBO;
import lk.ijse.learners.bo.custom.SchedulingBO;
import lk.ijse.learners.bo.custom.impl.SchedulingBOImpl;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import java.net.URL;
import java.util.ResourceBundle;

public class LessonMgmtPageController implements Initializable {
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
    SchedulingBO schedulingBO = (SchedulingBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SCHEDULE);
    private LessonDTO lessonDTO;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLists();

        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.LESSONS)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        setupLists();
                        RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.LESSONS, false);
                    }
                });

        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue && lessonDTO != null) {
                        try {
                            StudentDTO enrolledStudents = lessonBO.getAllStudentsByLessonId(lessonDTO.getLessonId());
                            listStdEnrolled.getItems().setAll(enrolledStudents);
                            RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST, false);
                        } catch (Exception e) {
                            AlertUtil.setErrorAlert("Failed to refresh student list");
                        }
                    }
                });

        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue && lessonDTO != null) {
                        try {
                            CourseDTO assignedCourse = lessonBO.getAllCoursesByLessonId(lessonDTO.getLessonId());
                            listCoursesAssigned.getItems().setAll(assignedCourse);
                            RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST, false);
                        } catch (Exception e) {
                            AlertUtil.setErrorAlert("Failed to refresh course list");
                        }
                    }
                });

        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue && lessonDTO != null) {
                        try {
                            InstructorDTO assignedInstructor = lessonBO.getAllInstructorsByLessonId(lessonDTO.getLessonId());
                            listInsAssigned.getItems().setAll(assignedInstructor);
                            RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST, false);
                        } catch (Exception e) {
                            AlertUtil.setErrorAlert("Failed to refresh instructor list");
                        }
                    }
                });
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
        if (lessonDTO == null) {
            AlertUtil.setErrorAlert("Please select a lesson to edit");
            return;
        }

        if (txtLsnName.getText().isEmpty() || txtStatus.getText().isEmpty()
                || txtDuration.getText().isEmpty()) {
            AlertUtil.setErrorAlert("Please fill all fields");
            return;
        }

        try {
            lessonDTO.setName(txtLsnName.getText());
            lessonDTO.setStatus(txtStatus.getText());
            String[] times = txtDuration.getText().split(" - ");
            lessonDTO.setStart_time(Timestamp.valueOf(times[0].trim()));
            lessonDTO.setEnd_time(Timestamp.valueOf(times[1].trim()));

            if (schedulingBO.updateScheduleLesson(lessonDTO)) {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.LESSONS, true);
                AlertUtil.setInfoAlert("Lesson schedule updated successfully");
                setupLists();
            } else {
                AlertUtil.setErrorAlert("Failed to update lesson schedule");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error occurred while updating lesson schedule");
            throw new RuntimeException(e);
        }

    }

    public void editCourseList(MouseEvent mouseEvent) {
    }
}
