package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentContext;
import lk.ijse.learners.bo.custom.*;
import lk.ijse.learners.bo.exception.DuplicateException;
import lk.ijse.learners.bo.exception.InUseException;
import lk.ijse.learners.bo.exception.NotFoundException;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.dto.*;
import lk.ijse.learners.entity.Course;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EnrollmentBOImpl implements EnrollmentBO {
    private final EnrollmentContext enrollmentContext = EnrollmentContext.getInstance();
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);

    @Override
    public boolean enrollStudent() {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            StudentDTO studentDTO = enrollmentContext.getStudentDTO();
            PaymentDTO paymentDTO = enrollmentContext.getPaymentDTO();
            List<CourseDTO> courseDTOList = enrollmentContext.getCourseDTOList();

            courseDTOList.forEach(courseDTO -> {
                try {
                    if (courseBO.findById(courseDTO.getCourseId()).isEmpty()) {
                        tx.rollback();
                        throw new NotFoundException("Course does not exist");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error when checking course id's", e);
                }
            });

            try {
                if (!studentBO.save(studentDTO)) {
                    tx.rollback();
                    AlertUtil.setErrorAlert("Failed to save student");
                }
            } catch (DuplicateException e) {
                tx.rollback();
                AlertUtil.setErrorAlert("Duplicate student: " + e.getMessage());
            } catch (InUseException e) {
                tx.rollback();
                AlertUtil.setErrorAlert("Validation error: " + e.getMessage());
            } catch (Exception e) {
                tx.rollback();
                AlertUtil.setErrorAlert("Unexpected error: Something went wrong. Please try again.");
                e.printStackTrace();
            }


            courseDTOList.forEach(courseDTO -> {
                try {
                    Course course = session.get(Course.class, courseDTO.getCourseId());
                    course.getStudents().add(entityDTOConverter.getStudentEntity(studentDTO));
                    session.merge(course);
                    if (!courseBO.update(courseDTO)) {
                        tx.rollback();
                        throw new RuntimeException("Failed to update course");
                    }
                } catch (Exception e) {
                    tx.rollback();
                    throw new RuntimeException("Error when updating courses", e);
                }
            });

            paymentDTO.setStudentId(studentDTO.getStudentId());
            if (!paymentBO.save(paymentDTO)) {
                tx.rollback();
                throw new RuntimeException("Failed to save payment");
            }

            tx.commit();
            AlertUtil.setInfoAlert("Successfully enrolled student!");
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateEnrolledStudent() {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();

        StudentDTO studentDTO = enrollmentContext.getStudentDTO();
        List<CourseDTO> courseDTOList = enrollmentContext.getCourseDTOList();
        courseDTOList.forEach(course -> {
            if (!course.getStudents().contains(studentDTO)) {
                course.getStudents().add(studentDTO);
            }
            try {
                if (!courseBO.update(course)) {
                    tx.rollback();
                    throw new RuntimeException("Failed to update course");
                }
            } catch (Exception e) {
                tx.rollback();
            } finally {
                session.close();
            }
        });
        return true;
    }

}
