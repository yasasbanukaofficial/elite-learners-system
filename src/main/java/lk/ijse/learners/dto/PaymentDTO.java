package lk.ijse.learners.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentDTO {
    private String paymentId;
    private StudentDTO student;
    private Date paymentDate;
    private String type;
    private BigDecimal amount;
    private boolean status;
}
