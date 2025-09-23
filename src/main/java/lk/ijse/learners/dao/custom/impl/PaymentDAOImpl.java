package lk.ijse.learners.dao.custom.impl;

import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.PaymentDAO;
import lk.ijse.learners.entity.Payment;
import lk.ijse.learners.entity.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class PaymentDAOImpl implements PaymentDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<Payment> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<Payment> query = session.createQuery(
                    "SELECT p FROM Payment p JOIN FETCH p.student", Payment.class
            );
            return query.list().isEmpty() ? null : query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select p.id from Payment p order by p.id desc", String.class).setMaxResults(1);
            return query.list().isEmpty() ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(Payment payment) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(payment);
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
    public boolean update(Payment payment) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(payment);
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
            Payment payment = session.get(Payment.class, id);
            session.remove(payment);
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
            Query<String> query = session.createQuery("select p.id from Payment p", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<Payment> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Payment payment = session.get(Payment.class, id);
            return Optional.ofNullable(payment);
        }
    }

    @Override
    public boolean isIdExisting(String id) {
        try(Session session = factoryConfiguration.getSession()) {
            String resultId = session.createQuery("select p.id from Payment p order by p.id desc", String.class).setMaxResults(1).uniqueResult();
            return resultId != null && resultId.equals(id);
        }
    }

    @Override
    public Student getStudentsByPaymentId(String payId) {
        try(Session session = factoryConfiguration.getSession()) {
            Query<Student> query = session.createQuery("select p.student from Payment p where p.paymentId = :id", Student.class);
            query.setParameter("id", payId);
            return query.uniqueResult();
        }
    }
}
