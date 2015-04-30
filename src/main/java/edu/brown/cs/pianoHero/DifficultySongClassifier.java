package edu.brown.cs.pianoHero;

public class DifficultySongClassifier implements SongClassifier {

  final int closeEnoughToBeHard = 4;
  final int farEnoughToBeEasy = 10;
  final int oneSecond = 10;

  @Override
  public String classifySong(Song s) {

    boolean[] keyStrokes = s.getKeystrokes();

    int songDifficulty = calculateDifficulty(keyStrokes);

    return null;
  }

  public int calculateDifficulty(boolean[] keyStrokes) {

    int keysLength = SongClassifier.totalKeys;

    int currentDifficulty = 0;

    int timeInterval = 0;
    int timeForPreviousStroke = 0;

    for (int i = 0; i < keysLength; i++) {
      timeInterval++;
      timeInterval = timeInterval % oneSecond;

      for (int j = 0; j < keyStrokes.length / keysLength; j++) {
        if (keyStrokes[j + i * keysLength]) {

          int timeBetween = timeInterval - timeForPreviousStroke;

          currentDifficulty = currentDifficulty + getCloseAddition(timeBetween);
          currentDifficulty = currentDifficulty
              - getFarSubtraction(timeBetween);

          timeForPreviousStroke = i;
        }
      }
    }
    return currentDifficulty;
  }

  public int getCloseAddition(int timeBetween) {
    int secondsBetween = timeBetween / oneSecond;

    int addition = 0;
    if (secondsBetween <= closeEnoughToBeHard) {
      addition = (secondsBetween + closeEnoughToBeHard - 1)
          % closeEnoughToBeHard;
    }
    return addition;
  }

  public int getFarSubtraction(int timeBetween) {
    int secondsBetween = timeBetween / oneSecond;

    int subtraction = 0;

    if (secondsBetween >= farEnoughToBeEasy) {
      int tooFar = farEnoughToBeEasy * 3 / 2;
      if (secondsBetween >= tooFar) {
        subtraction = tooFar;
      } else {
        subtraction = secondsBetween;
      }
    }
    return subtraction;

  }

}
