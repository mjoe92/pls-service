package de.vw.paso.service.partlist;

import de.vw.paso.exception.ServerException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class CreateDeletedEfsElementException extends ServerException {

  private final EfsElementDTO efsElement;

  public CreateDeletedEfsElementException(EfsElementDTO efsElement) {
    super("");
    this.efsElement = efsElement;
  }

  @Override
  public String getMessageKey() {
    return "validation.CreateDeletedEfsElementException";
  }

  public EfsElementDTO getEfsElement() {
    return efsElement;
  }
}
