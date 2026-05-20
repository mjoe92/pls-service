package de.vw.paso.service.modelimport;

import de.vw.paso.exception.ServiceConsumer;

public interface ILoadModelImportsConsumer extends ServiceConsumer {
  void loadModelImports(String salesKey, Integer modelYear, String salesTag);
  void loadModelImports(String salesKey);
}
