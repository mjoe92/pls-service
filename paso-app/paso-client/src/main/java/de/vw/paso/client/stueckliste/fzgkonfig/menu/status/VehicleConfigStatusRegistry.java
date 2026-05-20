package de.vw.paso.client.stueckliste.fzgkonfig.menu.status;

import java.util.EnumMap;
import java.util.Map;

import javafx.scene.image.Image;

import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.FzgKonfigIcon;
import de.vw.paso.vehicle.VehicleConfigStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VehicleConfigStatusRegistry {

  private static final Map<VehicleConfigStatus, Image> mapper = new EnumMap<>(VehicleConfigStatus.class);

  static {
    mapper.put(VehicleConfigStatus.INITIAL, null);
    mapper.put(VehicleConfigStatus.EDIT, ActionIcon.EDIT_24X24.getImage());
    mapper.put(VehicleConfigStatus.WAIT, FzgKonfigIcon.WAIT_24X24.getImage());
    mapper.put(VehicleConfigStatus.OK, FzgKonfigIcon.OK_24X24.getImage());
  }

  public static Image getImage(final VehicleConfigStatus status) {
    return mapper.get(status);
  }

}
