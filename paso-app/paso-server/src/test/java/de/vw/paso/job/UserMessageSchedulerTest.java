package de.vw.paso.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Arrays;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.message.MessageManager;
import de.vw.paso.message.UserMessageType;
import de.vw.paso.message.domain.UserMessage;
import de.vw.paso.repository.message.UserMessageRepository;
import de.vw.paso.util.TimestampUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMessageSchedulerTest extends AbstractServiceTests {

    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private MessageManager messageManager;

    @BeforeEach
    public void setUpDatabase() {
        userMessageRepository.deleteAll();
    }

    @Test
    public void testWithNoDeletableMessages() {
        setUpMessages(UserMessageType.ANNOUNCEMENT, TimestampUtils.getTimeStampDaysMinus(6),
                TimestampUtils.getTimeStampDaysMinus(6));
        setUpMessages(UserMessageType.PART_LIST_CREATED, TimestampUtils.getTimeStampDaysMinus(29),
                TimestampUtils.getTimeStampDaysMinus(29));

        messageManager.deleteUnfinishedMessages();

        assertEquals(4, userMessageRepository.findAll().size());
    }

    @Test
    public void testWithOneDeletableGlobalMessage() {
        Timestamp timestamp = TimestampUtils.getTimeStampDaysMinus(8);
        setUpMessages(UserMessageType.ANNOUNCEMENT, timestamp, TimestampUtils.getTimeStampDaysMinus(6));
        setUpMessages(UserMessageType.PART_LIST_CREATED, TimestampUtils.getTimeStampDaysMinus(29),
                TimestampUtils.getTimeStampDaysMinus(29));

        messageManager.deleteUnfinishedMessages();

        assertEquals(3, userMessageRepository.findAll().size());
        assertTrue(userMessageRepository.findAll().stream().noneMatch(msg -> msg.getCreated().equals(timestamp)));
    }

    @Test
    public void testWithOneDeletableSystemMessage() {
        Timestamp timestamp = TimestampUtils.getTimeStampDaysMinus(31);
        setUpMessages(UserMessageType.ANNOUNCEMENT, TimestampUtils.getTimeStampDaysMinus(6),
                TimestampUtils.getTimeStampDaysMinus(6));
        setUpMessages(UserMessageType.PART_LIST_CREATED, timestamp, TimestampUtils.getTimeStampDaysMinus(29));

        messageManager.deleteUnfinishedMessages();

        assertEquals(3, userMessageRepository.findAll().size());
        assertTrue(userMessageRepository.findAll().stream().noneMatch(msg -> msg.getCreated().equals(timestamp)));
    }

    @Test
    public void testWithAllDeletableSystemMessage() {
        Timestamp timestampSystemDeletable = TimestampUtils.getTimeStampDaysMinus(31);
        Timestamp timestampGlobalDeletable = TimestampUtils.getTimeStampDaysMinus(8);
        setUpMessages(UserMessageType.ANNOUNCEMENT, timestampGlobalDeletable, timestampGlobalDeletable);
        setUpMessages(UserMessageType.PART_LIST_CREATED, timestampSystemDeletable, timestampSystemDeletable);

        messageManager.deleteUnfinishedMessages();

        assertTrue(userMessageRepository.findAll().isEmpty());
    }

    private void setUpMessages(UserMessageType userMessageType, Timestamp... timestamps) {
        userMessageRepository.saveAll(
                Arrays.stream(timestamps).map(timestamp -> saveMessage(userMessageType, timestamp)).toList());
    }

    private UserMessage saveMessage(UserMessageType userMessageType, Timestamp timestamp) {
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId(TEST_USER_ID);
        userMessage.setMessage("MESSAGE");
        userMessage.setCreated(timestamp);
        userMessage.setRead(1);
        userMessage.setType(userMessageType);
        userMessage.setVehicleConfigId(1L);
        return userMessage;
    }
}
