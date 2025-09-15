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
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "stud_id", unique = true, nullable = false)
    private String studentId;

    @Column(name = "stud_fname", nullable = false, length = 50)
    private String firstName;

    @Column(name = "stud_lname", nullable = false, length = 50)
    private String lastName;

    @Column(name = "stud_dob", nullable = false)
    private Date dob;

    @Column(name = "stud_email", nullable = false)
    private String email;

    @Column(name = "stud_contact", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "stud_address", nullable = false)
    private String address;

    @OneToMany(
            mappedBy = "student",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private List<Payment> payments;

    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL
    )
    private List<Lesson> lessons;
}
