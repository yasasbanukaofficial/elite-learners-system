package lk.ijse.learners.dao.custom.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lk.ijse.learners.config.FactoryConfiguration;
import lk.ijse.learners.dao.custom.UserDAO;
import lk.ijse.learners.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private final FactoryConfiguration factoryConfiguration = FactoryConfiguration.getInstance();
    @Override
    public List<User> getAll() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public String getLastId() throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            Query<String> query = session.createQuery("select u.id from User u order by u.id desc", String.class).setMaxResults(1);
            return query.list() == null ? null : query.list().getFirst();
        }
    }

    @Override
    public boolean save(User entity) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(entity);
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
    public boolean update(User entity) throws Exception {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(entity);
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
            User user = session.get(User.class, id);
            session.remove(user);
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
            Query<String> query = session.createQuery("select u.id from User u", String.class);
            return query.list() == null ? null : query.list();
        }
    }

    @Override
    public Optional<User> findById(String id) throws Exception {
        try (Session session = factoryConfiguration.getSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public boolean existsByField(String field, String fieldValue) throws Exception {
        CriteriaBuilder criteriaBuilder = factoryConfiguration.getSession().getCriteriaBuilder();
        CriteriaQuery<User> userCriteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = userCriteriaQuery.from(User.class);
        userCriteriaQuery.select(root).where(criteriaBuilder.equal(root.get(field), fieldValue));
        Query<User> query = factoryConfiguration.getSession().createQuery(userCriteriaQuery);
        return !query.getResultList().isEmpty();
    }


}
