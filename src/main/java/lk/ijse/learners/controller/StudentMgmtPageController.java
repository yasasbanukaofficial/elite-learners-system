package lk.ijse.learners.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
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

    public TableView <StudentTM> tblStudents;
    public TableColumn <StudentTM, String> colSid;
    public TableColumn <StudentTM, String> colFname;
    public TableColumn <StudentTM, String> colLname;
    public TableColumn <StudentTM, String> colDob;
    public TableColumn <StudentTM, String> colContact;
    public TextField txtStdName;
    public TextField txtAddress;
    public TextField txtEmail;
    public TextField txtAge;
    public TextField txtContact;
    public ListView <String> listCoursesEnrolled;
    public DatePicker stdDob;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);

    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    private StudentDTO studentDTO;
    private List<String> courseNames;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTblColumn();
        loadTbl();

        tblStudents.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSel, newSel) -> {
            if (newSel != null) setupForm(newSel);
        });

        listCoursesEnrolled.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                boolean confirmed = AlertUtil.setConfirmationAlert(
                        "Before continuing",
                        "Are you sure you want to remove student from course?"
                );

                if (confirmed) {
                    removeCourse(newSel);
                } else {
                    listCoursesEnrolled.getSelectionModel().select(oldSel);
                }
            }
        });


    }

    public void openStdForm(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADD_STUDENT_FORM.getPath()));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    private void setupTblColumn() {
        colSid.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colFname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
    }

    private void loadTbl() {
        try {
            List<StudentDTO> allStudents = studentBO.getAll();
            tblStudents.setItems(FXCollections.observableList(
                    allStudents.stream().map(studentDTO -> new StudentTM(
                            studentDTO.getStudentId(),
                            studentDTO.getFirstName(),
                            studentDTO.getLastName(),
                            studentDTO.getDob(),
                            studentDTO.getContactNumber()
                    )).toList()
            ));

            if (!tblStudents.getItems().isEmpty()) {
                tblStudents.getSelectionModel().selectFirst();
                setupForm(tblStudents.getItems().getFirst());
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load students");
            e.printStackTrace();
        }
    }

    private void setupForm(StudentTM studentTM) {
        if (studentTM != null) {
            try {
                Optional<StudentDTO> student = studentBO.findById(studentTM.getStudentId());
                if (student.isEmpty()) {
                    AlertUtil.setErrorAlert("Student is not present in the database");
                } else {
                    listCoursesEnrolled.getItems().clear();

                    studentDTO = student.get();
                    txtStdName.setText(studentDTO.getFirstName() + " " + studentDTO.getLastName());
                    txtAddress.setText(studentDTO.getAddress());
                    txtEmail.setText(studentDTO.getEmail());

                    LocalDate dob = studentDTO.getDob().toLocalDate();
                    stdDob.setValue(dob);

                    int age = Period.between(dob, LocalDate.now()).getYears();
                    txtAge.setText(String.valueOf(age));

                    txtContact.setText(studentDTO.getContactNumber());

                    List <CourseDTO> enrolledCourses = entityDTOConverter.toCourseDTOList(courseBO.getAllEnrolledCoursesByStdId(studentTM.getStudentId()));
                    List <String> enrolledCourseNames = new ArrayList<>();
                    enrolledCourses.forEach(course -> enrolledCourseNames.add(course.getName()));
                    listCoursesEnrolled.getItems().addAll(enrolledCourseNames);
                    courseNames = enrolledCourseNames;
                }

            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load student details in the form");
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteStudent(ActionEvent actionEvent) {
        try {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to delete student ?")) {
                studentBO.delete(studentDTO.getStudentId());
            }
            loadTbl();
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
                        loadTbl();
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

    private void removeCourse(String courseName) {
        try {
            Optional<CourseDTO> courseDTO = courseBO.findByName(courseName);
            if (courseDTO.isPresent()) {
                CourseDTO course = courseDTO.get();
                course.getStudents().removeIf(std -> std.getStudentId().equals(studentDTO.getStudentId()));
                courseBO.update(course);

                listCoursesEnrolled.getItems().remove(courseName);
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update course when removing student");
            e.printStackTrace();
        }
    }


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
}
