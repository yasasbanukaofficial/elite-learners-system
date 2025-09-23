package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.entity.Lesson;

import java.util.List;
import java.util.Optional;

public interface InstructorBO extends CrudBO<InstructorDTO> {
    List<String> getAllAvailableInstructors() throws Exception;
    List<InstructorDTO> fetchInstructorListByName(List<String> instructorName) throws Exception;
    List<LessonDTO> getAllLessonsByInstructorId(String instructorId) throws Exception;
    Optional<InstructorDTO> findByName(String name) throws Exception;
}
