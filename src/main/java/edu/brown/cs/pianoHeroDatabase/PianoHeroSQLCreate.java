package edu.brown.cs.pianoHeroDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import edu.brown.cs.pianoHero.Song;
import edu.brown.cs.pianoHero.SongScore;

/**
 * This class creates a database for PianoHero
 *
 * @author valentin
 */
public class PianoHeroSQLCreate {

  private Connection conn;

  /**
   * Constructor for PianoHeroSQLCreate
   *
   * @param db
   *          - String: the path to the sqlite3 database.
   * @throws ClassNotFoundException
   *           - if there is no class found JDBC.
   * @throws SQLException
   *           - if there is an exception creating the tables.
   */
  public PianoHeroSQLCreate(String db) throws ClassNotFoundException,
  SQLException {
    // Load the driver class
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    // Set up a connection to the db
    conn = DriverManager.getConnection(urlToDB);

    // These delete the tables if they already exist
    Statement stat = conn.createStatement();
    stat.execute("DROP TABLE IF EXISTS Song");
    stat.execute("DROP TABLE IF EXISTS Score");
    stat.close();

    // schema to create a table for scores
    String schema = "CREATE TABLE Score (songId INTEGER, username  TEXT, scoreValue INTEGER);";

    buildTable(schema);

    // schema to create a table for songs
    String schema2 = "CREATE TABLE Song (id INTEGER, songName TEXT, songFile  TEXT,"
        + "songImage TEXT, songKeys BLOB);";

    buildTable(schema2);
  }

  /**
   * Adds a song the the sql database
   *
   * @param song
   *          : Song, the song to fill
   * @throws IOException
   *           : if there is an error writing on the file.
   * @throws SQLException
   *           : if there is an error with the query.
   */
  public void fillSong(Song song) throws IOException, SQLException {
    /*
     * prepares an all purpose insert statement for saving songs.
     */
    String query = "INSERT INTO song VALUES (?,?,?,?,?)";

    try (PreparedStatement ps = conn.prepareStatement(query)) {

      ps.setInt(1, song.get_id());
      ps.setString(2, song.get_title());
      ps.setString(3, song.get_mp3Path());
      ps.setString(4, song.get_imagePath());

      ps.setObject(5, song.get_keyStrokes());

      // java.sql.Array keys = conn.createArrayOf("VARCHAR",
      // song.get_keyStrokes());
      // ps.setArray(5, keys);

      ps.executeUpdate();
    } catch (SQLException e) {
      System.err.println("ERROR: error saving song to database");
    }

  }

  /**
   * Adds a score to the database.
   *
   * @param score
   *          : SongScore to add.
   * @throws IOException
   *           : if there is an error writing to the file.
   * @throws SQLException
   *           : if there is an error querying.
   */
  public void fillScore(SongScore score) throws SQLException {
    /*
     * prepare an all purpose insert statement; note the use of question marks.
     * Each question mark corresponds to an attribute. Essentially we are
     * building a tuple to insert into the table. This allows us to use the Same
     * PreparedStatement without having to create a new one for each insertion.
     */
    String query = "INSERT INTO score VALUES (?,?,?)";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, score.getSongID());
      ps.setString(2, score.getUserName());
      ps.setInt(3, score.getScore());

      ps.executeUpdate();
    } catch (SQLException e) {
      System.err.println("ERROR: couldn't save song to database");
    }

  }

  /**
   * Creates a new table according to the schema
   *
   * @param schema
   *          : String the schema for PianoHero tables.
   * @throws SQLException
   *           : if there is an error executing the updates.
   */
  private void buildTable(String schema) throws SQLException {
    // PreparedStatement to execute the command in
    // the argument schema. Since it will build a table, we do
    // not care about the results.

    PreparedStatement prep;
    prep = conn.prepareStatement(schema);
    prep.executeUpdate();

    prep.close();
  }

  /**
   * Closes any associated resources with the table
   *
   * @throws SQLException
   *           : if there is an error closing the database.
   */
  public void close() throws SQLException {
    conn.close();
  }
}
