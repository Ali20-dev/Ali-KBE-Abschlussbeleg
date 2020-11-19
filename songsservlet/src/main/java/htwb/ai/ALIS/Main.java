package htwb.ai.ALIS;

import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.repository.DBSongDAO;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.List;

public class Main {
    private static final String PERSISTANCE_UNIT_NAME = "d3ch05jd151n16";

    /**
     * Just for testing!
     * @param args
     */
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = null;
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);

            DBSongDAO dbSongDAO = new DBSongDAO(entityManagerFactory);

            //test song anlegen
            Song song = new Song("Test Titel", "AlabamaBois", "SonyLabel", 1969);
            Integer neueID = dbSongDAO.saveSong(song);
            System.out.println("Neue ID vom Song ist: " + neueID);
            List<Song> songs = dbSongDAO.findAllSongs();
            for(Song songFromDB : songs) {
                System.out.println(songFromDB.getArtist());
            }
            //dbSongDAO.deleteAllSongs();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Etwas stimmt nicht mit der Datenbank Verbindung: " + e.toString());
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }
}
