package de.vw.paso.logic.message;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import de.vw.paso.logic.activitylog.AdminActivityLogManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.message.UserMessageType;
import de.vw.paso.message.domain.UserMessage;
import de.vw.paso.repository.message.UserMessageRepository;
import de.vw.paso.util.TimestampUtils;
import de.vw.paso.utility.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageManager {

    private final UserMessageRepository userMessageRepository;
    private final UserManager userManager;
    private final AdminActivityLogManager adminActivityLogManager;

    @Value("${cleanup.after.days.global-message}")
    private int GLOBAL_MESSAGE_CLEANUP_DAYS;

    @Value("${cleanup.after.days.system-message}")
    private int SYSTEM_MESSAGE_CLEANUP_DAYS;

    public MessageManager(UserMessageRepository userMessageRepository, UserManager userManager,
            AdminActivityLogManager adminActivityLogManager) {
        this.userMessageRepository = userMessageRepository;
        this.userManager = userManager;
        this.adminActivityLogManager = adminActivityLogManager;
    }

    public List<UserMessage> load(String userId) {
        return userMessageRepository.findByUserIdAndRead(userId, UserMessage.MESSAGE_UNREAD);
    }

    public UserMessage save(final UserMessage message) {
        return userMessageRepository.save(message);
    }

    public List<UserMessage> loadUnreadMessagesOlderThan(String userId, int minutesOld) {
        LocalDateTime date = LocalDateTime.now().minusMinutes(minutesOld);
        return userMessageRepository.loadUnreadMessagesOlderThan(userId, DateUtil.convertToDate(date));
    }

    public List<UserMessage> getUserMessages(String userId) {
        List<UserMessage> messages = load(userId);

        messages.forEach(message -> {
            message.setRead(UserMessage.MESSAGE_READ);
            save(message);
        });
        return messages;
    }

    public void createGlobalMessage(String userMessage) {
        adminActivityLogManager.logSystemMessageCreated(userManager.getCurrentUserId(), userMessage);
        userManager.getAllUser().forEach(user -> {
            final UserMessage message = new UserMessage();

            message.setMessage(userMessage);
            message.setUserId(user.getId());
            message.setCreated(new Date());
            message.setType(UserMessageType.ANNOUNCEMENT);

            save(message);
        });
    }

    public void deleteUnfinishedMessages() {
        // global messages are announcements
        Collection<UserMessage> globalMessages = userMessageRepository.findByTypeAndCreatedLessThan(
                UserMessageType.ANNOUNCEMENT, TimestampUtils.getTimeStampDaysMinus(GLOBAL_MESSAGE_CLEANUP_DAYS));
        // system messages are not announcements
        Collection<UserMessage> systemMessages = userMessageRepository.findByTypeNotAndCreatedLessThan(
                UserMessageType.ANNOUNCEMENT, TimestampUtils.getTimeStampDaysMinus(SYSTEM_MESSAGE_CLEANUP_DAYS));

        userMessageRepository.deleteAll(Stream.concat(globalMessages.stream(), systemMessages.stream()).toList());
    }
}
