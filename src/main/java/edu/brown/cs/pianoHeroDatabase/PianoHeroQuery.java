package edu.brown.cs.pianoHeroDatabase;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.brown.cs.pianoHero.Song;
import edu.brown.cs.pianoHero.SongScore;

public class PianoHeroQuery {

  private final Connection conn;
  private final int SONGID_INDEX = 1;
  private final int SONGNAME_INDEX = 2;
  private final int SONGFILE_INDEX = 3;
  private final int SONGIMAGE_INDEX = 4;
  private final int SONGKEYS_INDEX = 5;

  private final int SCOREVALUE_INDEX = 2;
  private final int USERNAME_INDEX = 3;

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

  public List<Song> getAllSongs() throws SQLException {
    final String query = "SELECT * FROM song LIMIT 200;";
    final PreparedStatement prep = conn.prepareStatement(query);

    final ResultSet results = prep.executeQuery();
    final List<Song> songs = new ArrayList<Song>();
    while (results.next()) {
      final String title = results.getString(SONGNAME_INDEX);
      final int songId = results.getInt(SONGID_INDEX);
      final String mp3Path = results.getString(SONGFILE_INDEX);
      final String imagePath = results.getString(SONGIMAGE_INDEX);
      final Array keyStrokes = results.getArray(SONGKEYS_INDEX);
      final boolean[][] keys = (boolean[][]) keyStrokes.getArray();

      songs.add(new Song(title, songId, mp3Path, imagePath, keys));
    }
    prep.close();
    results.close();

    return songs;
  }

  public Collection<SongScore> getScoresForSong(int songId) throws SQLException {
    try {
      final String query = "SELECT * FROM score WHERE songId = ?";

      final PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, songId);
      final ResultSet results = prep.executeQuery();

      final Collection<SongScore> songScores = new ArrayList<SongScore>();
      while (results.next()) {
        final String username = results.getString(USERNAME_INDEX);
        final int scoreValue = results.getInt(SCOREVALUE_INDEX);
        final SongScore s = new SongScore(songId, scoreValue, username);
        songScores.add(s);
      }
      results.close();
      prep.close();

      return songScores;
    } catch (final SQLException e) {
      System.err.println("Error querying for neighbors: " + e.getMessage());
      return null;
    }

  }

  public Collection<SongScore> getScoresForUsername(String username)
      throws SQLException {
    try {
      final String query = "SELECT * FROM score WHERE username = ?";

      final PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, username);
      final ResultSet results = prep.executeQuery();

      final Collection<SongScore> songScores = new ArrayList<SongScore>();
      while (results.next()) {
        final int scoreValue = results.getInt(SCOREVALUE_INDEX);
        final int songId = results.getInt(SONGID_INDEX);
        final SongScore s = new SongScore(songId, scoreValue, username);
        songScores.add(s);
      }
      results.close();
      prep.close();

      return songScores;
    } catch (final SQLException e) {
      System.err.println("Error querying for neighbors: " + e.getMessage());
      return null;
    }

  }

  public Song getSongById(Integer id) throws SQLException {
    try {
      final String query = "SELECT * FROM song WHERE id = ?;";
      final PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, id);
      final ResultSet results = prep.executeQuery();
      results.next();

      final String title = results.getString(SONGNAME_INDEX);
      final int songId = results.getInt(SONGID_INDEX);
      final String mp3Path = results.getString(SONGFILE_INDEX);
      final String imagePath = results.getString(SONGIMAGE_INDEX);
      final Array keyStrokes = results.getArray(SONGKEYS_INDEX);
      final boolean[][] keys = (boolean[][]) keyStrokes.getArray();

      final Song s = new Song(title, songId, mp3Path, imagePath, keys);

      results.close();
      prep.close();

      return s;
    } catch (final SQLException e) {
      System.err.println("Error querying for neighbors: " + e.getMessage());
      return null;
    }

  }
}

