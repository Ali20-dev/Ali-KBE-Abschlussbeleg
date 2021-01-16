package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.Song;
import javassist.NotFoundException;

import javax.persistence.*;
import java.util.List;

public class DBSongDAOImpl implements DBSongDAO {

    private final String persistenceUnit = "d3ch05jd151n16";

    private final EntityManagerFactory entityManagerFactory;

    public DBSongDAOImpl() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    public int saveSong(Song song) throws PersistenceException {
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

    public List<Song> findAllSongs() throws PersistenceException {
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

    public Song findSong(int songId) throws NotFoundException, PersistenceException {
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

    @Override
    public void overwriteSong(Song song, int songId) throws NotFoundException, PersistenceException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Song loadedSong = entityManager.find(Song.class, songId);
            if(loadedSong == null)
                throw new NotFoundException("Der Song mit der ID " + songId + "wurde nicht gefunden!");
            loadedSong.setReleased(song.getReleased());
            loadedSong.setLabel(song.getLabel());
            loadedSong.setArtist(song.getArtist());
            loadedSong.setTitle(song.getTitle());
            entityTransaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void deleteSong(int songId) throws NotFoundException, PersistenceException {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Song loadedSong = entityManager.find(Song.class, songId);
            if (loadedSong == null)
                throw new NotFoundException("Der Song mit der ID " + songId + "wurde nicht gefunden!");
            else {
                entityManager.remove(loadedSong);
            }
            entityTransaction.commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
