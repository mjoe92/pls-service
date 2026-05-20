package de.vw.paso.client.util.desktop;

import java.io.IOException;

public class FileExistsException extends IOException {

  public FileExistsException() {
    super("File already exists");
  }
}
