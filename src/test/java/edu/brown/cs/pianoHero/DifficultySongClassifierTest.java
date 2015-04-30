package edu.brown.cs.pianoHero;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DifficultySongClassifierTest {

  @Test
  public void simpleSongsTest() {
    boolean[] easySong = {false, false};
    boolean[] harderSong = {false, true};

    // Song s1 = new Song(null, null, 1, null, null, 2, easySong);
    // Song s2 = new Song(null, null, 1, null, null, 2, harderSong);

    DifficultySongClassifier classifier = new DifficultySongClassifier();

    int s1D = classifier.calculateDifficulty(easySong);
    int s2D = classifier.calculateDifficulty(harderSong);

    System.out.println("difficulties: " + s1D + " and " + s2D);
    assertTrue(s2D > s1D);
  }
}