//
// package edu.brown.cs.db_lab.query;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;
//
// /**
// * Fill in the method stubs using your knowledge of SQL statements!
// *
// */
//
//
// public class HubwayQuery {
//
// private Connection conn;
//
// /**
// * This constructor takes in a path to the db file
// *
// * @param db
// * Path to the db file
// * @throws ClassNotFoundException
// * @throws SQLException
// */
// public HubwayQuery(String db) throws ClassNotFoundException, SQLException {
// // TODO(1): Set up a connection
// Class.forName("org.sqlite.JDBC");
// String urlToDB = "jdbc:sqlite:" + db;
//
// // TODO(2): Store the connection in a field
// conn = DriverManager.getConnection(urlToDB);
//
// }
//
// /**
// * Return all of the station names whose status correspond to 'Removed'. Make
// * sure the names are sorted in ascending order (ie. 'a' should come before
// * 'b')
// *
// * @param args
// * Empty array
// * @return A List of the station names, sorted in ascending order
// * @throws SQLException
// */
// public List<String> query1(String args[]) throws SQLException {
// // TODO(1): Write the query as a string
// String query =
// "SELECT station FROM stations WHERE status = 'Removed' ORDER BY station ASC";
//
// // TODO(2): Create a PreparedStatement
// PreparedStatement prep = conn.prepareStatement(query);
// // prep.setString(1);
//
// // TODO(3): Execute the query and retrieve a ResultStatement
//
// ResultSet rs = prep.executeQuery();
// // TODO(4): Add the results to this list
//
// List<String> toReturn = new ArrayList<String>();
//
// while (rs.next()) {
// String station = rs.getString(1);
// // Do something else with these results...
// toReturn.add(station);
// }
// rs.close();
// // List<String> toReturn = new ArrayList<String>();
//
// // TODO(5): Close the ResultSet and the PreparedStatement
//
// prep.close();
// return toReturn;
// }
//
// /**
// * Return all of the station names that are located inside the box formed by
// * two given points. The points are stored in the args parameter with latitude
// * and longitude coordinates. As with query1, the names should be sorted in
// * ascending order
// *
// * @param args
// * An array containing the lower left and upper right corners of the
// * bounding box. Indices 0 and 1 contain the latitude and longitude
// * of the lower left corner and indices 2 and 3 contain the latitude
// * and longitude of the upper right corner. The coordinates should be
// * doubles.
// *
// * @return A ResultSet with all of the station names inside of the bounding
// * box
// *
// * @throws SQLException
// */
// public List<String> query2(String args[]) throws SQLException {
// // TODO: Query 2
//
// // double minLat = Math.min(args[0])
// String query =
// "SELECT station FROM stations AS s WHERE s.lat >= ? AND s.lng >= ? AND s.lat <= ? AND s.lng <= ? ORDER BY station ASC";
// PreparedStatement prep = conn.prepareStatement(query);
//
// // HINT: Use PreparedStatment's method 'setDouble'
// // TODO(4): Add the results to this list
// prep.setDouble(1, Double.parseDouble(args[0]));
// prep.setDouble(2, Double.parseDouble(args[1]));
// prep.setDouble(3, Double.parseDouble(args[2]));
// prep.setDouble(4, Double.parseDouble(args[3]));
// ResultSet rs = prep.executeQuery();
//
// List<String> toReturn = new ArrayList<String>();
//
// while (rs.next()) {
// String station = rs.getString(1);
// // Do something else with these results...
// toReturn.add(station);
// }
// rs.close();
//
// return toReturn;
// }
//
// /**
// * Return the top 5 trips' ids (hubway_id) that started or ended at stations
// * within a bounding boxed formed by two given points. The ids should be
// * sorted in ascending order.
// *
// * @param args
// * An array containing the lower left and upper right corners of the
// * bounding box. Indices 0 and 1 contain the latitude and longitude
// * of the lower left corner and indices 2 and 3 contain the latitude
// * and longitude of the upper right corner
// * @return A List of all the trips ids (hubway_id)
// * @throws SQLException
// */
// public List<String> query3(String args[]) throws SQLException {
// // TODO: Query 3
// // At least one of the starting or ending stations must be in the bounding
// // box
//
// String query = "SELECT hubway_id FROM trips AS t, stations AS s WHERE "
// + "(s.lat >= ? AND s.lng >= ? AND s.lat <= ? AND s.lng <= ?)"
// + "AND (t.end_statn == s.id  OR t.start_statn == s.id)"
// + "ORDER BY hubway_id ASC LIMIT 5";
// PreparedStatement prep = conn.prepareStatement(query);
//
// // TODO(4): Add the results to this list
// prep.setDouble(1, Double.parseDouble(args[0]));
// prep.setDouble(2, Double.parseDouble(args[1]));
// prep.setDouble(3, Double.parseDouble(args[2]));
// prep.setDouble(4, Double.parseDouble(args[3]));
// ResultSet rs = prep.executeQuery();
//
// List<String> toReturn = new ArrayList<String>();
//
// while (rs.next()) {
// String trip = rs.getString(1);
// // Do something else with these results...
// toReturn.add(trip);
// }
// rs.close();
//
// return toReturn;
// }
//
// /**
// * Closes and cleans up any resources.
// *
// * @throws SQLException
// */
// public void close() throws SQLException {
// // TODO: Close the connection
// conn.close();
// }
// }
