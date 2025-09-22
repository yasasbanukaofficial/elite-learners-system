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
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.tm.InstructorTM;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InstructorMgmtPageController implements Initializable {
    public AnchorPane ancInstructor;
    public StackPane btnOpenInsForm;

    public TableView<InstructorTM> tblInstructor;
    public TableColumn<InstructorTM, String> colInsId;
    public TableColumn<InstructorTM, String> colName;
    public TableColumn<InstructorTM, String> colContact;
    public TableColumn<InstructorTM, String> colSpeciality;
    public TableColumn<InstructorTM, String> colAvailability;
    public ListView<InstructorDTO> listInstructors;
    public TextField txtInsName;
    public TextField txtEmail;
    public TextField txtInsContact;
    public TextField txtSpeciality;
    public CheckBox cbAvailable;
    public CheckBox cbNotAvailable;
    public ListView listCoursesAssigned;
    public Button btnDeleteIns;
    public Button btnEdit;
    public DatePicker insDobPicker;

    InstructorBO instuctorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    private InstructorDTO instructorDTO;
    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    public void openInsForm(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADD_INSTRUCTOR_FORM.getPath()));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLists();
        cbAvailable.setOnAction(e -> {
            if (cbAvailable.isSelected()) {
                cbAvailable.setSelected(false);
            }
        });
        cbNotAvailable.setOnAction(e -> {
            if (cbNotAvailable.isSelected()) {
                cbNotAvailable.setSelected(false);
            }
        });
        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.INSTRUCTORS).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    try {
                        listInstructors.getItems().setAll(instuctorBO.getAll());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.INSTRUCTORS, false);
                });
            }
        });

        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (instructorDTO != null) {
                        try {
                            listCoursesAssigned.getItems().setAll(courseBO.getAllEnrolledCoursesByInsId(instructorDTO.getInstructorId()));
                        } catch (Exception e) {
                            AlertUtil.setErrorAlert("Failed to set course details in the context");
                            throw new RuntimeException(e);
                        }
                    }
                    RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.COURSES_ASSIGNED_LIST, false);
                });
            }
        });
    }
    
    public void editCourseList(MouseEvent mouseEvent) {
        try {
            enrollmentContext.setInstructorDTO(instructorDTO);
            enrollmentContext.setCourseDTOList(courseBO.getAllEnrolledCoursesByInsId(instructorDTO.getInstructorId()));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to set course details in the context");
            throw new RuntimeException(e);
        }
        WindowManagerUtil.openForm(ViewPath.EDIT_ASSIGNED_COURSES.getPath(), false);
    }

    public void deleteInstructor(ActionEvent actionEvent) {
        try {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to delete student ?")) {
                instuctorBO.delete(instructorDTO.getInstructorId());
            }
            listInstructors.refresh();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to delete student");
            throw new RuntimeException(e);
        }
    }

    public void editInstructor(ActionEvent actionEvent) {
        String fullName = txtInsName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtInsContact.getText().trim();
        String speciality = txtSpeciality.getText().trim();
        String availability = cbAvailable.isSelected() ? "available" : cbNotAvailable.isSelected() ? "not available" : null;

        Date dob = Date.valueOf(insDobPicker.getValue());
        boolean unchanged = fullName.equals(instructorDTO.getName()) &&
                email.equals(instructorDTO.getEmail()) &&
                contact.equals(instructorDTO.getContact()) &&
                speciality.equals(instructorDTO.getSpeciality()) &&
                availability.equals(instructorDTO.getAvailability()) &&
                dob.equals(instructorDTO.getDob());

        if (unchanged) {
            AlertUtil.setErrorAlert("No changes made");
            return;
        }

        try {
            if (validateInstructorDetails(fullName, email, contact, speciality)) {
                InstructorDTO updatedInstructorDTO = new InstructorDTO(
                        instructorDTO.getInstructorId(),
                        fullName,
                        dob,
                        email,
                        contact,
                        speciality,
                        availability,
                        instuctorBO.getAllLessonsByInstructorId(instructorDTO.getInstructorId())
                );
                
                if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to update instructor details ?")) {
                    if (instuctorBO.update(updatedInstructorDTO)) {
                        setupLists();
                        listInstructors.refresh();
                    } else {
                        AlertUtil.setErrorAlert("Failed to update instructor");
                    }
                }
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update instructor");
            e.printStackTrace();
        }
    }

    private boolean validateInstructorDetails(String name, String email, String contact, String speciality) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String emailPattern = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        String contactPattern = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";

        if (!Auth.areRequiredFieldsFilled(name)){
            errorMsg.append("* Instructor's Name must not be empty\n");
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(email)){
            errorMsg.append("* Email must not be empty!\n");
            isValid = false;
        } else if (!email.matches(emailPattern)){
            errorMsg.append("* Email should be a valid one (ex: john@mail.com) !\n");
            isValid = false;
        }
        if (!Auth.areRequiredFieldsFilled(contact)){
            errorMsg.append("* You must include instructor's contact!\n");
            isValid = false;
        } else if (!contact.matches(contactPattern)){
            errorMsg.append("* Contact should be a valid one (ex: 0721231231 (LK), 4615555679 (US))!\n");
            isValid = false;
        }

        if (insDobPicker.getValue() == null){
            errorMsg.append("* You must include student date of birth!\n");
            isValid = false;
        } else {
            LocalDate birthDate = LocalDate.parse(insDobPicker.getValue().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            if (age < 18 || age > 60){
                errorMsg.append("* Instructor's age must be between 18 and 60 years.\n");
                isValid = false;
            }
        }

        if (!Auth.areRequiredFieldsFilled(speciality)){
            errorMsg.append("* You must include instructor's speciality!\n");
            isValid = false;
        }

        if (!isValid){
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
        }
        return isValid;
    }

    public void setupLists() {
        try {
            listInstructors.getItems().setAll(instuctorBO.getAll());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        listInstructors.setSelectionModel(null);
        listInstructors.setCellFactory(lv -> new ListCell<InstructorDTO>() {
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
                                    "-fx-padding: 12; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                    );

                    // Student details
                    Label lblName = new Label(instructor.getName());
                    lblName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                    Label lblEmail = new Label("📧 " + instructor.getEmail());
                    Label lblContact = new Label("📞 " + instructor.getContact());
                    Label lblAddress = new Label("🟢 " + instructor.getAvailability());
                    Label lblDob = new Label("🎂 " + instructor.getDob().toString());

                    card.getChildren().addAll(lblName, lblEmail, lblContact, lblAddress, lblDob);

                    card.setOnMouseClicked(event -> {
                        instructorDTO = instructor;
                        setupForm(new InstructorDTO(
                                instructor.getInstructorId(),
                                instructor.getName(),
                                instructor.getDob(),
                                instructor.getEmail(),
                                instructor.getContact(),
                                instructor.getSpeciality(),
                                instructor.getAvailability()
                        ));
                    });

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
                    lblDuration.setStyle("-fx-text-fill: gray;");

                    Label lblFees = new Label("Fees: " + course.getFees());
                    lblFees.setStyle("-fx-text-fill: green;");

                    card.getChildren().addAll(lblName, lblDuration, lblFees);

                    setGraphic(card);
                }
            }
        });
    }

    private void setupForm(InstructorDTO instructorDTO) {
        if (instructorDTO != null) {
            try {
                Optional<InstructorDTO> instructor = instuctorBO.findById(instructorDTO.getInstructorId());
                if (instructor.isEmpty()) {
                    AlertUtil.setErrorAlert("Student is not present in the database");
                } else {
                    listCoursesAssigned.getItems().clear();

                    this.instructorDTO = instructor.get();
                    txtInsName.setText(this.instructorDTO.getName());
                    txtInsContact.setText(this.instructorDTO.getContact());
                    txtEmail.setText(this.instructorDTO.getEmail());
                    txtSpeciality.setText(this.instructorDTO.getSpeciality());

                    LocalDate dob = this.instructorDTO.getDob().toLocalDate();
                    insDobPicker.setValue(dob);

                    if ("available".equalsIgnoreCase(this.instructorDTO.getAvailability())) {
                        cbAvailable.setSelected(true);
                        cbNotAvailable.setSelected(false);
                    } else if ("not available".equalsIgnoreCase(this.instructorDTO.getAvailability())) {
                        cbAvailable.setSelected(false);
                        cbNotAvailable.setSelected(true);
                    }

                    List <CourseDTO> enrolledCourses = courseBO.getAllEnrolledCoursesByInsId(instructorDTO.getInstructorId());
                    List <String> enrolledCourseNames = new ArrayList<>();
                    enrolledCourses.forEach(course -> enrolledCourseNames.add(course.getName()));
                    listCoursesAssigned.getItems().setAll(enrolledCourses);

                }

            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load student details in the form");
                throw new RuntimeException(e);
            }
        }
    }

}
