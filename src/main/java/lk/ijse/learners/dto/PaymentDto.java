package lk.ijse.learners.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentDto {
    private String paymentId;
    private StudentDto student;
    private Date paymentDate;
    private String type;
    private BigDecimal amount;
    private boolean status;
}
