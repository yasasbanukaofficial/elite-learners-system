package lk.ijse.learners.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @Column(name = "lesson_id",  nullable = false)
    private String lessonId;

    @ManyToOne
    @JoinColumn(name = "inst_id", referencedColumnName = "inst_id")
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "stud_id", referencedColumnName = "stud_id")
    private Student student;

    @Column(name = "lesson_name", nullable = false)
    private String name;

    @Column(name = "lesson_start_time",  nullable = false)
    private Timestamp start_time;

    @Column(name = "lesson_end_time",  nullable = false)
    private Timestamp end_time;

    @Column(name = "lesson_status", nullable = false)
    private boolean status;
}
