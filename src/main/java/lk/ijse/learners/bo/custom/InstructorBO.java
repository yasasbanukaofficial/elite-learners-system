package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.CrudBO;
import lk.ijse.learners.dto.InstructorDTO;

import java.util.List;

public interface InstructorBO extends CrudBO<InstructorDTO> {
    List<String> getAllAvailableInstructors() throws Exception;
    List<InstructorDTO> fetchInstructorListByName(List<String> instructorName) throws Exception;
}

