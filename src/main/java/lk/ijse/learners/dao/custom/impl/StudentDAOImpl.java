package lk.ijse.learners.dao.custom.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.StudentDAO;
import lk.ijse.learners.entity.Lesson;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.Student;
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
            Transaction transaction = session.beginTransaction();

            List<Student> students = session.createQuery(
                    "SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.lessons",
                    Student.class
            ).getResultList();

            transaction.commit();

            return students;
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
        CriteriaBuilder criteriaBuilder = factoryConfiguration.getSession().getCriteriaBuilder();
        CriteriaQuery<Student> studentCriteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> root = studentCriteriaQuery.from(Student.class);
        studentCriteriaQuery.select(root).where(criteriaBuilder.equal(root.get(field), fieldValue));
        Query<Student> query = factoryConfiguration.getSession().createQuery(studentCriteriaQuery);
        return !query.getResultList().isEmpty();
    }

    @Override
    public List<Payment> getAllPayments() {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Payment> query = session.createQuery("select s.payments from Student s", Payment.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public List<Payment> getAllPaymentsBySid(String sid) {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Payment> query = session.createQuery("select s.payments from Student s where s.id = :sid", Payment.class);
            query.setParameter("sid", sid);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public List<Lesson> getAllLessonsBySid(String sid) {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Lesson> query = session.createQuery("select s.lessons from Student s where s.id = :sid", Lesson.class);
            query.setParameter("sid", sid);
            return query.list() == null ? null : query.list();
        }
    }

    public Optional<Student> findByStudentName(String fName, String lName) {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Student> query = session.createQuery(
                    "SELECT s FROM Student s WHERE s.firstName = :fName AND s.lastName = :lName",
                    Student.class
            );
            query.setParameter("fName", fName);
            query.setParameter("lName", lName);
            query.setMaxResults(1);

            return query.uniqueResultOptional();
        }
    }



}
