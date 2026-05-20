package de.vw.paso.pls.repository;

import de.vw.paso.pls.model.domain.ProductData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductDataRepository extends MongoRepository<ProductData, ObjectId> {

  ProductData findByProductIdAndImportDate(final String productId, final LocalDate importDate);

  List<ProductData> findAllByProductId(final String productId);

  @Query("{ $or: [ { 'fileLocks': { $exists: true, $eq: [] } }, { 'fileLocks.expiryDate': { $lt: ?0 } } ] }")
  List<ProductData> findAllEmptyOrExpiredFileLock(final LocalDateTime expiryDate);

}
