package lk.ijse.learners.bo.context;

import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
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
    private static EnrollmentContext enrollmentContext;

    private StudentDTO studentDTO;
    private PaymentDTO paymentDTO;
    private InstructorDTO instructorDTO;
    private List<CourseDTO> courseDTOList;

    public static EnrollmentContext getInstance() {
        return enrollmentContext == null ? enrollmentContext = new EnrollmentContext() : enrollmentContext;
    }

    public void clear() {
        studentDTO = null;
        paymentDTO = null;
        instructorDTO = null;
        courseDTOList = null;
    }

}
