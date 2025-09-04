package lk.ijse.learners.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "stud_id", unique = true, nullable = false)
    private String studentId;

    @Column(name = "stud_fname", nullable = false, length = 50)
    private String firstName;

    @Column(name = "stud_lname", nullable = false, length = 50)
    private String lastName;

    @Column(name = "student_dob", nullable = false)
    private Date dob;

    @Column(name = "student_email", nullable = false, unique = true)
    private String email;

    @Column(name = "student_password", nullable = false, length = 50)
    private String password;

    @Column(name = "stud_contact", nullable = false, unique = true, length = 15)
    private String contactNumber;

    @Column(name = "stud_address", nullable = false)
    private String address;

}
