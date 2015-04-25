package edu.brown.cs.pianoHero;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import edu.brown.cs.pianoHeroDatabase.PianoHeroQuery;
import edu.brown.cs.pianoHeroDatabase.PianoHeroSQLCreate;
import edu.brown.cs.pianoHeroFiles.PianoHeroFileHandler;

public class PianoHeroManager {
  PianoHeroFileHandler phFileHandler;
  PianoHeroQuery phQuery;
  PianoHeroSQLCreate phSQLCreate;

  public PianoHeroManager(String dbPath) throws ClassNotFoundException,
      SQLException {
    // this.phQuery = phQuery;
    phFileHandler = new PianoHeroFileHandler();
    phSQLCreate = new PianoHeroSQLCreate(dbPath);
  }

  public void saveSong(Song song, File songFile, File songImage) {
    try {
      File songDest = new File("pianoHeroFiles/songs/"
          + "copied" + songFile.getName());
      PianoHeroFileHandler.copyFile(songFile, songDest);

      File imageDest = new File("pianoHeroFiles/songImages/"
          + "copied" + songImage.getName());
      PianoHeroFileHandler.copyFile(songImage, imageDest);

      phSQLCreate.fillSong(song);
    } catch (IOException e) {
      System.err.println("ERROR: error saving song");
    }
  }

}
