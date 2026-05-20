package de.vw.paso.client.base.service;

@FunctionalInterface
public interface ServiceCallWithoutReturn {

  void run() throws Exception;
}
