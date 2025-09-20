package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Lesson;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.Student;

import java.util.List;

public interface StudentDAO extends CrudDAO<Student> {
    boolean existsByField(String field, String fieldValue) throws Exception;
    List<Payment> getAllPayments();
    List<Payment> getAllPaymentsBySid(String sid);
    List<Lesson> getAllLessonsBySid(String sid);
}
