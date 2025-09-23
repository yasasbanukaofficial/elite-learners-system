package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface CourseBO extends CrudBO<CourseDTO> {
    List<CourseDTO> fetchCourseListByName(List<String> courseName) throws Exception;
    List<CourseDTO> getAllEnrolledCoursesByStdId(String stdId) throws Exception;
    List<CourseDTO> getAllEnrolledCoursesByInsId(String stdId) throws Exception;
    List<StudentDTO> getAllStudentsByCourseId (String courseId) throws Exception;
    List<InstructorDTO> getAllInstructorsByCourseId(String id) throws Exception;
    Optional<CourseDTO> findByStdName(String firstName, String lastName);
    Optional<CourseDTO> findByName(String newSel);
}

