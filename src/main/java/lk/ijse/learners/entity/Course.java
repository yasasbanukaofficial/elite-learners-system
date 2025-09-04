package lk.ijse.learners.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "course_description", nullable = false, length = 50)
    private String description;

    @Column(name = "course_type", nullable = false, length = 50)
    private String type;

}
