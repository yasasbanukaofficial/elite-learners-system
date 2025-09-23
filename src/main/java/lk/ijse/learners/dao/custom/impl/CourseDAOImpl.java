package lk.ijse.learners.dao.custom.impl;

import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.CourseDAO;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.entity.Course;
import lk.ijse.learners.entity.Instructor;
import lk.ijse.learners.entity.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAOImpl implements CourseDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<Course> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Course> query = session.createQuery("from Course", Course.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select c.id from Course c order by c.id desc", String.class).setMaxResults(1);
            return query.list().isEmpty() ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(Course course) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(course);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Course course) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(course);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Course course = session.get(Course.class, id);
            session.remove(course);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<String> getAllIds() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select c.id from Course c", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<Course> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Course course = session.get(Course.class, id);
            return Optional.ofNullable(course);
        }
    }

    @Override
    public List<Course> fetchCourseListByName(List<String> courseName) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Course> query = session.createQuery("from Course c where c.name in (:courseName)", Course.class);
            query.setParameterList("courseName", courseName);
            return query.list();
        }
    }


    @Override
    public List<Course> getAllEnrolledCoursesByStdId(String stdId) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Course> query = session.createQuery("select c from Course c join c.students s where s.id = :stdId", Course.class);
            query.setParameter("stdId", stdId);
            return query.list().isEmpty() ? null : query.list();
        }
    }

    @Override
    public List<Course> getAllEnrolledCoursesByInsId(String stdId) throws Exception {
        try(Session session = factoryConfiguration.getSession()){
            Query<Course> query = session.createQuery("select c from Course c join c.instructors i where i.id = :insId", Course.class);
            query.setParameter("insId", stdId);
            return query.list().isEmpty() ? new ArrayList<>() : query.list();
        }
    }

    @Override
    public List<Student> getAllStudentsByCourseId (String courseId) throws Exception {
        try(Session session = factoryConfiguration.getSession()){
            Query<Student> query = session.createQuery("select c.students from Course c where c.id = :courseId", Student.class);
            query.setParameter("courseId", courseId);
            return query.list().isEmpty() ? null : query.list();
        }
    }

    @Override
    public List<Instructor> getAllInstructorsByCourseId (String courseId) throws Exception {
        try(Session session = factoryConfiguration.getSession()){
            Query<Instructor> query = session.createQuery("select c.instructors from Course c where c.id = :courseId", Instructor.class);
            query.setParameter("courseId", courseId);
            return query.list().isEmpty() ? null : query.list();
        }
    }

    @Override
    public Optional<Course> findByName(String name) {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Course> query = session.createQuery(
                    "FROM Course c WHERE c.name = :name", Course.class
            );
            query.setParameter("name", name);
            query.setMaxResults(1);
            Course course = query.uniqueResult();
            return Optional.ofNullable(course);
        }
    }

    @Override
    public Optional<Course> findByStdName(String firstName, String lastName) {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Course> query = session.createQuery(
                    "SELECT c FROM Course c JOIN c.students s WHERE s.firstName = :firstName and s.lastName = :lastName",
                    Course.class
            );
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            query.setMaxResults(1);

            Course result = query.uniqueResult();
            return Optional.ofNullable(result);
        }
    }

}
