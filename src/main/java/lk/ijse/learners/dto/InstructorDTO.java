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

    public InstructorDTO(String instructorId, String name, Date dob, String email, String contact, String speciality, String availability) {
        this.instructorId = instructorId;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.contact = contact;
        this.speciality = speciality;
        this.availability = availability;
    }
}
