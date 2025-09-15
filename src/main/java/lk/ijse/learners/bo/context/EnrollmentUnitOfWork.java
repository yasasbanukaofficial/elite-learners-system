package lk.ijse.learners.bo.context;

import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.PaymentDTO;
import lk.ijse.learners.dto.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class EnrollmentUnitOfWork {
    private static EnrollmentUnitOfWork enrollmentUnitOfWork = new EnrollmentUnitOfWork();

    private StudentDTO studentDTO;
    private PaymentDTO paymentDTO;
    private List<CourseDTO> courseDTO;

    public static EnrollmentUnitOfWork getInstance() {
        return enrollmentUnitOfWork == null ? enrollmentUnitOfWork = new EnrollmentUnitOfWork() : enrollmentUnitOfWork;
    }

    public void clear() {
        studentDTO = null;
        paymentDTO = null;
        courseDTO = null;
    }

}
