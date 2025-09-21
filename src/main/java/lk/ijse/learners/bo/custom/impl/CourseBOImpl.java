package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.exception.NotFoundException;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.CourseDAO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;

import java.util.List;
import java.util.Optional;

public class CourseBOImpl implements CourseBO {
    private final CourseDAO courseDAO = (CourseDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.COURSE);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    @Override
    public List<CourseDTO> getAll() throws Exception {
        return entityDTOConverter.toCourseDTOList(courseDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return courseDAO.getLastId();
    }

    @Override
    public boolean save(CourseDTO courseDTO) throws Exception {
        return courseDAO.save(entityDTOConverter.getCourseEntity(courseDTO));
    }

    @Override
    public boolean update(CourseDTO courseDTO) throws Exception {
        return courseDAO.update(entityDTOConverter.getCourseEntity(courseDTO));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return courseDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return courseDAO.getAllIds();
    }

    @Override
    public Optional<CourseDTO> findById(String id) throws Exception {
        return courseDAO.findById(id).map(course -> {
            try {
                return entityDTOConverter.getCourseDTO(course);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert Course to DTO, findById", e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "COU-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, 1);
    }

    @Override
    public List<Course> fetchCourseListByName(List<String> courseName) throws Exception {
        return courseDAO.fetchCourseListByName(courseName);
    }

    @Override
    public List<Course> getAllEnrolledCoursesByStdId(String stdId) throws Exception {
        return courseDAO.getAllEnrolledCoursesByStdId(stdId);
    }

    @Override
    public Optional<CourseDTO> findByName(String newSel) {
        return courseDAO.findByName(newSel).map(course -> {
            try {
                return entityDTOConverter.getCourseDTO(course);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert Course to DTO, findByName", e);
            }
        });
    }


}