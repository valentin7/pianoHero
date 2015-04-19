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

    // try (PrintWriter writer = new PrintWriter(
    // "sick.txt", "UTF-8")) {
    // writer.println("The first line");
    // writer.println("The second line");
    // writer.close();
    // } catch (IOException e) {
    // System.err.println("ERROR: error saving file");
    // e.printStackTrace();
    // }

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

  public void saveFile(File file) {

  }

  public static String saveSongKeystrokes(boolean[][] keyStrokes, int songId) {
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
      // e.printStackTrace();
    }
    return keyStrokesPath;
  }

  public static boolean[][] convert1DBooleansTo2D(boolean[] array, int length) {
    boolean[][] boolean2d = new boolean[length][array.length / length];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < array.length / length; j++) {
        boolean2d[i][j] = array[i * j];
      }
    }
    return boolean2d;
  }

  public static boolean[] convert2DBooleansTo1D(boolean[][] boolean2D) {
    // boolean[][] boolean2d = new boolean[length][array.length / length];
    boolean[] boolean1D = new boolean[boolean2D.length * boolean2D[0].length];

    for (int i = 0; i < boolean2D.length; i++) {
      for (int j = 0; j < boolean2D[i].length; j++) {
        assert (boolean2D[i].length == boolean2D[0].length);
        boolean1D[j * (1 + i)] = boolean2D[i][j];
      }
    }
    return boolean1D;
  }

  public static File getSongImageFromPath(String path) {
    File image = new File(path);
    return image;
  }

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

  public static boolean[][] getStrokesArray(String fileName) {
    // This will reference one line at a time
    String line = null;

    // FileReader reads text files in the default encoding.
    try (FileReader fileReader =
        new FileReader(fileName)) {

      // It's good to always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader =
          new BufferedReader(fileReader);
      System.out.println("opened file");

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

      System.out.println(results1D);
      return convert1DBooleansTo2D(results1D, length);

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
