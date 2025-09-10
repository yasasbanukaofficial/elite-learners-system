package lk.ijse.learners.dao.custom.impl;

import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.StudentCourseDetailsDAO;
import lk.ijse.learners.entity.Course;
import lk.ijse.learners.entity.StudentCourseDetails;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StudentCourseDetailsDAOImpl implements StudentCourseDetailsDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<StudentCourseDetails> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<StudentCourseDetails> query = session.createQuery("from StudentCourseDetails", StudentCourseDetails.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select sc.id from StudentCourseDetails sc order by sc.id desc", String.class).setMaxResults(1);
            return query.list().isEmpty() ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(StudentCourseDetails studentCourseDetails) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(studentCourseDetails);
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
    public boolean update(StudentCourseDetails studentCourseDetails) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(studentCourseDetails);
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
            StudentCourseDetails studentCourseDetails = session.get(StudentCourseDetails.class, id);
            session.remove(studentCourseDetails);
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
            Query<String> query = session.createQuery("select sc.id from StudentCourseDetails sc", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<StudentCourseDetails> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            StudentCourseDetails studentCourseDetails = session.get(StudentCourseDetails.class, id);
            return Optional.ofNullable(studentCourseDetails);
        }
    }
}
