package de.vw.paso.service.partlist;

import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.apache.commons.lang3.StringUtils;

public class AppendToDeletedElementException extends AbstractServerValidationException {

  private final EfsElementDTO efsElement;

  public AppendToDeletedElementException(EfsElementDTO efsElement) {
    super(StringUtils.EMPTY);
    this.efsElement = efsElement;
  }

  @Override
  public void accept(ServiceConsumer consumer) {
    ((IAppendToDeletedElementConsumer) consumer).handle(this);
  }

  @Override
  public String getMessageKey() {
    return "validation.AppendToDeletedElementException";
  }

  public EfsElementDTO getEfsElement() {
    return efsElement;
  }
}
