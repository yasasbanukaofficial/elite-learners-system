package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.StudentDTO;
import lk.ijse.learners.entity.Lesson;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentBO extends CrudBO<StudentDTO> {
    List<Payment> getAllPayments();
    List<Payment> getAllPaymentsBySid(String sid);
    List<Lesson> getAllLessonsBySid(String sid);
    Optional<StudentDTO> findByStudentName(String fName, String lName);
}
