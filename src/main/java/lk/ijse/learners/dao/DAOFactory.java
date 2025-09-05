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
        switch (daoTypes) {
            case USER:
                return new UserDAOImpl();
            case STUDENT:
                return new StudentDAOImpl();
            case INSTRUCTOR:
                return new InstructorDAOImpl();
            case COURSE:
                return new CourseDAOImpl();
            default:
                return null;
        }
    }
}
