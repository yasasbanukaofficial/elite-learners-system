package lk.ijse.learners.bo.context;

import lk.ijse.learners.dto.*;
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
    private LessonDTO lessonDTO;
    private CourseDTO courseDTO;
    private List<CourseDTO> courseDTOList;
    private List<StudentDTO> stdDTOList;

    public static EnrollmentContext getInstance() {
        return enrollmentContext == null ? enrollmentContext = new EnrollmentContext() : enrollmentContext;
    }

    public void clear() {
        studentDTO = null;
        paymentDTO = null;
        instructorDTO = null;
        courseDTO = null;
        lessonDTO = null;
        courseDTOList = null;
        stdDTOList = null;
    }

}
