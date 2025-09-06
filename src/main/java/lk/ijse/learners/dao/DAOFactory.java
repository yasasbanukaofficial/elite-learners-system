package lk.ijse.learners.dao;

import lk.ijse.learners.dao.custom.QueryDAOImpl;
import lk.ijse.learners.dao.custom.impl.*;
import lk.ijse.learners.dao.custom.impl.CourseDAOImpl;
import lk.ijse.learners.dao.custom.impl.InstructorDAOImpl;
import lk.ijse.learners.dao.custom.impl.StudentDAOImpl;
import lk.ijse.learners.dao.custom.impl.UserDAOImpl;

public class DAOFactory {
    private static DAOFactory DAOFactory;
    private DAOFactory() {}
    public static DAOFactory getInstance() {
        return DAOFactory == null ? DAOFactory = new DAOFactory() : DAOFactory;
    }
    public enum DAOTypes {
        USER, STUDENT, INSTRUCTOR, COURSE, PAYMENT, LESSON, STUDENT_COURSE_DETAILS, QUERY;
    }

    public SuperDAO getDAO(DAOTypes daoTypes) {
        return switch (daoTypes) {
            case USER -> new UserDAOImpl();
            case STUDENT -> new StudentDAOImpl();
            case INSTRUCTOR -> new InstructorDAOImpl();
            case COURSE -> new CourseDAOImpl();
            case PAYMENT -> new PaymentDAOImpl();
            case LESSON -> new LessonDAOImpl();
            case STUDENT_COURSE_DETAILS -> new StudentCourseDetailsDAOImpl();
            case QUERY -> new QueryDAOImpl();
        };
    }
}
