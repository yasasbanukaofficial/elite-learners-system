package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentDTO {
    private String studentId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String email;
    private String contactNumber;
    private String address;
    private List<PaymentDTO> payments;
    private List<LessonDTO> lessons;

    public StudentDTO(String studentId, String firstName, String lastName, Date dob, String contactNumber) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.contactNumber = contactNumber;
    }
}
