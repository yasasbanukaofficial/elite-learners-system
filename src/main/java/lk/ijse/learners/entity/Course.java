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

    @Column(name = "course_type", nullable = false, length = 50)
    private String type;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL
    )
    private List<Lesson> lessons;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students;
}
