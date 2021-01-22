package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.Playlist;
import htwb.ai.ALIS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findAllByOwnerId(User ownerId);
}
