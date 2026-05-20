package de.vw.paso.service.partlist;

import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.ServiceConsumer;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class SameMaraInHierachyException extends AbstractServerValidationException {

  @Getter
  private String partNumber;

  public SameMaraInHierachyException(String partNumber) {
    super(StringUtils.EMPTY);
    this.partNumber = partNumber;
  }


  @Override
  public void accept(ServiceConsumer consumer) {
    ((IMaraHandlingConsumer)consumer).handle(this);
  }

  @Override
  public String getMessageKey() {
    return "validation.SameMaraInHierachyException";
  }


}
