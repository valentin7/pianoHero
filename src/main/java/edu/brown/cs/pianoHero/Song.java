package edu.brown.cs.pianoHero;

/**
 * Represents a song to be played in PianoHero.
 * 
 * @author nbyer
 *
 */
public class Song {

  private final String _title;
  private final int _id;
  private final String _mp3Path;
  private final String _imagePath;
  private final boolean[] _keyStrokes;

  /**
   * Instantiates a new Song.
   *
   * @param title
   *          the song's title
   * @param id
   *          the song's id
   * @param mp3Path
   *          the path to the song's mp3 file
   * @param imagePath
   *          the path to the song's image
   * @param keyStrokes
   *          the path to the 2D array containing the song's keystrokes
   */
  public Song(String title, int id, String mp3Path, String imagePath,
      boolean[] keyStrokes) {
    _title = title;
    _id = id;
    _mp3Path = mp3Path;
    _imagePath = imagePath;
    _keyStrokes = keyStrokes;
  }

  /**
   * Returns the song's id.
   *
   * @return the song's id
   */
  public int get_id() {
    return _id;
  }

  /**
   * Returns the path to the song's image file.
   *
   * @return the path to the song's image file
   */
  public String get_imagePath() {
    return _imagePath;
  }

  /**
   * Returns the path to the 2D array containing the song's keystrokes
   * information.
   *
   * @return the path to the 2D array containing the song's keystrokes
   *         information
   */
  public boolean[] get_keyStrokes() {
    return _keyStrokes;
  }

  /**
   * Returns the path to the song's mp3 file.
   *
   * @return the path to the song's mp3 file
   */
  public String get_mp3Path() {
    return _mp3Path;
  }

  /**
   * Returns the song's title.
   *
   * @return the song's title
   */
  public String get_title() {
    return _title;
  }

}
