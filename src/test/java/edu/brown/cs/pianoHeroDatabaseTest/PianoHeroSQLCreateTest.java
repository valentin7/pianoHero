package edu.brown.cs.pianoHeroDatabaseTest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import edu.brown.cs.pianoHero.Song;
import edu.brown.cs.pianoHeroDatabase.PianoHeroQuery;
import edu.brown.cs.pianoHeroDatabase.PianoHeroSQLCreate;

public class PianoHeroSQLCreateTest {

  @Test
  public void simpleFillSongTest() throws ClassNotFoundException, SQLException,
  IOException {
    PianoHeroSQLCreate db = new PianoHeroSQLCreate("toFillPianoHeroSQL.sqlite3");

    boolean[][] keyStrokes = {{false, true}};

    Song s = new Song("NewSong", 3, "pianoHeroFiles/Intro.mp3", "imagePath",
        keyStrokes);

    db.fillSong(s);

    PianoHeroQuery query = new PianoHeroQuery("toFillPianoHeroSQL.sqlite3");
    Song a = query.getSongById(3);
    assertTrue(a.get_title().equals("NewSong"));
  }

  @Test
  public void checkIfKeysConservedTest() throws ClassNotFoundException,
  SQLException,
  IOException {
    PianoHeroSQLCreate db = new PianoHeroSQLCreate("toFillPianoHeroSQL.sqlite3");

    boolean[][] keyStrokes = {{false, true}};

    Song s = new Song("NewSong", 3, "pianoHeroFiles/Intro.mp3", "imagePath",
        keyStrokes);

    db.fillSong(s);

    PianoHeroQuery query = new PianoHeroQuery("toFillPianoHeroSQL.sqlite3");
    Song a = query.getSongById(3);
    System.out.println("song's keyStrokes: " + a.get_keyStrokes());
    assertTrue(a.get_keyStrokes().equals(keyStrokes));
  }
}
