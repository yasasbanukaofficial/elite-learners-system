package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.custom.LessonBO;
import lk.ijse.learners.bo.custom.SchedulingBO;
import lk.ijse.learners.bo.custom.StudentBO;
import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;
import lk.ijse.learners.dto.StudentDTO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;

public class SchedulingBOImpl implements SchedulingBO {
    private final LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.LESSON);
    private final InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    private final StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    @Override
    public boolean scheduleLesson(LessonDTO lessonDTO) {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Optional<InstructorDTO> optionalInstructorDTO = instructorBO.findById(lessonDTO.getInstructorId());
            InstructorDTO instructorDTO = optionalInstructorDTO.get();

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime lessonEndTime = lessonDTO.getEnd_time().toLocalDateTime();

            instructorDTO.setAvailability(currentTime.isAfter(lessonEndTime) ? "available" : "not available");
            if (!instructorBO.update(instructorDTO)) {
                tx.rollback();
                return false;
            }

            if (lessonBO.save(lessonDTO)) {
                tx.commit();
                return true;
            }
            tx.rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean updateScheduleLesson(LessonDTO lessonDTO) {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            Optional<InstructorDTO> optionalInstructorDTO = instructorBO.findById(lessonDTO.getInstructorId());
            InstructorDTO instructorDTO = optionalInstructorDTO.get();

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime lessonEndTime = lessonDTO.getEnd_time().toLocalDateTime();

            instructorDTO.setAvailability(currentTime.isAfter(lessonEndTime) ? "available" : "not available");
            if (!instructorBO.update(instructorDTO)) {
                tx.rollback();
                return false;
            }

            if (lessonBO.update(lessonDTO)) {
                tx.commit();
                return true;
            }
            tx.rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean removeInstructorFromLesson(LessonDTO lessonDTO) {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            if (lessonDTO.getInstructorId() != null) {
                Optional<InstructorDTO> optionalInstructorDTO = instructorBO.findById(lessonDTO.getInstructorId());
                if (optionalInstructorDTO.isPresent()) {
                    InstructorDTO instructorDTO = optionalInstructorDTO.get();
                    instructorDTO.setAvailability("available");
                    if (!instructorBO.update(instructorDTO)) {
                        tx.rollback();
                        return false;
                    }
                }
            }

            lessonDTO.setInstructorId(null);
            if (lessonBO.update(lessonDTO)) {
                tx.commit();
                return true;
            }
            tx.rollback();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean editEnrolledStudent(LessonDTO lessonDTO, String newStudentId) {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            Optional<StudentDTO> optionalStudentDTO = studentBO.findById(newStudentId);
            if (optionalStudentDTO.isEmpty()) {
                tx.rollback();
                return false;
            }

            lessonDTO.setStudentId(newStudentId);

            if (lessonBO.update(lessonDTO)) {
                tx.commit();
                return true;
            }
            tx.rollback();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        }
        return false;
    }


    @Override
    public boolean cancelLesson(String lessonId) {
        return false;
    }

}
