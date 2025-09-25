package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.custom.PaymentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.PaymentDTO;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddPaymentFormController implements Initializable {
    public AnchorPane ancAddPayment;

    public Label lblPayId;
    public DatePicker dpPayDate;
    public TextField txtAmount;

    public CheckBox cbCard;
    public CheckBox cbCash;

    public Button btnCancel;
    public Button btnPay;
    public ImageView btnCloseStdForm;

    public CheckBox cbPaid;
    public CheckBox cbNotPaid;

    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);
    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblPayId.setText(loadNextId());
        dpPayDate.setValue(LocalDate.now());

        cbCard.setOnAction(e -> {
            if (cbCard.isSelected()) cbCash.setSelected(false);
        });
        cbCash.setOnAction(e -> {
            if (cbCash.isSelected()) cbCard.setSelected(false);
        });

        cbPaid.setOnAction(e -> {
            if (cbPaid.isSelected()) cbNotPaid.setSelected(false);
        });
        cbNotPaid.setOnAction(e -> {
            if (cbNotPaid.isSelected()) cbPaid.setSelected(false);
        });
    }

    @FXML
    public void exitPaymentForm(MouseEvent mouseEvent) {
        WindowManagerUtil.closeForm(ancAddPayment);
    }

    @FXML
    public void closePaymentForm(ActionEvent actionEvent) {
        WindowManagerUtil.closeForm(ancAddPayment);
    }

    @FXML
    public void addPayment(ActionEvent actionEvent) {
        String payId = lblPayId.getText();
        String studentId = enrollmentContext.getStudentDTO() != null ? enrollmentContext.getStudentDTO().getStudentId() : "";
        Date payDate = Date.valueOf(dpPayDate.getValue());
        String amount = txtAmount.getText();
        String type = cbCard.isSelected() ? "card" : cbCash.isSelected() ? "cash" : null;
        String status = cbPaid.isSelected() ? "paid" : cbNotPaid.isSelected() ? "not paid" : null;

        if (validatePaymentDetails(amount, type, status)) {
            enrollmentContext.setPaymentDTO(new PaymentDTO(
                    payId,
                    studentId,
                    payDate,
                    type,
                    new BigDecimal(Double.parseDouble(amount)),
                    status
            ));
            WindowManagerUtil.closeForm(ancAddPayment);
        }
    }

    // Validation
    private boolean validatePaymentDetails(String amount, String type, String status) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String amountPattern = "^(?:-(?:[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*))|(?:0|(?:[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*))))(?:\\.\\d+|)$";
        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        txtAmount.setStyle(normalStyle);
        cbCard.setStyle("-fx-border-color: transparent;");
        cbCash.setStyle("-fx-border-color: transparent;");
        cbPaid.setStyle("-fx-border-color: transparent;");
        cbNotPaid.setStyle("-fx-border-color: transparent;");

        // Amount validation
        if (!Auth.areRequiredFieldsFilled(amount)) {
            txtAmount.setStyle(errorStyle);
            errorMsg.append(" * Please enter the amount.\n");
            isValid = false;
        } else if (!amount.matches(amountPattern) || Double.parseDouble(amount) <= 0.00) {
            txtAmount.setStyle(errorStyle);
            errorMsg.append(" * Amount must be a positive number.\n");
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(type)) {
            cbCard.setStyle(errorStyle);
            cbCash.setStyle(errorStyle);
            errorMsg.append(" * Please select a payment type.\n");
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(status)) {
            cbPaid.setStyle(errorStyle);
            cbNotPaid.setStyle(errorStyle);
            errorMsg.append(" * Please select payment status (Paid/Not Paid).\n");
            isValid = false;
        }

        if (!isValid) {
            AlertUtil.setErrorAlert("Please fix the following issues:\n\n" + errorMsg.toString());
        }
        return isValid;
    }

    private String loadNextId() {
        try {
            return paymentBO.loadNextId();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load next id");
            e.printStackTrace();
            return "PAY-000";
        }
    }
}
