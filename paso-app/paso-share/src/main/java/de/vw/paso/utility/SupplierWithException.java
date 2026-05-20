package de.vw.paso.utility;

import de.vw.paso.exception.ServerException;

@FunctionalInterface
public interface SupplierWithException<T> {

  T get() throws ServerException;

}
