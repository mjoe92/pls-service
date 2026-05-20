package de.vw.paso.exception;

import java.io.Serializable;

public interface IServerException extends Serializable {

  String getMessageKey();

}
