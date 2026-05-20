package de.vw.paso.pls.repository;

import java.util.List;

import de.vw.paso.pls.model.domain.TiWhImportQueue;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TiWhImportQueueRepository extends MongoRepository<TiWhImportQueue, String> {

  TiWhImportQueue findByProductId(String productId);

  TiWhImportQueue findByRequestedTrue();

  List<TiWhImportQueue> findByRequestedFalse();
}
