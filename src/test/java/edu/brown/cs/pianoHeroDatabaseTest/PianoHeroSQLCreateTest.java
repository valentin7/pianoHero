package edu.brown.cs.pianoHeroDatabaseTest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.pianoHero.Song;
import edu.brown.cs.pianoHero.SongScore;
import edu.brown.cs.pianoHeroDatabase.PianoHeroQuery;
import edu.brown.cs.pianoHeroDatabase.PianoHeroSQLCreate;

public class PianoHeroSQLCreateTest {

  @Test
  public void simpleFillSongTest() throws ClassNotFoundException, SQLException,
    IOException {
    PianoHeroSQLCreate db = new PianoHeroSQLCreate("toFillPianoHeroSQL.sqlite3");

    boolean[] keyStrokes = {false, true};

    Song s = new Song("NewSong", 3, "pianoHeroFiles/Intro.mp3", "imagePath",
        keyStrokes);

    db.fillSong(s);

    PianoHeroQuery query = new PianoHeroQuery("toFillPianoHeroSQL.sqlite3");
    Song a = query.getSongById(3);
    assertTrue(a.get_title().equals("NewSong"));
  }

  @Test
  public void simpleFillScoreTest() throws ClassNotFoundException,
    SQLException,
    IOException {
    PianoHeroSQLCreate db = new PianoHeroSQLCreate("toFillPianoHeroSQL.sqlite3");

    SongScore s = new SongScore(7, 99, "JJ");

    db.fillScore(s);

    PianoHeroQuery query = new PianoHeroQuery("toFillPianoHeroSQL.sqlite3");
    List<SongScore> all = query.getScoresForUsername("JJ");

    assertTrue(all.get(0).getScore() == 99);
  }

  @Test
  public void checkIfKeysConservedTest() throws ClassNotFoundException,
    SQLException,
    IOException {
    PianoHeroSQLCreate db = new PianoHeroSQLCreate("toFillPianoHeroSQL.sqlite3");

    boolean[] keyStrokes = {false, true};

    Song s = new Song("NewSong", 3, "pianoHeroFiles/Intro.mp3", "imagePath",
        keyStrokes);

    db.fillSong(s);

    PianoHeroQuery query = new PianoHeroQuery("toFillPianoHeroSQL.sqlite3");
    Song a = query.getSongById(3);
    System.out.println("song's keyStroke path:  " + a.get_keyStrokesPath());

    // boolean[][] keyStrokes = PianoHeroFileHandler.get
    // assertTrue(a.getKeystrokes().equals(keyStrokes));
  }
}
