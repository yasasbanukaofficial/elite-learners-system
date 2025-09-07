package lk.ijse.learners.config;

import javafx.scene.control.Alert;
import lk.ijse.learners.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private SessionFactory sessionFactory;

    private FactoryConfiguration() {
        Properties properties = new Properties();
        try {
            properties.load(
                    FactoryConfiguration.class.getClassLoader().getResourceAsStream("hibernate.properties")
            );
            Configuration cfg = new Configuration().addProperties(properties)
                    .addAnnotatedClass(Course.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Payment.class)
                    .addAnnotatedClass(Instructor.class)
                    .addAnnotatedClass(Lesson.class)
                    .addAnnotatedClass(StudentCourseDetails.class)
                    .addAnnotatedClass(Student.class);

            sessionFactory = cfg.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading hibernate properties", e);
        }
    }

    public static FactoryConfiguration getInstance() {
        return factoryConfiguration == null ? factoryConfiguration = new FactoryConfiguration() : factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
