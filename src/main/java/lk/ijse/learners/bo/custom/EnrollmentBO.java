package lk.ijse.learners.bo.custom;

import lk.ijse.learners.bo.SuperBO;

public interface EnrollmentBO extends SuperBO {
    boolean enrollStudent();
    boolean updateEnrolledStudent();
}
