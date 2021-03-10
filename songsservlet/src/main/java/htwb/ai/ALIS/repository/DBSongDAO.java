package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.Song;
import javassist.NotFoundException;

import javax.persistence.*;
import java.util.List;

public class DBSongDAO {

    private final EntityManagerFactory entityManagerFactory;

    public DBSongDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Integer saveSong(Song song) throws PersistenceException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {

            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(song);
            entityTransaction.commit();
            return song.getId();
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

    public List<Song> findAllSongs() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT s FROM Song s");
            return (List<Song>) query.getResultList();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public Song findSong(int songId) throws NotFoundException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT s FROM Song s WHERE id=" + songId);
            if (query.getResultList().size() < 1) {
                throw new NotFoundException("Der Song mit der ID " + songId + "wurde nicht gefunden!");
            }
            return (Song) query.getResultList().get(0);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void deleteAllSongs() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createQuery("DELETE FROM Song");
            query.executeUpdate();
            entityTransaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
