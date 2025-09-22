package lk.ijse.learners.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.StudentDTO;
import lk.ijse.learners.tm.StudentTM;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class StudentMgmtPageController implements Initializable{
    public AnchorPane ancStudent;
    public StackPane btnOpenStdForm;
    public StackPane btnSearch;

    public TextField txtStdName;
    public TextField txtAddress;
    public TextField txtEmail;
    public TextField txtAge;
    public TextField txtContact;
    public ListView <CourseDTO> listCoursesEnrolled;
    public DatePicker stdDob;
    public ListView<StudentDTO> listStudents;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);

    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private StudentDTO studentDTO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLists();
        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.STUDENT)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        Platform.runLater(() -> {
                            try {
                                List<StudentDTO> students = studentBO.getAll();
                                listStudents.getItems().setAll(students);
                            } catch (Exception e) {
                                AlertUtil.setErrorAlert("Failed to refresh students list");
                                e.printStackTrace();
                            }
                            RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.STUDENT, false);
                        });
                    }
                });


        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.COURSES_ENROLLED_LIST)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        Platform.runLater(() -> {
                            if (studentDTO != null) {
                                try {
                                    List<CourseDTO> enrolledCourses =courseBO.getAllEnrolledCoursesByStdId(studentDTO.getStudentId());
                                    listCoursesEnrolled.getItems().setAll(enrolledCourses);
                                } catch (Exception e) {
                                    AlertUtil.setErrorAlert("Failed to refresh enrolled courses");
                                    e.printStackTrace();
                                }
                            }
                            RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES_ENROLLED_LIST, false);
                        });
                    }
                });
    }

    // CRUD Operations
    public void deleteStudent(ActionEvent actionEvent) {
        try {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to delete student ?")) {
                studentBO.delete(studentDTO.getStudentId());
            }
            listStudents.refresh();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to delete student");
            throw new RuntimeException(e);
        }
    }

    public void editStudent(ActionEvent actionEvent) {
        String fullName = txtStdName.getText().trim();
        String firstName = fullName;
        String secondName = "";

        if (fullName.contains(" ")) {
            int index = fullName.indexOf(" ");
            firstName = fullName.substring(0, index).trim();
            secondName = fullName.substring(index + 1).trim();
        }

        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();
        Date dob = Date.valueOf(stdDob.getValue());

        boolean unchanged =
                firstName.equals(studentDTO.getFirstName()) &&
                secondName.equals(studentDTO.getLastName()) &&
                email.equals(studentDTO.getEmail()) &&
                contact.equals(studentDTO.getContactNumber()) &&
                address.equals(studentDTO.getAddress()) &&
                dob.equals(studentDTO.getDob());

        if (unchanged) {
            AlertUtil.setErrorAlert("No changes detected. Please modify some details before saving.");
            return;
        }

        try {
            if (validateStudentDetails(firstName, secondName, email, contact, address)) {
                StudentDTO updatedStdDTO = new StudentDTO(
                        studentDTO.getStudentId(),
                        firstName,
                        secondName,
                        dob,
                        email,
                        contact,
                        address,
                        entityDTOConverter.toPaymentDTOList(studentBO.getAllPaymentsBySid(studentDTO.getStudentId())),
                        entityDTOConverter.toLessonDTOList(studentBO.getAllLessonsBySid(studentDTO.getStudentId()))
                );
                if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to update student details ?")) {
                    if (studentBO.update(updatedStdDTO)) {
                        setupLists();
                    } else {
                        AlertUtil.setErrorAlert("Failed to update student");
                    }
                }

            }

        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error updating student");
            e.printStackTrace();
        }
    }

    // Utility methods
    private boolean validateStudentDetails(String fName, String lName, String email, String contact, String address) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        String contactPattern = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";

        if (!Auth.areRequiredFieldsFilled(fName)){
            errorMsg.append("* Student's Name must not be empty\n");
            isValid = false;
        }

        if (stdDob.getValue() == null){
            errorMsg.append("* You must include student date of birth!\n");
            isValid = false;
        } else {
            LocalDate birthDate = stdDob.getValue();
            int age = LocalDate.now().getYear() - birthDate.getYear();
            if (age < 18 || age > 60){
                errorMsg.append("* Student age must be between 18 and 60 years.\n");
                isValid = false;
            }
        }
        if (!Auth.areRequiredFieldsFilled(email)){
            errorMsg.append("* Must include instructor's email!\n");
            isValid = false;
        } else if (!email.matches(emailPattern)){
            errorMsg.append("* Email should be a valid one (ex: john@mail.com) !\n");
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(contact)){
            errorMsg.append("* You must include student's contact!\n");
            isValid = false;
        } else if (!contact.matches(contactPattern)){
            errorMsg.append("* Contact should be a valid one (ex: 0721231231 (LK), 4615555679 (US))!\n");
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(address)){
            errorMsg.append("* You must include students address!\n");
            isValid = false;
        }

        if (!isValid){
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }
        return isValid;
    }

    public void editCourseList(MouseEvent mouseEvent) {
        try {
            enrollmentContext.setStudentDTO(studentDTO);
            enrollmentContext.setCourseDTOList(courseBO.getAllEnrolledCoursesByStdId(studentDTO.getStudentId()));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to set course details in the context");
            throw new RuntimeException(e);
        }
        WindowManagerUtil.openForm(ViewPath.EDIT_ENROLLED_COURSES.getPath(), false);
    }

    // Setup Methods
    public void openStdForm(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADD_STUDENT_FORM.getPath()));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public void setupLists() {
        try {
            listStudents.getItems().setAll(studentBO.getAll());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        listStudents.setSelectionModel(null);
        listStudents.setCellFactory(lv -> new ListCell<StudentDTO>() {
            @Override
            protected void updateItem(StudentDTO student, boolean empty) {
                super.updateItem(student, empty);

                if (empty || student == null) {
                    setGraphic(null);
                } else {
                    VBox card = new VBox(8);
                    card.setStyle(
                            "-fx-background-color: white; " +
                                    "-fx-border-color: #ddd; " +
                                    "-fx-border-radius: 10; " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-padding: 12; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                    );

                    // Student details
                    Label lblName = new Label(student.getFirstName() + " " + student.getLastName());
                    lblName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                    Label lblEmail = new Label("📧 " + student.getEmail());
                    Label lblContact = new Label("📞 " + student.getContactNumber());
                    Label lblAddress = new Label("🏠 " + student.getAddress());
                    Label lblDob = new Label("🎂 " + student.getDob().toString());

                    // Add all labels to card
                    card.getChildren().addAll(lblName, lblEmail, lblContact, lblAddress, lblDob);

                    card.setOnMouseClicked(event -> {
                        studentDTO = student;
                        setupForm(new StudentDTO(
                                student.getStudentId(),
                                student.getFirstName(),
                                student.getLastName(),
                                student.getDob(),
                                student.getContactNumber()
                        ));
                    });

                    setGraphic(card);
                }
            }
        });


        listCoursesEnrolled.setCellFactory(lv -> new ListCell<CourseDTO>() {
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
                    lblDuration.setStyle("-fx-text-fill: gray;");

                    Label lblFees = new Label("Fees: " + course.getFees());
                    lblFees.setStyle("-fx-text-fill: green;");

                    card.getChildren().addAll(lblName, lblDuration, lblFees);

                    setGraphic(card);
                }
            }
        });
    }

    private void setupForm(StudentDTO studentDTO) {
        if (studentDTO != null) {
            try {
                Optional<StudentDTO> student = studentBO.findById(studentDTO.getStudentId());
                if (student.isEmpty()) {
                    AlertUtil.setErrorAlert("Student is not present in the database");
                } else {
                    listCoursesEnrolled.getItems().clear();

                    this.studentDTO = student.get();
                    txtStdName.setText(this.studentDTO.getFirstName() + " " + this.studentDTO.getLastName());
                    txtAddress.setText(this.studentDTO.getAddress());
                    txtEmail.setText(this.studentDTO.getEmail());

                    LocalDate dob = this.studentDTO.getDob().toLocalDate();
                    stdDob.setValue(dob);

                    int age = Period.between(dob, LocalDate.now()).getYears();
                    txtAge.setText(String.valueOf(age));

                    txtContact.setText(this.studentDTO.getContactNumber());

                    List <CourseDTO> enrolledCourses = courseBO.getAllEnrolledCoursesByStdId(studentDTO.getStudentId());
                    List <String> enrolledCourseNames = new ArrayList<>();
                    enrolledCourses.forEach(course -> enrolledCourseNames.add(course.getName()));
                    listCoursesEnrolled.getItems().setAll(enrolledCourses);

                }

            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load student details in the form");
                throw new RuntimeException(e);
            }
        }
    }

}
