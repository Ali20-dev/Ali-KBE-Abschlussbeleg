package htwb.ai.ALIS.service;

import htwb.ai.ALIS.model.User;
import htwb.ai.ALIS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl() {

    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean authenticateUser(String userId, String userPassword) {
        Optional<User> loadedUser = userRepository.findById(userId);
        if (loadedUser.isPresent()) {
            if (loadedUser.get().getPassword().equals(userPassword) && loadedUser.get().getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void saveToken(User user, String token) {
        Optional<User> loadedUser = userRepository.findById(user.getUserId());
        if (loadedUser.isPresent()) {
            loadedUser.get().setAccessToken(token);
            userRepository.saveAndFlush(loadedUser.get());
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null) {
            return false;
        }
        Optional<User> loadedUser = userRepository.findByAccessToken(token);
        return loadedUser.isPresent();
    }

    @Override
    public Optional<User> getUserForToken(String token) {
        return userRepository.findByAccessToken(token);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
}
