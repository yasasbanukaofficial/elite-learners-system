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

    @Column(name = "course_name", nullable = false, length = 50)
    private String name;

    @Column(name = "course_description", nullable = false, length = 200)
    private String description;

    @Column(name = "course_duration", nullable = false, length = 200)
    private String duration;

    @Column(name = "course_fee", nullable = false, length = 20)
    private String fees;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_instructor_associate",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "inst_id")
    )
    private List<Instructor> instructors;

    @OneToMany(
            mappedBy = "course",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<Lesson> lessons;

    @OneToMany(
            mappedBy = "course",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<StudentCourseDetails> studentCourseDetails;
}
