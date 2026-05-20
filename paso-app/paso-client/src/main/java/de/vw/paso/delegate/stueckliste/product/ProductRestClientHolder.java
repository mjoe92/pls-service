package de.vw.paso.delegate.stueckliste.product;

import java.util.Objects;

import de.vw.paso.delegate.util.ObjectMapperHolder;
import de.vw.paso.delegate.util.PasoRestClient;

public class ProductRestClientHolder {

  private static ProductRestClient INSTANCE;

  public static void setInstance(ProductRestClient instance) {
    ProductRestClientHolder.INSTANCE = instance;
  }

  public static ProductRestClient getInstance() {
    if (Objects.isNull(INSTANCE)) {
      synchronized (ProductRestClientHolder.class) {
        INSTANCE = new ProductRestClient(ObjectMapperHolder.getInstance(), PasoRestClient.getInstance());
      }
    }
    return INSTANCE;
  }
}
