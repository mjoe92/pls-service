package de.vw.paso.login.client;

import javafx.application.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paso {

  private static final Logger LOG = LoggerFactory.getLogger(Paso.class);

  public static void main(String[] args) throws PasoLoginPropertyException {
    LOG.info("=======================================================================");
    LOG.info(" Start PASO");
    LOG.info("=======================================================================");
    PasoClientProperties.load(args);

    Application.launch(PasoJavaFXApplication.class, args);
  }

}
