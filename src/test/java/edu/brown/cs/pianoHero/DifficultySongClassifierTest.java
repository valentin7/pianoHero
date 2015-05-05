package edu.brown.cs.pianoHero;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DifficultySongClassifierTest {

  @Test
  public void simpleSongsTest() {
    boolean[] easySong = {false, false, false, false, false, false, false,
        false, false, false, false, false, false};
    boolean[] harderSong = {false, true, false, false, false, false, false,
        false, false, false, false, false, false};

    // Song s1 = new Song(null, null, 1, null, null, 2, easySong);
    // Song s2 = new Song(null, null, 1, null, null, 2, harderSong);

    DifficultySongClassifier classifier = new DifficultySongClassifier();

    int s1D = classifier.calculateDifficulty(easySong);
    int s2D = classifier.calculateDifficulty(harderSong);

    System.out.println("difficulties: " + s1D + " and " + s2D);
    assertTrue(s2D > s1D);
  }

  @Test
  public void multipeTimesTest() {
    boolean[] easySong = {
        false, false, false, false, false, false, false,
        false, true, false, false, false, false,

        false, false, false, false, false, false, false,
        false, false, false, false, false, false,

        false, false, false, false, false, false, false,
        false, false, false, false, false, false};

    boolean[] harderSong = {
        false, false, false, false, false, false, false,
        false, true, false, false, false, false,

        false, false, false, false, false, false, false,
        false, false, false, true, false, false,

        false, false, false, false, false, false, false,
        false, true, false, false, false, false};

    // Song s1 = new Song(null, null, 1, null, null, 2, easySong);
    // Song s2 = new Song(null, null, 1, null, null, 2, harderSong);

    DifficultySongClassifier classifier = new DifficultySongClassifier();

    int s1D = classifier.calculateDifficulty(easySong);
    int s2D = classifier.calculateDifficulty(harderSong);

    System.out.println("difficulties: " + s1D + " and " + s2D);
    assertTrue(s2D > s1D);
  }

  @Test
  public void multipeStrokesInLineTest() {
    boolean[] easySong = {
        false, false, false, false, false, false, false,
        false, true, false, false, false, false,

        false, false, false, false, false, false, false,
        false, false, true, false, false, false,

        false, false, false, false, false, false, false,
        false, false, true, false, false, false};

    boolean[] harderSong = {
        false, false, false, false, false, false, false,
        false, true, false, false, false, false,

        false, true, false, false, false, false, false,
        false, false, false, true, false, false,

        false, false, false, false, false, false, false,
        false, true, true, false, false, false};

    DifficultySongClassifier classifier = new DifficultySongClassifier();

    int s1D = classifier.calculateDifficulty(easySong);
    int s2D = classifier.calculateDifficulty(harderSong);

    System.out.println("difficulties: " + s1D + " and " + s2D);
    assertTrue(s2D > s1D);
  }

  @Test
  public void sameDifficultyDifferentKeys() {
    boolean[] easySong = {
        false, false, false, false, false, false, false,
        false, true, false, false, false, false,

        false, false, false, false, false, false, false,
        false, false, true, false, false, false,

        false, false, false, false, false, false, false,
        false, false, true, false, false, false};

    boolean[] harderSong = {
        false, true, false, false, false, false, false,
        false, false, false, false, false, false,

        false, false, false, false, false, false, false,
        false, true, false, false, false, false,

        false, false, false, false, true, false, false,
        false, false, false, false, false, false};

    DifficultySongClassifier classifier = new DifficultySongClassifier();

    int s1D = classifier.calculateDifficulty(easySong);
    int s2D = classifier.calculateDifficulty(harderSong);

    System.out.println("difficulties: " + s1D + " and " + s2D);
    assertTrue(s2D == s1D);
  }

}
