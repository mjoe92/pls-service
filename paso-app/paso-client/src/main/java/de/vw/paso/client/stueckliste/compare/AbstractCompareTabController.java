package de.vw.paso.client.stueckliste.compare;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.Pair;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.cell.RemoveSummaryHighlightStylingEvent;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.control.treetable.TreeFilteringUpdateEvent;
import de.vw.paso.client.explorer.vehicleconfig.converter.DateTimeStringConverter;
import de.vw.paso.client.stueckliste.column.alignment.ColumnAlignment;
import de.vw.paso.client.stueckliste.efs.control.CommonCellUtil;
import de.vw.paso.client.stueckliste.efs.tree.ColumnSequenceChangeEvent;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedCompareTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.compare.ComparableRow;
import de.vw.paso.compare.costgroup.CostGroupCompareRow;
import de.vw.paso.compare.fgset.FGSetCompareRow;
import de.vw.paso.compare.partgroup.PartGroupCompareRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.EfsWeightUtil;
import de.vw.paso.utility.MathUtil;
import de.vw.paso.utility.StringConstant;

public abstract class AbstractCompareTabController<T extends ComparableRow<T>> extends BaseController<Tab> {

    protected static final String SUMMARY_MESSAGE = I18N.getString("table.row.summary");
    protected static final String UNKNOWN_MESSAGE = I18N.getString("table.row.unknown");
    protected static final String WEIGHT_UNIT = "g";
    private static final String HEADER_TEXT_SEPARATOR = StringConstant.SPACE_SLASH_SPACE;

    private static final DecimalFormat DEFAULT_DECIMAL_FORMATTER = new DecimalFormat("#,###",
            new DecimalFormatSymbols(Locale.GERMAN));

    private final List<TreeItem<T>> treeItems4Navigation;
    private final Map<Long, Map<ApCompareGroup, Double>> mapOfWeightsInPartList;
    private final Map<Long, Map<ApCompareGroup, String>> mapOfNumberOfPartsInPartList;
    private final Map<TreeTableColumn<T, String>, Collection<TreeTableColumn<T, String>>> mapOfExtraColumns;
    private final Map<ApCompareGroup, Map<TreeTableColumn<T, Object>, TreeTableColumn<T, String>>> mapOfApColumns;
    private final Map<TreeTableColumn<T, Object>, Integer> deltaColumnIndexMap;
    private final ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists;
    private final SelectionHighlightManager<T> highlightManager;

    private boolean isNavigateOverTreeItems;
    private int navigationIndex;

    private BooleanProperty disablePropertyNavigateForward;
    private BooleanProperty disablePropertyNavigateBack;
    private BooleanProperty disablePropertyClearFilters;
    private BooleanProperty disablePropertyCloseSummary;
    private Map<BorderPane, AbstractSummarisedTabController> mapOfSummaryControllers;

    public AbstractCompareTabController() {
        treeItems4Navigation = new ArrayList<>();
        mapOfWeightsInPartList = new HashMap<>();
        mapOfNumberOfPartsInPartList = new HashMap<>();
        mapOfExtraColumns = new HashMap<>();
        mapOfApColumns = new HashMap<>();
        deltaColumnIndexMap = new HashMap<>();
        mapOfDeltaWeightsOfPartLists = MultimapBuilder.hashKeys().arrayListValues().build();
        highlightManager = new SelectionHighlightManager<>();
        navigationIndex = -1;
    }

    public abstract void handleActionExcelExport();

    @Override
    public Tab getControl() {
        return getTab();
    }

    @Override
    public Parent getStyleableParent() {
        return getSplitPane();
    }

    @Override
    public void stop() {
        super.stop();

        treeItems4Navigation.clear();
    }

    public void handleActionCloseSummary() {
        BorderPane closedTab = (BorderPane) getSplitPane().getItems().remove(1);

        RemoveSummaryHighlightStylingEvent<T> highlightEvent = new RemoveSummaryHighlightStylingEvent<>(
                getTreeTableView().getRoot().getValue());
        EventBus.getInstance().post(highlightEvent);

        mapOfSummaryControllers.remove(closedTab);
        disablePropertyCloseSummary.set(true);
    }

