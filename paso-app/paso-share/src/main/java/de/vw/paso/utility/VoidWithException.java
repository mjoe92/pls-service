package de.vw.paso.utility;

import de.vw.paso.exception.ServerException;

@FunctionalInterface
public interface VoidWithException {

  void run() throws ServerException;

}
