package de.vw.paso.service.partlist;

import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class MovingHierachyConflictException extends AbstractServerValidationException {

  private final EfsElementDTO efsElement;

  public MovingHierachyConflictException(EfsElementDTO efsElement) {
    super("");
    this.efsElement = efsElement;
  }

  @Override
  public void accept(ServiceConsumer consumer) {
    ((IMoveEfsElementConsumer) consumer).handle(this);
  }

  @Override
  public String getMessageKey() {
    return "validation.MovingHierachyConflictException";
  }

  public EfsElementDTO getEfsElement() {
    return efsElement;
  }
}
