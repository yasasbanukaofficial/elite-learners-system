package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.StudentDAO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public class StudentBOImpl implements StudentBO {
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    @Override
    public List<StudentDTO> getAll() throws Exception {
        return entityDTOConverter.toStudentDTOList(studentDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return studentDAO.getLastId();
    }

    @Override
    public boolean save(StudentDTO studentDto) throws Exception {
        return studentDAO.save(entityDTOConverter.getStudentEntity(studentDto));
    }

    @Override
    public boolean update(StudentDTO dto) throws Exception {
        return studentDAO.update(entityDTOConverter.getStudentEntity(dto));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return studentDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return studentDAO.getAllIds();
    }

    @Override
    public Optional<StudentDTO> findById(String id) throws Exception {
        return studentDAO.findById(id).map(student -> {
            try {
                return entityDTOConverter.getStudentDTO(student);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert Student to DTO, findById", e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "STD-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, 1);
    }
}
