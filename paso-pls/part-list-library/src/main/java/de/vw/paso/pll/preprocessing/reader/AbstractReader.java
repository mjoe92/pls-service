package de.vw.paso.pll.preprocessing.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractReader implements AutoCloseable {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractReader.class);

  public static final String FILE_MODE_READ = "r";

  protected int rowsToSkip;

  AbstractReader() {
    rowsToSkip = 1;
  }

}
