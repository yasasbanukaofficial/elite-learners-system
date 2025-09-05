package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentCourseDetails {
    private int studentCourseDetailsId;
    private StudentDto student;
    private CourseDto course;
    private Date enrollmentDate;
    private String status;
    private String grade;
}
