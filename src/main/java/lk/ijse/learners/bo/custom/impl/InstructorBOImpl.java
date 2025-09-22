package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.InstructorDAO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InstructorBOImpl implements InstructorBO {
    private final InstructorDAO instructorDAO = (InstructorDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.INSTRUCTOR);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    @Override
    public List<InstructorDTO> getAll() throws Exception {
        return entityDTOConverter.toInstructorDTOList(instructorDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return instructorDAO.getLastId();
    }

    @Override
    public boolean save(InstructorDTO instructorDTO) throws Exception {
        return instructorDAO.save(entityDTOConverter.getInstructorEntity(instructorDTO));
    }

    @Override
    public boolean update(InstructorDTO instructorDTO) throws Exception {
        return instructorDAO.update(entityDTOConverter.getInstructorEntity(instructorDTO));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return instructorDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return instructorDAO.getAllIds();
    }

    @Override
    public Optional<InstructorDTO> findById(String id) throws Exception {
        return instructorDAO.findById(id).map(instructor -> {
            try {
                return entityDTOConverter.getInstructorDTO(instructor);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert Instructor to DTO, findById", e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "INS-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, 1);
    }

    @Override
    public List<String> getAllAvailableInstructors() throws Exception {
        return instructorDAO.getAllAvailableInstructors() == null || instructorDAO.getAllAvailableInstructors().isEmpty() ? new ArrayList<>() : instructorDAO.getAllAvailableInstructors();
    }

    @Override
    public List<InstructorDTO> fetchInstructorListByName(List<String> instructorName) throws Exception {
        return entityDTOConverter.toInstructorDTOList(instructorDAO.fetchInstructorListByName(instructorName));
    }

    @Override
    public List<LessonDTO> getAllLessonsByInstructorId(String instructorId) throws Exception {
        return entityDTOConverter.toLessonDTOList(instructorDAO.getAllLessonsByInstructorId(instructorId));
    }
}

