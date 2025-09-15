package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Course;

import java.util.List;

public interface CourseDAO extends CrudDAO<Course> {
    List<Course> fetchCourseListByName(List<String> instructorName) throws Exception;
}
