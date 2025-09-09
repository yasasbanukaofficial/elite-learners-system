package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Payment;

public interface PaymentDAO extends CrudDAO<Payment> {
    boolean isIdExisting(String id);
}
