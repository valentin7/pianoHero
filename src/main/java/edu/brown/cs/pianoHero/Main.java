package edu.brown.cs.pianoHero;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
      return new ModelAndView(variables, "highScores.ftl");
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
        return GSON.toJson(song);
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

      // TODO figure out best way to store mp3/ image files -> should
      // back end or front end store them and how?
      // final String mp3Path = qm.value("mp3Path");
      // final String imagePath = qm.value("imagePath");
      File image = GSON.fromJson(qm.value("songImage"), File.class);
      System.out.println("image file name::");
      System.out.println(image);
      // System.out.println(image.getName());

      File mp3 = GSON.fromJson(qm.value("songMp3"), File.class);
      System.out.println("song mp3 file name::");
      System.out.println(mp3);
      // System.out.println(mp3.getName());
      // TODO figure out if this works? should we be storing songs as 2D arrays
      // or as HashMaps?
      // final Map keyStrokes = GSON.fromJson(qm.value("keyStrokes"),
      // Map.class);

      // TODO store the song data in the database

      return null;
    }
  }

  private static class SongFactoryView implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      final Map<String, Object> variables =
          ImmutableMap.of("title", "PianoHero: Song Factory");
      return new ModelAndView(variables, "songFactory.ftl");
    }
  }

  private static final Gson GSON = new Gson();

  private static final int STATUS = 500;
  private static String dbPath = "pianoHeroSQL.sqlite3";
  private static PianoHeroQuery phquery;
  private static PianoHeroManager phManager;
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
      phManager = new PianoHeroManager(dbPath);
    } catch (ClassNotFoundException | SQLException e) {
      System.err.println("ERROR: Error connecting to database.");
      System.exit(-1);
    }

    // PianoHeroFileHandler phFileHandler = new PianoHeroFileHandler();
    // phFileHandler.doFileHandling();
    doTest();
    runSparkServer();
  }

  private static void doTest() {

    Set<File> allMp3s = new HashSet<File>();
    File mp3File = new File("otherDirectory/Intro.mp3");
    File imageFile = new File("otherDirectory/otherKiwiCover.png");

    // PianoHeroFileHandler.getAllFilesAndFolder(mp3Dir, allMp3s);
    //
    // for (File fm : allMp3s) {
    // System.out.println("song:");
    // System.out.println(fm);
    // if (!fm.isDirectory()) {
    // File dest = new File(fm.getParentFile().toString(), "new"
    // + fm.getName());
    // // PianoHeroFileHandler.copyFile(fm, dest);
    // }
    // }

    System.out.println("path:");
    System.out.println(mp3File.getPath());
    System.out.println(mp3File.toString());

    boolean[][] keyStrokes = { {false, true}, {true, false}};
    Song s = new Song("testSong", 2, mp3File.getPath(),
        imageFile.getPath(), keyStrokes);
    phManager.saveSong(s, mp3File, imageFile);
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
