package de.vw.paso.client.stueckliste.fzgkonfig.menu.item;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import de.vw.paso.client.base.I18N;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.vehicle.VehicleConfigCategory;

public class FzgKonfigStuecklisteMenuItem extends FzgKonfigKategorieMenuItem {

  private StringBinding stuecklisteFzgBinding;
  private StringBinding stuecklisteMotBinding;
  private StringBinding stuecklisteGetrBinding;

  private ObjectProperty<ImportStatus> importStatusFzg;
  private ObjectProperty<ImportStatus> importStatusMot;
  private ObjectProperty<ImportStatus> importStatusGetr;

  FzgKonfigStuecklisteMenuItem(final Integer index, final VehicleConfigCategory vehicleConfigCategory) {
    super(index, vehicleConfigCategory);

    vehicleConfigProperty().addListener((obs, oldVal, newVal) -> updateTiWhImports());
  }

  private void updateTiWhImports() {
    VehicleConfigDTO vehicleConfig = getVehicleConfig();

    setImportStatusFzg(
      vehicleConfig.getTiWhImportVehicle() == null ? null : vehicleConfig.getTiWhImportVehicle().getImportStatus());
    setImportStatusMot(
      vehicleConfig.getTiWhImportMotor() == null ? null : vehicleConfig.getTiWhImportMotor().getImportStatus());
    setImportStatusGetr(
      vehicleConfig.getTiWhImportGearbox() == null ? null : vehicleConfig.getTiWhImportGearbox().getImportStatus());
  }

  StringBinding stuecklisteFzgBinding() {
    if (stuecklisteFzgBinding == null) {
      stuecklisteFzgBinding = Bindings.createStringBinding(() -> {
        if (getVehicleConfig().getTiWhImportVehicle() != null) {
          return getVehicleConfig().getTiWhImportVehicle().getProductKey();
        }

        return null;
      }, vehicleConfigProperty());
    }
    return stuecklisteFzgBinding;
  }

  StringBinding stuecklisteMotBinding() {
    if (stuecklisteMotBinding == null) {
      stuecklisteMotBinding = Bindings.createStringBinding(() -> {
        if (getVehicleConfig().getTiWhImportMotor() != null) {
          return getVehicleConfig().getTiWhImportMotor().getProductKey();
        }

        return null;
      }, vehicleConfigProperty());
    }
    return stuecklisteMotBinding;
  }

  StringBinding stuecklisteGetrBinding() {
    if (stuecklisteGetrBinding == null) {
      stuecklisteGetrBinding = Bindings.createStringBinding(() -> {
        if (getVehicleConfig().getTiWhImportGearbox() != null) {
          return getVehicleConfig().getTiWhImportGearbox().getProductKey();
        }

        return null;
      }, vehicleConfigProperty());
    }
    return stuecklisteGetrBinding;
  }

  public VehicleConfigCategory getVehicleConfigCategory() {
    return VehicleConfigCategory.KONFIGURATION;
  }

  public final ObjectProperty<ImportStatus> importStatusFzgProperty() {
    if (importStatusFzg == null) {
      importStatusFzg = new SimpleObjectProperty<>(getVehicleConfig().getTiWhImportVehicle() == null ? null
        : getVehicleConfig().getTiWhImportVehicle().getImportStatus());
    }
    return importStatusFzg;
  }

  public final ImportStatus getImportStatusFzg() {
    return this.importStatusFzgProperty().get();
  }

  public final void setImportStatusFzg(final ImportStatus importStatusFzg) {
    this.importStatusFzgProperty().set(importStatusFzg);
  }

  public final ObjectProperty<ImportStatus> importStatusMotProperty() {
    if (importStatusMot == null) {
      importStatusMot = new SimpleObjectProperty<>(getVehicleConfig().getTiWhImportMotor() == null ? null
        : getVehicleConfig().getTiWhImportMotor().getImportStatus());
    }
    return this.importStatusMot;
  }

  public final ImportStatus getImportStatusMot() {
    return this.importStatusMotProperty().get();
  }

  public final void setImportStatusMot(final ImportStatus importStatusMot) {
    this.importStatusMotProperty().set(importStatusMot);
  }

  public final ObjectProperty<ImportStatus> importStatusGetrProperty() {
    if (importStatusGetr == null) {
      importStatusGetr = new SimpleObjectProperty<>(getVehicleConfig().getTiWhImportGearbox() == null ? null
        : getVehicleConfig().getTiWhImportGearbox().getImportStatus());
    }
    return this.importStatusGetr;
  }

  public final ImportStatus getImportStatusGetr() {
    return this.importStatusGetrProperty().get();
  }

  public final void setImportStatusGetr(final ImportStatus importStatusGetr) {
    this.importStatusGetrProperty().set(importStatusGetr);
  }

  @Override
  protected ReadOnlyStringProperty categoryTextProperty() {
    if (categoryText == null) {
      categoryText = new SimpleStringProperty(I18N.getString("header.stueckliste"));
    }
    return categoryText;
  }
}
