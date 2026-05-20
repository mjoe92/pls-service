package de.vw.paso.client.base.service;

@FunctionalInterface
public interface ServiceCallWithReturn<V> {

  V run() throws Exception;
}
