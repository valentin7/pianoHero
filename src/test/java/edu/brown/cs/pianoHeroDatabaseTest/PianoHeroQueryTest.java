package edu.brown.cs.pianoHeroDatabaseTest;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.pianoHero.Song;
import edu.brown.cs.pianoHero.SongScore;
import edu.brown.cs.pianoHeroDatabase.PianoHeroQuery;

public class PianoHeroQueryTest {

  @Test
  public void simpleSongTest() throws ClassNotFoundException, SQLException {
    PianoHeroQuery query = new PianoHeroQuery("pianoHeroSQL.sqlite3");
    Song a = query.getSongById(1);
    assertTrue(a.get_title().equals("Intro"));
  }

  @Test
  public void song2Test() throws ClassNotFoundException, SQLException {
    PianoHeroQuery query = new PianoHeroQuery("pianoHeroSQL.sqlite3");
    Song a = query.getSongById(2);
    assertTrue(a.get_title().equals("Ertesuppe"));
  }

  @Test
  public void gettingScoresTest() throws ClassNotFoundException, SQLException {
    PianoHeroQuery query = new PianoHeroQuery("pianoHeroSQL.sqlite3");
    List<SongScore> songScores = query.getScoresForSong(1);

    assertTrue(songScores.get(0).getUserName().equals("testUser1"));
    assertTrue(songScores.get(0).getScore() == 99);
  }

  @Test
  public void gettingMultipleScoresTest() throws ClassNotFoundException,
  SQLException {
    PianoHeroQuery query = new PianoHeroQuery("pianoHeroSQL.sqlite3");
    List<SongScore> songScores = query.getScoresForSong(2);

    assertTrue(songScores.get(0).getUserName().equals("testUser1"));
    assertTrue(songScores.get(0).getScore() == 88);

    assertTrue(songScores.get(1).getUserName().equals("testUser2"));
    assertTrue(songScores.get(1).getScore() == 32);
  }

  @Test
  public void gettingScoreByUsernameTest()
      throws ClassNotFoundException,
      SQLException {
    PianoHeroQuery query = new PianoHeroQuery("pianoHeroSQL.sqlite3");
    List<SongScore> songScores = query
        .getScoresForUsername("testUser2");

    assertTrue(songScores.get(0).getUserName().equals("testUser2"));
    assertTrue(songScores.get(0).getScore() == 32);
  }

  @Test
  public void gettingMultipleScoresByUsernameTest()
      throws ClassNotFoundException,
      SQLException {
    PianoHeroQuery query = new PianoHeroQuery("pianoHeroSQL.sqlite3");
    List<SongScore> songScores = query
        .getScoresForUsername("testUser1");

    assertTrue(songScores.get(0).getUserName().equals("testUser1"));
    assertTrue(songScores.get(0).getScore() == 99);

    assertTrue(songScores.get(1).getUserName().equals("testUser1"));
    assertTrue(songScores.get(1).getScore() == 88);
  }
}
