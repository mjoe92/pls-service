package de.vw.paso.pls.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PlsLogRepository extends MongoRepository<PlsLog, String> {

  @Query(value = "{'logTime' : { $lte: ?0 } }", delete = true)
  void deleteOldLogs(LocalDate date);

  List<PlsLog> findByLogTimeGreaterThan(LocalDateTime startDate);
}
