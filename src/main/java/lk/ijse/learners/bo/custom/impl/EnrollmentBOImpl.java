package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.context.EnrollmentUnitOfWork;
import lk.ijse.learners.bo.custom.*;
import lk.ijse.learners.bo.exception.NotFoundException;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.StudentCourseDetailsDAO;
import lk.ijse.learners.dao.custom.impl.StudentCourseDetailsDAOImpl;
import lk.ijse.learners.dto.*;
import lk.ijse.learners.entity.Lesson;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.StudentCourseDetails;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EnrollmentBOImpl implements EnrollmentBO {
    private final EnrollmentUnitOfWork enrollmentUnitOfWork = EnrollmentUnitOfWork.getInstance();
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();
    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PAYMENT);
    CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    StudentCourseDetailsBO studentCourseDetailsBO = (StudentCourseDetailsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT_COURSE_DETAILS);

    StudentCourseDetailsDAO studentCourseDetailsDAO = (StudentCourseDetailsDAOImpl) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT_COURSE_DETAILS);
    public boolean enrollStudent() {
        Session session = FactoryConfiguration.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            StudentDTO studentDTO = enrollmentUnitOfWork.getStudentDTO();
            PaymentDTO paymentDTO = enrollmentUnitOfWork.getPaymentDTO();
            CourseDTO courseDTO = enrollmentUnitOfWork.getCourseDTO();

            System.out.println(studentDTO.getStudentId());

            if (courseBO.findById(courseDTO.getCourseId()).isEmpty()) {
                tx.rollback();
                throw new NotFoundException("Course does not exist");
            }

            StudentCourseDetailsDTO scdDTO = new StudentCourseDetailsDTO(
                    studentCourseDetailsBO.loadNextId(),
                    studentDTO.getStudentId(),
                    courseDTO.getCourseId(),
                    Date.valueOf(LocalDate.now()),
                    "Enrolled",
                    "NONE"
            );


            if (!studentBO.save(studentDTO)) {
                tx.rollback();
                throw new RuntimeException("Failed to save student");
            }

            paymentDTO.setStudentId(studentDTO.getStudentId());
            if (!paymentBO.save(paymentDTO)) {
                tx.rollback();
                throw new RuntimeException("Failed to save payment");
            }

            if (!studentCourseDetailsBO.save(scdDTO)) {
                tx.rollback();
                throw new RuntimeException("Failed to save student course details");
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

}
