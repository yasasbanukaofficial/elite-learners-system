package lk.ijse.learners.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T> extends SuperDAO {
    List <T> getAll() throws Exception;
    String getLastId() throws Exception;
    boolean save(T entity) throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(String id) throws Exception;
    List<String> getAllIds() throws Exception;
    Optional<T> findById(String id) throws Exception;
}
