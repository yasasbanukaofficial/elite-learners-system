package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Instructor;
import lk.ijse.learners.entity.Lesson;

import java.util.List;

public interface InstructorDAO extends CrudDAO<Instructor> {
    List<String> getAllAvailableInstructors() throws Exception;
    List<Instructor> fetchInstructorListByName(List<String> instructorName) throws Exception;
    List<Lesson> getAllLessonsByInstructorId(String instructorId) throws Exception;
    java.util.Optional<Instructor> findByName(String name) throws Exception;
}
