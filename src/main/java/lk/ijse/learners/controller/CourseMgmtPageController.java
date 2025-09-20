package lk.ijse.learners.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;
import lk.ijse.learners.entity.Instructor;
import lk.ijse.learners.tm.CourseTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CourseMgmtPageController implements Initializable {
    public AnchorPane ancCourse;
    public StackPane btnOpenCourseForm;

    public TableView <CourseTM> tblCourse;
    public TableColumn <CourseTM, String> colId;
    public TableColumn <CourseTM, String> colCName;
    public TableColumn <CourseTM, String> colDuration;
    public TableColumn <CourseTM, String> colFee;

    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);


    public void openCourseForm(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(ViewPath.ADD_COURSE_FORM.getPath()));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTbl();
        loadTbl();
    }

    private void showTbl() {
        colId.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colCName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
    }

    private void loadTbl() {
        try {
            List<CourseDTO> allCourse = courseBO.getAll();
            tblCourse.setItems(FXCollections.observableList(
                    allCourse.stream().map(
                            course -> new CourseTM(
                                    course.getCourseId(),
                                    course.getName(),
                                    course.getDuration(),
                                    course.getFees()
                            )
                    ).toList()
            ));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load courses to the table");
            e.printStackTrace();
        }
    }
}
