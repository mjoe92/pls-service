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
import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;
import de.vw.paso.client.main.ribbonmenu.partgroup.RibbonMenuPartGroupEvent;
import de.vw.paso.client.main.ribbonmenu.partgroup.RibbonMenuPartGroupListener;
import de.vw.paso.client.stueckliste.efs.tree.SingleVehiclePartGroupController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabEvent;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.historie.event.EfsElementSelectionEvent;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Getter;

@FXController(name = "part-group-tab")
public class PartGroupTabController extends BasePartlistTabController implements RibbonMenuPartGroupListener {

    @FXML
    private Tab tabPartGroup;
    @Getter
    @FXML
    private SplitPane splitPaneEfsView;
    @FXML
    private BorderPane borderPaneTab;
    @FXML
    private SingleVehiclePartGroupController partGroupTreeController;

    @Getter
    private EfsViewTabPaneController efsViewTabPaneController;

    private ObjectProperty<PartListViewMode> ansichtEfsProperty;

    private ObjectProperty<EventHandler<Event>> selectTabEventHandler = new SimpleObjectProperty<>(this,
            "tabSelectionEventHandler");

    private EventHandler<EfsElementSelectionEvent> selectEfsElementEventHandler;

    @Override
    public AbstractRibbonMenuEvent<?> getRibbonMenuEvent() {
        return new RibbonMenuPartGroupEvent(this, getControl().getText());
    }

    @Override
    public Tab getControl() {
        return tabPartGroup;
    }

    @Override
    public Parent getStyleableParent() {
        return borderPaneTab;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);

        registerSubController(partGroupTreeController);

        partGroupTreeController.setParentController(this);
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
                partGroupTreeController.scrollToFirstColumn();
            }
        });
    }

    private void createScrollToLastColumnListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                partGroupTreeController.scrollToLastColumn();
            }
        });
    }

    private void createExpandTreeItemListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                partGroupTreeController.setSelectedItemExpanded();
            }
        });
    }

    private void createCollapseTreeItemListener() {
        final KeyCombination keyCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

        getStyleableParent().addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyCombo.match(keyEvent)) {
                partGroupTreeController.setSelectedItemCollapsed();
            }
        });
    }

    public void handleActionSelectEfsElement(EfsElementSelectionEvent event) {
        partGroupTreeController.selectElementById(event.getEfsElementId());
    }

    public void fillGui(final List<EfsElementDTO> efsElements) {
        partGroupTreeController.setEfsElements(efsElements);
    }

    public void setPartGroups(List<PartGroupVMO> partGroups) {
        this.partGroupTreeController.setPartGroups(partGroups);
    }

    @Override
    public void handleActionShowCompareDialog() {
        this.partGroupTreeController.handleActionShowCompareDialog();
    }

    @Override
    public void handleActionCloseSummary() {
        this.partGroupTreeController.handleActionCloseSummary();
    }

    @Override
    public BooleanProperty disablePropertyCompare() {
        return this.partGroupTreeController.disablePropertyCompare();
    }

    @Override
    public BooleanProperty disablePropertyCloseSummary() {
        return this.partGroupTreeController.disablePropertyCloseSummary();
    }

    @Override
    public void handleActionNavigateBack() {
        this.partGroupTreeController.handleActionNavigateBack();
    }

    @Override
    public void handleActionNavigateForward() {
        this.partGroupTreeController.handleActionNavigateForward();
    }

    @Override
    public void handleActionCollapseTree() {
        this.partGroupTreeController.handleActionCollapseTree();
    }

    @Override
    public void handleActionCollapseAllTree() {
        this.partGroupTreeController.handleActionCollapseAllTree();
    }

    @Override
    public void handleActionExpandTree() {
        this.partGroupTreeController.handleActionExpandTree();
    }

    @Override
    public void handleActionExpandAllTree() {
        this.partGroupTreeController.handleActionExpandAllTree();
    }

    @Override
    public void handleActionClearFilters() {
        this.partGroupTreeController.handleActionClearFilters();
    }

    @Override
    public void handleActionResetSorting() {
        this.partGroupTreeController.handleActionResetSorting();
    }

    @Override
    public BooleanProperty toggleDisplayNumberOfPartsProperty() {
        return this.partGroupTreeController.toggleDisplayNumberOfPartsProperty();
    }

    @Override
    public BooleanProperty disablePropertyNavigateBack() {
        return this.partGroupTreeController.disablePropertyNavigateBack();
    }

    @Override
    public BooleanProperty disablePropertyNavigateForward() {
        return this.partGroupTreeController.disablePropertyNavigateForward();
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        return this.partGroupTreeController.disablePropertyClearFilters();
    }

    @Override
    public BooleanProperty disablePropertyResetSorting() {
        return this.partGroupTreeController.disablePropertyResetSorting();
    }

    @Override
    public void handleActionExcelExport() {
        this.partGroupTreeController.handleActionExcelExport();
    }

    @Override
    public BooleanProperty disablePropertyExcelExport() {
        return this.partGroupTreeController.disablePropertyExcelExport();
    }

    @Override
    public ObjectProperty<PartListViewMode> viewModeEfsProperty() {
        return ansichtEfsProperty;
    }

    public void setAnsichtEfsProperty(final ObjectProperty<PartListViewMode> ansichtEfsProperty) {
        this.ansichtEfsProperty = ansichtEfsProperty;
    }

    public final ObjectProperty<EventHandler<Event>> selectTabEventHandler() {
        return selectTabEventHandler;
    }

    public final void setSelectTabAction(final EventHandler<Event> handler) { // NO_UCD (use private)
        selectTabEventHandler().set(handler);
    }

    public void setBindedTableList(final Map<Class<?>, TableColumnHeaderChangeListener> tables) {
        this.partGroupTreeController.setBindedColumnHeaderListeners(tables);
    }

}
