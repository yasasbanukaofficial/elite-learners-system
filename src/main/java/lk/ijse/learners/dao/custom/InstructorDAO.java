package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Instructor;

import java.util.List;

public interface InstructorDAO extends CrudDAO<Instructor> {
    List<String> getAllAvailableInstructors() throws Exception;
    List<Instructor> fetchInstructorListByName(List<String> instructorName) throws Exception;
}
