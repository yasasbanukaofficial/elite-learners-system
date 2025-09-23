package lk.ijse.learners.bo.custom;

import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.Optional;

public interface SchedulingBO {
    boolean scheduleLesson(LessonDTO lessonDTO);
}
