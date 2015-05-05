package edu.brown.cs.pianoHeroFiles;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PianoHeroFileHandler {

  public void doFileHandling() {

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("filename.txt"), "utf-8"))) {
      File dir = new File("pianoHeroFiles/songImages/");
      File actualFile = new File(dir, "hey");

      File f = new File("C:\\pianoHeroFiles\\songImages\\kiwiCover.png");
      System.out.println(f.getName().toString());

      File newDir = new File("pianoHeroFiles/songImages/new/");
      File newFile = new File(f, "kiwi2.png");

      // System.out.println(actualFile);
      writer.write("something");

      writeFile("pianoHeroFiles/test.txt", "something, brah.");

      Set<File> allMp3s = new HashSet<File>();
      File mp3Dir = new File("pianoHeroFiles/songs/");
      getAllFilesAndFolder(mp3Dir, allMp3s);

      for (File fm : allMp3s) {
        System.out.println("song:");
        System.out.println(fm);
        if (!fm.isDirectory()) {
          File dest = new File(fm.getParentFile().toString(), "new"
              + fm.getName());
          copyFile(fm, dest);
        }
      }

    } catch (IOException e) {
      System.err.println("ERROR: error saving the file");
      e.printStackTrace();
    }
  }

  /**
   * Saves an image to file directory and returns its saved path as a string
   *
   * @param image
   *          file
   * @return path saved
   */
  public static String saveImage(String imageName) {
    try {
      File image = new File("Images/" + imageName);
      // File imageDir = new File("pianoHeroFiles/songImages/");
      File imageDir = new File("src/main/resources/static/img/");

      File saveDir = new File("../img/");

      File dest = new File(imageDir, ""
          + image.getName());

      File savedDir = new File(saveDir, ""
          + image.getName());

      copyFile(image, dest);

      return savedDir.getPath();
    } catch (IOException e) {
      System.err.println("ERROR: error saving image");
    }
    return null;
  }

  /**
   * Saves an mp3 file directory and returns its saved path as a string
   *
   * @param mp3
   *          file
   * @return path saved
   */
  public static String saveMp3(String mp3Name) {
    try {
      File mp3 = new File("Songs/" + mp3Name);
      // File songsDir = new File("pianoHeroFiles/songs/");
      File songsDir = new File("src/main/resources/static/songs/");
      File saveDir = new File("../songs/");

      File dest = new File(songsDir, ""
          + mp3.getName());

      File saveDest = new File(saveDir, ""
          + mp3.getName());
      copyFile(mp3, dest);

      return saveDest.getPath();
    } catch (IOException e) {
      System.err.println("ERROR: error saving image");
    }
    return null;
  }

  /**
   * Saves the 1d-array boolean of keystrokes for a given song id.
   *
   * @param keyStrokes
   *          : 1d-array of booleans
   * @param songId
   *          : int, the song id
   * @return String of the path where the keystrokes file was saved.
   */
  public static String saveSongKeystrokes(boolean[] keyStrokes, int songId) {
    String path = "pianoHeroFiles/songKeyStrokes/";
    String keyStrokesID = songId + "_keyStrokes.txt";
    String keyStrokesPath = path + keyStrokesID;

    try (PrintWriter writer = new PrintWriter(keyStrokesPath, "UTF-8")) {
      String line = "";

      // this is for the fake, testing songs.
      if (keyStrokes == null) {
        System.out.println("FAKEEEEE");
        line += "1000100100010100010101";
      }

      for (int i = 0; i < keyStrokes.length; i++) {
        String add = keyStrokes[i] ? "1" : "0";
        line += add;
      }
      writer.println(line);

      writer.close();
    } catch (IOException e) {
      System.err
      .println("ERROR: error saving keystrokes for songId: " + songId);
    }
    return keyStrokesPath;
  }

  /**
   * Saves a 2d-array boolean of keystrokes for a given song id.
   *
   * @param keyStrokes
   *          : 2d-array of booleans
   * @param songId
   *          : int, the song id
   * @return String of the path where the keystrokes file was saved.
   */
  public static String save2DSongKeystrokes(boolean[][] keyStrokes, int songId) {
    String path = "pianoHeroFiles/songKeyStrokes/";
    String keyStrokesID = songId + "_keyStrokes.txt";
    String keyStrokesPath = path + keyStrokesID;
    try (PrintWriter writer = new PrintWriter(keyStrokesPath, "UTF-8")) {

      for (int i = 0; i < keyStrokes.length; i++) {
        String line = "";
        for (int j = 0; j < keyStrokes[i].length; j++) {
          String add = keyStrokes[i][j] ? "1" : "0";
          line += add;
        }
        writer.println(line);
      }
      writer.close();
    } catch (IOException e) {
      System.err
      .println("ERROR: error saving keystrokes for songId: " + songId);
    }
    return keyStrokesPath;
  }

  /**
   * Converts a 1d array of booleans to a 2d array of booleans.
   *
   * @param array
   *          : the initial 1d array
   * @param length
   *          : the length of the partitions.
   * @return the converted 2d array.
   */
  public static boolean[][] convert1DBooleansTo2D(boolean[] array, int length) {
    boolean[][] boolean2d = new boolean[length][array.length / length];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < array.length / length; j++) {
        boolean2d[i][j] = array[j + i * length];
      }
    }
    return boolean2d;
  }

  /**
   * Converts a 2d array of booleans to a 1d array of booleans.
   *
   * @param array
   *          : the initial 2d array
   * @return the converted 1d array.
   */
  public static boolean[] convert2DBooleansTo1D(boolean[][] boolean2D) {
    boolean[] boolean1D = new boolean[boolean2D.length * boolean2D[0].length];

    for (int i = 0; i < boolean2D.length; i++) {
      for (int j = 0; j < boolean2D[i].length; j++) {
        assert (boolean2D[i].length == boolean2D[0].length);
        boolean1D[j + i * boolean2D.length] = boolean2D[i][j];
      }
    }
    return boolean1D;
  }

  /**
   * Returns a file from a given string path
   *
   * @param path
   *          string representing the file path
   * @return the File in the path
   */
  public static File getFileFromPath(String path) {
    File file = new File(path);
    return file;
  }

  /**
   * Saves all the files and folders in a set, for a given initial folder.
   *
   * @param folder
   *          the initial folder to look all files for.
   * @param all
   *          the set of files to save on
   */
  public static void getAllFilesAndFolder(File folder, Set<File> all) {
    all.add(folder);
    if (folder.isFile()) {
      return;
    }
    for (File file : folder.listFiles()) {
      if (file.isFile()) {
        all.add(file);
      }
      if (file.isDirectory()) {
        getAllFilesAndFolder(file, all);
      }
    }
  }

  /**
   * Gets the file of the strokes and converts it to a 1d boolean array to
   * return
   *
   * @param fileName
   *          the file name of the keystrokes
   * @return the 1d array of the strokes
   */
  public static boolean[] getStrokesArray(String fileName) {
    // This will reference one line at a time
    String line = null;

    // FileReader reads text files in the default encoding.
    try (FileReader fileReader =
        new FileReader(fileName)) {

      // It's good to always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader =
          new BufferedReader(fileReader);

      int length = 0;
      ArrayList<Boolean> results = new ArrayList<Boolean>();
      while ((line = bufferedReader.readLine()) != null) {
        if (line != null) {
          length = line.length();
          for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '0') {
              results.add(false);
            } else if (line.charAt(i) == '1') {
              results.add(true);
            }
          }
        }
      }

      boolean[] results1D = new boolean[results.size()];
      for (int i = 0; i < results.size(); i++) {
        results1D[i] = results.get(i);
      }

      bufferedReader.close();

      return results1D;

      // convert1DBooleansTo2D(results1D, length);

    } catch (FileNotFoundException ex) {
      System.out.println(
          "Unable to open file '" +
              fileName + "'");
    } catch (IOException ex) {
      System.out.println(
          "Error reading file '"
              + fileName + "'");
    }
    return null;
  }

  /**
   * Copies a file from an initial source path file to a destination
   *
   * @param src
   *          - the initial source file
   * @param dst
   *          - the destination path file
   * @throws IOException
   *           exception with file handling
   */
  public static void copyFile(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);
    try {
      // Transfer bytes from in to out
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    } catch (IOException e) {
      System.err.println("ERROR: couldn't copy file in directory");
    } finally {
      in.close();
      out.close();
    }
  }

  /**
   * Save the given text to the given filename.
   *
   * @param canonicalFilename
   *          Like /Users/al/foo/bar.txt
   * @param text
   *          All the text you want to save to the file as one String.
   * @throws IOException
   */
  public static void writeFile(String canonicalFilename, String text)
      throws IOException
  {
    File file = new File(canonicalFilename);
    BufferedWriter out = new BufferedWriter(new FileWriter(file));
    out.write(text);
    out.close();
  }

  /**
   * Write an array of bytes to a file. Presumably this is binary data; for
   * plain text use the writeFile method.
   */
  public static void writeFileAsBytes(String fullPath, byte[] bytes)
      throws IOException
  {
    OutputStream bufferedOutputStream = new BufferedOutputStream(
        new FileOutputStream(fullPath));
    InputStream inputStream = new ByteArrayInputStream(bytes);
    int token = -1;

    while ((token = inputStream.read()) != -1) {
      bufferedOutputStream.write(token);
    }
    bufferedOutputStream.flush();
    bufferedOutputStream.close();
    inputStream.close();
  }

  /**
   * Convert a byte array to a boolean array. Bit 0 is represented with false,
   * Bit 1 is represented with 1
   *
   * @param bytes
   *          byte[]
   * @return boolean[]
   */
  public static boolean[] byteArray2BitArray(byte[] bytes) {
    boolean[] bits = new boolean[bytes.length * 8];
    for (int i = 0; i < bytes.length * 8; i++) {
      if ((bytes[i / 8] & (1 << (7 - (i % 8)))) > 0) {
        bits[i] = true;
      }
    }
    return bits;
  }

}
