package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentUnitOfWork;
import lk.ijse.learners.bo.custom.PaymentBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.controller.auth.Auth;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.PaymentDTO;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddPaymentFormController implements Initializable {
    public AnchorPane ancAddPayment;
    public Label lblPayId;
    public ImageView btnCloseStdForm;
    public DatePicker dpPayDate;
    public TextField txtAmount;
    public CheckBox cbCard;
    public CheckBox cbCash;
    public Button btnCancel;
    public Button btnPay;

    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);

    private final EnrollmentUnitOfWork enrollmentUnitOfWork = EnrollmentUnitOfWork.getInstance();

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

    public void closePaymentForm(ActionEvent actionEvent) {
        Stage window = (Stage) ancAddPayment.getScene().getWindow();
        window.close();
    }

    public void addPayment(ActionEvent actionEvent) {
        String payId = lblPayId.getText();
        String studentId = "";
        Date payDate = Date.valueOf(LocalDate.now());
        double amount = Double.parseDouble(txtAmount.getText());
        String type = cbCard.isSelected() ? "card" : cbCash.isSelected() ? "cash" : null;
        String status = "paid";

        if (validateStudentDetails(amount, type, status)) {
            enrollmentUnitOfWork.setPaymentDTO(new PaymentDTO(
                    payId,
                    studentId,
                    payDate,
                    type,
                    new BigDecimal(amount),
                    status
            ));
        }
        closePaymentForm(actionEvent);
    }

    private boolean validateStudentDetails(double amount, String type, String status) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

        if (!Auth.areRequiredFieldsFilled(String.valueOf(amount))){
            txtAmount.setStyle(errorStyle);
            errorMsg.append("Please enter a the amount to pay\n");
            isValid = false;
        } else if (amount < 0.00) {
            txtAmount.setStyle(errorStyle);
            errorMsg.append("Amount must be greater than zero.\n");
            isValid = false;
        }

        if (!Auth.areRequiredFieldsFilled(type)){
            cbCard.setStyle(errorStyle);
            cbCash.setStyle(errorStyle);
            errorMsg.append("Please select a payment type.\n");
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

    public void exitPaymentForm(MouseEvent mouseEvent) {
        closePaymentForm(new ActionEvent());
    }
}
