package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

public interface LessonBO extends CrudBO<LessonDTO> {
    StudentDTO getAllStudentsByLessonId(String lessonId) throws Exception;
    InstructorDTO getAllInstructorsByLessonId(String lessonId) throws Exception;
    CourseDTO getAllCoursesByLessonId(String lessonId) throws Exception;
}

