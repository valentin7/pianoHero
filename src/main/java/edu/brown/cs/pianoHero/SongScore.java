package edu.brown.cs.pianoHero;

/**
 * Represents a user's score achieved on a particular PianoHero song.
 *
 * @author nbyer
 *
 */
public class SongScore {

  private final int _score;
  private final int _songID;
  private final String _userName;

  /**
   * Instantiates a SongScore.
   *
   * @param songID
   *          the song's numeric ID
   * @param score
   *          the user's score
   * @param userName
   *          the name of the user who received this score
   */
  public SongScore(int songID, int score, String userName) {
    _score = score;
    _songID = songID;
    _userName = userName;
  }

  /**
   * Returns the user's score.
   * 
   * @return the user's score
   */
  public int getScore() {
    return _score;
  }

  /**
   * Returns the song ID.
   * 
   * @return the song ID
   */
  public int getSongID() {
    return _songID;
  }

  /**
   * Returns the name of the user who received the score.
   * 
   * @return the name of the user who received the score
   */
  public String getUserName() {
    return _userName;
  }

}
