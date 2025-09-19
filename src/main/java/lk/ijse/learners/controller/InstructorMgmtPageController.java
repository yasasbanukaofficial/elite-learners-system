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
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.custom.impl.InstructorBOImpl;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.tm.InstructorTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    
    InstructorBO instuctorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);

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
        setupTblColumn();
        loadTbl();
    }
    
    private void setupTblColumn() {
        colInsId.setCellValueFactory(new PropertyValueFactory<>("instructorId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colSpeciality.setCellValueFactory(new PropertyValueFactory<>("speciality"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }
    
    private void loadTbl() {
        try {
            List<InstructorDTO> allInstructors = instuctorBO.getAll();
            tblInstructor.setItems(FXCollections.observableList(
                    allInstructors.stream().map(
                            instructorDTO -> new InstructorTM(
                                    instructorDTO.getInstructorId(),
                                    instructorDTO.getName(),
                                    instructorDTO.getContact(),
                                    instructorDTO.getSpeciality(),
                                    instructorDTO.getAvailability()
                            )
                    ).toList()
            ));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load instructors");
            e.printStackTrace();
        }
    }
}
