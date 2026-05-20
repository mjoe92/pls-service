package de.vw.paso.pls.repository;

import de.vw.paso.pls.model.domain.CorruptedImport;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CorruptedImportRepository extends MongoRepository<CorruptedImport, ObjectId> {

}
