package htwb.ai.ALIS.controller;

import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.repository.DBSongDAO;
import org.json.JSONObject;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class SongsServlet extends HttpServlet {

    private static final String PERSISTANCE_UNIT_NAME = "d3ch05jd151n16";

    private static final long serialVersionUID = 1L;

    DBSongDAO dbSongDAO;

    @Override
    public void init(ServletConfig servletConfig) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
        dbSongDAO = new DBSongDAO(entityManagerFactory);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            PrintWriter responseWriter = response.getWriter();
            if (request.getQueryString().startsWith("songId")) {
                System.out.println(request.getParameter("songId"));
                int songId = Integer.parseInt(request.getParameter("songId"));
                Song song = dbSongDAO.findSong(songId);
                JSONObject jsonObject = new JSONObject(song);
                responseWriter.println(jsonObject);
                response.setStatus(200);
            } else if (request.getQueryString().equals("all")) {
                List<Song> listOfSongs = dbSongDAO.findAllSongs();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("[");
                for (int i = 0; i < listOfSongs.size(); i++) {
                    Song song = listOfSongs.get(i);
                    JSONObject jsonObject = new JSONObject(song);
                    stringBuilder.append(jsonObject);
                    if (i != listOfSongs.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                stringBuilder.append("]");
                responseWriter.println(stringBuilder.toString());
                response.setStatus(200);
            } else {
                response.setStatus(400);
            }
        } catch (EntityNotFoundException e) {
            response.setStatus(204);
        } catch (Exception e) {
            response.setStatus(400);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter responseWriter = response.getWriter();
        //wenn hier schon eine IOException kommt, ist wahrscheinlich der Server sowieso kaputt
        //weil er nicht mehr antworten kann


        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        Song newSong = new Song();
        JSONObject jsonObject = null;
//        try {
            jsonObject = new JSONObject(stringBuilder.toString());
//        } catch (Exception e) {
//            response.setStatus(400);
//            return;
//        }
        try {
            String title = jsonObject.getString("title");
            newSong.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            //not acceptable, wenn user keinen titel im json objekt hat
        }
        try {
            String artist = jsonObject.getString("artist");
            newSong.setArtist(artist);
        } catch (Exception e) {
            //optional
        }
        try {
            String label = jsonObject.getString("label");
            newSong.setLabel(label);
        } catch (Exception e) {
            //optional
        }
        try {
            int released = jsonObject.getInt("released");
            newSong.setReleased(released);
        } catch (Exception e) {
            //optional
        }
        int newSongID = dbSongDAO.saveSong(newSong);
        //sollte geklappt haben, also:
        response.setStatus(HttpServletResponse.SC_CREATED);
        String newURL = "/songsservlet-ALIS/songs?songId=" + newSongID;
        response.setHeader("Location", newURL);
        responseWriter.println();
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        //laut RFC 2616
        response.setHeader("Allow", "[post, get]");
        response.setStatus(405);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        doPut(request, response);
    }
}
