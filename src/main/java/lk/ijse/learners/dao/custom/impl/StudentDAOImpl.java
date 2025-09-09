package lk.ijse.learners.dao.custom.impl;

import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.StudentDAO;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.Student;
import lk.ijse.learners.entity.StudentCourseDetails;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<Student> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Student> query = session.createQuery("from Student", Student.class);
            return query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select s.id from Student s order by s.id desc", String.class).setMaxResults(1);
            return query.list().isEmpty() ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(Student student) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(student);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Student student) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(student);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
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
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.remove(student);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<String> getAllIds() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select s.id from Student s", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<Student> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Student student = session.get(Student.class, id);
            return Optional.ofNullable(student);
        }
    }

    @Override
    public boolean existsByField(String field, String fieldValue) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Student> query = session.createQuery("select :field from Student s where :field = :fieldValue", Student.class);
            query.setParameter("field", field);
            query.setParameter("fieldValue", fieldValue);
            return query.list().isEmpty();
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Payment> query = session.createQuery("select s.payments from Student s", Payment.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public List<StudentCourseDetails> getAllSCD() {
        try (Session session = factoryConfiguration.getSession()) {
            Query<StudentCourseDetails> query = session.createQuery("select s.studentCourseDetails from Student s", StudentCourseDetails.class);
            return query.list() == null ? null : query.list();
        }
    }
}
