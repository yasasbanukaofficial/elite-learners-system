package lk.ijse.learners.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "course")
public class Course {
    @Id
    @Column(name = "course_id", nullable = false)
    private String courseId;

    @ManyToOne
    @JoinColumn(name = "inst_id", referencedColumnName = "inst_id")
    private Instructor instructor;

    @Column(name = "course_name", nullable = false, length = 50)
    private String name;

    @Column(name = "course_description", nullable = false, length = 200)
    private String description;

    @Column(name = "course_duration", nullable = false, length = 200)
    private String duration;

    @Column(name = "course_fee", nullable = false, length = 20)
    private String fees;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL
    )
    private List<Lesson> lessons;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL
    )
    private List<StudentCourseDetails> studentCourseDetails;
}