    public void handleActionNavigateBack() {
        try {
            isNavigateOverTreeItems = true;

            --navigationIndex;

            setSelectedTreeItem(treeItems4Navigation.get(navigationIndex));
        } finally {
            isNavigateOverTreeItems = false;
        }
    }

    public void handleActionNavigateForward() {
        try {
            isNavigateOverTreeItems = true;

            ++navigationIndex;

            setSelectedTreeItem(treeItems4Navigation.get(navigationIndex));
        } finally {
            isNavigateOverTreeItems = false;
        }
    }

    public void handleActionClearFilters() {
        getTreeTableView().clearFilters();
        disablePropertyClearFilters().set(true);
    }

    public BooleanProperty disablePropertyCloseSummary() {
        if (disablePropertyCloseSummary == null) {
            disablePropertyCloseSummary = new SimpleBooleanProperty(true);
        }

        return disablePropertyCloseSummary;
    }

    public BooleanProperty disablePropertyNavigateBack() {
        if (disablePropertyNavigateBack == null) {
            disablePropertyNavigateBack = new SimpleBooleanProperty(true);
        }

        return disablePropertyNavigateBack;
    }

    public BooleanProperty disablePropertyNavigateForward() {
        if (disablePropertyNavigateForward == null) {
            disablePropertyNavigateForward = new SimpleBooleanProperty(true);
        }

        return disablePropertyNavigateForward;
    }

    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyClearFilters == null) {
            disablePropertyClearFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyClearFilters;
    }

    protected abstract CustomTreeTableView<T> getTreeTableView();

    protected abstract SplitPane getSplitPane();

    protected abstract Tab getTab();

    protected abstract AbstractCompareResult getResult();

    protected ListMultimap<Long, Map<ApCompareGroup, Double>> getMapOfDeltaWeightsOfPartLists() {
        return mapOfDeltaWeightsOfPartLists;
    }

    protected Map<ApCompareGroup, Map<TreeTableColumn<T, Object>, TreeTableColumn<T, String>>> getMapOfApColumns() {
        return mapOfApColumns;
    }

    protected Class<? extends AbstractSummarisedTabController> getSummarisedTabControllerClass() {
        throw new UnsupportedOperationException();
    }

    protected SplitPane getCompareSplitPane() {
        throw new UnsupportedOperationException();
    }

    protected Class<? extends AbstractSummarisedCompareTabController> getSummarisedCompareTabControllerClass() {
        throw new UnsupportedOperationException();
    }

    protected void setResult(AbstractCompareResult result) {
        for (VehicleConfigDTO vehicleConfig : result.getVehicleConfigs()) {
            Label label = new Label(getTextForHeader(vehicleConfig));
            label.setTooltip(new Tooltip(getTooltipText(vehicleConfig)));
            label.setPadding(new Insets(0, 10, 0, 0));

            boolean isReference = result.getReference() != null && vehicleConfig.equals(result.getReference());
            if (isReference) {
                label.setGraphic(new ImageView(StuecklisteIcon.REFERENCE_PART_LIST_FLAG_20x20.getImage()));
            }

            TreeTableColumn<T, Object> group = new TreeTableColumn<>(vehicleConfig.getName());
            group.setGraphic(label);

            for (ApCompareGroup apGroup : ApCompareGroup.values()) {
                TreeTableColumn<T, String> apColumn = createAPColumn(vehicleConfig, apGroup);
                group.getColumns().add(apColumn);
                getTreeTableView().setHeaderHeight(group, 48);

                if (mapOfExtraColumns != null) {
                    mapOfExtraColumns.put(apColumn, createExtraApColumns(vehicleConfig, apGroup));
                }

                if (mapOfApColumns != null) {
                    addToMapOfApColumns(apGroup, group, apColumn);
                }
            }

            if (isReference) {
                getTreeTableView().getColumns().add(2, group);
            } else {
                getTreeTableView().getColumns().add(group);
            }
        }

        for (Pair<VehicleConfigDTO, VehicleConfigDTO> configPair : getConfigPairsForCompare(result)) {
            TreeTableColumn<T, Object> group = new TreeTableColumn<>(
                    StringConstant.SPACE + " \n Differenz " + configPair.getValue().getName() + " zu "
                            + configPair.getKey().getName() + "\n" + StringConstant.SPACE);

            for (ApCompareGroup apGroup : ApCompareGroup.values()) {
                TreeTableColumn<T, String> column = createDeltaColumn(configPair.getValue(), configPair.getKey(),
                        apGroup);
                group.getColumns().add(column);

                if (mapOfApColumns != null) {
                    addToMapOfApColumns(apGroup, group, column);
                }
            }

            int index = 0;
            int colIndex = 0;
            for (TreeTableColumn<T, ?> column : getTreeTableView().getColumns()) {
                String name =
                        result.getReference() == null ? configPair.getKey().getName() : configPair.getValue().getName();
                if (column.getText().equals(name)) {
                    colIndex = index;
                    continue;
                }

                index++;
            }

            getTreeTableView().getColumns().add(++colIndex, group);
            if (deltaColumnIndexMap != null) {
                deltaColumnIndexMap.put(group, colIndex);
            }
        }
    }

    protected void calculateSumRow() {
        for (VehicleConfigDTO vehicleConfig : getResult().getVehicleConfigs()) {
            Collection<EfsElementDTO> efsElements = EfsElementResolver.getElementsInPartList(
                    vehicleConfig.getVehiclePartList());
            Map<ApCompareGroup, Double> weightsOfPartList = EfsWeightUtil.calculate(efsElements);

            weightsOfPartList.put(ApCompareGroup.SUM,
                    MathUtil.nullSafeAddition(weightsOfPartList.get(ApCompareGroup.HUT),
                            weightsOfPartList.get(ApCompareGroup.PLATFORM),
                            weightsOfPartList.get(ApCompareGroup.SYSTEM)));

            mapOfWeightsInPartList.put(vehicleConfig.getId(), weightsOfPartList);

            if (mapOfNumberOfPartsInPartList != null) {
                mapOfNumberOfPartsInPartList.put(vehicleConfig.getId(), calculateNumberOfParts(efsElements));
            }
        }

        for (Pair<VehicleConfigDTO, VehicleConfigDTO> configPair : getConfigPairsForCompare(getResult())) {
            Long vehicleConfigId = configPair.getValue().getId();
            Map<ApCompareGroup, Double> weightsOfSecondLast = mapOfWeightsInPartList.get(vehicleConfigId);
            Map<ApCompareGroup, Double> weightsOfLast = mapOfWeightsInPartList.get(configPair.getKey().getId());

            for (ApCompareGroup apGroup : ApCompareGroup.values()) {
                Double lastWeight = weightsOfLast.get(apGroup);
                Double secondLastWeight = weightsOfSecondLast.get(apGroup);

                Double delta = calculateDelta(lastWeight, secondLastWeight);
                Map<ApCompareGroup, Double> apCompareGroupWeights = Collections.singletonMap(apGroup, delta);

                mapOfDeltaWeightsOfPartLists.put(vehicleConfigId, apCompareGroupWeights);
            }
        }
    }

    protected void handleSelection(TreeItem<T> newValue) {
        if (newValue != null && newValue.getValue() != null) {
            addTreeItem4Navigation(newValue);
        }

        setNavigationState();
    }

    protected String getTextForHeader(VehicleConfigDTO vehicleConfig) {
        ModelDTO model = vehicleConfig.getModel();

        String firstRow = vehicleConfig.getVehicleProject().getProjectName() + HEADER_TEXT_SEPARATOR
                + vehicleConfig.getVehicleProject().getProductKey();

        String secondRow = model == null ? I18N.getString("label.modell.noModell") :
                model.getModelKey() + HEADER_TEXT_SEPARATOR + model.getModelImport().getSalesRegion().id()
                + HEADER_TEXT_SEPARATOR + model.getModelImport().getModelYear().toString();

        String thirdRow = I18N.getString("label.datenstand.erstellt") + StringConstant.SPACE
                + new DateTimeStringConverter().toString(Date.from(vehicleConfig.getTimestampCreate().toInstant()));

        return firstRow + "\n" + secondRow + "\n" + thirdRow;
    }

    protected String getTooltipText(VehicleConfigDTO vehicleConfig) {
        DateTimeStringConverter converter = new DateTimeStringConverter();
        String result = I18N.getString("label.fahrzeug.gueltigkeit") + StringConstant.SPACE + converter.toString(
                vehicleConfig.getValidDate());

        if (vehicleConfig.getModel() != null) {
            result +=
                    "\n" + I18N.getString("label.modell.bezeichnung") + StringConstant.SPACE + vehicleConfig.getModel()
                            .getDescription() + "\n" + I18N.getString("label.modell.einsatz") + StringConstant.SPACE
                            + converter.toString(vehicleConfig.getModel().getBeginDate()) + HEADER_TEXT_SEPARATOR
                            + converter.toString(vehicleConfig.getModel().getEndDate());
        }

        return result + "\n" + I18N.getString("label.datenstand.geaendert") + StringConstant.SPACE + converter.toString(
                vehicleConfig.getVehiclePartList().getTimestampChange()) + " - status";
    }

    protected TreeItem<T> getSelectedTreeItem() {
        if (getTreeTableView().getSelectionModel().isEmpty()) {
            return null;
        }

        return getTreeTableView().getSelectionModel().getSelectedItem();
    }

    protected void handleDisplayNumberOfParts(Boolean newVal) {
        if (newVal) {
            for (TreeTableColumn<T, String> column : mapOfExtraColumns.keySet()) {
                getTreeTableView().setHeaderHeight(column, 24);
                if (column.isVisible()) {
                    column.getColumns().addAll(mapOfExtraColumns.get(column));
                    getTreeTableView().setColGraphicWidthBindingFor2NestedColumns(column);
                }
            }
        } else {
            for (TreeTableColumn<T, String> column : mapOfExtraColumns.keySet()) {
                column.getColumns().removeAll(mapOfExtraColumns.get(column));
                getTreeTableView().setHeaderHeight(column, 48);
                getTreeTableView().setColGraphicWidthBindingFor2NestedColumns(column);
            }
        }

        highlightManager.setStyleToTreeTableColumnGroups(getTreeTableView(), true);
    }

    protected void handleDisplayDeltaColumns(boolean newVal) {
        List<TreeTableColumn<T, ?>> columns = getTreeTableView().getColumns();
        if (!newVal) {
            columns.removeAll(deltaColumnIndexMap.keySet());
            return;
        }

        for (TreeTableColumn<T, Object> column : deltaColumnIndexMap.keySet()) {
            if (deltaColumnIndexMap.get(column) > columns.size()) {
                columns.add(column);
                continue;
            }

            columns.add(deltaColumnIndexMap.get(column), column);
        }
    }

    protected void setMapOfOpenedSummaryController(Map<BorderPane, AbstractSummarisedTabController> controllerList) {
        this.mapOfSummaryControllers = controllerList;
    }

    protected <S> void initColumnAlignment(TreeTableColumn<T, S> column) {
        ColumnAlignment columnAlignment = ColumnAlignment.findByColumnName(column.getId());
        column.setStyle(columnAlignment.getAlignment());
    }

    protected Callback<TreeTableColumn<T, String>, TreeTableCell<T, String>> createCellFactory(Class<?> dataType,
            VehicleConfigDTO vehicleConfig, ApCompareGroup ap) {
        return param -> {
            ReadOnlyTreeTableCell<T, String> cell = new ReadOnlyTreeTableCell<>(dataType);
            CommonCellUtil.formatCell(cell);
            if (vehicleConfig == null) {
                return cell;
            }

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, createSummaryViewEventHandler(cell, vehicleConfig, ap));

            return cell;
        };
    }

    protected String getItemNameForSummaryView(TreeItem<T> treeItem) {
        return treeItem.getValue().getSet();
    }

    @Subscribe
    private void disableClearFilterButton(TreeFilteringUpdateEvent event) {
        disablePropertyClearFilters().set(!getTreeTableView().isFiltered());
    }

    private void saveOpenedControllers(AbstractSummarisedTabController controller) {
        mapOfSummaryControllers.put(controller.getControl(), controller);
    }

    private AbstractSummarisedTabController getOpenedController() {
        if (mapOfSummaryControllers.isEmpty()) {
            return null;
        }

        return mapOfSummaryControllers.values().iterator().next();
    }

    private void addToMapOfApColumns(ApCompareGroup ap, TreeTableColumn<T, Object> parent,
            TreeTableColumn<T, String> child) {
        if (!mapOfApColumns.containsKey(ap)) {
            mapOfApColumns.put(ap, new HashMap<>());
        }

        mapOfApColumns.get(ap).put(parent, child);
    }

    private Double calculateDelta(T row, Long secondLast, Long last, ApCompareGroup ap) {
        Map<ApCompareGroup, Double> secondLastWeights = new HashMap<>();
        Map<ApCompareGroup, Double> lastWeights = new HashMap<>();

        if (row instanceof FGSetCompareRow || row instanceof CostGroupCompareRow
                || row instanceof PartGroupCompareRow) {
            secondLastWeights = row.getWeights(secondLast);
            lastWeights = row.getWeights(last);
        }

        Double lastWeight = getWeight(lastWeights, ap);
        Double secondWeight = getWeight(secondLastWeights, ap);

        return calculateDelta(lastWeight, secondWeight);
    }

    private Double calculateDelta(Double first, Double last) {
        if (first == null && last == null) {
            return null;
        }

        if (first == null || last == null) {
            return 0d;
        }

        return first - last;
    }

    private String getNumberOfPartsForCell(Collection<EfsElementDTO> efsElements) {
        int count = 0;
        for (EfsElementDTO efsElement : efsElements) {
            String partNumber = efsElement.getPartNumber();
            if (partNumber.equals(SpecialPartNumberType.GAP.getLabel()) || partNumber.equals(
                    SpecialPartNumberType.NO_MARA.getLabel())) {
                boolean isPiece = efsElement.getQuantityUnit().equals(QuantityUnit.PIECE.getShortName());
                count += isPiece ? efsElement.getQuantity() : 1;
            }
        }

        return count == 0 ? null : String.valueOf(count);
    }

    //todo: into one loop
    private Map<ApCompareGroup, String> calculateNumberOfParts(Collection<EfsElementDTO> efsElements) {
        ApCompareGroup[] apCompareGroups = { ApCompareGroup.PLATFORM, ApCompareGroup.SYSTEM, ApCompareGroup.HUT };
        Map<ApCompareGroup, String> result = new HashMap<>(apCompareGroups.length);
        for (ApCompareGroup apGroup : apCompareGroups) {
            Collection<EfsElementDTO> filteredElements = efsElements.stream()
                    .filter(efsElement -> apGroup.containsAp(efsElement.getAp())).toList();
            String numberOfPartsForCell = getNumberOfPartsForCell(filteredElements);
            result.put(apGroup, numberOfPartsForCell);
        }

        String cellNumbers = getNumberOfPartsForCell(efsElements);
        result.put(ApCompareGroup.SUM, cellNumbers);

        return result;
    }

    private Collection<Pair<VehicleConfigDTO, VehicleConfigDTO>> getConfigPairsForCompare(
            AbstractCompareResult result) {
        Collection<Pair<VehicleConfigDTO, VehicleConfigDTO>> configPairs = new ArrayList<>();
        VehicleConfigDTO reference = result.getReference();
        if (reference == null) {
            for (int index = 0; index < result.getVehicleConfigs().size() - 1; index++) {
                VehicleConfigDTO secondVehicleConfig = result.getVehicleConfigs().get(index + 1);
                VehicleConfigDTO firstVehicleConfig = result.getVehicleConfigs().get(index);
                var configPair = new Pair<>(secondVehicleConfig, firstVehicleConfig);

                configPairs.add(configPair);
            }

            return configPairs;
        }

        for (VehicleConfigDTO config : result.getVehicleConfigs()) {
            if (config.equals(reference)) {
                continue;
            }

            var configPair = new Pair<>(reference, config);
            configPairs.add(configPair);
        }

        return configPairs;
    }

    private void addTreeItem4Navigation(TreeItem<T> newValue) {
        if (newValue == null || newValue.getValue() == null || isNavigateOverTreeItems) {
            return;
        }

        if (navigationIndex >= 0) {
            TreeItem<T> treeItemAtCurrentIndex = treeItems4Navigation.get(navigationIndex);
            if (newValue.getValue().equals(treeItemAtCurrentIndex.getValue())) {
                return;
            }
        }

        // if the current index is not in the last position of the list
        if (treeItems4Navigation.size() != (navigationIndex + 1)) {
            Collection<TreeItem<T>> tmpList = new ArrayList<>(navigationIndex);
            for (int i = 0; i <= navigationIndex; i++) {
                tmpList.add(treeItems4Navigation.get(i));
            }

            treeItems4Navigation.clear();
            treeItems4Navigation.addAll(tmpList);
        }

        treeItems4Navigation.add(newValue);

        ++navigationIndex;
    }

    private void setNavigationState() {
        disablePropertyNavigateBack().set(navigationIndex <= 0);
        disablePropertyNavigateForward().set(navigationIndex + 1 == treeItems4Navigation.size());
    }

    private void setSelectedTreeItem(TreeItem<T> treeItem) {
        TreeItem<T> parent = treeItem.getParent();
        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        CustomTreeTableView<T> treeView = getTreeTableView();
        treeView.getSelectionModel().clearSelection();
        treeView.scrollTo(treeView.getRow(treeItem));

        treeView.getSelectionModel().select(treeView.getRow(treeItem), treeView.getColumns().getFirst());
        treeView.requestFocus();
    }

    private Double getWeight(Map<ApCompareGroup, Double> weights, ApCompareGroup ap) {
        if (weights == null) {
            return null;
        }

        return weights.get(ap);
    }

    private String getAPColumnName(ApCompareGroup ap) {
        return I18N.getString(ap.getI18nKey());
    }

    private <S> TreeTableColumn<T, S> createColumn(ApCompareGroup ap) {
        TreeTableColumn<T, S> column = new TreeTableColumn<>(getAPColumnName(ap));
        column.setId(ap.getColumnId());
        initColumnAlignment(column);

        return column;
    }

    private TreeTableColumn<T, String> createAPColumn(VehicleConfigDTO vehicleConfig, ApCompareGroup ap) {
        TreeTableColumn<T, String> column = createColumn(ap);
        column.setCellValueFactory(param -> {
            T row = param.getValue().getValue();
            if (row == null) {
                return null;
            }

            if (row.isSum()) {
                return new SimpleStringProperty(
                        DEFAULT_DECIMAL_FORMATTER.format(mapOfWeightsInPartList.get(vehicleConfig.getId()).get(ap)));
            }

            Map<ApCompareGroup, Double> weights = row.getWeights(vehicleConfig.getId());
            if (weights != null) {
                Double weight = weights.get(ap);
                if (weight != null) {
                    return new SimpleStringProperty(DEFAULT_DECIMAL_FORMATTER.format(weight));
                }
            }

            return null;
        });

        column.setCellFactory(createCellFactory(Double.class, vehicleConfig, ap));
        return column;
    }

    private boolean isPrimaryMouseButtonDoubleClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2;
    }

    private EventHandler<MouseEvent> createSummaryViewEventHandler(ReadOnlyTreeTableCell<T, String> cell,
            VehicleConfigDTO vehicleConfig, ApCompareGroup ap) {
        return event -> {
            if (isPrimaryMouseButtonDoubleClick(event)) {
                event.consume();
                if (cell.getText().isEmpty()) {
                    return;
                }

                displaySummaryViewOfCell(cell, vehicleConfig, ap);
            }
        };
    }

    private void displaySummaryViewOfCell(ReadOnlyTreeTableCell<T, String> cell, VehicleConfigDTO vehicleConfig,
            ApCompareGroup ap) {
        TreeItem<T> treeItem = cell.getTableRow().getTreeItem();
        if (treeItem.getValue().isSum()) {
            return;
        }

        RemoveSummaryHighlightStylingEvent<T> highlightEvent = new RemoveSummaryHighlightStylingEvent<>(
                cell.getTreeTableView().getRoot().getValue());
        EventBus.getInstance().post(highlightEvent);

        List<EfsElementDTO> elements = treeItem.getValue().getElements(vehicleConfig.getId(), ap);
        showEfsElements(elements, ap, getItemNameForSummaryView(treeItem), vehicleConfig);
        cell.setStylingForSummaryItem();
    }

    private TreeTableColumn<T, String> createDeltaColumn(VehicleConfigDTO secondLast, VehicleConfigDTO last,
            ApCompareGroup ap) {
        TreeTableColumn<T, String> column = createColumn(ap);
        column.setCellValueFactory(param -> {
            T row = param.getValue().getValue();
            if (row == null) {
                return null;
            }

            if (row.isSum()) {
                Collection<Map<ApCompareGroup, Double>> partListToDeltaWeights = mapOfDeltaWeightsOfPartLists.get(
                        secondLast.getId());
                Double totalWeight = partListToDeltaWeights.stream().filter(e -> e.get(ap) != null).findFirst().get()
                        .get(ap);
                return new SimpleStringProperty(DEFAULT_DECIMAL_FORMATTER.format(totalWeight));
            }

            Double delta = calculateDelta(row, secondLast.getId(), last.getId(), ap);
            if (delta == null) {
                return new SimpleStringProperty(null);
            }

            return new SimpleStringProperty(DEFAULT_DECIMAL_FORMATTER.format(delta));
        });

        column.setCellFactory(createCellFactoryForDelta(secondLast, last, ap));
        return column;
    }

    private Callback<TreeTableColumn<T, String>, TreeTableCell<T, String>> createCellFactoryForDelta(
            VehicleConfigDTO secondLastVehicleConfig, VehicleConfigDTO lastVehicleConfig, ApCompareGroup ap) {
        return param -> {
            ReadOnlyTreeTableCell<T, String> cell = new ReadOnlyTreeTableCell<>(Double.class);
            CommonCellUtil.formatCell(cell);
            if (lastVehicleConfig == null || secondLastVehicleConfig == null) {
                return cell;
            }

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED,
                    createDeltaSummaryViewEventHandler(cell, secondLastVehicleConfig, lastVehicleConfig, ap));
            return cell;
        };
    }

    private EventHandler<MouseEvent> createDeltaSummaryViewEventHandler(ReadOnlyTreeTableCell<T, String> cell,
            VehicleConfigDTO secondLastVehicleConfig, VehicleConfigDTO lastVehicleConfig, ApCompareGroup ap) {
        return event -> {
            if (!isPrimaryMouseButtonDoubleClick(event)) {
                return;
            }

            event.consume();
            if (cell.getText().isEmpty()) {
                return;
            }

            displayDeltaSummaryViewOfCell(cell, secondLastVehicleConfig, lastVehicleConfig, ap);
        };
    }

    private void displayDeltaSummaryViewOfCell(ReadOnlyTreeTableCell<T, String> cell,
            VehicleConfigDTO secondLastVehicleConfig, VehicleConfigDTO lastVehicleConfig, ApCompareGroup ap) {
        TreeItem<T> treeItem = cell.getTableRow().getTreeItem();
        if (treeItem.getValue().isSum()) {
            return;
        }

        RemoveSummaryHighlightStylingEvent<T> highlightEvent = new RemoveSummaryHighlightStylingEvent<>(
                cell.getTreeTableView().getRoot().getValue());
        EventBus.getInstance().post(highlightEvent);

        Map<Long, List<EfsElementDTO>> elementsMap = new HashMap<>();
        elementsMap.put(secondLastVehicleConfig.getId(),
                treeItem.getValue().getElements(secondLastVehicleConfig.getId(), ap));
        elementsMap.put(lastVehicleConfig.getId(), treeItem.getValue().getElements(lastVehicleConfig.getId(), ap));
        showEfsElementsForDelta(elementsMap, secondLastVehicleConfig, lastVehicleConfig, ap,
                getItemNameForSummaryView(treeItem));

        cell.setStylingForSummaryItem();
    }

    private Collection<TreeTableColumn<T, String>> createExtraApColumns(VehicleConfigDTO vehicleConfig,
            ApCompareGroup ap) {
        TreeTableColumn<T, String> columnWeight = new TreeTableColumn<>(I18N.getString("tablecolumn.weight"));
        columnWeight.setStyle(ColumnAlignment.AP_WEIGHT.getAlignment());

        Long vehicleConfigId = vehicleConfig.getId();
        columnWeight.setCellValueFactory(param -> {
            T row = param.getValue().getValue();
            if (row == null) {
                return null;
            }

            if (row.isSum()) {
                Double weight = mapOfWeightsInPartList.get(vehicleConfigId).get(ap);
                return new SimpleStringProperty(
                        DEFAULT_DECIMAL_FORMATTER.format(weight) + StringConstant.SPACE + WEIGHT_UNIT);
            }

            Map<ApCompareGroup, Double> weights = row.getWeights(vehicleConfigId);
            if (weights == null) {
                return null;
            }

            Double weight = weights.get(ap);
            if (weight == null) {
                return null;
            }

            return new SimpleStringProperty(
                    DEFAULT_DECIMAL_FORMATTER.format(weight) + StringConstant.SPACE + WEIGHT_UNIT);
        });

        columnWeight.setCellFactory(createCellFactory(Double.class, vehicleConfig, ap));

        TreeTableColumn<T, String> columnNum = new TreeTableColumn<>(I18N.getString("tablecolumn.num"));
        columnNum.setStyle(ColumnAlignment.AP_NUM.getAlignment());
        columnNum.setCellValueFactory(param -> {
            T row = param.getValue().getValue();
            if (row == null) {
                return null;
            }

            if (row.isSum()) {
                return new SimpleStringProperty(mapOfNumberOfPartsInPartList.get(vehicleConfigId).get(ap));
            }

            return new SimpleStringProperty(getNumberOfPartsForCell(row.getElements(vehicleConfigId, ap)));
        });

        columnNum.setCellFactory(createCellFactory(Integer.class, vehicleConfig, ap));
        return List.of(columnNum, columnWeight);
    }

    private void showEfsElements(List<EfsElementDTO> efsElements, ApCompareGroup ap, String set,
            VehicleConfigDTO vehicleConfig) {
        try {
            AbstractSummarisedTabController controller = BaseController.load(getSummarisedTabControllerClass());
            if (controller.getSelectedEfsElement() == null) {
                String columnName = getAPColumnName(ap);
                String itemName = set != null ? set : UNKNOWN_MESSAGE;
                controller.initEfsElementList(efsElements, columnName, itemName, null, vehicleConfig.getName(), true);

                if (getOpenedController() != null) {
                    EventBus.getInstance().post(new ColumnSequenceChangeEvent(null,
                            getOpenedController().getTableView().getColumns().stream().map(TableColumnBase::getText)
                                    .collect(Collectors.toList()), getOpenedController().getClass()));

                    EventBus.getInstance().post(new VisibleColumnsCompareChangeEvent(
                            getOpenedController().getTableView().getVisibleLeafColumns().stream()
                                    .map(TableColumnBase::getText).collect(Collectors.toList()),
                            getOpenedController().getClass()));
                }

                saveOpenedControllers(controller);
            } else {
                controller.initEfsElementList(efsElements, controller.getSelectedEfsElement());
            }

            SplitPane compareSplitPane = getCompareSplitPane();
            if (compareSplitPane.getItems().size() > 1) {
                compareSplitPane.getItems().remove(1);
            }

            compareSplitPane.getItems().add(controller.getControl());
            compareSplitPane.setDividerPositions(0.65d);

            disablePropertyCloseSummary().set(false);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private void showEfsElementsForDelta(Map<Long, List<EfsElementDTO>> efsElements,
            VehicleConfigDTO secondLastVehicleConfig, VehicleConfigDTO lastVehicleConfig, ApCompareGroup ap,
            String set) {
        AbstractSummarisedCompareTabController controller = BaseController.load(
                getSummarisedCompareTabControllerClass());

        String columnName = getAPColumnName(ap);
        String itemName = set != null ? set : UNKNOWN_MESSAGE;
        controller.initEfsElementList(efsElements, secondLastVehicleConfig, lastVehicleConfig, columnName, itemName);

        SplitPane compareSplitPane = getCompareSplitPane();
        if (compareSplitPane.getItems().size() > 1) {
            compareSplitPane.getItems().remove(1);
        }

        compareSplitPane.getItems().add(controller.getControl());
        compareSplitPane.setDividerPositions(0.65d);

        disablePropertyCloseSummary().set(false);
    }
}