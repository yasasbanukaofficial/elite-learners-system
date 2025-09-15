package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InstructorDTO {
    private String instructorId;
    private String name;
    private Date dob;
    private String email;
    private String contact;
    private String speciality;
    private String availability;
    private List<LessonDTO> lessons;
}
