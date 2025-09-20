package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;

import java.util.List;

public interface CourseBO extends CrudBO<CourseDTO> {
    List<Course> fetchCourseListByName(List<String> courseName) throws Exception;
    List<Course> getAllEnrolledCoursesByStdId(String stdId) throws Exception;
}

