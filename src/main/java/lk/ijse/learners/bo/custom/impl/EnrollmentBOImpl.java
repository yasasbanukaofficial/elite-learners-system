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

            for (CourseDTO courseDTO : courseDTOList) {
                if (courseBO.findById(courseDTO.getCourseId()).isEmpty()) {
                    throw new NotFoundException("Course does not exist");
                }
            }

            if (!studentBO.save(studentDTO)) {
                throw new RuntimeException("Failed to save student");
            }

            for (CourseDTO courseDTO : courseDTOList) {
                Course course = session.get(Course.class, courseDTO.getCourseId());
                course.getStudents().add(entityDTOConverter.getStudentEntity(studentDTO));
                session.merge(course);
            }

            paymentDTO.setStudentId(studentDTO.getStudentId());
            if (!paymentBO.save(paymentDTO)) {
                throw new RuntimeException("Failed to save payment");
            }

            tx.commit();
            AlertUtil.setInfoAlert("Successfully enrolled student!");
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            AlertUtil.setErrorAlert("Enrollment failed: " + e.getMessage());
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

        try {
            StudentDTO studentDTO = enrollmentContext.getStudentDTO();
            List<CourseDTO> courseDTOList = enrollmentContext.getCourseDTOList();

            for (CourseDTO course : courseDTOList) {
                if (!course.getStudents().contains(studentDTO)) {
                    course.getStudents().add(studentDTO);
                }
                if (!courseBO.update(course)) {
                    throw new RuntimeException("Failed to update course");
                }
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            AlertUtil.setErrorAlert("Failed to update enrolled courses");
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

}
