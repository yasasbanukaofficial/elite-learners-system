package lk.ijse.learners.tm;

import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.PaymentDTO;
import lombok.*;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentTM {
    private String studentId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String contactNumber;
}
