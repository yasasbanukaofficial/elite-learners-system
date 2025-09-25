package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.custom.LessonBO;
import lk.ijse.learners.bo.custom.SchedulingBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.bo.custom.impl.SchedulingBOImpl;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import java.net.URL;
import java.util.ResourceBundle;

public class LessonMgmtPageController implements Initializable {
    public AnchorPane ancLesson;
    public StackPane btnOpenLessonForm;
    public TextField txtLsnName;
    public TextField txtStatus;
    public ListView <StudentDTO> listStdEnrolled;
    public ListView <InstructorDTO> listInsAssigned;
    public Button btnDeleteLesson;
    public Button btnReschedule;
    public ListView <LessonDTO> listLessons;
    public ListView <CourseDTO> listCoursesAssigned;
    public DatePicker dpStartTime;
    public Spinner<Integer> spinnerStartHr;
    public Spinner<Integer> spinnerStartMin;
    public DatePicker dpEndTime;
    public Spinner<Integer> spinnerEndHr;
    public Spinner<Integer> spinnerEndMin;

    LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.LESSON);
    SchedulingBO schedulingBO = (SchedulingBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SCHEDULE);
    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private LessonDTO lessonDTO;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spinnerStartHr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        spinnerStartMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spinnerEndHr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10));
        spinnerEndMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
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

                    Label lblStart = new Label("Start Date: "
                            + formatDateHuman(lesson.getStart_time()) + ", Time: " + formatTime(lesson.getStart_time()));

                    Label lblEnd = new Label("End Date: "
                            + formatDateHuman(lesson.getEnd_time()) + ", Time: " + formatTime(lesson.getEnd_time()));


                    Label lblStatus = new Label("Status: " + lesson.getStatus());

                    card.getChildren().addAll(lblName, lblStart, lblEnd, lblStatus);
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

                LocalDateTime startDateTime = this.lessonDTO.getStart_time().toLocalDateTime();
                dpStartTime.setValue(startDateTime.toLocalDate());
                spinnerStartHr.getValueFactory().setValue(startDateTime.getHour());
                spinnerStartMin.getValueFactory().setValue(startDateTime.getMinute());

                LocalDateTime endDateTime = this.lessonDTO.getEnd_time().toLocalDateTime();
                dpEndTime.setValue(endDateTime.toLocalDate());
                spinnerEndHr.getValueFactory().setValue(endDateTime.getHour());
                spinnerEndMin.getValueFactory().setValue(endDateTime.getMinute());

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
        try {
            if (lessonDTO == null) {
                AlertUtil.setErrorAlert("Please select a lesson first");
                return;
            }

            Optional<StudentDTO> student = studentBO.findById(lessonDTO.getStudentId());
            if (student.isEmpty()) {
                AlertUtil.setErrorAlert("No students found for this lesson");
                return;
            }

            enrollmentContext.setLessonDTO(lessonDTO);
            enrollmentContext.setStudentDTO(student.get());

            WindowManagerUtil.openForm(ViewPath.EDIT_ASSIGNED_STUDENTS_TO_LSN.getPath(), false);
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error loading student details");
            throw new RuntimeException(e);
        }
    }

    public void editInsList(MouseEvent mouseEvent) {
        try {
            if (lessonDTO == null) {
                AlertUtil.setErrorAlert("Please select a lesson first");
                return;
            }

            Optional<InstructorDTO> instructor = instructorBO.findById(lessonDTO.getInstructorId());
            if (instructor.isEmpty()) {
                AlertUtil.setErrorAlert("No instructor found for this lesson");
                return;
            }

            enrollmentContext.setLessonDTO(lessonDTO);
            enrollmentContext.setInstructorDTO(instructor.get());

            WindowManagerUtil.openForm(ViewPath.EDIT_ASSIGNED_INSTRUCTORS_TO_LSN.getPath(), false);
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error loading instructor details");
            throw new RuntimeException(e);
        }
    }

    public void editCourseList(MouseEvent mouseEvent) {
        try {
            if (lessonDTO == null) {
                AlertUtil.setErrorAlert("Please select a lesson first");
                return;
            }

            CourseDTO assignedCourse = lessonBO.getAllCoursesByLessonId(lessonDTO.getLessonId());
            if (assignedCourse == null) {
                AlertUtil.setErrorAlert("No course found for this lesson");
                return;
            }

            enrollmentContext.setLessonDTO(lessonDTO);
            enrollmentContext.setCourseDTO(assignedCourse);

            WindowManagerUtil.openForm(ViewPath.EDIT_ENROLLED_COURSES_TO_LSN.getPath(), false);
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error loading course details");
            throw new RuntimeException(e);
        }
    }

    public void deleteLesson(ActionEvent actionEvent) {
        if (lessonDTO == null) {
            AlertUtil.setErrorAlert("Please select a lesson to delete");
            return;
        }

        try {
            if (AlertUtil.setConfirmationAlert("Delete Lesson", "Are you sure you want to delete this lesson?")) {
                if (schedulingBO.deleteLesson(lessonDTO.getLessonId())) {
                    RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.LESSONS, true);
                    AlertUtil.setInfoAlert("Lesson deleted successfully");
                    clearForm();
                } else {
                    AlertUtil.setErrorAlert("Failed to delete lesson");
                }
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error occurred while deleting lesson");
            throw new RuntimeException(e);
        }
    }

    private void clearForm() {
        txtLsnName.clear();
        txtStatus.clear();
        dpStartTime.setValue(null);
        dpEndTime.setValue(null);
        spinnerStartHr.getValueFactory().setValue(0);
        spinnerStartMin.getValueFactory().setValue(0);
        spinnerEndHr.getValueFactory().setValue(0);
        spinnerEndMin.getValueFactory().setValue(0);
        listStdEnrolled.getItems().clear();
        listInsAssigned.getItems().clear();
        listCoursesAssigned.getItems().clear();
        lessonDTO = null;
    }

    public void editSchedule(ActionEvent actionEvent) {
        if (lessonDTO == null) {
            AlertUtil.setErrorAlert("Please select a lesson to edit");
            return;
        }

        if (txtLsnName.getText().isEmpty() || txtStatus.getText().isEmpty()
                || dpStartTime.getValue() == null || dpEndTime.getValue() == null) {
            AlertUtil.setErrorAlert("Please fill all fields");
            return;
        }

        try {
            lessonDTO.setName(txtLsnName.getText());
            lessonDTO.setStatus(txtStatus.getText());

            LocalDate startDate = dpStartTime.getValue();
            LocalTime startTime = LocalTime.of(spinnerStartHr.getValue(), spinnerStartMin.getValue());
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

            LocalDate endDate = dpEndTime.getValue();
            LocalTime endTime = LocalTime.of(spinnerEndHr.getValue(), spinnerEndMin.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            // ✅ Validation: End must be after Start
            if (!endDateTime.isAfter(startDateTime)) {
                AlertUtil.setErrorAlert("End time must be after start time");
                return;
            }

            lessonDTO.setStart_time(Timestamp.valueOf(startDateTime));
            lessonDTO.setEnd_time(Timestamp.valueOf(endDateTime));

            if (schedulingBO.updateScheduleLesson(lessonDTO)) {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.LESSONS, true);
                AlertUtil.setInfoAlert("Lesson schedule updated successfully");
                setupLists();
            } else {
                AlertUtil.setErrorAlert("Failed to update lesson schedule");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error occurred while updating lesson schedule: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }




    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String getDayWithSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1:  return day + "st";
            case 2:  return day + "nd";
            case 3:  return day + "rd";
            default: return day + "th";
        }
    }

    private String formatDateMachine(Timestamp timestamp) {
        if (timestamp == null) return "";
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String formatDateHuman(Timestamp timestamp) {
        if (timestamp == null) return "";
        var dt = timestamp.toLocalDateTime();
        String month = dt.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        String dayWithSuffix = getDayWithSuffix(dt.getDayOfMonth());
        return month + " " + dayWithSuffix + " " + dt.getYear();
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) return "";
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}
