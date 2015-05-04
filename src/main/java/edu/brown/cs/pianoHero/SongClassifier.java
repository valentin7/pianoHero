package edu.brown.cs.pianoHero;

/**
 * The SongClassifier Interface is for classes that classify songs in different
 * categories, such as difficulty, genre, etc.
 *
 * @author valentin
 *
 */
public interface SongClassifier {

  /**
   * The totalKeys used in the keyboard for playing a song.
   */
  public static final int totalKeys = 13;

  /**
   * Classifies the song and returns it's classification as a string.
   *
   * @param s
   *          - the song to be classified.
   * @return string - the type of song this is.
   */
  public String classifySong(Song s);

}
