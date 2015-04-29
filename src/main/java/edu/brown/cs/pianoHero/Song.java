package edu.brown.cs.pianoHero;

import java.io.File;

import edu.brown.cs.pianoHeroFiles.PianoHeroFileHandler;

/**
 * Represents a song to be played in PianoHero.
 *
 * @author nbyer
 *
 */
public class Song {

  private final String _title;
  private final int _id;
  private final boolean[] _keyStrokes;
  private String _mp3Path;
  private File _mp3File;
  private File _imageFile;
  private String _imagePath;
  private String _keyStrokesPath;
  private String _artistName;
  private int _length;

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
  public Song(String title, String artistName, int id, String mp3Path, String imagePath,
      int length, boolean[] keyStrokes) {
    _title = title;
    _artistName = artistName;
    _id = id;
    _mp3Path = mp3Path;
    _imagePath = imagePath;
    _length = length;
    _keyStrokes = keyStrokes;
    _keyStrokesPath = PianoHeroFileHandler.saveSongKeystrokes(keyStrokes, id);

    _mp3File = new File(_mp3Path);
    _imageFile = new File(_imagePath);
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
   * Returns the song's length.
   *
   * @return the song's length
   */
  public int get_length() {
    return _length;
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
   * Returns the song's artist's name.
   *
   * @return the song's artist's name
   */
  public String get_artistName() {
    return _artistName;
  }

  /**
   * Returns the path to the 2D array containing the song's keystrokes
   * information.
   *
   * @return the path to the 2D array containing the song's keystrokes
   *         information
   */
  public String get_keyStrokesPath() {
    return _keyStrokesPath;
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

  /**
   * Gets the image associated with this song.
   *
   * @return image File.
   */
  public File getImageFile() {
    return _imageFile;
  }

  /**
   * Gets the mp3 file associated with this song.
   *
   * @return mp3 file.
   */
  public File getMp3File() {
    return _mp3File;
  }

  /**
   * Gets the keystrokes array associated with this song.
   *
   * @return boolean array with key strokes.
   */
  public boolean[] getKeystrokes() {
    return _keyStrokes;
  }
}
