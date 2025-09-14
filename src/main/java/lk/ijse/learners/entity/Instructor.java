package lk.ijse.learners.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "instructor")
public class Instructor {
    @Id
    @Column(name = "inst_id",  unique = true, nullable = false)
    private String instructorId;

    @Column(name = "inst_name", nullable = false)
    private String name;

    @Column(name = "inst_dob", nullable = false)
    private Date dob;

    @Column(name = "inst_email", nullable = false, unique = true)
    private String email;

    @Column(name = "inst_contact", nullable = false, length = 15)
    private String contact;

    @Column(name = "inst_speciality", nullable = false, length = 15)
    private String speciality;

    @Column(name = "inst_availability", nullable = false)
    private String availability;

    @ManyToMany(mappedBy = "instructors", fetch = FetchType.EAGER)
    private List<Course> courses;

    @OneToMany(
            mappedBy = "instructor",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<Lesson> lessons;
}
