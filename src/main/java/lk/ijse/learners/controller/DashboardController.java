package lk.ijse.learners.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.*;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.dto.PaymentDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public AnchorPane ancDashboard;
    public Label lblStdCount;
    public Label lblInsCount;
    public Label lblCoursesCount;
    public Label lblLsnCount;
    public PieChart countChart;
    public Label lblIncome;
    public ListView<PaymentDTO> listPayment;

    InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.LESSON);
    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            List<PaymentDTO> paymentDTOS = paymentBO.getAll();
            int instructorCount = (instructorBO.getAll() == null || instructorBO.getAll().isEmpty()) ? 0 : instructorBO.getAll().size();
            int studentCount = (studentBO.getAll() == null || studentBO.getAll().isEmpty()) ? 0 : studentBO.getAll().size();
            int courseCount = (courseBO.getAll() == null || courseBO.getAll().isEmpty()) ? 0 : courseBO.getAll().size();
            int lessonCount = (lessonBO.getAll() == null || lessonBO.getAll().isEmpty()) ? 0 : lessonBO.getAll().size();
            int availableInstructorCount = (instructorBO.getAllAvailableInstructors() == null || instructorBO.getAllAvailableInstructors().isEmpty()) ? 0 : instructorBO.getAllAvailableInstructors().size();

            lblInsCount.setText(String.valueOf(instructorCount));
            lblStdCount.setText(String.valueOf(studentCount));
            lblCoursesCount.setText(String.valueOf(courseCount));
            lblLsnCount.setText(String.valueOf(lessonCount));

            PieChart.Data instructorsData = new PieChart.Data("Instructors (" + instructorCount + ")", instructorCount);
            PieChart.Data availableInsData = new PieChart.Data("Available Instructors (" + availableInstructorCount + ")", availableInstructorCount);

            countChart.getData().clear();
            countChart.getData().addAll(instructorsData, availableInsData);
            countChart.setTitle("Instructor and Available Instructor Count");

            setupTotalIncomeLbl(paymentDTOS);
            setupPaymentList(paymentDTOS);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.DASHBOARD).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    try {
                        List<PaymentDTO> payments = paymentBO.getAll();
                        setupPaymentList(payments);
                        setupTotalIncomeLbl(paymentBO.getAll());
                    } catch (Exception e) {
                        AlertUtil.setErrorAlert("Failed to refresh students list");
                        e.printStackTrace();
                    }
                    RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.DASHBOARD, false);
                });
            }
        });
    }

    private void setupPaymentList(List<PaymentDTO> paymentDTOS) {
        listPayment.getItems().setAll(paymentDTOS);
        listPayment.setCellFactory(lv -> new ListCell<PaymentDTO>() {
            @Override
            protected void updateItem(PaymentDTO payment, boolean empty) {
                super.updateItem(payment, empty);

                if (empty || payment == null) {
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

                    try {
                        StudentDTO student = studentBO.findById(payment.getStudentId()).orElse(null);
                        String studentName = student != null ?
                                student.getFirstName() + " " + student.getLastName() :
                                "Unknown Student";

                        Label lblStudent = new Label(studentName);
                        lblStudent.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                        lblStudent.setTextFill(Color.BLACK);

                        Label lblAmount = new Label("💰 " + payment.getAmount().toString());
                        lblAmount.setStyle("-fx-font-size: 12px;");
                        lblAmount.setTextFill(Color.RED);

                        Label lblDate = new Label("📅 " + payment.getPaymentDate().toString());
                        lblDate.setStyle("-fx-font-size: 12px;");
                        lblDate.setTextFill(Color.GREY);

                        Label lblStatus = new Label("🔴 " + payment.getStatus());
                        lblStatus.setStyle("-fx-font-size: 12px;");
                        lblStatus.setTextFill(Color.GREEN);

                        card.getChildren().addAll(lblStudent, lblAmount, lblDate, lblStatus);

                        setGraphic(card);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void setupTotalIncomeLbl(List<PaymentDTO> paymentDTOS) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        for (PaymentDTO paymentDTO : paymentDTOS) {
            totalIncome = totalIncome.add(paymentDTO.getAmount());
        }
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String incomeAsString = formatter.format(totalIncome);
        lblIncome.setText("Rs. " + incomeAsString);
    }

    public void navigateToPage(ViewPath path) {
        try {
            ancDashboard.getChildren().clear();
            AnchorPane pane = FXMLLoader.load(getClass().getResource(path.getPath()));
            pane.prefWidthProperty().bind(ancDashboard.widthProperty());
            pane.prefHeightProperty().bind(ancDashboard.heightProperty());
            ancDashboard.getChildren().add(pane);
        } catch (IOException e) {
            AlertUtil.setErrorAlert("Failed to load page!");
            throw new RuntimeException(e);
        }
    }
}