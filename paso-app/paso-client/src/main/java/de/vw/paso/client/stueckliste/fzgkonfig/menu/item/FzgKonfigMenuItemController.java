package de.vw.paso.client.stueckliste.fzgkonfig.menu.item;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.service.PollingServiceController;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.RefreshDatenstandEvent;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.status.VehicleConfigStatusRegistry;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.service.vehicle.VehicleConfigCategoryStatusDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.VehicleConfigStatus;

@FXController(name = "fzg-konfig-menuitem")
public class FzgKonfigMenuItemController extends BaseController<VBox> {

    protected static final int WAIT_POLL = 5000;

    private static final PseudoClass PSEUDO_CLASS_SELECTABLE = PseudoClass.getPseudoClass("selected");

    @FXML
    private VBox paneFzgKonfigMenu;
    @FXML
    protected Label labelIndex;
    @FXML
    protected Label labelName;
    @FXML
    protected ImageView statusIcon;
    @FXML
    private Label labelInfoText;

    private FzgKonfigKategorieMenuItem menuItem;
    private BooleanBinding selectable;
    private BooleanProperty valid;
    private ObjectBinding<Image> statusImageBinding;

    private final BooleanProperty poll = new SimpleBooleanProperty();

    public void initialize(Integer index, VehicleConfigCategory fzgKonfigKategorie,
            ObjectProperty<VehicleConfigDTO> vehicleConfig) {
        menuItem = createMenuItem(index, fzgKonfigKategorie);
        menuItem.vehicleConfigProperty().addListener((observable, oldVal, newVal) -> handleVehicleConfigChange());
        menuItem.vehicleConfigProperty().bind(vehicleConfig);

        labelIndex.textProperty().bind(Bindings.createStringBinding(() -> menuItem.menuIndexProperty().get() + 1 + ".",
                menuItem.menuIndexProperty()));
        labelName.textProperty().bind(menuItem.categoryTextProperty());
        statusIcon.imageProperty().bind(statusImageBinding());

        setPseudoClassSelectable(isSelectable());
        selectableProperty().addListener((obs, oldVal, newVal) -> setPseudoClassSelectable(newVal));
    }

    protected FzgKonfigKategorieMenuItem createMenuItem(Integer index, VehicleConfigCategory vehicleConfigCategory) {
        FzgKonfigKategorieMenuItem fzgKonfigKategorieMenuItem = new FzgKonfigKategorieMenuItem(index,
                vehicleConfigCategory);
        labelInfoText.textProperty().bind(fzgKonfigKategorieMenuItem.infoTextProperty());

        return fzgKonfigKategorieMenuItem;
    }

    public FzgKonfigKategorieMenuItem getMenuItem() {
        return menuItem;
    }

    private void setPseudoClassSelectable(Boolean newVal) {
        this.labelName.pseudoClassStateChanged(PSEUDO_CLASS_SELECTABLE, newVal);
    }

    private void handleVehicleConfigChange() {
        for (VehicleConfigCategoryStatusDTO categoryStatus : getVehicleConfig().getVehicleConfigCategoryStatus()) {
            if (menuItem.getVehicleConfigCategory() == categoryStatus.getVehicleConfigCategory()) {
                if (categoryStatus.getVehicleConfigStatus() == VehicleConfigStatus.WAIT) {
                    poll();
                }
                if (categoryStatus.getVehicleConfigStatus() != VehicleConfigStatus.INITIAL) {
                    menuItem.setVehicleConfigStatus(categoryStatus.getVehicleConfigStatus());
                }

                return;
            }
        }
    }

    protected final VehicleConfigDTO getVehicleConfig() {
        return menuItem.getVehicleConfig();
    }

    @Override
    public void stop() {
        super.stop();
        validProperty().unbind();
    }

    public final ObjectProperty<VehicleConfigStatus> vehicleConfigStatusPropery() {
        return menuItem.vehicleConfigStatusProperty();
    }

    private ObjectBinding<Image> statusImageBinding() {
        if (statusImageBinding == null) {
            statusImageBinding = Bindings.createObjectBinding(
                    () -> VehicleConfigStatusRegistry.getImage(menuItem.getVehicleConfigStatus()),
                    menuItem.vehicleConfigStatusProperty());
        }

        return statusImageBinding;
    }

    public boolean isSelectable() {
        return selectableProperty().get();
    }

    private BooleanBinding selectableProperty() {
        if (selectable == null) {
            selectable = Bindings.createBooleanBinding(() -> switch (this.menuItem.getVehicleConfigStatus()) {
                case EDIT, OK, WAIT -> true;
                case INITIAL -> false;
            }, menuItem.vehicleConfigStatusProperty());
        }
        return selectable;
    }

    public final ReadOnlyObjectProperty<VehicleConfigCategory> vehicleConfigCategoryProperty() {
        return this.menuItem.vehicleConfigCategoryProperty();
    }

    public final BooleanProperty validProperty() {
        if (valid == null) {
            valid = new SimpleBooleanProperty();
        }
        return valid;
    }

    public final boolean isValid() {
        return this.validProperty().get();
    }

    private BooleanProperty pollProperty() {
        return poll;
    }

    public final void setPoll(final boolean poll) {
        pollProperty().set(poll);
    }

    protected void poll() {
        if (menuItem.getVehicleConfigCategory() == VehicleConfigCategory.MODELL) {
            pollDatenstand();
        }
    }

    protected void pollDatenstand() {
        PollingServiceController<VehicleConfigCategoryStatusDTO> serviceController = new PollingServiceController<>();
        serviceController.setOnSucceeded(e -> refreshDatenstand());
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(WAIT_POLL);
        serviceController.pollWhile(
                kategorieStatus -> VehicleConfigStatus.WAIT.equals(kategorieStatus.getVehicleConfigStatus()));
        serviceController.start(() -> VehicleConfigRestClientHolder.getInstance()
                .loadVehicleConfigCategoryStatus(menuItem.vehicleConfigProperty().get().getId(),
                        menuItem.getVehicleConfigCategory()));
        pollProperty().bindBidirectional(serviceController.pollProperty());
    }

    private void refreshDatenstand() {
        EventBus.getInstance().post(new RefreshDatenstandEvent());
    }

    @Override
    public VBox getControl() {
        return paneFzgKonfigMenu;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }
}
