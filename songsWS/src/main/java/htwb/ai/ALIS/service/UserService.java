package htwb.ai.ALIS.service;

import htwb.ai.ALIS.model.User;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
public interface UserService {

    /**
     * Finds a user from the database.
     *
     * @param userId id of user
     */
    boolean authenticateUser(String userId, String userPassword);

    void registerUser(@Valid User user);

    void saveToken(@Valid User user, String token);

    /**
     * Returns if a token is valid or not
     *
     * @param token string token from auth header
     * @return true if valid, else false
     */
    boolean validateToken(String token);

    /**
     * Returns user for given token
     *
     * @param token token
     * @return user of token
     */
    Optional<User> getUserForToken(String token);

    /**
     * Returns user by id
     *
     * @param userId userid
     * @return user of userid
     */
    Optional<User> getUserById(String userId);
}
