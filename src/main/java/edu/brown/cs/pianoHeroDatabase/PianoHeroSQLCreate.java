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
 * This class creates a database from sample csv files
 *
 */
public class PianoHeroSQLCreate {

  private Connection conn;

  /**
   *
   * @param db
   * @throws ClassNotFoundException
   * @throws SQLException
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
    String schema2 = "CREATE TABLE Song (id INTEGER, songName TEXT, songFile  INTEGER,"
        + "songImage TEXT, songKeys BLOB);";

    buildTable(schema2);

    // schema to create a table called enrollment
    // String schema3 = "CREATE TABLE enrollment(" + "name TEXT," +
    // "course TEXT,"
    // + "sem TEXT," + "grade TEXT,"
    // + "FOREIGN KEY (name) REFERENCES student(name)"
    // + "ON DELETE CASCADE ON UPDATE CASCADE);";
    //
    // buildTable(schema3);
  }

  public void fillSong(Song song) throws IOException, SQLException {
    /*
     * prepare an all purpose insert statement; note the use of question marks.
     * Each question mark corresponds to an attribute. Essentially we are
     * building a tuple to insert into the table. This allows us to use the Same
     * PreparedStatement without having to create a new one for each insertion.
     */
    String query = "INSERT INTO song VALUES (?,?,?,?)";
    PreparedStatement ps = conn.prepareStatement(query);

    ps.setInt(1, song.get_id());
    ps.setString(2, song.get_title());
    ps.setString(3, song.get_mp3Path());
    ps.setString(4, song.get_imagePath());

    java.sql.Array keys = conn.createArrayOf("VARCHAR", song.get_keyStrokes());

    ps.setArray(5, keys);

    ps.executeUpdate();

    /*
     * Make sure to close all of your resources. Most JDBC classes need to be
     * closed.
     */
    ps.close();
  }

  public void fillScore(SongScore score) throws IOException, SQLException {
    /*
     * prepare an all purpose insert statement; note the use of question marks.
     * Each question mark corresponds to an attribute. Essentially we are
     * building a tuple to insert into the table. This allows us to use the Same
     * PreparedStatement without having to create a new one for each insertion.
     */
    String query = "INSERT INTO score VALUES (?,?,?)";
    PreparedStatement ps = conn.prepareStatement(query);

    ps.setInt(1, score.getSongID());
    ps.setInt(2, score.getScore());
    ps.setString(3, score.getUserName());

    ps.executeUpdate();

    /*
     * Make sure to close all of your resources. Most JDBC classes need to be
     * closed.
     */
    ps.close();
  }

  /**
   * Creates a new table according to the schema
   *
   * @param schema
   * @throws SQLException
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
   */
  public void close() throws SQLException {
    conn.close();
  }
}
