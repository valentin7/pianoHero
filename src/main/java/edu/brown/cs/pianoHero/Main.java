package edu.brown.cs.pianoHero;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.SparkBase;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.pianoHeroDatabase.PianoHeroQuery;
import freemarker.template.Configuration;

public class Main {

  private static class ExceptionPrinter implements ExceptionHandler {

    @Override
    public void
        handle(final Exception e, final Request req, final Response res) {
      res.status(STATUS);
      final StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  private static class HighScoresHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      final Map<Integer, Collection<SongScore>> scores =
          new HashMap<Integer, Collection<SongScore>>();
      
      // FOR TESTING PURPOSES
      songIDs.add(1);
      songIDs.add(2);

      try {
        for (final Integer id : songIDs) {
          final Collection<SongScore> songScores = phquery.getScoresForSong(id);
          scores.put(id, songScores);
        }
        return GSON.toJson(scores);
      } catch (final SQLException e) {
        System.err.println("ERROR: Error receiving high scores from database.");
        return null;
      }
    }
  }

  private static class HighScoresView implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      final Map<String, Object> variables =
          ImmutableMap.of("title", "PianoHero: High Scores");
      return new ModelAndView(variables, "highscores.ftl");
    }
  }

  private static class MainMenuSongsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      List<Song> songs;
      try {
        songs = phquery.getAllSongs();
        for (final Song song : songs) {
          songIDs.add(song.get_id());
        }

        return GSON.toJson(songs);
      } catch (final SQLException e) {
        System.err.println("ERROR: Error receiving songs from database.");
        return GSON.toJson(null);
      }
    }
  }

  private static class PlaySongHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      final QueryParamsMap qm = req.queryMap();
      final int songID = Integer.parseInt(qm.value("songID"));

      try {
        Song song = phquery.getSongById(songID);
        List<SongScore> scores = phquery.getScoresForSong(songID);
        
        final Map<String, Object> variables =
            ImmutableMap.of("song", song, "highScore", scores.get(0));
        return GSON.toJson(variables);
      } catch (SQLException e) {
        System.err
        .println("ERROR: Error receiving song information from database.");
        return GSON.toJson(null);
      }
    }
  }

  private static class MainMenuView implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      final Map<String, Object> variables =
          ImmutableMap.of("title", "PianoHero");
      return new ModelAndView(variables, "main.ftl");
    }
  }

  private static class PlaySongView implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      final Map<String, Object> variables =
          ImmutableMap.of("title", "PianoHero: Play Song");
      return new ModelAndView(variables, "game.ftl");
    }
  }

  private static class SongFactoryHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      final QueryParamsMap qm = req.queryMap();
      final String title = qm.value("songTitle");
      final String mp3Path = qm.value("mp3Path");
      final String imagePath = qm.value("imagePath");
      final boolean[] keyStrokes = GSON.fromJson(qm.value("keyStrokes"), boolean[].class);
      
      // File image = GSON.fromJson(qm.value("songImage"), File.class);
      // System.out.println("image file name::");
      // System.out.println(image);
      // System.out.println(image.getName());

      // File mp3 = GSON.fromJson(qm.value("songMp3"), File.class);
      // System.out.println("song mp3 file name::");
      // System.out.println(mp3);
      // System.out.println(mp3.getName());
      
      System.out.println("NEW SONG RECIEVED");
      System.out.println("Title: " + title);
      System.out.println("MP3 path: " + mp3Path);
      System.out.println("Image path: " + imagePath);
      System.out.print("Keystrokes array: ");
      for (boolean bool : keyStrokes) {
        System.out.print(bool + " ");
      }
      

      // TODO store the song data in the database

      return GSON.toJson(true);
    }
  }

  private static class SongFactoryView implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      final Map<String, Object> variables =
          ImmutableMap.of("title", "PianoHero: Song Factory");
      return new ModelAndView(variables, "songfactory.ftl");
    }
  }

  private static final Gson GSON = new Gson();

  private static final int STATUS = 500;
  private static String dbPath = "pianoHeroSQL.sqlite3";
  private static PianoHeroQuery phquery;
  private static ArrayList<Integer> songIDs = new ArrayList<Integer>();

  private static FreeMarkerEngine createEngine() {
    final Configuration config = new Configuration();
    final File templates =
        new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (final IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  public static void main(String[] args) {
    try {
      phquery = new PianoHeroQuery(dbPath);
    } catch (ClassNotFoundException | SQLException e) {
      System.err.println("ERROR: Error connecting to database.");
      System.exit(-1);
    }

    PianoFileHandler phFileHandler = new PianoFileHandler();

    phFileHandler.doFileHandling();

    runSparkServer();
  }

  private static void runSparkServer() {
    SparkBase.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    final FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/", new MainMenuView(), freeMarker);
    Spark.get("/highscores", new HighScoresView(), freeMarker);
    Spark.get("/playsong", new PlaySongView(), freeMarker);
    Spark.get("/songfactory", new SongFactoryView(), freeMarker);
    Spark.get("/getsongs", new MainMenuSongsHandler());
    Spark.get("/gethighscores", new HighScoresHandler());
    Spark.post("/storesong", new SongFactoryHandler());
    Spark.post("/getsongtoplay", new PlaySongHandler());
  }
}
