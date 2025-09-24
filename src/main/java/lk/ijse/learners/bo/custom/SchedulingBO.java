package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.SuperBO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.Optional;

public interface SchedulingBO extends SuperBO {
    boolean scheduleLesson(LessonDTO lessonDTO);
    boolean updateScheduleLesson(LessonDTO lessonDTO);
    boolean removeInstructorFromLesson(LessonDTO lessonDTO);
    boolean cancelLesson(String lessonId);
    boolean editEnrolledStudent(LessonDTO lessonDTO, String newStudentId);
}
