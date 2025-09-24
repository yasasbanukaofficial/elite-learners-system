package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.bo.SuperBO;
import lk.ijse.learners.dto.UserDTO;
import lk.ijse.learners.entity.User;

import java.util.Optional;

public interface UserBO extends CrudBO<UserDTO> {
    boolean existsByUsername(String username);
    Optional<User> findByName(String username);
}
