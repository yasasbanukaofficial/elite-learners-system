package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentCourseDetailsDTO {
    private String studentCourseDetailsId;
    private String studentId;
    private String courseId;
    private Date enrollmentDate;
    private String status;
    private String grade;
}
