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
    private Long lessonId;

    @Column(name = "lesson_name", nullable = false)
    private String name;

    @Column(name = "lesson_start_time",  nullable = false)
    private Timestamp start_time;

    @Column(name = "lesson_end_time",  nullable = false)
    private Timestamp end_time;

    @Column(name = "lesson_status", nullable = false)
    private boolean status;
}
