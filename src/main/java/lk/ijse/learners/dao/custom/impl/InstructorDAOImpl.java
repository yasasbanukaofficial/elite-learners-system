package lk.ijse.learners.dao.custom.impl;

import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.InstructorDAO;
import lk.ijse.learners.entity.Instructor;
import lk.ijse.learners.entity.Lesson;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class InstructorDAOImpl implements InstructorDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<Instructor> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Instructor> query = session.createQuery("from Instructor", Instructor.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select i.instructorId from Instructor i order by i.instructorId desc", String.class).setMaxResults(1);
            return query.list().isEmpty() ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(Instructor instructor) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(instructor);
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
    public boolean update(Instructor instructor) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(instructor);
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
            Instructor instructor = session.get(Instructor.class, id);
            session.remove(instructor);
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
            Query<String> query = session.createQuery("select i.id from Instructor i", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<Instructor> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Instructor instructor = session.get(Instructor.class, id);
            return Optional.ofNullable(instructor);
        }
    }

    public List<String> getAllAvailableInstructors() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select i.name from Instructor i where i.availability = 'available' ", String.class);
            return query.list().isEmpty() ? null : query.list();
        }
    }

    @Override
    public List<Instructor> fetchInstructorListByName(List<String> instructorName) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Instructor> query = session.createQuery("from Instructor i where i.name in (:instructorName)", Instructor.class);
            query.setParameterList("instructorName", instructorName);
            return query.list();
        }
    }

    public List<Lesson> getAllLessonsByInstructorId(String instructorId) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Lesson> query = session.createQuery("select i.lessons from Instructor i where i.instructorId = :instructorId", Lesson.class);
            query.setParameter("instructorId", instructorId);
            return query.list().isEmpty() ? null : query.list();
        }
    }

    @Override
    public Optional<Instructor> findByName(String name) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Instructor> query = session.createQuery(
                "FROM Instructor i WHERE i.name = :name",
                Instructor.class
            );
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        }
    }
}
