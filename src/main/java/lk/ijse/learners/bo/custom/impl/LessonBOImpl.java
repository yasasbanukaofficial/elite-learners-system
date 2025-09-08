package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.LessonBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.LessonDAO;
import lk.ijse.learners.dto.LessonDTO;

import java.util.List;
import java.util.Optional;

public class LessonBOImpl implements LessonBO {
    private final LessonDAO lessonDAO = (LessonDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.LESSON);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    @Override
    public List<LessonDTO> getAll() throws Exception {
        return entityDTOConverter.toLessonDTOList(lessonDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return lessonDAO.getLastId();
    }

    @Override
    public boolean save(LessonDTO lessonDTO) throws Exception {
        return lessonDAO.save(entityDTOConverter.getLessonEntity(lessonDTO));
    }

    @Override
    public boolean update(LessonDTO lessonDTO) throws Exception {
        return lessonDAO.update(entityDTOConverter.getLessonEntity(lessonDTO));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return lessonDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return lessonDAO.getAllIds();
    }

    @Override
    public Optional<LessonDTO> findById(String id) throws Exception {
        return lessonDAO.findById(id).map(lesson -> {
            try {
                return entityDTOConverter.getLessonDTO(lesson);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert Lesson to DTO, findById", e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "LSN-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, "001");
    }
}

