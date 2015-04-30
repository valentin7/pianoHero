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
import edu.brown.cs.pianoHeroDatabase.PianoHeroSQLCreate;
import edu.brown.cs.pianoHeroFiles.PianoHeroFileHandler;
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

      // try {
      for (final Integer id : songIDs) {
        final Collection<SongScore> songScores = phquery.getScoresForSong(id);
        scores.put(id, songScores);
      }
      return GSON.toJson(scores);
      // } catch (final SQLException e) {
      // System.err.println("ERROR: Error receiving high scores from database.");
      // return null;
      // }
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
  
  private static class SaveScoreHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      final QueryParamsMap qm = req.queryMap();
      final int songID = Integer.parseInt(qm.value("songID"));
      final int score = Integer.parseInt(qm.value("score"));
      final String user = qm.value("user");

      SongScore ss = new SongScore(songID, score, user);
      saveScoreInDb(ss);
      
      return GSON.toJson(null);
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

      Song song = phquery.getSongById(songID);
      List<SongScore> scores = phquery.getScoresForSong(songID);
      if (scores.isEmpty()) {
        scores.add(new SongScore(songID, 0, "Default"));
      }
      System.out.println(song.get_length());
      
      final Map<String, Object> variables =
          ImmutableMap.of("song", song, "highScore", scores.get(0));
      return GSON.toJson(variables);
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
      final String title = qm.value("title");
      final String artist = qm.value("artist");
      final int length = (int) Double.parseDouble(qm.value("length"));
      final String mp3Name = qm.value("mp3File");
      final String imageName = qm.value("imgFile");
      final boolean[] keyStrokes = GSON.fromJson(qm.value("keyStrokes"), boolean[].class);

      String savedMp3Path = PianoHeroFileHandler.saveMp3(mp3Name);
      String savedImagePath = PianoHeroFileHandler.saveImage(imageName);

      Song s = new Song(title, artist, 7, savedMp3Path, savedImagePath, length, keyStrokes);
      saveSongInDb(s);
      phManager.saveSong(s, mp3Name, imageName);
      maxID++;

      return GSON.toJson(true);
    }
  }

  private static class SongFactoryView implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      final Map<String, Object> variables =
          ImmutableMap.of("title", "PianoHero: Song Factory");
      return new ModelAndView(variables, "create.ftl");
    }
  }

  private static final Gson GSON = new Gson();

  private static final int STATUS = 500;
  private static String dbPath = "pianoHeroSQL.sqlite3";
  private static PianoHeroQuery phquery;
  private static PianoHeroSQLCreate phSQLcreate;
  private static int maxID;

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
      phSQLcreate = new PianoHeroSQLCreate(dbPath);
      phManager = new PianoHeroManager(dbPath);
      System.out.println(maxID);
    } catch (ClassNotFoundException | SQLException e) {
      System.err.println("ERROR: Error connecting to database.");
      System.exit(-1);
    }

    // PianoHeroFileHandler phFileHandler = new PianoHeroFileHandler();
    // phFileHandler.doFileHandling();
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

    System.out.println("initial paths:");
    System.out.println(mp3File.getPath());
    System.out.println(imageFile.getPath());

    boolean[] keyStrokes = {false, true, true, false};
    System.out.println("initial strokes: ");
    printKeyStrokes(keyStrokes);

    //String savedMp3Path = PianoHeroFileHandler.saveMp3(mp3File);
    //String savedImagePath = PianoHeroFileHandler.saveImage(imageFile);

    //Song s = new Song("testSong", "testArtist", 2, savedMp3Path,
     //   savedImagePath, 200, keyStrokes);
    //saveSongInDb(s);

    // phManager.saveSong(s, mp3File, imageFile);

    System.out.println();
    System.out
        .println("now showing we get the Files and keystrokes from the song:");
    Song retrievedSong = phquery.getSongById(2);
    File sImage = retrievedSong.getImageFile();
    File sSong = retrievedSong.getMp3File();
    boolean[] retrievedStrokes = PianoHeroFileHandler
        .getStrokesArray(retrievedSong.get_keyStrokesPath());

    System.out.println(sImage.getPath());
    System.out.println(sSong.getPath());
    System.out.println("retrieved keyStrokes:");
    printKeyStrokes(retrievedStrokes);

    System.out.println();
    System.out.println("High scores:");
    SongScore score = new SongScore(7, 99, "JJ");
    SongScore score2 = new SongScore(6, 32, "JJ");
    SongScore score3 = new SongScore(7, 22, "Tom");

    List<SongScore> scores = new ArrayList<SongScore>();
    scores.add(score);
    scores.add(score2);
    scores.add(score3);

    saveScoresInDb(scores);
    System.out.println("Scores from the database are: ");
    printScoresForSong(7);
    System.out.println();
    printScoresForUser("JJ");
  }

  /**
   * prints keystrokes for given 2d boolean array.
   *
   * @param keyStrokes
   */
  private static void print2DKeyStrokes(boolean[][] keyStrokes) {
    for (int i = 0; i < keyStrokes.length; i++) {
      for (int j = 0; j < keyStrokes[i].length; j++) {
        System.out.print(keyStrokes[i][j] + " ");
      }
      System.out.println();
    }
  }

  /**
   * prints keystrokes for given 2d boolean array.
   *
   * @param keyStrokes
   */
  private static void printKeyStrokes(boolean[] keyStrokes) {
    for (int i = 0; i < keyStrokes.length; i++) {
      System.out.print(keyStrokes[i] + " ");
    }
  }

  /**
   * prints the scores for a given song.
   *
   * @param keyStrokes
   */
  private static void printScoresForSong(int id) {
    List<SongScore> scores = phquery.getScoresForSong(id);
    for (int i = 0; i < scores.size(); i++) {
      System.out.println(scores.get(i).toString());
    }
  }

  /**
   * prints the scores for a given user.
   *
   * @param username
   */
  private static void printScoresForUser(String username) {
    List<SongScore> scores = phquery.getScoresForUsername(username);
    for (int i = 0; i < scores.size(); i++) {
      System.out.println(scores.get(i).toString());
    }
  }

  private static void saveSongInDb(Song s) {
    phSQLcreate.fillSong(s);
  }

  private static void saveScoreInDb(SongScore s) {
    phSQLcreate.fillScore(s);
  }

  private static void saveScoresInDb(List<SongScore> scores) {
    for (SongScore s : scores) {
      saveScoreInDb(s);
    }
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
    Spark.post("/savescore", new SaveScoreHandler());
  }
}
