package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;
import lk.ijse.learners.entity.Instructor;
import lk.ijse.learners.entity.Student;

import java.util.List;
import java.util.Optional;

public interface CourseDAO extends CrudDAO<Course> {
    List<Course> fetchCourseListByName(List<String> instructorName) throws Exception;
    List<Course> getAllEnrolledCoursesByStdId(String stdId) throws Exception;
    List<Course> getAllEnrolledCoursesByInsId(String stdId) throws Exception;
    List<Student> getAllStudentsByCourseId (String courseId) throws Exception;
    List<Instructor> getAllInstructorsByCourseId(String id) throws Exception;
    Optional<Course> findByStdName(String firstName, String lastName);
    Optional<Course> findByName(String newSel);
}
