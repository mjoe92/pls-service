package de.vw.paso.delegate.stueckliste.pst;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;

public class PstRestClientHolder {

  private static PstRestClient INSTANCE;

  public static void setInstance(PstRestClient instance) {
    PstRestClientHolder.INSTANCE = instance;
  }

  public static PstRestClient getInstance() {
    synchronized (PstRestClientHolder.class) {
      if (Objects.isNull(INSTANCE)) {
        INSTANCE = new PstRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
      }
      return INSTANCE;
    }
  }
}
