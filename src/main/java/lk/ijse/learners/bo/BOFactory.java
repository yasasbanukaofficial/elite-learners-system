package lk.ijse.learners.bo;

import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.custom.impl.*;
import lk.ijse.learners.dao.custom.impl.UserDAOImpl;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory(){}

    public static BOFactory getInstance() {
        return (boFactory == null)?boFactory = new BOFactory() : boFactory;
    }
    
    public enum BOTypes{
         STUDENT, INSTRUCTOR, COURSE, PAYMENT, LESSON, QUERY, ENROLLMENT, SCHEDULE;
    }
    
    public SuperBO getBO(BOTypes boTypes) {
        return switch (boTypes){
//            case USER -> new UserBOImpl();
            case STUDENT ->  new StudentBOImpl();
            case INSTRUCTOR -> new InstructorBOImpl();
            case COURSE -> new CourseBOImpl();
            case PAYMENT -> new PaymentBOImpl();
            case LESSON -> new LessonBOImpl();
            case ENROLLMENT -> new EnrollmentBOImpl();
            case SCHEDULE -> new SchedulingBOImpl();
            case QUERY -> new QueryBOImpl();
        };
    }
}
