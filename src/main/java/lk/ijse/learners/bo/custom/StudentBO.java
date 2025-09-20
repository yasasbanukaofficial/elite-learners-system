package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.StudentDTO;
import lk.ijse.learners.entity.Lesson;
import lk.ijse.learners.entity.Payment;

import java.util.List;

public interface StudentBO extends CrudBO<StudentDTO> {
    List<Payment> getAllPayments();
    List<Payment> getAllPaymentsBySid(String sid);
    List<Lesson> getAllLessonsBySid(String sid);
}
