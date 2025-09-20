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
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.StudentDTO;
import lk.ijse.learners.entity.Student;
import lk.ijse.learners.tm.StudentTM;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);

    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTblColumn();
        loadTbl();

        tblStudents.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSel, newSel) -> {
            if (newSel != null) setupForm(newSel);
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
                    return;
                } else {
                    StudentDTO studentDTO = student.get();
                    txtStdName.setText(studentDTO.getFirstName() + " " + studentDTO.getLastName());
                    txtAddress.setText(studentDTO.getAddress());
                    txtEmail.setText(studentDTO.getEmail());

                    LocalDate dob = studentDTO.getDob().toLocalDate();
                    int age = Period.between(dob, LocalDate.now()).getYears();
                    txtAge.setText(String.valueOf(age));

                    txtContact.setText(studentDTO.getContactNumber());

                    List <CourseDTO> enrolledCourses = entityDTOConverter.toCourseDTOList(courseBO.getAllEnrolledCoursesByStdId(studentTM.getStudentId()));
                    List <String> enrolledCourseNames = new ArrayList<>();
                    enrolledCourses.forEach(course -> enrolledCourseNames.add(course.getName()));
                    listCoursesEnrolled.getItems().addAll(enrolledCourseNames);
                }

            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load student details in the form");
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteStudent(ActionEvent actionEvent) {
    }

    public void editStudent(ActionEvent actionEvent) {
    }
}
