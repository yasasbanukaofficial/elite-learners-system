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
public class EnrollmentContext {
    private static EnrollmentContext enrollmentContext = new EnrollmentContext();

    private StudentDTO studentDTO;
    private PaymentDTO paymentDTO;
    private List<CourseDTO> courseDTO;

    public static EnrollmentContext getInstance() {
        return enrollmentContext == null ? enrollmentContext = new EnrollmentContext() : enrollmentContext;
    }

    public void clear() {
        studentDTO = null;
        paymentDTO = null;
        courseDTO = null;
    }

}
