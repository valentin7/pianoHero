package edu.brown.cs.pianoHeroDatabaseTest;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import edu.brown.cs.pianoHeroFiles.PianoHeroFileHandler;

public class PianoHeroFileHandlerTest {

  @Test
  public void arraysConversionTests() {
    boolean[][] boolean2d = { {false, true}, {true, false}};

    boolean[] boolean1D = PianoHeroFileHandler.convert2DBooleansTo1D(boolean2d);
    System.out.println(Arrays.toString(boolean1D));
    boolean[] shouldBe = {false, true, true, false};
    for (int i = 0; i < shouldBe.length; i++) {
      assertTrue(boolean1D[i] == shouldBe[i]);
    }
    assertTrue(boolean1D.length == shouldBe.length);
  }

  @Test
  public void arrays1Dto2D() {
    boolean[] boolean1d = {false, true, true, false};

    boolean[][] boolean2D = PianoHeroFileHandler
        .convert1DBooleansTo2D(boolean1d, 2);

    System.out.println(Arrays.toString(boolean2D));
    boolean[][] shouldBe = { {false, true}, {true, false}};

    for (int i = 0; i < shouldBe.length; i++) {
      for (int j = 0; j < shouldBe[i].length; j++) {
        assertTrue(boolean2D[i][j] == shouldBe[i][j]);
      }
    }
    assertTrue(boolean2D.length == shouldBe.length);
  }
}
