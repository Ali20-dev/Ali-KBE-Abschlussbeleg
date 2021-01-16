package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.User;
import javassist.NotFoundException;

import javax.persistence.PersistenceException;
import javax.validation.Valid;

public interface UserDAO {

    /**
     * Finds a user from the database.
     * @param userId id of user
     */
    boolean authenticateUser(String userId, String userPassword) throws NotFoundException, PersistenceException;

    void registerUser(@Valid User user);
}
