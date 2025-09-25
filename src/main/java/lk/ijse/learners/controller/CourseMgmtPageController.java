package lk.ijse.learners.controller;

import javafx.application.Platform;
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
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CourseMgmtPageController implements Initializable {
    public AnchorPane ancCourse;
    public StackPane btnOpenCourseForm;

    public TextField txtCourseName;
    public TextField txtDescription;
    public Label lblEnrollmentCount;
    public TextField txtDuration;
    public TextField txtFees;
    public ListView <StudentDTO> listStdEnrolled;
    public ListView <InstructorDTO> listInsAssigned;
    public Button btnDeleteCourses;
    public Button btnEdit;
    public ListView <CourseDTO> listCourses;
    public ListView <LessonDTO> listLesAssigned;

    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    private InstructorDTO instructorDTO;
    private CourseDTO courseDTO;
    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    public void openCourseForm(MouseEvent mouseEvent) throws IOException {
        WindowManagerUtil.openForm(ViewPath.ADD_COURSE_FORM.getPath(), false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       setupLists();

       RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.COURSES).addListener((observable, oldValue, newValue) -> {
           if (newValue) {
               Platform.runLater(() -> {
                   try {
                       listCourses.getItems().setAll(courseBO.getAll());
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
                   RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES, false);
               });
           }
       });
       RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST).addListener((observable, oldValue, newValue) -> {
           if (newValue) {
               Platform.runLater(() -> {
                   try {
                       List<StudentDTO> stdEnrolled = courseBO.getAllStudentsByCourseId(courseDTO.getCourseId());
                       listStdEnrolled.getItems().setAll(stdEnrolled);
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
                   RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.STUDENT_ENROLLED_LIST, false);
               });
           }
       });

        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    try {
                        List<InstructorDTO> instructorsEnrolled = courseBO.getAllInstructorsByCourseId(courseDTO.getCourseId());
                        listInsAssigned.getItems().setAll(instructorsEnrolled);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTOR_ENROLLED_LIST, false);
                });
            }
        });


    }

    public void editStdList(MouseEvent mouseEvent) {
        try {
            enrollmentContext.setCourseDTO(courseDTO);
            enrollmentContext.setStdDTOList(courseBO.getAllStudentsByCourseId(courseDTO.getCourseId()));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to set course details in the context");
            throw new RuntimeException(e);
        }
        WindowManagerUtil.openForm(ViewPath.EDIT_ENROLLED_STUDENTS.getPath(), false);
    }

    public void editInsList(MouseEvent mouseEvent) {
        try {
            enrollmentContext.setCourseDTO(courseDTO);
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to set course details in the context");
            throw new RuntimeException(e);
        }
        WindowManagerUtil.openForm(ViewPath.EDIT_ASSIGNED_INSTRUCTORS.getPath(), false);
    }

    public void deleteCourses(ActionEvent actionEvent) {
        try {
            if (AlertUtil.setConfirmationAlert("Before Continue", "Are you sure you want to delete course ?")) {
                courseBO.delete(courseDTO.getCourseId());
            }
            setupLists();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to delete course");
            throw new RuntimeException(e);
        }
    }

    public void editCourses(ActionEvent actionEvent) {
        if (courseDTO == null) {
            AlertUtil.setErrorAlert("Please select a course to edit");
            return;
        }

        if (txtCourseName.getText().isEmpty() || txtDescription.getText().isEmpty()
                || txtDuration.getText().isEmpty() || txtFees.getText().isEmpty()) {
            AlertUtil.setErrorAlert("Please fill all fields");
            return;
        }

        try {
            courseDTO.setName(txtCourseName.getText());
            courseDTO.setDescription(txtDescription.getText());
            courseDTO.setDuration(txtDuration.getText());
            courseDTO.setFees(txtFees.getText());

            if (courseBO.update(courseDTO)) {
                RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES, true);
                AlertUtil.setInfoAlert("Course updated successfully");
            } else {
                AlertUtil.setErrorAlert("Failed to update course");
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error occurred while updating course");
            throw new RuntimeException(e);
        }
    }

    public void editLesList(MouseEvent mouseEvent) {
    }

    public void setupLists() {
        try {
            List<CourseDTO> courseList = courseBO.getAll();
            listCourses.getItems().setAll(courseList);
            if (!courseList.isEmpty()) {
                listCourses.getSelectionModel().select(0);
                setupForm(courseList.get(0));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load course list", e);
        }

        listCourses.setSelectionModel(null);
        listCourses.setCellFactory(lv -> new ListCell<CourseDTO>() {
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

                    card.setOnMouseClicked(event -> setupForm(course));

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

    private void setupForm(CourseDTO courseDTO) {
        if (courseDTO != null) {
            try {
                Optional<CourseDTO> course = courseBO.findById(courseDTO.getCourseId());
                if (course.isEmpty()) {
                    AlertUtil.setErrorAlert("Course is not present in the database");
                    return;
                }

                this.courseDTO = course.get();

                txtCourseName.setText(this.courseDTO.getName());
                txtDescription.setText(this.courseDTO.getDescription());
                txtDuration.setText(this.courseDTO.getDuration());
                txtFees.setText(this.courseDTO.getFees());

                List<StudentDTO> enrolledStudents = courseBO.getAllStudentsByCourseId(this.courseDTO.getCourseId());
                listStdEnrolled.getItems().setAll(enrolledStudents);

                lblEnrollmentCount.setText(String.valueOf(enrolledStudents.size()));

                List<InstructorDTO> assignedInstructors = courseBO.getAllInstructorsByCourseId(this.courseDTO.getCourseId());
                listInsAssigned.getItems().setAll(assignedInstructors);

            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load course details");
                throw new RuntimeException(e);
            }
        }
    }

}
