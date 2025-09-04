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
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "payment_id",  nullable = false)
    private String paymentId;

    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "payment_type", nullable = false)
    private String type;

    @Column(name = "payment_amount", nullable = false,  scale = 2)
    private float amount;

    @Column(name = "payment_status", nullable = false)
    private boolean status;
}
