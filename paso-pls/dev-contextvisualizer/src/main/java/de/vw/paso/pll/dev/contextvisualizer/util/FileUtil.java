package de.vw.paso.pll.dev.contextvisualizer.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileUtil {

  public static void openTextFile(File file) throws IOException {
    try {
      Runtime.getRuntime().exec("\"C:\\Program Files (x86)\\Notepad++\\notepad++.exe\"" + file.getAbsolutePath());
    } catch (Exception e) {
      Desktop.getDesktop().open(file);
    }
  }
}
