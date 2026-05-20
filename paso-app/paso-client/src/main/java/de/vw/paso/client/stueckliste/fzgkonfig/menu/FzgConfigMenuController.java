package de.vw.paso.client.stueckliste.fzgkonfig.menu;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowEfsTabEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.UpdateVehicleConfigEvent;
import de.vw.paso.client.explorer.vehicleconfig.event.UpdateVehicleConfigEvent.UpdateEventType;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.item.FzgKonfigKategorieMenuItem;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.item.FzgKonfigMenuItemController;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.item.FzgKonfigStuecklisteMenuItemController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.pls.PlsRestClientHolder;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.delegate.usergroupservice.UserGroupRestClientHolder;
import de.vw.paso.pls.PartListStatus;
import de.vw.paso.pls.Status;
import de.vw.paso.service.pls.PlsRequestResultDTO;
import de.vw.paso.service.userproperty.SaveUserPropertyDTO;
import de.vw.paso.service.vehicle.ISaveVehicleConfigConsumer;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.PropertyType;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.VehicleConfigStatus;
import de.vw.paso.vehicle.dto.PartListRequestDTO;
import org.apache.commons.lang3.StringUtils;

@FXController(name = "fzg-config-menu")
public class FzgConfigMenuController extends BaseController<GridPane>
        implements Initializable, ISaveVehicleConfigConsumer {

    private final ObjectProperty<VehicleConfigDTO> vehicleConfig;

    @FXML
    private GridPane paneMenu;
    @FXML
    private ListView<FzgKonfigMenuItemController> listViewFzgConfigMenu;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Button buttonWeiter;
    @FXML
    private Button buttonFinish;

    private Runnable closeAction;

    private ObjectBinding<VehicleConfigCategory> selectedVehicleConfigCategoryBinding;

    private BooleanBinding previousSelectableBinding;
    private BooleanBinding nextSelectableBinding;
    private BooleanProperty valid;
    private BooleanProperty dirtyProperty;

    public FzgConfigMenuController() {
        vehicleConfig = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        listViewFzgConfigMenu.addEventFilter(KeyEvent.KEY_PRESSED, Event::consume);
        listViewFzgConfigMenu.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> setButtonEnablement(newValue));

        setCellFactory();
        initButtons();
    }

    private void setButtonEnablement(FzgKonfigMenuItemController newValue) {
        if (VehicleConfigCategory.ZUSAMMENFASSUNG.equals(newValue.vehicleConfigCategoryProperty().get())) {
            buttonFinish.setVisible(true);
            buttonFinish.setDisable(!getVehicleConfig().isEditable());
        } else if (buttonFinish.isVisible()) {
            buttonFinish.setVisible(false);
        }
    }

    private void initButtons() {
        buttonPrevious.setGraphic(new ImageView(ActionIcon.BACK_16x16.getImage()));
        buttonPrevious.disableProperty().bind(Bindings.not(previousSelectableBinding()));

        buttonWeiter.setGraphic(new ImageView(ActionIcon.FORWARD_16x16.getImage()));
        buttonWeiter.disableProperty().bind(Bindings.not(nextSelectableBinding()));
        buttonWeiter.visibleProperty().bind(buttonFinish.visibleProperty().not());
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }

    @Override
    public void saveVehicleConfig(VehicleConfigDTO vehicleConfig, Runnable onSaveSuccess) {
        boolean isNew = vehicleConfig.getId() == null;
        if ((vehicleConfig.getVehicleProject() != null) && (vehicleConfig.getName() != null)) {
            ServiceController<VehicleConfigDTO> serviceController = new ServiceController<>();

            serviceController.setOnSucceeded(e -> onSaveSuccess(serviceController.getValue(), isNew, onSaveSuccess));
            serviceController.setOnFailed(e -> handleException(serviceController.getException()));
            serviceController.setExecutionTime(1000);
            serviceController.start(() -> {
                VehicleConfigDTO result = VehicleConfigRestClientHolder.getInstance().saveFzgKonfig(vehicleConfig);
                if (isNew) {
                    UserPropertyRestClientHolder.getInstance()
                            .save(new SaveUserPropertyDTO(PropertyType.RECENTLY_USED, result.getId().toString()));
                    vehicleConfig.getUserGroups().stream().findFirst().ifPresent(
                            userGroup -> UserGroupRestClientHolder.getInstance()
                                    .addVehicleConfigToUserGroup(userGroup.getId(), result.getId()));
                }
                return result;
            });
        }
    }

    private void onSaveSuccess(VehicleConfigDTO vehicleConfig, boolean create, Runnable onSaveSuccess) {
        vehicleConfigProperty().set(vehicleConfig);

        EventBus.getInstance().post(new UpdateVehicleConfigEvent(getVehicleConfig(),
                create ? UpdateEventType.CREATE : UpdateEventType.UPDATE));

        if (onSaveSuccess != null) {
            onSaveSuccess.run();
        }
    }

    private void tryRequestPartList() {
        if (canRequestPartList()) {
            VehicleConfigDTO selectedConfig = getVehicleConfig();

            doAsync(() -> {
                PartListRequestDTO request = new PartListRequestDTO(selectedConfig.getId(),
                        selectedConfig.getVehicleProject().getProductKey());
                PlsRequestResultDTO result = PlsRestClientHolder.getInstance().requestPartList(request);
                if (PartListStatus.READY == result.status()) {
                    return PlsRestClientHolder.getInstance().createPartList(selectedConfig.getId());
                }

                return result.vehicleConfigDTO();
            }, result -> {
                if (result.getVehiclePartList() != null) {
                    ShowEfsTabEvent showEfsTabEvent = new ShowEfsTabEvent(result, true);
                    EventBus.getInstance().post(showEfsTabEvent);
                } else {
                    closeAction.run();
                }
                EventBus.getInstance().post(new UpdateVehicleConfigEvent(result, UpdateEventType.UPDATE));
            }, () -> closeAction.run());
        } else {
            buttonFinish.setDisable(false);
            stop();
        }
    }

    @Override
    public void start() {
        createMenuItems();
    }

    @Override
    public void stop() {
        super.stop();

        validProperty().unbind();

        for (FzgKonfigMenuItemController controller : listViewFzgConfigMenu.getItems()) {
            controller.setPoll(false);
            controller.stop();
        }
    }

    public BooleanProperty dirtyProperty() {
        if (dirtyProperty == null) {
            dirtyProperty = new SimpleBooleanProperty(false);
        }

        return dirtyProperty;
    }

    private boolean isDirty() {
        return dirtyProperty().get();
    }

    private void createMenuItems() {
        List<VehicleConfigCategory> vehicleConfigCategories = List.of(VehicleConfigCategory.values());

        ObjectProperty<VehicleConfigStatus>[] menuStatus = new ObjectProperty[vehicleConfigCategories.size()];

        ObservableList<FzgKonfigMenuItemController> menuItems = FXCollections.observableArrayList();

        for (int i = 0; i < vehicleConfigCategories.size(); i++) {
            FzgKonfigMenuItemController controller = createMenuItem(i, vehicleConfigCategories.get(i));

            menuItems.add(controller);

            menuStatus[i] = controller.vehicleConfigStatusPropery();
        }

        initBindings(menuStatus);

        listViewFzgConfigMenu.setItems(menuItems);
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        if (vehicleConfig.getStatus() != null && vehicleConfig.getStatus().equals(Status.COMPLETE) || (
                vehicleConfig.getVehiclePartList() == null && vehicleConfig.getPlsProductDataId() != null)) {
            listViewFzgConfigMenu.getSelectionModel().select(VehicleConfigCategory.ZUSAMMENFASSUNG.ordinal());
        } else {
            listViewFzgConfigMenu.getSelectionModel().select(0);
            listViewFzgConfigMenu.getSelectionModel().getSelectedItem().getMenuItem()
                    .updateVehicleConfigStatus(VehicleConfigStatus.EDIT);
        }
    }

    private VehicleConfigDTO getVehicleConfig() {
        return vehicleConfigProperty().get();
    }

    private FzgKonfigMenuItemController createMenuItem(int index, VehicleConfigCategory vehicleConfigCategory) {
        FzgKonfigMenuItemController menuItemController;

        if (vehicleConfigCategory.equals(VehicleConfigCategory.FZG_PROJEKT)) {
            menuItemController = BaseController.load(FzgKonfigStuecklisteMenuItemController.class);
        } else {
            menuItemController = BaseController.load(FzgKonfigMenuItemController.class);
        }

        menuItemController.validProperty().bind(validProperty());
        menuItemController.initialize(index, vehicleConfigCategory, vehicleConfigProperty());
        menuItemController.start();
        return menuItemController;
    }

    private void handleMousePressed(MouseEvent event, FzgKonfigMenuItemController menuItemController) {
        if (menuItemController.isSelectable()) {
            return;
        }

        event.consume();
    }

    private void setCellFactory() {
        Callback<ListView<FzgKonfigMenuItemController>, ListCell<FzgKonfigMenuItemController>> cellFactory = list -> {
            ListCell<FzgKonfigMenuItemController> cell = new ListCell<>() {
                @Override
                protected void updateItem(FzgKonfigMenuItemController item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else if (item.getControl() != null) {
                        setText(null);
                        Node currentNode = getGraphic();
                        Node newNode = item.getControl();
                        if (currentNode == null || !currentNode.equals(newNode)) {
                            setGraphic(newNode);
                        }
                    } else {
                        setText(item.toString());
                        setGraphic(null);
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    return;
                }

                FzgKonfigMenuItemController ctrl = listViewFzgConfigMenu.getItems().get(cell.getIndex());
                handleMousePressed(event, ctrl);
            });
            return cell;
        };
        listViewFzgConfigMenu.setCellFactory(cellFactory);
    }

    public final ObjectProperty<VehicleConfigDTO> vehicleConfigProperty() {
        return vehicleConfig;
    }

    public final ObjectBinding<VehicleConfigCategory> selectedVehicleConfigCategoryBinding() {
        if (selectedVehicleConfigCategoryBinding == null) {
            selectedVehicleConfigCategoryBinding = Bindings.createObjectBinding(() -> {
                if (getSelectedItem() == null) {
                    return null;
                }

                return getSelectedItem().vehicleConfigCategoryProperty().get();
            }, listViewFzgConfigMenu.getSelectionModel().selectedItemProperty());
        }

        return selectedVehicleConfigCategoryBinding;
    }

    private BooleanBinding previousSelectableBinding() {
        if (previousSelectableBinding == null) {
            previousSelectableBinding = Bindings.createBooleanBinding(() -> getSelectedIndex() > 0,
                    listViewFzgConfigMenu.getSelectionModel().selectedItemProperty());
        }

        return previousSelectableBinding;
    }

    private BooleanBinding nextSelectableBinding() {
        if (nextSelectableBinding == null) {
            nextSelectableBinding = Bindings.createBooleanBinding(() -> {
                if (getSelectedIndex() + 1 >= listViewFzgConfigMenu.getItems().size()) {
                    return false;
                }

                return isValid();
            }, listViewFzgConfigMenu.getSelectionModel().selectedItemProperty(), validProperty());
        }

        return nextSelectableBinding;
    }

    public final BooleanProperty validProperty() {
        if (valid == null) {
            valid = new SimpleBooleanProperty(false) {
                @Override
                protected void invalidated() {
                    handleValidChange();
                }
            };
        }

        return valid;
    }

    public final boolean isValid() {
        return this.validProperty().get();
    }

    private void handleValidChange() {
        if (isValid()) {
            getSelectedItem().getMenuItem().updateVehicleConfigStatus(VehicleConfigStatus.OK);

            FzgKonfigMenuItemController nextSelectableItem = getNextSelectableItem();
            if (nextSelectableItem == null) {
                return;
            }

            FzgKonfigKategorieMenuItem menuItem = nextSelectableItem.getMenuItem();
            if (VehicleConfigStatus.INITIAL.equals(menuItem.getVehicleConfigStatus())) {
                nextSelectableItem.getMenuItem().updateVehicleConfigStatus(VehicleConfigStatus.EDIT);
            }
        } else {
            getSelectedItem().getMenuItem().updateVehicleConfigStatus(VehicleConfigStatus.EDIT);
        }
    }

    public FzgKonfigMenuItemController getSelectedItem() {
        return listViewFzgConfigMenu.getSelectionModel().getSelectedItem();
    }

    public FzgKonfigMenuItemController getNextSelectableItem() {
        int nextSelectableIndex = getSelectedIndex() + 1;
        return nextSelectableIndex < listViewFzgConfigMenu.getItems().size() ?
                listViewFzgConfigMenu.getItems().get(nextSelectableIndex) : null;
    }

    private int getSelectedIndex() {
        return listViewFzgConfigMenu.getSelectionModel().getSelectedIndex();
    }

    @Override
    public GridPane getControl() {
        return paneMenu;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    private void initBindings(Observable[] menuStati) {
        allStatusOkProperty().bind(Bindings.createBooleanBinding(() -> {
            for (Observable observable : menuStati) {
                ObjectProperty<VehicleConfigStatus> status = (ObjectProperty<VehicleConfigStatus>) observable;

                if (status.getValue() != null && status.get() != VehicleConfigStatus.OK) {
                    return true;
                }
            }

            return getVehicleConfig().getVehiclePartList() != null;
        }, menuStati));
    }

    private BooleanProperty allStatusOkProperty;

    public final BooleanProperty allStatusOkProperty() {
        if (allStatusOkProperty == null) {
            allStatusOkProperty = new SimpleBooleanProperty(false);
        }

        return allStatusOkProperty;
    }

    @FXML
    private void handleButtonPrevious(Event event) {
        listViewFzgConfigMenu.getSelectionModel().selectPrevious();
    }

    @FXML
    private void handleButtonWeiter(Event event) {
        listViewFzgConfigMenu.getSelectionModel().selectNext();
    }

    @FXML
    private void handleButtonFinish() {
        saveVehicleConfig(getVehicleConfig(), this::tryRequestPartList);
    }

    private boolean canRequestPartList() {
        VehicleConfigDTO selectedConfig = getVehicleConfig();

        return selectedConfig.getValidDate() != null && selectedConfig.getVehicleProject() != null
                && StringUtils.isNotEmpty(selectedConfig.getPrNumberString()) && StringUtils.isNotEmpty(
                selectedConfig.getVehicleProject().getProductKey()) && selectedConfig.getVehiclePartList() == null
                && selectedConfig.getStatus().canRequestPartList();
    }

    public void saveVehicleConfig(Runnable onSaveSuccess) {
        if (isDirty()) {
            saveVehicleConfig(getVehicleConfig(), onSaveSuccess);
        }
    }
}
