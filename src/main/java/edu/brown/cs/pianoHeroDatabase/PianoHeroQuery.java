package edu.brown.cs.pianoHeroDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.pianoHero.Song;
import edu.brown.cs.pianoHero.SongScore;
import edu.brown.cs.pianoHeroFiles.PianoHeroFileHandler;

/**
 * Class that handles all the queries to the SQL database for Piano Hero.
 *
 * @author valentin
 *
 */
public class PianoHeroQuery {

  private final Connection conn;
  private final int SONGID_INDEX = 1;
  private final int SONGNAME_INDEX = 2;
  private final int SONGFILE_INDEX = 3;
  private final int SONGIMAGE_INDEX = 4;
  private final int SONGKEYS_INDEX = 5;
  private final int ARTIST_INDEX = 6;

  private final int USERNAME_INDEX = 2;
  private final int SCOREVALUE_INDEX = 3;

  /**
   * Constructs a PianoHeroQuery with the directory at the given path.
   *
   * @param path
   *          The path to the database of songs and scores.
   * @throws SQLException
   *           if a database access error occurs or one of the sql methods are
   *           called on a closed connection
   * @throws ClassNotFoundException
   *           if the class cannot be located
   */
  public PianoHeroQuery(String path) throws SQLException,
      ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    conn = DriverManager.getConnection("jdbc:sqlite:" + path);
    final Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys = ON;");
    System.out.println("got here!!!");
    stat.close();
  }

  /**
   * Closes the connection.
   *
   * @throws SQLException
   *           if a database access error occurs
   */
  public void close() throws SQLException {
    conn.close();
  }

  /**
   * Gets a Song by its id.
   *
   * @param id
   *          int the song's id
   * @return the Song
   * @throws SQLException
   *           : error with the query
   */
  public Song getSongById(int id) {
    String query = "SELECT * FROM Song WHERE Song.id = ?;"; // WHERE id = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet results = prep.executeQuery();
      results.next();

      String title = results.getString(SONGNAME_INDEX);
      int songId = results.getInt(SONGID_INDEX);
      String mp3Path = results.getString(SONGFILE_INDEX);
      String imagePath = results.getString(SONGIMAGE_INDEX);
      // Object keyStrokes = results.getObject(SONGKEYS_INDEX);
      final String keyStrokesPath = results.getString(SONGKEYS_INDEX);
      final String artistName = results.getString(ARTIST_INDEX);

      System.out.println("keyStrokes path:: " + keyStrokesPath);
      Song s;

      boolean[] songKeyStrokes = PianoHeroFileHandler
          .getStrokesArray(keyStrokesPath);

      s = new Song(title, artistName, songId, mp3Path, imagePath, songKeyStrokes);

      // if (keyStrokes instanceof String) {
      // // this means we're getting from the fake, dummy database. So we put
      // // null for the keys.
      // s = new Song(title, songId, mp3Path, imagePath, null);
      // System.out.println("songkEYS GOT: " + keyStrokes);
      // } else {
      // System.out.println("DIDNT GET STRING!!");
      // boolean[][] keys = (boolean[][]) keyStrokes;
      // s = new Song(title, songId, mp3Path, imagePath, keys);
      // }

      results.close();
      prep.close();

      return s;
    } catch (SQLException e) {
      System.err.println("Error querying  : " + e.getMessage());
      return null;

    }
  }

  /**
   * Gets all songs from the database with a limit of 200.
   *
   * @return a list of all songs
   * @throws SQLException
   *           if there is an error querying
   */
  public List<Song> getAllSongs() throws SQLException {
    final String query =
        "SELECT * FROM Song LIMIT 200;";

    try (
        final PreparedStatement prep =
        conn.prepareStatement(query)) {

      final ResultSet results = prep.executeQuery();
      final List<Song> songs = new ArrayList<Song>();

      while (results.next()) {
        final String title = results.getString(SONGNAME_INDEX);
        final int songId = results.getInt(SONGID_INDEX);
        final String mp3Path = results.getString(SONGFILE_INDEX);
        final String imagePath = results.getString(SONGIMAGE_INDEX);
        // final Object keyStrokes = results.getObject(SONGKEYS_INDEX);
        final String keyStrokesPath = results.getString(SONGKEYS_INDEX);
        final String artistName = results.getString(ARTIST_INDEX);

        Song s;
        boolean[] songKeyStrokes = PianoHeroFileHandler
            .getStrokesArray(keyStrokesPath);

        s = new Song(title, artistName, songId, mp3Path, imagePath, songKeyStrokes);

        // if (keyStrokes instanceof String) {
        // // this means we're getting from the fake, dummy database. So we put
        // // null for the keys.
        // s = new Song(title, songId, mp3Path, imagePath, null);
        // } else {
        // boolean[][] keys = (boolean[][]) keyStrokes;
        // s = new Song(title, songId, mp3Path, imagePath, keys);
        // }
        songs.add(s);

      }
      results.close();
      prep.close();
      return songs;

    } catch (SQLException e) {
      System.err.println("ERROR: error retrieving all songs");
    }

    return null;
  }

  /**
   * Gets a list of song scores for a certain song id.
   *
   * @param songId
   *          : int the id of the song.
   * @return a list of songscores fot the song's id.
   * @throws SQLException
   *           if there is error querying the database.
   */
  public List<SongScore> getScoresForSong(int songId) {
    try {
      String query = "SELECT * FROM Score WHERE songId = ? ORDER BY scoreValue DESC;";

      final PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, songId);
      final ResultSet results = prep.executeQuery();

      List<SongScore> songScores = new ArrayList<SongScore>();
      while (results.next()) {
        final String username = results.getString(USERNAME_INDEX);
        final int scoreValue = results.getInt(SCOREVALUE_INDEX);
        final SongScore s = new SongScore(songId, scoreValue, username);
        songScores.add(s);
      }
      results.close();
      prep.close();

      return songScores;

    } catch (SQLException e) {
      System.err.println("Error querying: " + e.getMessage());

      return null;
    }
  }

  /**
   * Gets a list of scores for a certain user.
   *
   * @param username
   *          - String, the username of the user.
   * @return a list of songscores.
   * @throws SQLException
   *           if there is an error in the query.
   */
  public List<SongScore> getScoresForUsername(String username) {
    try {
      String query = "SELECT * FROM Score WHERE username = ? ORDER BY scoreValue DESC;";

      final PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, username);
      final ResultSet results = prep.executeQuery();

      List<SongScore> songScores = new ArrayList<SongScore>();
      while (results.next()) {
        final int scoreValue = results.getInt(SCOREVALUE_INDEX);
        final int songId = results.getInt(SONGID_INDEX);
        final SongScore s = new SongScore(songId, scoreValue, username);
        songScores.add(s);
      }
      results.close();
      prep.close();

      return songScores;
    } catch (SQLException e) {
      System.err.println("Error querying: " + e.getMessage());
      return null;
    }

  }
}
