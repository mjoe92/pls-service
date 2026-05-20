package de.vw.paso.repository.message;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.vw.paso.message.UserMessageType;
import de.vw.paso.message.domain.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

  List<UserMessage> findByUserIdAndRead(String userId, int onlyUnread);

  @Query("select m from UserMessage m where m.userId = :userId and m.read=0 and m.created < :created")
  List<UserMessage> loadUnreadMessagesOlderThan(@Param("userId") String userId, @Param("created") Date t);

  @Modifying
  @Transactional
  List<UserMessage> deleteByVehicleConfigIdIn(Collection<Long> ids);

  Set<UserMessage> findByTypeAndCreatedLessThan(UserMessageType type, Timestamp timestampCreate);

  Set<UserMessage> findByTypeNotAndCreatedLessThan(UserMessageType type, Timestamp timestampCreate);
}
