package edu.brown.cs.pianoHero;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class PianoFileHandler {

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
      getAllFileAndFolder(mp3Dir, allMp3s);

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

  public static void getAllFileAndFolder(File folder, Set<File> all) {
    all.add(folder);
    if (folder.isFile()) {
      return;
    }
    for (File file : folder.listFiles()) {
      if (file.isFile()) {
        all.add(file);
      }
      if (file.isDirectory()) {
        getAllFileAndFolder(file, all);
      }
    }
  }

  static void copyFile(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);

    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
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

    while ((token = inputStream.read()) != -1)
    {
      bufferedOutputStream.write(token);
    }
    bufferedOutputStream.flush();
    bufferedOutputStream.close();
    inputStream.close();
  }

}
