package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.bo.custom.PaymentBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.PaymentDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentMgmtPageController implements Initializable {
    public AnchorPane ancPayment;
    public TextField txtPayType;
    public TextField txtAmount;
    public DatePicker dpPaymentDate;
    public CheckBox cbPaid;
    public CheckBox cbNotPaid;
    public ListView<StudentDTO> listLinkedStd;
    public Button btnDeletePay;
    public Button btnEdit;
    public ListView<PaymentDTO> listPayment;
    
    private PaymentDTO paymentDTO;
    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);
    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupList();

        cbPaid.setOnAction(e -> {
            if (cbPaid.isSelected()) {
                cbNotPaid.setSelected(false);
            }
        });
        cbNotPaid.setOnAction(e -> {
            if (cbNotPaid.isSelected()) {
                cbPaid.setSelected(false);
            }
        });

    }

    public void editPayment(ActionEvent actionEvent) {
        String payType = txtPayType.getText().trim();
        String amount = txtAmount.getText().trim();
        String status = cbPaid.isSelected() ? "paid" : cbNotPaid.isSelected() ? "not paid" : null;

        Date paymentDate = Date.valueOf(dpPaymentDate.getValue());

        boolean unchanged = payType.equals(paymentDTO.getType()) &&
                amount.equals(paymentDTO.getAmount().toString()) &&
                status.equals(paymentDTO.getStatus()) &&
                paymentDate.equals(paymentDTO.getPaymentDate());

        if (unchanged) {
            AlertUtil.setErrorAlert("No changes made");
            return;
        }

        try {
            if (validatePaymentDetails(payType, amount, status)) {
                PaymentDTO updatedPaymentDTO = new PaymentDTO(
                        paymentDTO.getPaymentId(),
                        paymentBO.getStudentsByPaymentId(paymentDTO.getPaymentId()).getStudentId(),
                        paymentDate,
                        payType,
                        new BigDecimal(amount),
                        status
                );

                if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to update payment details?")) {
                    if (paymentBO.update(updatedPaymentDTO)) {
                        setupList();
                        listPayment.refresh();
                        RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.DASHBOARD, true);
                    } else {
                        AlertUtil.setErrorAlert("Failed to update payment");
                    }
                }
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update payment");
            e.printStackTrace();
        }
    }

    private boolean validatePaymentDetails(String payType, String amount, String status) {
        if (payType.isEmpty()) {
            AlertUtil.setErrorAlert("Payment type cannot be empty");
            return false;
        }
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                AlertUtil.setErrorAlert("Amount must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.setErrorAlert("Invalid amount value");
            return false;
        }
        if (status == null) {
            AlertUtil.setErrorAlert("Payment status must be selected");
            return false;
        }
        return true;
    }

    private void setupList() {
        try {
            List<PaymentDTO> paymentDTOList = paymentBO.getAll();
            listPayment.getItems().setAll(paymentDTOList);
            if(!paymentDTOList.isEmpty()) {
                listPayment.getSelectionModel().select(0);
                setupForm(paymentDTOList.get(0));
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Nothing to showcase since there are no payments made");
            listPayment.getItems().clear();
        }
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
                        lblStudent.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                        lblStudent.setTextFill(Color.BLACK);
                        Label lblAmount = new Label("💰 " + payment.getAmount().toString());
                        lblAmount.setTextFill(Color.RED);
                        Label lblDate = new Label("📅 " + payment.getPaymentDate().toString());
                        lblDate.setTextFill(Color.GREY);
                        Label lblStatus = new Label("🔴 " + payment.getStatus());
                        lblStatus.setTextFill(Color.GREEN);

                        card.getChildren().addAll(lblStudent, lblAmount, lblDate, lblStatus);

                        card.setOnMouseClicked(event -> {
                            setupForm(payment);
                        });

                        setGraphic(card);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        listLinkedStd.setCellFactory(lv -> new ListCell<StudentDTO>() {
            @Override
            protected void updateItem(StudentDTO student, boolean empty) {
                super.updateItem(student, empty);

                if (empty || student == null) {
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

                    Label lblName = new Label(student.getFirstName() + " " + student.getLastName());
                    lblName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label lblId = new Label("ID: " + student.getStudentId());
                    lblId.setStyle("-fx-text-fill: gray;");

                    card.getChildren().addAll(lblName, lblId);
                    setGraphic(card);
                }
            }
        });
    }

    private void setupForm(PaymentDTO paymentDTO) {
        if (paymentDTO != null) {
            try {
                Optional<PaymentDTO> payment = paymentBO.findById(paymentDTO.getPaymentId());
                if (payment.isEmpty()) {
                    AlertUtil.setErrorAlert("Payment is not present in the database");
                } else {
                    listLinkedStd.getItems().clear();

                    this.paymentDTO = payment.get();
                    txtPayType.setText(this.paymentDTO.getType());
                    txtAmount.setText(this.paymentDTO.getAmount().toString());
                    dpPaymentDate.setValue(this.paymentDTO.getPaymentDate().toLocalDate());

                    if ("paid".equalsIgnoreCase(this.paymentDTO.getStatus())) {
                        cbPaid.setSelected(true);
                        cbNotPaid.setSelected(false);
                    } else if ("not paid".equalsIgnoreCase(this.paymentDTO.getStatus())) {
                        cbPaid.setSelected(false);
                        cbNotPaid.setSelected(true);
                    }

                    StudentDTO studentDTO  = paymentBO.getStudentsByPaymentId(paymentDTO.getPaymentId());
                    listLinkedStd.getItems().setAll(studentDTO);
                }
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to load payment details in the form");
                throw new RuntimeException(e);
            }
        }
    }
}
