package lk.ijse.learners.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private String userId;
    private String name;
    private String age;
    private String email;
    private String password;
    private String contactNumber;
    private String role;
}
