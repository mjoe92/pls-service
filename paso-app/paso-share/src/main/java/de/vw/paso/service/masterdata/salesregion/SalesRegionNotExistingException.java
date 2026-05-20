package de.vw.paso.service.masterdata.salesregion;

import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.modelimport.IImportModelConsumer;
import lombok.Getter;

public class SalesRegionNotExistingException extends AbstractServerValidationException {
  @Getter
  private final ModelImport modelImport;

  public SalesRegionNotExistingException(String message, ModelImport modelImport) {
    super(message);
    this.modelImport = modelImport;
  }

  @Override
  public String getMessageKey() {
    return "validation.SalesRegionNotExisting";
  }

  @Override
  public void accept(ServiceConsumer consumer) {
    ((IImportModelConsumer)consumer).handle(this);
  }
}
