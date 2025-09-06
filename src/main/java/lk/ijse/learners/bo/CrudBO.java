package lk.ijse.learners.bo;

import java.util.List;
import java.util.Optional;

public interface CrudBO<T> extends SuperBO {
    List<T> getAll() throws Exception;
    String getLastId() throws Exception;
    boolean save(T entity) throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(String id) throws Exception;
    List<String> getAllIds() throws Exception;
    Optional<T> findById(String id) throws Exception;
}
