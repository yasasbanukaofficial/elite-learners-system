package lk.ijse.learners.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "student_course_details")
public class StudentCourseDetails {
    @Id
    @Column(name = "stud_course_id")
    private String studentCourseDetailsId;

    @ManyToOne
    @JoinColumn(name = "stud_id", referencedColumnName = "stud_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course;

    @Column(name = "enrolled_date", nullable = false)
    private Date enrollmentDate;

    @Column(name = "completion_status", nullable = false)
    private String status;

    @Column(name = "grade")
    private String grade;
}
