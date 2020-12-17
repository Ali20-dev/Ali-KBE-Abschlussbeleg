package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByAccessToken(String token);

}
