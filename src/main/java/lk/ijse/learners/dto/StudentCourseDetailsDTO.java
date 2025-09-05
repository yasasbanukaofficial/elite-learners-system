package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentCourseDetailsDTO {
    private int studentCourseDetailsId;
    private StudentDTO student;
    private CourseDTO course;
    private Date enrollmentDate;
    private String status;
    private String grade;
}
