package de.vw.paso.client.stueckliste.compare;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.image.ImageView;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.main.ribbonmenu.compare.config.RibbonMenuCompareConfigListener;
import de.vw.paso.client.model.tree.AbstractFlatTreeItem;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.TreeTableCellFactory;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.compare.config.CompareStatus;
import de.vw.paso.compare.config.ConfigCompareRow;
import de.vw.paso.delegate.stammdaten.prnumber.PrNumberRestClientHolder;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.PrNumberUtil;

@FXController(name = "config-compare-tab")
public class ConfigCompareTabController extends AbstractCompareTabController<ConfigCompareRow>
        implements RibbonMenuCompareConfigListener {

    private static final String HIGHLIGHT_COMMON = "highlight-common";
    private static final String HIGHLIGHT_DIFFERENCES = "highlight-differences";
    private static final String HIGHLIGHT_DIFFERENCES_DESCRIPTION = "highlight-differences-description";
    private static final String COL_STYLE = "highlight-col-selection";
    private static final String ROW_STYLE = "highlight-row-selection";

    @FXML
    private Tab tabConfig;
    @FXML
    private SplitPane configCompareSplitPane;
    @FXML
    private CustomTreeTableView<ConfigCompareRow> configTreeView;
    @FXML
    private TreeTableColumn<ConfigCompareRow, String> prNumberFamily;
    @FXML
    private TreeTableColumn<ConfigCompareRow, String> prNumber;
    @FXML
    private TreeTableColumn<ConfigCompareRow, String> description1;
    @FXML
    private TreeTableColumn<ConfigCompareRow, String> description2;
    @FXML
    private TreeTableColumn<ConfigCompareRow, Date> prNumberBeginDate;
    @FXML
    private TreeTableColumn<ConfigCompareRow, String> prNumberBeginDateKey;
    @FXML
    private TreeTableColumn<ConfigCompareRow, Date> prNumberEndDate;
    @FXML
    private TreeTableColumn<ConfigCompareRow, String> prNumberEndDateKey;

    private final SelectionHighlightManager<ConfigCompareRow> highlightManager;

    private final List<PrNumberDTO> allPrNumbers;
    private final Map<String, AbstractFlatTreeItem<ConfigCompareRow>> prFamilyToTreeItemMap;
    private final Map<String, AbstractFlatTreeItem<ConfigCompareRow>> prNumberAndAddNameToTreeItemMap;
    private final Set<AbstractFlatTreeItem<ConfigCompareRow>> familiesWithDiff;
    private final Set<AbstractFlatTreeItem<ConfigCompareRow>> familiesWithComm;
    private final Table<String, Long, CompareStatus> prNumberVehicleConfigIdToStatusTable;
    private final Map<String, List<PrNumberDTO>> familyToPrNumbersMap;
    private final Table<Long, String, PrNumberDTO> vcIdFamilyNameToSelectedPrNumber;
    private final Set<String> families;

    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty toggleDisplayAllProperty;
    private BooleanProperty togglePropertyFilterDiff;
    private BooleanProperty togglePropertyFilterCommon;
    private BooleanProperty toggleGroupPrNumbers;

    private List<VehicleConfigDTO> vehicleConfigs;

    private AbstractFlatTreeItem<ConfigCompareRow> root;

    private boolean isResettingSort;

    public ConfigCompareTabController() {
        highlightManager = new SelectionHighlightManager<>();
        allPrNumbers = new ArrayList<>();
        prFamilyToTreeItemMap = new HashMap<>();
        prNumberAndAddNameToTreeItemMap = new HashMap<>();
        familiesWithDiff = new HashSet<>();
        familiesWithComm = new HashSet<>();
        prNumberVehicleConfigIdToStatusTable = HashBasedTable.create();
        familyToPrNumbersMap = new HashMap<>();
        vcIdFamilyNameToSelectedPrNumber = HashBasedTable.create();
        families = new HashSet<>();
        vehicleConfigs = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initColumns();
        initTable();

        highlightManager.initTable(configTreeView, ROW_STYLE, COL_STYLE);
    }

    @Override
    public void stop() {
        super.stop();
        highlightManager.removeFromTable();
    }

    @Override
    protected CustomTreeTableView<ConfigCompareRow> getTreeTableView() {
        return configTreeView;
    }

    @Override
    protected SplitPane getSplitPane() {
        return configCompareSplitPane;
    }

    @Override
    protected Tab getTab() {
        return tabConfig;
    }

    @Override
    protected AbstractCompareResult getResult() {
        return null;
    }

    private void initTable() {
        configTreeView.makeHeaderWrappable();
        configTreeView.makeFilterable();
        configTreeView.setShowRoot(false);
        configTreeView.setEditable(false);
        configTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleSelection(newValue));

        configTreeView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<ConfigCompareRow, ?>>) change -> resetSort());

        for (TreeTableColumn<ConfigCompareRow, ?> column : configTreeView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> resetSort());

            initColumnAlignment(column);
        }
    }

    private void resetSort() {
        if (!isResettingSort) {
            disablePropertyResetSorting().set(false);
        }
    }

    private void initColumns() {
        prNumberFamily.setCellValueFactory(cellData -> createPrNumberFamily(cellData.getValue()));
        prNumber.setCellValueFactory(cellData -> createPrNumberName(cellData.getValue()));

        prNumber.setCellFactory(param -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);

                setText(text);
                TreeTableRow<ConfigCompareRow> treeTableRow = getTableRow();
                if (treeTableRow == null || treeTableRow.getTreeItem() == null) {
                    return;
                }

                ConfigCompareRow value = treeTableRow.getTreeItem().getValue();
                if (value != null && toggleGroupPrNumbers.get() && value.isGroupSubItem()) {
                    setText(null);
                }
            }
        });

        description1.setCellValueFactory(data -> createPrNumberFamilyDescription(data.getValue()));
        description2.setCellValueFactory(data -> createPrNumberAdditionDescription(data.getValue()));
        prNumberBeginDateKey.setCellValueFactory(data -> createPrNumberStartKey(data.getValue()));
        prNumberEndDateKey.setCellValueFactory(data -> createPrNumberEndDateKey(data.getValue()));

        prNumberBeginDate.setCellValueFactory(data -> createPrNumberBeginDate(data.getValue()));
        prNumberBeginDate.setCellFactory(TreeTableCellFactory.forDateColumn());

        prNumberEndDate.setCellValueFactory(data -> createPrNumberEndDate(data.getValue()));
        prNumberEndDate.setCellFactory(TreeTableCellFactory.forDateColumn());
    }

    private SimpleStringProperty createPrNumberEndDateKey(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        return row.getPrNumberFamily() == null ? new SimpleStringProperty(row.getPrNumber().endKey()) : null;
    }

    private SimpleObjectProperty<Date> createPrNumberEndDate(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        return row.getPrNumberFamily() == null ? new SimpleObjectProperty<>(row.getPrNumber().endDate()) : null;
    }

    private SimpleStringProperty createPrNumberStartKey(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        return row.getPrNumberFamily() == null ? new SimpleStringProperty(row.getPrNumber().startKey()) : null;
    }

    private SimpleObjectProperty<Date> createPrNumberBeginDate(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        return row.getPrNumberFamily() == null ? new SimpleObjectProperty<>(row.getPrNumber().startDate()) : null;
    }

    private SimpleStringProperty createPrNumberAdditionDescription(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        return row.getPrNumberFamily() == null ? new SimpleStringProperty(row.getPrNumber().additionalName()) : null;
    }

    private SimpleStringProperty createPrNumberFamilyDescription(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        String text = row.getPrNumberFamily() == null ? row.getPrNumber().description() :
                row.getPrNumberFamily().description();
        return new SimpleStringProperty(text);
    }

    private SimpleStringProperty createPrNumberName(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        PrNumberFamilyDTO family = row.getPrNumberFamily();
        return family == null ? new SimpleStringProperty(row.getPrNumber().name()) : null;
    }

    private SimpleStringProperty createPrNumberFamily(TreeItem<ConfigCompareRow> data) {
        ConfigCompareRow row = data.getValue();
        if (row == null) {
            return null;
        }

        PrNumberFamilyDTO family = row.getPrNumberFamily();
        return family == null ? null : new SimpleStringProperty(family.name());
    }

    private void loadPrNumbers() {
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            Collection<PrNumberDTO> prNumbersConfig = PrNumberRestClientHolder.getInstance()
                    .loadPrNumbersForConfig(vehicleConfig.getVehicleProject().getId()).prNumberDTOList();

            allPrNumbers.addAll(prNumbersConfig);
            Collection<String> selectedPrNumbers = PrNumberUtil.split(vehicleConfig.getPrNumberString());
            for (PrNumberDTO prNumberDTO : prNumbersConfig) {
                families.add(prNumberDTO.prNumberFamily().name());
                familyToPrNumbersMap.computeIfAbsent(prNumberDTO.prNumberFamily().name(), name -> new ArrayList<>())
                        .add(prNumberDTO);
                prNumberVehicleConfigIdToStatusTable.put(prNumberDTO.name() + prNumberDTO.additionalName(),
                        vehicleConfig.getId(), CompareStatus.NOT_SELECTED);
                //todo: need to check it!
                if (selectedPrNumbers.contains(prNumberDTO.name())) {
                    vcIdFamilyNameToSelectedPrNumber.put(vehicleConfig.getId(), prNumberDTO.prNumberFamily().name(),
                            prNumberDTO);
                }
            }
        }

        allPrNumbers.sort(Comparator.comparing(prNumber -> ((PrNumberDTO) prNumber).prNumberFamily().name())
                .thenComparing(prNr -> ((PrNumberDTO) prNr).name()));
    }

    public void setVehicleConfigs(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO reference) {
        this.vehicleConfigs = vehicleConfigs;

        if (!isSameProductKey(vehicleConfigs)) {
            prNumberBeginDate.setVisible(false);
            prNumberBeginDateKey.setVisible(false);

            prNumberEndDate.setVisible(false);
            prNumberEndDateKey.setVisible(false);
        }

        loadPrNumbers();
        createTreeItems();
        calculateCompareStatus();
        createVehicleConfigColumns(reference);
    }

    private boolean isSameProductKey(List<VehicleConfigDTO> vehicleConfigs) {
        return vehicleConfigs.stream().map(vc -> vc.getVehicleProject().getProductKey()).distinct().count() == 1;
    }

    private void calculateCompareStatus() {
        for (String family : families) {
            Collection<PrNumberDTO> selectedPrNumbers = vehicleConfigs.stream()
                    .map(config -> vcIdFamilyNameToSelectedPrNumber.get(config.getId(), family))
                    .filter(Objects::nonNull).toList();

            // check same pr number and same additional name
            Collection<String> prNames = new HashSet<>();
            Collection<String> addNames = new HashSet<>();
            for (PrNumberDTO prNumber : selectedPrNumbers) {
                prNames.add(prNumber.name());
                addNames.add(prNumber.additionalName());
            }

            CompareStatus status = CompareStatus.SELECTED_SAME;
            if (prNames.size() > 1) {
                status = CompareStatus.SELECTED_DIFFERENT_PR_NUMBER;
            } else {
                if (addNames.size() > 1 || vehicleConfigs.size() != selectedPrNumbers.size()) {
                    status = CompareStatus.SELECTED_DIFFERENT_DESCRIPTION;
                }
            }

            for (VehicleConfigDTO config : vehicleConfigs) {
                PrNumberDTO prNumber = vcIdFamilyNameToSelectedPrNumber.get(config.getId(), family);
                if (prNumber != null) {
                    prNumberVehicleConfigIdToStatusTable.put(prNumber.name() + prNumber.additionalName(),
                            config.getId(), status);
                }
            }

            switch (status) {
                case SELECTED_SAME:
                    familiesWithComm.add(prFamilyToTreeItemMap.get(family));
                    break;
                case SELECTED_DIFFERENT_DESCRIPTION:
                case SELECTED_DIFFERENT_PR_NUMBER:
                    familiesWithDiff.add(prFamilyToTreeItemMap.get(family));
            }
        }
    }

    private void createVehicleConfigColumns(VehicleConfigDTO reference) {
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            boolean isReference = vehicleConfig.equals(reference);

            TreeTableColumn<ConfigCompareRow, Object> configColumn = new TreeTableColumn<>(vehicleConfig.getName());
            configColumn.setCellFactory(param -> new TreeTableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(null);
                    getStyleClass().remove(HIGHLIGHT_COMMON);
                    getStyleClass().remove(HIGHLIGHT_DIFFERENCES);
                    getStyleClass().remove(HIGHLIGHT_DIFFERENCES_DESCRIPTION);

                    TreeItem<ConfigCompareRow> rowItem = getTableRow().getTreeItem();
                    if (rowItem == null) {
                        return;
                    }

                    ConfigCompareRow value = rowItem.getValue();
                    if (value.getPrNumberFamily() != null) {
                        return;
                    }

                    CompareStatus compareStatus = prNumberVehicleConfigIdToStatusTable.get(
                            rowItem.getValue().getPrNumber().name() + rowItem.getValue().getPrNumber().additionalName(),
                            vehicleConfig.getId());
                    if (compareStatus == null) {
                        return;
                    }

                    RadioButton radioButton = new RadioButton();
                    radioButton.setDisable(true);
                    switch (compareStatus) {
                        case NOT_SELECTED:
                            radioButton.setSelected(false);
                            break;
                        case SELECTED_SAME:
                            radioButton.setSelected(true);
                            getStyleClass().add(HIGHLIGHT_COMMON);
                            break;
                        case SELECTED_DIFFERENT_PR_NUMBER:
                            radioButton.setSelected(true);
                            getStyleClass().add(HIGHLIGHT_DIFFERENCES);
                            break;
                        case SELECTED_DIFFERENT_DESCRIPTION:
                            radioButton.setSelected(true);
                            getStyleClass().add(HIGHLIGHT_DIFFERENCES_DESCRIPTION);
                            break;
                    }

                    setGraphic(radioButton);
                }
            });

            Label label = new Label(getTextForHeader(vehicleConfig));
            label.setTooltip(new Tooltip(getTooltipText(vehicleConfig)));
            label.setPadding(new Insets(0, 10, 0, 0));

            if (isReference) {
                label.setGraphic(new ImageView(StuecklisteIcon.REFERENCE_PART_LIST_FLAG_20x20.getImage()));
            }

            configColumn.setGraphic(label);

            if (isReference) {
                configTreeView.getColumns().add(8, configColumn);
            } else {
                configTreeView.getColumns().add(configColumn);
            }
        }
    }

    private void createTreeItems() {
        root = new AbstractFlatTreeItem<>(new ConfigCompareRow());

        String lastPrNumberName = null;
        for (PrNumberDTO numb : allPrNumbers) {
            AbstractFlatTreeItem<ConfigCompareRow> familyItem = prFamilyToTreeItemMap.get(numb.prNumberFamily().name());

            if (familyItem == null) {
                familyItem = new AbstractFlatTreeItem<>(new ConfigCompareRow(numb.prNumberFamily()));
                familyItem.setExpanded(true);
                prFamilyToTreeItemMap.put(numb.prNumberFamily().name(), familyItem);

                root.getSourceChildren().add(familyItem);
            }

            String prNumberAndAddName = numb.name() + numb.additionalName();
            if (!prNumberAndAddNameToTreeItemMap.containsKey(prNumberAndAddName)) {
                ConfigCompareRow rowObject = new ConfigCompareRow(numb, numb.name().equals(lastPrNumberName));
                AbstractFlatTreeItem<ConfigCompareRow> treeItem = new AbstractFlatTreeItem<>(rowObject);
                prNumberAndAddNameToTreeItemMap.put(prNumberAndAddName, treeItem);
                familyItem.getSourceChildren().add(treeItem);

                lastPrNumberName = numb.name();
            }
        }
        configTreeView.setRoot(root);
        initSorting();
    }

    private void handleFilterDifferences(Boolean newVal) {
        if (newVal) {
            this.root.getSourceChildren().setAll(sortFamilyTreeItems(familiesWithDiff));
            handleToggleGroup();
        }
    }

    private void handleFilterCommons(Boolean newVal) {
        if (newVal) {
            this.root.getSourceChildren().setAll(sortFamilyTreeItems(familiesWithComm));
            handleToggleGroup();
        }
    }

    private void handleDisplayAll(Boolean newVal) {
        if (newVal) {
            this.root.getSourceChildren().setAll(sortFamilyTreeItems(prFamilyToTreeItemMap.values()));
            handleToggleGroup();
        }
    }

    private void handleToggleGroup() {
        getTreeTableView().refresh();
    }

    private Collection<AbstractFlatTreeItem<ConfigCompareRow>> sortFamilyTreeItems(
            Collection<AbstractFlatTreeItem<ConfigCompareRow>> familiesUnSorted) {

        List<AbstractFlatTreeItem<ConfigCompareRow>> families = new ArrayList<>(familiesUnSorted);
        families.sort(Comparator.comparing(item -> item.getValue().getPrNumberFamily().name()));

        return families;
    }

    @Override
    public void handleActionExcelExport() {
        // not used, is never called.
    }

    @Override
    public BooleanProperty disablePropertyExcelExport() {
        // not used, is never called.
        if (disablePropertyExcelExport == null) {
            disablePropertyExcelExport = new SimpleBooleanProperty(true);
        }

        return disablePropertyExcelExport;
    }

    @Override
    public void handleActionCollapseTree() {
        boolean hasSelected = getSelectedTreeItem() != null;
        TreeItem<ConfigCompareRow> toSelect = hasSelected ? getSelectedTreeItem() : configTreeView.getRoot();
        ExpandCollapseUtil.setExpanded(configTreeView, toSelect, false, hasSelected);
    }

    @Override
    public void handleActionCollapseAllTree() {
        ExpandCollapseUtil.collapseAll(configTreeView.getRoot());
    }

    @Override
    public void handleActionExpandTree() {
        boolean hasSelected = getSelectedTreeItem() != null;
        TreeItem<ConfigCompareRow> toSelect = hasSelected ? getSelectedTreeItem() : configTreeView.getRoot();
        ExpandCollapseUtil.setExpanded(configTreeView, toSelect, true, hasSelected);
    }

    @Override
    public void handleActionExpandAllTree() {
        ExpandCollapseUtil.expandAll(configTreeView.getRoot());
    }

    @Override
    public void handleActionResetSorting() {
        isResettingSort = true;

        initSorting();
        disablePropertyResetSorting().set(true);

        isResettingSort = false;
    }

    private void initSorting() {
        configTreeView.getSortOrder().setAll(prNumberFamily);
    }

    @Override
    public BooleanProperty toggleDisplayAllProperty() {
        if (toggleDisplayAllProperty == null) {
            toggleDisplayAllProperty = new SimpleBooleanProperty(true);

            toggleDisplayAllProperty.addListener((obs, oldVal, newVal) -> handleDisplayAll(newVal));
        }

        return toggleDisplayAllProperty;
    }

    @Override
    public BooleanProperty toggleFilterDiffProperty() {
        if (togglePropertyFilterDiff == null) {
            togglePropertyFilterDiff = new SimpleBooleanProperty(false);

            togglePropertyFilterDiff.addListener((obs, oldVal, newVal) -> handleFilterDifferences(newVal));
        }

        return togglePropertyFilterDiff;
    }

    @Override
    public BooleanProperty toggleFilterCommProperty() {
        if (togglePropertyFilterCommon == null) {
            togglePropertyFilterCommon = new SimpleBooleanProperty(false);

            togglePropertyFilterCommon.addListener((obs, oldVal, newVal) -> handleFilterCommons(newVal));
        }

        return togglePropertyFilterCommon;
    }

    @Override
    public BooleanProperty toggleGroupPrNumbersProperty() {
        if (toggleGroupPrNumbers == null) {
            toggleGroupPrNumbers = new SimpleBooleanProperty(true);

            toggleGroupPrNumbers.addListener((obs, oldVal, newVal) -> handleToggleGroup());
        }

        return toggleGroupPrNumbers;
    }

    @Override
    public void actionReopenCompareTabs() {
        EventBus.getInstance().post(new ReopenCompareTabsEvent(vehicleConfigs));
    }
}
