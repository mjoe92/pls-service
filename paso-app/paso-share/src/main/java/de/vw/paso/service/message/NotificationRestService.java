package de.vw.paso.service.message;

import de.vw.paso.message.Notification;

public interface NotificationRestService {

  String URL = "/api/notification";

  Notification pollMessages(String userId);

  void createUserMessage(String userMessage);
}
