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

    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);
    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       lblPayId.setText(loadNextId());
       dpPayDate.setValue(LocalDate.now());
       cbCard.setOnAction(e -> {
           if (cbCard.isSelected()) {
               cbCash.setSelected(false);
           }
       });
       cbCash.setOnAction(e -> {
           if (cbCash.isSelected()) {
               cbCard.setSelected(false);
           }
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
        String studentId = "";
        Date payDate = Date.valueOf(LocalDate.now());
        String amount = txtAmount.getText();
        String type = cbCard.isSelected() ? "card" : cbCash.isSelected() ? "cash" : null;
        String status = "paid";

        if (validateStudentDetails(amount, type)) {
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


    // Utility methods
    private boolean validateStudentDetails(String amount, String type) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;


        String amountPattern = "^(?:-(?:[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*))|(?:0|(?:[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*))))(?:.\\d+|)$";
        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String checkBoxStyle = "-fx-border-color: transparent; -fx-background-color: transparent; -fx-border-radius: 0px; -fx-border-width: 0px; -fx-background-radius: 10px";

        // Initial State of UI Components
        txtAmount.setStyle(normalStyle);
        cbCard.setStyle(checkBoxStyle);
        cbCash.setStyle(checkBoxStyle);

        if (!Auth.areRequiredFieldsFilled(String.valueOf(amount))){
            txtAmount.setStyle(errorStyle);
            errorMsg.append(" * Please enter a the amount to pay\n");
            isValid = false;
        }
        if (!amount.matches(amountPattern) || Double.parseDouble(amount) < 0.00) {
            txtAmount.setStyle(errorStyle);
            errorMsg.append(" * Amount must be an integer an it should be greater than zero.\n");
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(type)){
            cbCard.setStyle(errorStyle);
            cbCash.setStyle(errorStyle);
            errorMsg.append(" * Please select a payment type.\n");
            isValid = false;
        }

        if (!isValid){
            AlertUtil.setErrorAlert("Please solve these issues before proceeding \n\n" + errorMsg.toString());
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
