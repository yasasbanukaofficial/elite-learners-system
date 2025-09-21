package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseDAO extends CrudDAO<Course> {
    List<Course> fetchCourseListByName(List<String> instructorName) throws Exception;
    List<Course> getAllEnrolledCoursesByStdId(String stdId) throws Exception;
    Optional<Course> findByName(String newSel);
}
