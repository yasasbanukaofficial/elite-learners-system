package lk.ijse.learners.dao;

import lk.ijse.learners.dao.custom.impl.CourseDAOImpl;
import lk.ijse.learners.dao.custom.impl.InstructorDAOImpl;
import lk.ijse.learners.dao.custom.impl.StudentDAOImpl;
import lk.ijse.learners.dao.custom.impl.UserDAOImpl;

public class DAOFactory {
    private static DAOFactory DAOFactory;
    private DAOFactory() {}
    public enum DAOTypes {
        USER, STUDENT, INSTRUCTOR, COURSE, PAYMENT, SUBJECT, LESSON, QUERY;
    }

    public SuperDAO getDAO(DAOTypes daoTypes) {
        return switch (daoTypes) {
            case USER -> new UserDAOImpl();
            case STUDENT -> new StudentDAOImpl();
            case INSTRUCTOR -> new InstructorDAOImpl();
            case COURSE -> new CourseDAOImpl();
            default -> null;
        };
    }
}
