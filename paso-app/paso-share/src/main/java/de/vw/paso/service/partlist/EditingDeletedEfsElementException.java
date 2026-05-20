package de.vw.paso.service.partlist;

import de.vw.paso.exception.ServerException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.apache.commons.lang3.StringUtils;

public class EditingDeletedEfsElementException extends ServerException {

  private final EfsElementDTO efsElement;

  public EditingDeletedEfsElementException(EfsElementDTO efsElement) {
    super(StringUtils.EMPTY);
    this.efsElement = efsElement;
  }

  @Override
  public String getMessageKey() {
    return "validation.EditingDeletedEfsElementException";
  }

  public EfsElementDTO getEfsElement() {
    return efsElement;
  }
}
