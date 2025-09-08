package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.StudentCourseDetailsBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.StudentCourseDetailsDAO;
import lk.ijse.learners.dto.StudentCourseDetailsDTO;

import java.util.List;
import java.util.Optional;

public class StudentCourseDetailsBOImpl implements StudentCourseDetailsBO {
    private final StudentCourseDetailsDAO studentCourseDetailsDAO = (StudentCourseDetailsDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT_COURSE_DETAILS);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    @Override
    public List<StudentCourseDetailsDTO> getAll() throws Exception {
        return entityDTOConverter.toStudentCourseDetailsDTOList(studentCourseDetailsDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return studentCourseDetailsDAO.getLastId();
    }

    @Override
    public boolean save(StudentCourseDetailsDTO studentCourseDetailsDTO) throws Exception {
        return studentCourseDetailsDAO.save(entityDTOConverter.getStudentCourseDetailsEntity(studentCourseDetailsDTO));
    }

    @Override
    public boolean update(StudentCourseDetailsDTO studentCourseDetailsDTO) throws Exception {
        return studentCourseDetailsDAO.update(entityDTOConverter.getStudentCourseDetailsEntity(studentCourseDetailsDTO));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return studentCourseDetailsDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return studentCourseDetailsDAO.getAllIds();
    }

    @Override
    public Optional<StudentCourseDetailsDTO> findById(String id) throws Exception {
        return studentCourseDetailsDAO.findById(id).map(studentCourseDetails -> {
            try {
                return entityDTOConverter.getStudentCourseDetailsDTO(studentCourseDetails);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert StudentCourseDetails to DTO, findById", e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "SCD-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, "001");
    }
}


