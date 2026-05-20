package de.vw.paso.job;

import java.util.Collection;

import de.vw.paso.logic.message.MessageManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.message.domain.UserMessage;
import de.vw.paso.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final UserManager userManager;
    private final MessageManager messageManager;

    @Value("${notification.message.send.after}")
    private Integer notificationMessageSendAfter;

    public NotificationScheduler(UserManager userManager, MessageManager messageManager) {
        this.userManager = userManager;
        this.messageManager = messageManager;
    }

    @Scheduled(cron = "${notification.checkmessage.cron}")
    public void checkMessages() {
        Collection<User> allUser = userManager.getAllUsersForNotification();
        for (User user : allUser) {
            Collection<UserMessage> messages = messageManager.loadUnreadMessagesOlderThan(user.getId(),
                    notificationMessageSendAfter);

            for (UserMessage message : messages) {
                message.setRead(UserMessage.MESSAGE_READ);
                messageManager.save(message);
            }
        }
    }
}