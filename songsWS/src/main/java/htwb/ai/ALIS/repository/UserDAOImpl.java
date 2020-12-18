package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.User;
import javassist.NotFoundException;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
public class UserDAOImpl implements UserDAO {

    private final String persistenceUnit = "d3ch05jd151n16";

    private final EntityManagerFactory entityManagerFactory;

    public UserDAOImpl() {
        System.out.println("Creating UserDaoImpl!");
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    @Override
    public boolean authenticateUser(String userId, String userPassword) throws NotFoundException, PersistenceException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            User loadedUser = entityManager.find(User.class, userId);
            if (loadedUser == null)
                throw new NotFoundException("Der Song mit der ID " + userId + "wurde nicht gefunden!");
            if(loadedUser.getPassword().equals(userPassword)){
                return true;
            } else {
                return false;
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void registerUser(User user) {
        EntityManager entityManager = null;
        try {

            entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(user);
            entityTransaction.commit();
        } catch (IllegalStateException | EntityExistsException | RollbackException e) {
            if (entityManager != null) {
                //rollback falls etwas schief gegangen ist
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new PersistenceException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
