package de.vw.paso.client.main.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import de.vw.paso.client.base.AbstractController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.PollingServiceController;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.explorer.vehicleconfig.event.UpdateVehicleConfigEvent;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.StatusUtil;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.message.NotificationRestClientHolder;
import de.vw.paso.message.Notification;
import de.vw.paso.service.message.UserMessageDTO;
import de.vw.paso.service.user.UserDTO;

public class NotificationController extends AbstractController {

  private static final String KEY_ANNOUNCEMENT_TITLE = "notification.announcement.title";

  private static final String KEY_PARTLISTUPDATE_TITLE = "notification.partlistupdate.title";
  private static final String KEY_PARTLISTUPDATE_HEADER = "notification.partlistupdate.header";
  private static final String KEY_PARTLISTUPDATE_MESSAGE = "notification.partlistupdate.message";

  private static final long DEFAULT_POLL_INTERVAL_IN_MS = 20000;
  private static final long MAX_POLL_INTERVAL_IN_MS = 60000;
  private static final int POLL_INCREASE_IN_MS = 10000;

  private UserDTO user;

  public NotificationController() {
  }

  public void start(UserDTO user) {
    this.user = user;
    pollNotifications();
  }

  private void pollNotifications() {
    PollingServiceController<Notification> serviceController = new PollingServiceController<>();
    serviceController.setOnSucceeded(e -> {
      handleMessage(serviceController.getValue());
      serviceController.setExecutionTime(DEFAULT_POLL_INTERVAL_IN_MS);
    });
    serviceController.setOnFailed(e -> {
      if (serviceController.getExecutionTime() < MAX_POLL_INTERVAL_IN_MS) {
        serviceController.setExecutionTime(serviceController.getExecutionTime() + POLL_INCREASE_IN_MS);
      } else {
        // Handle exception after we increased the timeout to the maximum
        handleException(serviceController.getException());
      }
    });
    serviceController.setExecutionTime(DEFAULT_POLL_INTERVAL_IN_MS);
    serviceController.setPoll(true);
    serviceController.start(() -> NotificationRestClientHolder.getInstance().pollMessages(user.getId()));
  }

  private void handleMessage(Notification notification) {
    List<UserMessageDTO> userMessages = notification.getUserMessages();
    List<UserMessageDTO> partListsToUpdate = new ArrayList<>();
    List<UserMessageDTO> createdPartLists = new ArrayList<>();
    List<UserMessageDTO> announcements = new ArrayList<>();
    for (UserMessageDTO userMessage : userMessages) {
      switch (userMessage.getType()) {
        case PART_LIST_READY, PART_LIST_IN_CREATION, PART_LIST_ERROR -> partListsToUpdate.add(userMessage);
        case PART_LIST_CREATED -> createdPartLists.add(userMessage);
        case ANNOUNCEMENT -> announcements.add(userMessage);
      }
    }
    announcements.forEach(this::showUserMessage);
    updatePartLists(partListsToUpdate, false);
    updatePartLists(createdPartLists, true);
  }

  private void updatePartLists(List<UserMessageDTO> partListUpdates, boolean notifyUser) {
    if (!partListUpdates.isEmpty()) {
      List<Long> vehicleConfigIds = partListUpdates.stream().map(UserMessageDTO::getVehicleConfigId).toList();
      doAsync(() -> VehicleConfigRestClientHolder.getInstance()
          .loadVehicleConfigs(vehicleConfigIds.stream().map(Object::toString).toList()).vehicleConfigDTOList(),
        result -> {
          EventBus.getInstance()
            .post(new UpdateVehicleConfigEvent(result, UpdateVehicleConfigEvent.UpdateEventType.UPDATE));

          if (notifyUser) {
            String configNames = result.stream().map(
              e -> e.getName() + " (" + e.getVehicleProject().getProjectName() + ")" + " -> " + StatusUtil.getName(
                e.getStatus())).collect(Collectors.joining(", "));
            String message = I18N.getString(KEY_PARTLISTUPDATE_MESSAGE) + "\n" + configNames;
            DialogUtil.showConfirmationDialog(Alert.AlertType.INFORMATION,
              I18N.getString(KEY_PARTLISTUPDATE_TITLE),
              I18N.getString(KEY_PARTLISTUPDATE_HEADER), message, ButtonType.CLOSE);
          }
        });
    }
  }

  private void showUserMessage(UserMessageDTO msg) {
    DialogUtil.showConfirmationDialog(Alert.AlertType.INFORMATION,
      I18N.getString(KEY_ANNOUNCEMENT_TITLE), null, msg.getMessage(), ButtonType.CLOSE);
  }
}
