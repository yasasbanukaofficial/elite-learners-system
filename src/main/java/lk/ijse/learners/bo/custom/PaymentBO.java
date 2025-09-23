package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.PaymentDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.List;

public interface PaymentBO extends CrudBO<PaymentDTO> {
    boolean isIdExisting(String id);
    StudentDTO getStudentsByPaymentId(String payId) throws Exception;
}

