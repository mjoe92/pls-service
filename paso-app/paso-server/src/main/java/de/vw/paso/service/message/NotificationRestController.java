package de.vw.paso.service.message;

import java.util.List;

import de.vw.paso.logic.message.MessageManager;
import de.vw.paso.message.Notification;
import de.vw.paso.message.domain.UserMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = NotificationRestService.URL)
public class NotificationRestController implements NotificationRestService {

    private final MessageManager messageManager;

    public NotificationRestController(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Override
    @GetMapping(path = "/{userId}")
    public Notification pollMessages(@PathVariable String userId) {
        List<UserMessage> messages = messageManager.getUserMessages(userId);
        return new Notification(messages.stream().map(this::toUserMessageDTO).toList());
    }

    @Override
    @PostMapping
    @Transactional
    public void createUserMessage(@RequestBody String userMessage) {
        messageManager.createGlobalMessage(userMessage);
    }

    private UserMessageDTO toUserMessageDTO(UserMessage userMessage) {
        UserMessageDTO userMessageDTO = new UserMessageDTO();
        userMessageDTO.setId(userMessage.getId());
        userMessageDTO.setMessage(userMessage.getMessage());
        userMessageDTO.setUserId(userMessage.getUserId());
        userMessageDTO.setRead(userMessage.getRead());
        userMessageDTO.setCreated(userMessage.getCreated());
        userMessageDTO.setType(userMessage.getType());
        userMessageDTO.setVehicleConfigId(userMessage.getVehicleConfigId());

        return userMessageDTO;
    }
}
