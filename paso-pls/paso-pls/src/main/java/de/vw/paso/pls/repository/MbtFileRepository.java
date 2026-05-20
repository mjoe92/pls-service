package de.vw.paso.pls.repository;

import java.util.Optional;

import de.vw.paso.pls.model.domain.MbtFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MbtFileRepository extends MongoRepository<MbtFile, ObjectId> {
  Optional<MbtFile> findByFileName(String fileName);
}
