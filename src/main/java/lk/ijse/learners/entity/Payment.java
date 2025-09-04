package lk.ijse.learners.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "payment_id",  nullable = false)
    private String paymentId;

    @ManyToOne
    @JoinColumn(name = "stud_id", referencedColumnName = "stud_id")
    private Student student;

    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "payment_type", nullable = false)
    private String type;

    @Column(name = "payment_amount", nullable = false,  scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false)
    private boolean status;
}
