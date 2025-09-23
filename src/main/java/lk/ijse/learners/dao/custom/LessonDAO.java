package lk.ijse.learners.dao.custom;

import lk.ijse.learners.dao.CrudDAO;
import lk.ijse.learners.entity.Course;
import lk.ijse.learners.entity.Instructor;
import lk.ijse.learners.entity.Lesson;
import lk.ijse.learners.entity.Student;

public interface LessonDAO extends CrudDAO<Lesson> {
    Student getAllStudentsByLessonId(String lessonId) throws Exception;
    Instructor getAllInstructorsByLessonId(String lessonId) throws Exception;
    Course getAllCoursesByLessonId(String lessonId) throws Exception;
}
