package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.Student;

import java.util.List;

public interface PaymentDAO extends CrudDAO<Payment> {
    boolean isIdExisting(String id);
    Student getStudentsByPaymentId(String payId);
}
