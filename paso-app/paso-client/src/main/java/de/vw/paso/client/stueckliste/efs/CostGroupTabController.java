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
import de.vw.paso.client.main.ribbonmenu.costgroup.RibbonMenuCostGroupEvent;
import de.vw.paso.client.main.ribbonmenu.costgroup.RibbonMenuCostGroupListener;
import de.vw.paso.client.stueckliste.efs.tree.SingleVehicleCostGroupController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabEvent;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Getter;

@FXController(name = "cost-group-tab")
public class CostGroupTabController extends BasePartlistTabController implements RibbonMenuCostGroupListener {

    @FXML
    private Tab tabCostGroup;
    @Getter
    @FXML
    private SplitPane splitPaneEfsView;
    @FXML
    private BorderPane borderPaneTab;
    @FXML
    private SingleVehicleCostGroupController costGroupTreeController;

    @Getter
    private EfsViewTabPaneController efsViewTabPaneController;

    private ObjectProperty<PartListViewMode> ansichtEfsProperty;

    private ObjectProperty<EventHandler<Event>> selectTabEventHandler = new SimpleObjectProperty<>(this,
            "tabSelectionEventHandler");

    private EventHandler<EfsElementSelectionEvent> selectEfsElementEventHandler;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        registerSubController(costGroupTreeController);

        costGroupTreeController.setParentController(this);
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
                costGroupTreeController.scrollToFirstColumn();
            }
        });
    }

    private void createScrollToLastColumnListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                costGroupTreeController.scrollToLastColumn();
            }
        });
    }

    private void createExpandTreeItemListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                costGroupTreeController.setSelectedItemExpanded();
            }
        });
    }

    private void createCollapseTreeItemListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                costGroupTreeController.setSelectedItemCollapsed();
            }
        });
    }

    public void handleActionSelectEfsElement(EfsElementSelectionEvent event) {
        costGroupTreeController.selectElementById(event.getEfsElementId());
    }

    public void fillGui(final List<EfsElementDTO> efsElements) {
        costGroupTreeController.setEfsElemente(efsElements);
    }

    public void setCostGroups(final List<CostGroupDTO> costGroups) {
        if (!costGroups.isEmpty()) {
            costGroupTreeController.setCostGroups(costGroups);
        }
    }

    public void setAnsichtEfsProperty(final ObjectProperty<PartListViewMode> ansichtEfsProperty) {
        this.ansichtEfsProperty = ansichtEfsProperty;
    }

    @Override
    public Tab getControl() {
        return tabCostGroup;
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
    public RibbonMenuCostGroupEvent getRibbonMenuEvent() {
        return new RibbonMenuCostGroupEvent(this, getControl().getText());
    }

    @Override
    public ObjectProperty<PartListViewMode> viewModeEfsProperty() {
        return ansichtEfsProperty;
    }

    @Override
    public void handleActionNavigateBack() {
        this.costGroupTreeController.handleActionNavigateBack();
    }

    @Override
    public void handleActionNavigateForward() {
        this.costGroupTreeController.handleActionNavigateForward();
    }

    @Override
    public void handleActionCollapseTree() {
        this.costGroupTreeController.handleActionCollapseTree();
    }

    @Override
    public void handleActionCollapseAllTree() {
        this.costGroupTreeController.handleActionCollapseAllTree();
    }

    @Override
    public void handleActionExpandTree() {
        this.costGroupTreeController.handleActionExpandTree();
    }

    @Override
    public void handleActionExpandAllTree() {
        this.costGroupTreeController.handleActionExpandAllTree();
    }

    @Override
    public void handleActionClearFilters() {
        this.costGroupTreeController.handleActionClearFilters();
    }

    @Override
    public void handleActionResetSorting() {
        this.costGroupTreeController.handleActionResetSorting();
    }

    @Override
    public void handleActionCloseSummary() {
        this.costGroupTreeController.handleActionCloseSummary();
    }

    @Override
    public BooleanProperty disablePropertyNavigateBack() {
        return costGroupTreeController.disablePropertyNavigateBack();
    }

    @Override
    public BooleanProperty disablePropertyNavigateForward() {
        return costGroupTreeController.disablePropertyNavigateForward();
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        return costGroupTreeController.disablePropertyClearFilters();
    }

    @Override
    public BooleanProperty disablePropertyResetSorting() {
        return this.costGroupTreeController.disablePropertyResetSorting();
    }

    @Override
    public BooleanProperty disablePropertyCloseSummary() {
        return this.costGroupTreeController.disablePropertyCloseSummary();
    }

    public final ObjectProperty<EventHandler<Event>> selectTabEventHandler() { // NO_UCD (use private)
        return selectTabEventHandler;
    }

    public final void setSelectTabAction(final EventHandler<Event> handler) { // NO_UCD (use private)
        selectTabEventHandler().set(handler);
    }

    public void setBindedTableList(final Map<Class<?>, TableColumnHeaderChangeListener> tables) {
        this.costGroupTreeController.setBindedColumnHeaderListeners(tables);
    }

    @Override
    public void handleActionShowCompareDialog() {
        this.costGroupTreeController.handleActionShowCompareDialog();
    }

    @Override
    public BooleanProperty disablePropertyCompare() {
        return this.costGroupTreeController.disablePropertyCompare();
    }

    @Override
    public void handleActionExcelExport() {
        this.costGroupTreeController.handleActionExcelExport();
    }

    @Override
    public BooleanProperty disablePropertyExcelExport() {
        return this.costGroupTreeController.disablePropertyExcelExport();
    }

    @Override
    public BooleanProperty toggleDisplayNumberOfPartsProperty() {
        return this.costGroupTreeController.toggleDisplayNumberOfPartsProperty();
    }
}
