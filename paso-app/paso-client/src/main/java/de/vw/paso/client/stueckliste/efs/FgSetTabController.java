package de.vw.paso.client.stueckliste.efs;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.control.tablebase.TableColumnHeaderChangeListener;
import de.vw.paso.client.main.ribbonmenu.fgset.RibbonMenuFgSetEvent;
import de.vw.paso.client.main.ribbonmenu.fgset.RibbonMenuFgSetListener;
import de.vw.paso.client.stueckliste.efs.tree.SingleVehicleFgSetController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabEvent;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.Getter;

@FXController(name = "fg-set-tab")
public class FgSetTabController extends BasePartlistTabController implements RibbonMenuFgSetListener {

    @FXML
    private Tab tabFgSet;
    @Getter
    @FXML
    private SplitPane splitPaneEfsView;
    @FXML
    private BorderPane borderPaneTab;
    @FXML
    private SingleVehicleFgSetController fgSetTreeController;

    @Getter
    private EfsViewTabPaneController efsViewTabPaneController;

    private ObjectProperty<PartListViewMode> ansichtEfsProperty;

    private ObjectProperty<EventHandler<Event>> selectTabEventHandler = new SimpleObjectProperty<>(this,
            "tabSelectionEventHandler");

    private EventHandler<EfsElementSelectionEvent> selectEfsElementEventHandler;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        registerSubController(fgSetTreeController);

        fgSetTreeController.setParentController(this);
    }

    @Override
    public void start() {
        loadEfsViewTabPane();

        registerEventHandlers();

        createScrollToFirstColumnListener();
        createScrollToLastColumnListener();
        createExpandTreeItemListener();
        createCollapseTreeItemListener();
    }

    private void registerEventHandlers() {
        efsViewTabPaneController.setEfsSelectionAction(getSelectEfsElementEventHandler());
    }

    private EventHandler<EfsElementSelectionEvent> getSelectEfsElementEventHandler() {
        if (selectEfsElementEventHandler == null) {
            selectEfsElementEventHandler = this::handleActionSelectEfsElement;
        }

        return selectEfsElementEventHandler;
    }

    private void createScrollToFirstColumnListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                fgSetTreeController.scrollToFirstColumn();
            }
        });
    }

    private void createScrollToLastColumnListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                fgSetTreeController.scrollToLastColumn();
            }
        });
    }

    private void createExpandTreeItemListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                fgSetTreeController.setSelectedItemExpanded();
            }
        });
    }

    private void createCollapseTreeItemListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                fgSetTreeController.setSelectedItemCollapsed();
            }
        });
    }

    public void handleActionSelectEfsElement(EfsElementSelectionEvent event) {
        fgSetTreeController.selectElementById(event.getEfsElementId());
    }

    public void fillGui(VehicleConfigDTO vehicleConfigDTO, final List<EfsElementDTO> efsElements) {
        fgSetTreeController.setEfsElemente(vehicleConfigDTO, efsElements);
    }

    public void setSetKeys(final List<SetKeyDTO> setKeysDTO) {
        fgSetTreeController.setSetKeys(setKeysDTO);
    }

    public void setAnsichtEfsProperty(final ObjectProperty<PartListViewMode> ansichtEfsProperty) {
        this.ansichtEfsProperty = ansichtEfsProperty;
    }

    @Override
    public Tab getControl() {
        return tabFgSet;
    }

    @Override
    public Parent getStyleableParent() {
        return borderPaneTab;
    }

    private void loadEfsViewTabPane() {
        efsViewTabPaneController = load(EfsViewTabPaneController.class);
        efsViewTabPaneController.setViewTabAction(this::handleEfsViewTabChange);
    }

    private void handleEfsViewTabChange(final EfsViewTabEvent event) {
        if (event.getTabCount() <= 0) {
            splitPaneEfsView.getItems().remove(efsViewTabPaneController.getControl());
        } else if (!splitPaneEfsView.getItems().contains(efsViewTabPaneController.getControl())) {
            splitPaneEfsView.setDividerPositions(0.65);
            splitPaneEfsView.getItems().add(efsViewTabPaneController.getControl());
        } else if (splitPaneEfsView.getDividerPositions()[0] > 0.65) {
            splitPaneEfsView.setDividerPositions(0.65);
        }
    }

    @Override
    public RibbonMenuFgSetEvent getRibbonMenuEvent() {
        return new RibbonMenuFgSetEvent(this, getControl().getText());
    }

    @Override
    public ObjectProperty<PartListViewMode> viewModeEfsProperty() {
        return ansichtEfsProperty;
    }

    @Override
    public void handleActionNavigateBack() {
        this.fgSetTreeController.handleActionNavigateBack();
    }

    @Override
    public void handleActionNavigateForward() {
        this.fgSetTreeController.handleActionNavigateForward();
    }

    @Override
    public void handleActionCollapseTree() {
        this.fgSetTreeController.handleActionCollapseTree();
    }

    @Override
    public void handleActionCollapseAllTree() {
        this.fgSetTreeController.handleActionCollapseAllTree();
    }

    @Override
    public void handleActionExpandTree() {
        this.fgSetTreeController.handleActionExpandTree();
    }

    @Override
    public void handleActionExpandAllTree() {
        this.fgSetTreeController.handleActionExpandAllTree();
    }

    @Override
    public void handleActionClearFilters() {
        this.fgSetTreeController.handleActionClearFilters();
    }

    @Override
    public void handleActionResetSorting() {
        this.fgSetTreeController.handleActionResetSorting();
    }

    @Override
    public void handleActionCloseSummary() {
        this.fgSetTreeController.handleActionCloseSummary();
    }

    @Override
    public BooleanProperty disablePropertyNavigateBack() {
        return fgSetTreeController.disablePropertyNavigateBack();
    }

    @Override
    public BooleanProperty disablePropertyNavigateForward() {
        return fgSetTreeController.disablePropertyNavigateForward();
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        return fgSetTreeController.disablePropertyClearFilters();
    }

    @Override
    public BooleanProperty disablePropertyResetSorting() {
        return fgSetTreeController.disablePropertyResetSorting();
    }

    @Override
    public BooleanProperty disablePropertyCloseSummary() {
        return fgSetTreeController.disablePropertyCloseSummary();
    }

    public final ObjectProperty<EventHandler<Event>> selectTabEventHandler() { // NO_UCD (use private)
        return selectTabEventHandler;
    }

    public final void setSelectTabAction(final EventHandler<Event> handler) { // NO_UCD (use private)
        selectTabEventHandler().set(handler);
    }

    public void setBindedTableList(final Map<Class<?>, TableColumnHeaderChangeListener> tables) {
        this.fgSetTreeController.setBindedColumnHeaderListeners(tables);
    }

    @Override
    public void handleActionShowCompareDialog() {
        fgSetTreeController.handleActionShowCompareDialog();
    }

    @Override
    public BooleanProperty disablePropertyCompare() {
        return this.fgSetTreeController.disablePropertyCompare();
    }

    @Override
    public void handleActionExcelExport() {
        this.fgSetTreeController.handleActionExcelExport();
    }

    @Override
    public BooleanProperty disablePropertyExcelExport() {
        return this.fgSetTreeController.disablePropertyExcelExport();
    }

    @Override
    public BooleanProperty toggleDisplayNumberOfPartsProperty() {
        return this.fgSetTreeController.toggleDisplayNumberOfPartsProperty();
    }
}
