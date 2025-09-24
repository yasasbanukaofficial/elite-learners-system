package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.User;

public interface UserDAO extends CrudDAO<User> {
    boolean existsByField(String field, String fieldValue) throws Exception;
}
