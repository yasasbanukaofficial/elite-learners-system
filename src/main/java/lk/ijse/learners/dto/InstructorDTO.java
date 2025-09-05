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
    private int instructorId;
    private String name;
    private Date dob;
    private String email;
    private String contact;
    private String speciality;
    private boolean availability;
    private List<CourseDTO> courses;
    private List<LessonDTO> lessons;
}
