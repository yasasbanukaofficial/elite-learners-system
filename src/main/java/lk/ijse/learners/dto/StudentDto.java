package lk.ijse.learners.dto;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentDto {
    private String studentId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String email;
    private String password;
    private String contactNumber;
    private String address;
    private List<PaymentDto> payments;
    private List<LessonDto> lessons;
    private List<StudentCourseDetails> studentCourseDetails;
}
