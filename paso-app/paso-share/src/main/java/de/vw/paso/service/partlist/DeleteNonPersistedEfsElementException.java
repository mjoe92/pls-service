package de.vw.paso.service.partlist;

import de.vw.paso.exception.ServerException;
import de.vw.paso.partlist.domain.EfsElement;
import lombok.Getter;

public class DeleteNonPersistedEfsElementException extends ServerException {

  @Getter
  private final EfsElement efsElement;

  public DeleteNonPersistedEfsElementException(final String message, final EfsElement efsElement) {
    super(message);
    this.efsElement = efsElement;
  }

  @Override
  public String getMessageKey() {
    return "server.DeleteNonPersistedEfsElement";
  }
}
