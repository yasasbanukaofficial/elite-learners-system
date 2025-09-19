package lk.ijse.learners.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.StudentDTO;
import lk.ijse.learners.tm.StudentTM;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StudentMgmtPageController implements Initializable{
    public AnchorPane ancStudent;
    public StackPane btnOpenStdForm;
    public StackPane btnSearch;

    public TableView<StudentTM> tblStudents;
    public TableColumn<StudentTM, String> colSid;
    public TableColumn<StudentTM, String> colFname;
    public TableColumn<StudentTM, String> colLname;
    public TableColumn<StudentTM, String> colDob;
    public TableColumn<StudentTM, String> colContact;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTblColumn();
        loadTbl();
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
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load students");
            e.printStackTrace();
        }
    }
}
