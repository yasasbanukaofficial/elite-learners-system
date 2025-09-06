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
    private String studentId;
    private Date paymentDate;
    private String type;
    private BigDecimal amount;
    private String status;
}
