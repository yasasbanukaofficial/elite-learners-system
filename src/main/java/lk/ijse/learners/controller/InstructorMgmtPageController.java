package lk.ijse.learners.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.InstructorBO;
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
    public ListView<InstructorDTO> listInstructors;

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

        try {
            listInstructors.getItems().addAll(instuctorBO.getAll());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        listInstructors.setCellFactory(lv -> new ListCell<InstructorDTO>() {
            @Override
            protected void updateItem(InstructorDTO instructor, boolean empty) {
                super.updateItem(instructor, empty);

                if (empty || instructor == null) {
                    setGraphic(null);
                } else {
                    // Card container
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
                    Label lblAvailability = new Label("🟢 " + instructor.getAvailability());
                    Label lblDob = new Label("🎂 " + instructor.getDob().toString());

                    card.getChildren().addAll(lblName, lblEmail, lblContact, lblAvailability, lblDob);

                    card.setOnMouseClicked(event -> {
                        System.out.println("Clicked instructor: " + instructor.getName());
                    });

                    setGraphic(card);
                }
            }
        });
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
