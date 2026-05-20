package de.vw.paso.pll.preprocessing.reader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import de.vw.paso.pll.preprocessing.PreprocessingContext;
import de.vw.paso.pll.preprocessing.PreprocessingException;
import de.vw.paso.pll.preprocessing.FileFormatChecker;
import de.vw.paso.pll.preprocessing.TiWhFileType;

public class FileChecker {

  public final FileFormatChecker fileFormatChecker;

  public FileChecker() {
    fileFormatChecker = new FileFormatChecker();
  }

  /**
   * Check if input files from context are complete, can be read and parsed as the
   * correct format. There must be one file for each TIWHFileType.
   *
   * @param ctx
   *   containing the input files
   * @throws PreprocessingException
   *   if completeness is not given.
   */
  public void checkCompleteness(PreprocessingContext ctx) throws PreprocessingException {
    for (TiWhFileType type : TiWhFileType.values()) {
      Optional<File> fileOptional = ctx.getInputFiles().stream().filter(f -> TiWhFileType.isType(type, f.getName()))
        .findFirst();

      if (fileOptional.isEmpty()) {
        throw new PreprocessingException("Input files do not contain a file of type %s", type.name());
      }

      File file = fileOptional.get();
      try {
        if (fileFormatChecker.testFormat(file.toPath(), type)) {
          ctx.getMappedFiles().put(type, file);
        } else {
          throw new PreprocessingException("Could not parse file %s as format %s", file.getName(), type);
        }
      } catch (IOException e) {
        throw new PreprocessingException(e, "Failed to read file %s", file.getName());
      }
    }
  }
}
