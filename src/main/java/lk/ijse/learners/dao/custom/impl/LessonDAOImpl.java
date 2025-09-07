package lk.ijse.learners.dao.custom.impl;

import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.LessonDAO;
import lk.ijse.learners.entity.Lesson;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class LessonDAOImpl implements LessonDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<Lesson> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Lesson> query = session.createQuery("from Lesson", Lesson.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select l.id from Lesson l order by l.id desc", String.class).setMaxResults(1);
            return query.list() == null ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(Lesson lesson) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(lesson);
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
    public boolean update(Lesson lesson) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(lesson);
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
            Lesson lesson = session.get(Lesson.class, id);
            session.remove(lesson);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> getAllIds() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select l.id from Lesson l", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<Lesson> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Lesson lesson = session.get(Lesson.class, id);
            return Optional.ofNullable(lesson);
        }
    }
}
