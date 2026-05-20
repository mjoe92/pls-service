package de.vw.paso.client.stueckliste.efs.tree;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.RemoveSummaryHighlightStylingEvent;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowCompareTabEvent;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.PartGroupTabController;
import de.vw.paso.client.stueckliste.efs.export.partgroup.PartGroupExcelExporter;
import de.vw.paso.client.stueckliste.efs.tree.cell.AggregatedEfsCellFactory;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeItemPropertyNames;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeObject;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabPaneController;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialog;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialogResult;
import de.vw.paso.client.stueckliste.efs.views.summarised.AbstractSummarisedTabController;
import de.vw.paso.client.stueckliste.efs.views.summarised.PartGroupSummarisedTabController;
import de.vw.paso.client.stueckliste.event.PartGroupTreeRefreshEvent;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.TreeItemUtil;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.SpecPartGroupCategory;
import de.vw.paso.utility.StringConstant;

@FXController(name = "part-group-tree")
public class SingleVehiclePartGroupController extends SingleVehicleBaseController<PartGroupTreeObject> {

    private static final String FG_SET_ROW_SELECTION = "part-group-highlight-row-selection";
    private static final String FG_SET_COLUMN_SELECTION = "part-group-highlight-col-selection";

    @FXML
    private CustomTreeTableView<PartGroupTreeObject> partGroupTreeTableView;
    @FXML
    private TreeTableColumn<PartGroupTreeObject, Integer> colPartGroup;
    @FXML
    private TreeTableColumn<PartGroupTreeObject, String> colDescription;
    @FXML
    private TreeTableColumn<PartGroupTreeObject, Double> colPlatform;
    @FXML
    private TreeTableColumn<PartGroupTreeObject, Double> colSystem;
    @FXML
    private TreeTableColumn<PartGroupTreeObject, Double> colHut;
    @FXML
    private TreeTableColumn<PartGroupTreeObject, Double> colWeightAll;

    private final TreeTableColumn<PartGroupTreeObject, Double> colPlatWeight;
    private final TreeTableColumn<PartGroupTreeObject, Integer> colPlatNum;
    private final TreeTableColumn<PartGroupTreeObject, Double> colSystemWeight;
    private final TreeTableColumn<PartGroupTreeObject, Integer> colSystemNum;
    private final TreeTableColumn<PartGroupTreeObject, Double> colHutWeight;
    private final TreeTableColumn<PartGroupTreeObject, Integer> colHutNum;
    private final TreeTableColumn<PartGroupTreeObject, Double> colAllWeight;
    private final TreeTableColumn<PartGroupTreeObject, Integer> colAllNum;

    private final PartGroupTreeModel partGroupTreeModel;
    private final SelectionHighlightManager<PartGroupTreeObject> highlightManager;
    private final ObservableList<PartGroupVMO> partGroups;
    private final PartGroupTreeItem root;
    private final Map<Integer, PartGroupTreeItem> mgrTreeItemMap;
    private final Map<Integer, PartGroupTreeItem> vnrMgrItem;
    private final Collection<PartGroupTreeItem> unknownMgrTreeItems;
    private final Collection<PartGroupTreeItem> unknownUgrTreeItems;

    private PartGroupTabController parentController;
    private String lastSelectedColumnProperty;
    private SplitPane splitPaneEfsView;

    private BooleanProperty disablePropertyCompare;
    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty disablePropertyCloseSummary;
    private BooleanProperty toggleDisplayNumberOfPartsProperty;

    public SingleVehiclePartGroupController() {
        colPlatWeight = new TreeTableColumn<>();
        colPlatNum = new TreeTableColumn<>();
        colSystemWeight = new TreeTableColumn<>();
        colSystemNum = new TreeTableColumn<>();
        colHutWeight = new TreeTableColumn<>();
        colHutNum = new TreeTableColumn<>();
        colAllWeight = new TreeTableColumn<>();
        colAllNum = new TreeTableColumn<>();
        partGroupTreeModel = new PartGroupTreeModel();
        highlightManager = new SelectionHighlightManager<>();
        partGroups = FXCollections.observableArrayList();
        root = new PartGroupTreeItem(new PartGroupTreeObject(new PartGroupVMO()));
        mgrTreeItemMap = new HashMap<>();
        vnrMgrItem = new HashMap<>();
        unknownMgrTreeItems = new ArrayList<>();
        unknownUgrTreeItems = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        highlightManager.initTable(partGroupTreeTableView, FG_SET_ROW_SELECTION, FG_SET_COLUMN_SELECTION);

        partGroupTreeModel.setRoot(root);

        initSubColumns();

        partGroupTreeTableView.getSortOrder()
            .addListener((ListChangeListener<TreeTableColumn<PartGroupTreeObject, ?>>) change -> {
                if (!isResettingSort) {
                    disablePropertyResetSorting().set(false);
                }
            });

        for (TreeTableColumn<PartGroupTreeObject, ?> column : partGroupTreeTableView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
                if (!isResettingSort) {
                    disablePropertyResetSorting().set(false);
                }
            });
        }
    }

    @Override
    protected void stop() {
        super.stop();

        partGroupTreeModel.removeAllElements();

        EfsElementResolver.removeListener(this);

        highlightManager.removeFromTable();
    }

    @Override
    public void selectElementById(Long id) {
    }

    @Override
    protected void initTreeTable() {
        partGroupTreeTableView.showRootProperty().set(false);
        partGroupTreeTableView.setRoot(partGroupTreeModel.getRoot());
        partGroupTreeTableView.setEditable(false);
        partGroupTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        partGroupTreeTableView.getSelectionModel().setCellSelectionEnabled(true);
        partGroupTreeTableView.makeHeaderWrappable();
        partGroupTreeTableView.makeFilterable();
        partGroupTreeTableView.setHeaderHeight(48);
        partGroupTreeTableView.getSortOrder().addListener(
            (ListChangeListener<? super TreeTableColumn<PartGroupTreeObject, ?>>) change -> disablePropertyResetSorting().set(
                false));
    }

    @Override
    protected void initTreeTableColumns() {
        initColumn(colPartGroup, cellData -> {
            PartGroupTreeItem item = (PartGroupTreeItem) cellData.getValue();

            if (item.getUserObject().getAggregationObject().isCategory()) {
                return item.propertyPartGroupCategory();
            } else if (item.getUserObject().getAggregationObject().isMgr()) {
                return item.propertyPartGroup();
            }

            return item.propertyPartGroupUgr();
        }, AggregatedEfsCellFactory.forReadOnlyPartGroupIntegerColumn(PartGroupTreeItemPropertyNames.PART_GROUP), e -> {
            if (e.getAggregationObject().isCategory()) {
                return e.getAggregationObject().getCategory();
            } else if (e.getAggregationObject().isMgr()) {
                return e.getAggregationObject().getMgr();
            }

            return e.getAggregationObject().getUgr();
        }, PartGroupTreeItemPropertyNames.PART_GROUP);

        initColumn(colDescription, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyDescription(),
            AggregatedEfsCellFactory.forReadOnlyStringColumn(PartGroupTreeItemPropertyNames.DESCRIPTION),
            e -> e.getAggregationObject().getDescription(), PartGroupTreeItemPropertyNames.DESCRIPTION);
        initColumn(colPlatform, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightPlatform(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.PLATFORM,
                this::handleClickEvent), PartGroupTreeObject::getPlatform, PartGroupTreeItemPropertyNames.PLATFORM);
        initColumn(colSystem, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightSystem(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.SYSTEM,
                this::handleClickEvent), PartGroupTreeObject::getSystem, PartGroupTreeItemPropertyNames.SYSTEM);
        initColumn(colHut, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightHut(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.HUT,
                this::handleClickEvent), PartGroupTreeObject::getHut, PartGroupTreeItemPropertyNames.HUT);
        initColumn(colWeightAll, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightAll(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.WEIGHT_ALL,
                this::handleClickEvent), PartGroupTreeObject::getWeightAll, PartGroupTreeItemPropertyNames.WEIGHT_ALL);
    }

    private void initSubColumns() {
        String textNumber = I18N.getString("treetablecolumn.num");
        String textWeight = I18N.getString("treetablecolumn.weight");

        colPlatWeight.setText(textWeight);
        initColumn(colPlatWeight, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightPlatform(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.PLATFORM,
                this::handleClickEvent), PartGroupTreeObject::getPlatform, PartGroupTreeItemPropertyNames.PLATFORM);

        colPlatNum.setText(textNumber);
        colPlatNum.setCellValueFactory(cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyNumPlatform());
        colPlatNum.setCellFactory(
            AggregatedEfsCellFactory.forReadOnlyIntegerColumn(PartGroupTreeItemPropertyNames.PLATFORM,
                this::handleClickEvent));

        colSystemWeight.setText(textWeight);
        initColumn(colSystemWeight, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightSystem(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.SYSTEM,
                this::handleClickEvent), PartGroupTreeObject::getSystem, PartGroupTreeItemPropertyNames.SYSTEM);

        colSystemNum.setText(textNumber);
        colSystemNum.setCellValueFactory(cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyNumSystem());
        colSystemNum.setCellFactory(
            AggregatedEfsCellFactory.forReadOnlyIntegerColumn(PartGroupTreeItemPropertyNames.SYSTEM,
                this::handleClickEvent));

        colHutWeight.setText(textWeight);
        initColumn(colHutWeight, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightHut(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.HUT,
                this::handleClickEvent), PartGroupTreeObject::getHut, PartGroupTreeItemPropertyNames.HUT);

        colHutNum.setText(textNumber);
        colHutNum.setCellValueFactory(cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyNumHut());
        colHutNum.setCellFactory(AggregatedEfsCellFactory.forReadOnlyIntegerColumn(PartGroupTreeItemPropertyNames.HUT,
            this::handleClickEvent));

        colAllWeight.setText(textWeight);
        initColumn(colAllWeight, cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyWeightAll(),
            AggregatedEfsCellFactory.forReadOnlyDoubleColumn(PartGroupTreeItemPropertyNames.WEIGHT_ALL,
                this::handleClickEvent), PartGroupTreeObject::getWeightAll, PartGroupTreeItemPropertyNames.WEIGHT_ALL);

        colAllNum.setText(textNumber);
        colAllNum.setCellValueFactory(cellData -> ((PartGroupTreeItem) cellData.getValue()).propertyNumAll());
        colAllNum.setCellFactory(
            AggregatedEfsCellFactory.forReadOnlyIntegerColumn(PartGroupTreeItemPropertyNames.WEIGHT_ALL,
                this::handleClickEvent));
    }

    @Override
    protected CustomTreeTableView<PartGroupTreeObject> getTreeTableView() {
        return partGroupTreeTableView;
    }

    @Override
    protected AbstractTreeModel<PartGroupTreeItem, PartGroupTreeObject> getTreeModel() {
        return partGroupTreeModel;
    }

    @Override
    public void onEfsElementUpdate(Collection<EfsElementDTO> efsElements) {
        if (efsElements.isEmpty()) {
            return;
        }

        EfsElementDTO efsElement = efsElements.iterator().next();
        if (!parentController.getVehiclePartListId().equals(efsElement.getVehiclePartListId())) {
            return;
        }

        for (EfsElementDTO efsElementDTO : efsElements) {
            refreshPartGroup(efsElementDTO);
        }

        partGroupTreeTableView.requestFocus();

        if (lastSelectedColumnProperty != null) {
            handleClickEvent(true, lastSelectedColumnProperty);
        }
    }

    public void setEfsElements(Collection<EfsElementDTO> efsElements) {
        Map<String, List<EfsElementDTO>> efsElementCollectorUgr = new HashMap<>();
        Map<String, List<EfsElementDTO>> efsElementCollectorMgr = new HashMap<>();
        Map<Integer, List<EfsElementDTO>> efsElementCollectorVnr = new HashMap<>();

        collectEfsElementsByPartNumber(efsElements, efsElementCollectorMgr, efsElementCollectorUgr,
            efsElementCollectorVnr);

        PartGroupTreeItem mgr;
        for (PartGroupVMO partGroup : partGroups) {
            if (partGroup.isMgr()) {
                mgr = partGroupTreeModel.addElement(new PartGroupTreeObject(partGroup), true);
                vnrMgrItem.put(partGroup.getCategory(), mgr);

                if (partGroup.getCategory().equals(SpecPartGroupCategory.NORM_PART_GROUP.getCategory())) {
                    Collection<EfsElementDTO> normEfsElements = efsElementCollectorVnr.get(partGroup.getCategory())
                        .stream()
                        .filter(efsElement -> !efsElement.getEfsElementMara().getPartNumberMittelgruppe().equals("052"))
                        .toList();

                    efsElementCollectorVnr.get(partGroup.getCategory()).removeAll(normEfsElements);

                    if (partGroup.getMgr() == 0) {
                        mgr.getUserObject().getEfsElements().addAll(normEfsElements);
                    }

                    partGroupTreeModel.calculateWeights(mgr);
                } else if (partGroup.getCategory().equals(SpecPartGroupCategory.WHT_PART_GROUP.getCategory())
                    || partGroup.getCategory().equals(SpecPartGroupCategory.A_PART_GROUP.getCategory())) {
                    Collection<EfsElementDTO> removedElements = efsElementCollectorVnr.remove(partGroup.getCategory());
                    if (removedElements != null) {
                        mgr.getUserObject().getEfsElements().addAll(removedElements);
                    }

                    partGroupTreeModel.calculateWeights(mgr);
                } else if (partGroup.getMgrEnd() == null) {
                    efsElementCollectorMgr.remove(PartGroupUtil.groupToString(partGroup.getMgr()));

                    mgrTreeItemMap.put(partGroup.getMgr(), mgr);
                } else {
                    for (int index = partGroup.getMgr(); index <= partGroup.getMgrEnd(); index++) {
                        efsElementCollectorMgr.remove(PartGroupUtil.groupToString(index));

                        mgrTreeItemMap.put(index, mgr);
                    }
                }
            }

            if (partGroup.isUgr()) {
                if (partGroup.getCategory().equals(SpecPartGroupCategory.NORM_PART_GROUP.getCategory())
                    && partGroup.getMgr() == 52) {
                    List<EfsElementDTO> normEfsElements = efsElementCollectorVnr.get(partGroup.getCategory()).stream()
                        .filter(efsElement -> efsElement.getEfsElementMara().getPartNumberMittelgruppe()
                            .equals(PartGroupUtil.groupToString(partGroup.getMgr()))).filter(
                            efsElement -> efsElement.getEfsElementMara().getPartNumberEndNumber()
                                .equals(PartGroupUtil.groupToString(partGroup.getUgr()))).toList();

                    efsElementCollectorVnr.get(partGroup.getCategory()).removeAll(normEfsElements);

                    if (!normEfsElements.isEmpty()) {
                        PartGroupTreeItem ugrItem = partGroupTreeModel.createTreeItem(
                            new PartGroupTreeObject(partGroup, new ArrayList<>(normEfsElements)));

                        vnrMgrItem.get(SpecPartGroupCategory.NORM_PART_GROUP.getCategory()).getSourceChildren()
                            .add(ugrItem);

                        partGroupTreeModel.calculateWeights(ugrItem);
                    }
                } else if (efsElementCollectorUgr.containsKey(PartGroupUtil.groupToString(partGroup.getUgr()))) {
                    List<EfsElementDTO> efsElementsUgr = collectEfsElementsForUgrPartGroup(partGroup,
                        efsElementCollectorUgr);

                    if (!efsElementsUgr.isEmpty()) {
                        PartGroupTreeItem ugrItem = partGroupTreeModel.createTreeItem(
                            new PartGroupTreeObject(partGroup, efsElementsUgr));
                        mgr = mgrTreeItemMap.get(partGroup.getMgr());

                        efsElementCollectorUgr.get(PartGroupUtil.groupToString(partGroup.getUgr()))
                            .removeAll(efsElementsUgr);

                        mgr.getSourceChildren().add(ugrItem);

                        partGroupTreeModel.calculateWeights(ugrItem);
                    }
                }
            }
        }

        for (Integer key : new ArrayList<>(mgrTreeItemMap.keySet())) {
            PartGroupTreeItem mgrTreeItem = mgrTreeItemMap.get(key);

            if (mgrTreeItem.getChildren().isEmpty() && mgrTreeItem.getParent() != null) {
                partGroupTreeModel.removeElement(mgrTreeItem);
                mgrTreeItemMap.remove(mgrTreeItem.getUserObject().getAggregationObject().getMgr());
            }
        }

        handleUnknownNormPartGroups(efsElementCollectorVnr);
        handleUnknownPartGroups(efsElementCollectorMgr, efsElementCollectorUgr);

        for (PartGroupTreeItem item : this.unknownUgrTreeItems) {
            partGroupTreeModel.calculateWeights(item);
            partGroupTreeModel.cacheTreeItem(item);
        }

        PartGroupTreeItem summaryRowTreeItem = partGroupTreeModel.createTreeItem(
            new PartGroupTreeObject(new PartGroupVMO()));
        summaryRowTreeItem.getUserObject().getAggregationObject().setDescription(SUMMARY_MESSAGE);
        summaryRowTreeItem.propertySummaryRow().set(true);

        root.getSourceChildren().add(summaryRowTreeItem);

        partGroupTreeModel.calculateNumberOfParts();
        partGroupTreeModel.updateSummaryValues();

        partGroupTreeTableView.setRoot(root);
        partGroupTreeTableView.getRoot().setExpanded(true);
    }

    public void setPartGroups(List<PartGroupVMO> partGroups) {
        this.partGroups.setAll(partGroups);

        PartGroupUtil.sortByParent(this.partGroups);
    }

    public void setParentController(PartGroupTabController parentController) {
        this.parentController = parentController;
    }

    public void scrollToFirstColumn() {
        partGroupTreeTableView.scrollToColumnIndex(0);
    }

    public void scrollToLastColumn() {
        int size = partGroupTreeTableView.getColumns().size();
        partGroupTreeTableView.scrollToColumnIndex(size - 1);
    }

    public void handleActionShowCompareDialog() {
        VehicleConfigDTO vehicleConfig = parentController.getVehiclePartList().getVehicleConfig();
        List<VehicleConfigDTO> selectedConfigsList = new ArrayList<>();
        selectedConfigsList.add(vehicleConfig);

        Collection<VehicleConfigDTO> vehicleConfigs = VehicleConfigRestClientHolder.getInstance()
            .loadNonDeletedVehicleConfigs().vehicleConfigDTOList();

        List<VehicleConfigDTO> finalVehicleConfigs = vehicleConfigs.stream().filter(e -> e.getVehiclePartList() != null)
            .collect(Collectors.toList());

        ComparePartListSelectionDialog dialog = new ComparePartListSelectionDialog(selectedConfigsList, null,
            finalVehicleConfigs, parentController.getMainTabPaneController().getOpenPartListIDs());
        Optional<ComparePartListSelectionDialogResult> result = dialog.showAndWait();
        dialog.unregisterEventBus();
        result.ifPresent(comparePartListSelectionDialogResult -> EventBus.getInstance().post(
            new ShowCompareTabEvent(comparePartListSelectionDialogResult.getSelectedVehicleConfigs(),
                comparePartListSelectionDialogResult.getReferenceVehicleConfig())));
    }

    public void handleActionResetSorting() {
        isResettingSort = true;

        colPartGroup.setSortType(TreeTableColumn.SortType.ASCENDING);

        partGroupTreeTableView.getSortOrder().setAll(colPartGroup);
        partGroupTreeTableView.sort();

        TreeItem<PartGroupTreeObject> summary = partGroupTreeTableView.getRoot().getChildren().removeFirst();
        TreeItem<PartGroupTreeObject> unknown = partGroupTreeTableView.getRoot().getChildren().removeFirst();

        boolean isSummary = summary.getValue().getAggregationObject().getDescription().equals(SUMMARY_MESSAGE);
        if (isSummary) {
            partGroupTreeTableView.getRoot().getChildren().addLast(unknown);
            partGroupTreeTableView.getRoot().getChildren().addLast(summary);
        } else {
            partGroupTreeTableView.getRoot().getChildren().addLast(summary);
            partGroupTreeTableView.getRoot().getChildren().addLast(unknown);
        }

        partGroupTreeTableView.getSortOrder().clear();

        disablePropertyResetSorting().set(true);
        isResettingSort = false;
    }

    @Override
    protected void initSorting() {
        handleActionResetSorting();
    }

    public void handleActionExcelExport() {
        VehicleConfigDTO vehicleConfig = parentController.getVehiclePartList().getVehicleConfig();

        String fileName =
            "PartGroupExport_" + vehicleConfig.getVehicleProject().getProjectName() + StringConstant.UNDERLINE
                + vehicleConfig.getName();

        fileName = fileName.replaceAll("/+", StringConstant.UNDERLINE);

        try {
            new PartGroupExcelExporter(fileName, List.of(vehicleConfig), partGroupTreeModel.getRoot()).export(
                I18N.getString("excel.default.sheet.name"));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void handleActionCloseSummary() {
        lastSelectedColumnProperty = null;
        splitPaneEfsView.getItems().remove(1);
        EventBus.getInstance().post(new RemoveSummaryHighlightStylingEvent<>(getTreeTableView().getRoot().getValue()));
        disablePropertyCloseSummary.set(true);
    }

    public BooleanProperty toggleDisplayNumberOfPartsProperty() {
        if (toggleDisplayNumberOfPartsProperty == null) {
            toggleDisplayNumberOfPartsProperty = new SimpleBooleanProperty(false);

            toggleDisplayNumberOfPartsProperty.addListener((obs, oldVal, newVal) -> handleDisplayNumberOfParts(newVal));
        }

        return toggleDisplayNumberOfPartsProperty;

    }

    public BooleanProperty disablePropertyCloseSummary() {
        if (disablePropertyCloseSummary == null) {
            disablePropertyCloseSummary = new SimpleBooleanProperty(true);
        }

        return disablePropertyCloseSummary;
    }

    public BooleanProperty disablePropertyCompare() {
        if (disablePropertyCompare == null) {
            disablePropertyCompare = new SimpleBooleanProperty(false);
        }

        return disablePropertyCompare;
    }

    public BooleanProperty disablePropertyExcelExport() {
        if (disablePropertyExcelExport == null) {
            disablePropertyExcelExport = new SimpleBooleanProperty(false);
        }

        return disablePropertyExcelExport;
    }

    private void collectEfsElementsByPartNumber(Collection<EfsElementDTO> efsElements,
        Map<String, List<EfsElementDTO>> efsElementCollectorMgr,
        Map<String, List<EfsElementDTO>> efsElementCollectorUgr,
        Map<Integer, List<EfsElementDTO>> efsElementCollectorVnr) {

        for (EfsElementDTO efsElement : efsElements) {
            boolean mm = true;
            String partNumberVnr = efsElement.getEfsElementMara().getPartNumberVornummer();
            String partNumberMgr = efsElement.getEfsElementMara().getPartNumberMittelgruppe();
            String partNumberUgr = efsElement.getEfsElementMara().getPartNumberEndNumber();

            if (partNumberVnr != null) {
                if (partNumberVnr.startsWith(SpecPartGroupCategory.NORM_PART_GROUP.getCategoryStr())) {
                    mm = false;

                    if (!efsElementCollectorVnr.containsKey(SpecPartGroupCategory.NORM_PART_GROUP.getCategory())) {
                        efsElementCollectorVnr.put(SpecPartGroupCategory.NORM_PART_GROUP.getCategory(),
                            new ArrayList<>());
                    }

                    efsElementCollectorVnr.get(SpecPartGroupCategory.NORM_PART_GROUP.getCategory()).add(efsElement);
                } else if (partNumberVnr.startsWith("A")) {
                    mm = false;

                    if (!efsElementCollectorVnr.containsKey(SpecPartGroupCategory.A_PART_GROUP.getCategory())) {
                        efsElementCollectorVnr.put(SpecPartGroupCategory.A_PART_GROUP.getCategory(), new ArrayList<>());
                    }

                    efsElementCollectorVnr.get(SpecPartGroupCategory.A_PART_GROUP.getCategory()).add(efsElement);
                } else if (partNumberVnr.equals(SpecPartGroupCategory.WHT_PART_GROUP.getCategoryStr())) {
                    mm = false;

                    if (!efsElementCollectorVnr.containsKey(SpecPartGroupCategory.WHT_PART_GROUP.getCategory())) {
                        efsElementCollectorVnr.put(SpecPartGroupCategory.WHT_PART_GROUP.getCategory(),
                            new ArrayList<>());
                    }

                    efsElementCollectorVnr.get(SpecPartGroupCategory.WHT_PART_GROUP.getCategory()).add(efsElement);
                }
            }

            if (mm) {
                if (partNumberUgr != null) {
                    if (!efsElementCollectorUgr.containsKey(partNumberUgr)) {
                        efsElementCollectorUgr.put(partNumberUgr, new ArrayList<>());
                    }

                    efsElementCollectorUgr.get(partNumberUgr).add(efsElement);
                }

                if (partNumberMgr != null) {
                    if (!efsElementCollectorMgr.containsKey(partNumberMgr)) {
                        efsElementCollectorMgr.put(partNumberMgr, new ArrayList<>());
                    }

                    efsElementCollectorMgr.get(partNumberMgr).add(efsElement);
                }
            }
        }
    }

    private List<EfsElementDTO> collectEfsElementsForUgrPartGroup(PartGroupVMO partGroup,
        Map<String, List<EfsElementDTO>> efsElementCollectorUgr) {
        List<EfsElementDTO> efsElementsUgr = new ArrayList<>();

        if (partGroup.getMgrEnd() == null) {
            efsElementsUgr = efsElementCollectorUgr.get(PartGroupUtil.groupToString(partGroup.getUgr())).stream()
                .filter(e -> Integer.parseInt(e.getEfsElementMara().getPartNumberMittelgruppe()) == partGroup.getMgr())
                .collect(Collectors.toList());

            return efsElementsUgr;
        }

        for (int index = partGroup.getMgr(); index < partGroup.getMgrEnd(); index++) {
            Collection<EfsElementDTO> list = new ArrayList<>();
            for (EfsElementDTO e : efsElementCollectorUgr.get(PartGroupUtil.groupToString(partGroup.getUgr()))) {
                if (Integer.parseInt(e.getEfsElementMara().getPartNumberMittelgruppe()) == index) {
                    list.add(e);
                }
            }

            efsElementsUgr.addAll(list);
        }

        return efsElementsUgr;
    }

    private void handleUnknownPartGroups(Map<String, List<EfsElementDTO>> efsElementCollectorMgr,
        Map<String, List<EfsElementDTO>> efsElementCollectorUgr) {
        Map<String, PartGroupTreeItem> unknownMgrTreeItems = new HashMap<>();

        PartGroupTreeItem unknownRootTreeItem = partGroupTreeModel.createTreeItem(
            new PartGroupTreeObject(new PartGroupVMO()));
        unknownRootTreeItem.getUserObject().getAggregationObject().setDescription("Unknown");
        unknownRootTreeItem.propertyUnknownRoot().set(true);

        root.getSourceChildren().add(unknownRootTreeItem);

        Collection<Integer> partNumbersMgr = efsElementCollectorMgr.keySet().stream().mapToInt(Integer::parseInt)
            .sorted().boxed().toList();

        for (Integer partNumMgr : partNumbersMgr) {
            PartGroupVMO partGroupVMO = new PartGroupVMO();
            partGroupVMO.setMgr(partNumMgr);

            PartGroupTreeItem treeItem = partGroupTreeModel.createTreeItem(new PartGroupTreeObject(partGroupVMO));

            unknownMgrTreeItems.put(PartGroupUtil.groupToString(partNumMgr), treeItem);
            this.unknownMgrTreeItems.add(treeItem);
        }

        for (String partNumUgr : efsElementCollectorUgr.keySet()) {
            if (PartGroupUtil.isUgrNumeric(partNumUgr)) {
                continue;
            }

            List<EfsElementDTO> efsElementsUgr = efsElementCollectorUgr.get(partNumUgr);
            if (!efsElementsUgr.isEmpty()) {
                mapEfsElementsWithUnknownUgr(efsElementsUgr, unknownMgrTreeItems, partNumUgr, false);
            }
        }

        Collection<PartGroupTreeItem> sortedItems = sortAndCacheUnknownMgrTreeItems();
        unknownRootTreeItem.getSourceChildren().addAll(sortedItems);

        removeEmptyMgrPartGroups(unknownMgrTreeItems, unknownRootTreeItem);
    }

    private void handleUnknownNormPartGroups(Map<Integer, List<EfsElementDTO>> efsElementCollectorVnr) {
        Map<String, PartGroupTreeItem> unknownMgrTreeItems = new HashMap<>();
        Collection<EfsElementDTO> efsElements = efsElementCollectorVnr.get(
            SpecPartGroupCategory.NORM_PART_GROUP.getCategory());
        if (efsElements.isEmpty()) {
            return;
        }

        Map<String, List<EfsElementDTO>> mapOfElements = new HashMap<>();
        for (EfsElementDTO efsElement : efsElements) {
            String partNumUgr = efsElement.getEfsElementMara().getPartNumberEndNumber();
            if (!PartGroupUtil.isUgrNumeric(partNumUgr)) {
                continue;
            }

            if (!mapOfElements.containsKey(partNumUgr)) {
                mapOfElements.put(partNumUgr, new ArrayList<>());
            }

            mapOfElements.get(partNumUgr).add(efsElement);
        }

        for (String ugr : mapOfElements.keySet()) {
            mapEfsElementsWithUnknownUgr(mapOfElements.get(ugr), unknownMgrTreeItems, ugr, true);
        }
    }

    private void removeEmptyMgrPartGroups(Map<String, PartGroupTreeItem> unknownMgrTreeItems,
        PartGroupTreeItem unknownRootTreeItem) {
        unknownMgrTreeItems.keySet().forEach(key -> {
            if (unknownMgrTreeItems.get(key).getChildren().isEmpty()) {
                unknownRootTreeItem.getChildren().remove(unknownMgrTreeItems.get(key));
            }
        });
    }

    private void mapEfsElementsWithUnknownUgr(List<EfsElementDTO> efsElementsUgr,
        Map<String, PartGroupTreeItem> unknownMgrTreeItems, String partNumUgr, boolean isNorm) {
        Map<String, PartGroupTreeItem> unknownUgrTreeItems = new HashMap<>();

        for (EfsElementDTO efsElement : efsElementsUgr) {
            PartGroupVMO ugrVMO = new PartGroupVMO();
            ugrVMO.setUgr(Integer.parseInt(partNumUgr));
            ugrVMO.setMgr(Integer.parseInt(efsElement.getEfsElementMara().getPartNumberMittelgruppe()));

            PartGroupTreeItem mgrTreeItem = unknownMgrTreeItems.get(
                efsElement.getEfsElementMara().getPartNumberMittelgruppe());
            PartGroupTreeItem ugrTreeItem = unknownUgrTreeItems.get(
                efsElement.getEfsElementMara().getPartNumberEndNumber());

            if (ugrTreeItem == null) {
                ugrTreeItem = partGroupTreeModel.createTreeItem(new PartGroupTreeObject(ugrVMO));

                unknownUgrTreeItems.put(efsElement.getEfsElementMara().getPartNumberEndNumber(), ugrTreeItem);
            } else if (!ugrTreeItem.getUserObject().getAggregationObject().getMgr().equals(ugrVMO.getMgr())) {
                ugrTreeItem = partGroupTreeModel.createTreeItem(new PartGroupTreeObject(ugrVMO));
            }

            ugrTreeItem.getUserObject().getEfsElements().add(efsElement);

            if (mgrTreeItem == null) {
                if (isNorm) {
                    ugrVMO.setCategory(100);
                }

                PartGroupVMO mgrVMO = new PartGroupVMO(ugrVMO.getCategory(), ugrVMO.getMgr(), null);
                PartGroupTreeItem partGroupTreeItem = partGroupTreeModel.createTreeItem(
                    new PartGroupTreeObject(mgrVMO));

                PartGroupTreeItem knownMgrTreeItem;

                if (isNorm) {
                    knownMgrTreeItem = vnrMgrItem.get(SpecPartGroupCategory.NORM_PART_GROUP.getCategory());
                } else {
                    knownMgrTreeItem = mgrTreeItemMap.get(mgrVMO.getMgr());
                }

                if (knownMgrTreeItem != null) {
                    mgrVMO.setDescription(knownMgrTreeItem.getUserObject().getAggregationObject().getDescription());
                }

                unknownMgrTreeItems.put(efsElement.getEfsElementMara().getPartNumberMittelgruppe(), partGroupTreeItem);
                this.unknownMgrTreeItems.add(partGroupTreeItem);

                partGroupTreeItem.getChildren().add(ugrTreeItem);
            } else {
                mgrTreeItem.getChildren().remove(ugrTreeItem);
                mgrTreeItem.getChildren().add(ugrTreeItem);
            }

            unknownUgrTreeItems.put(partNumUgr, ugrTreeItem);
            this.unknownUgrTreeItems.add(ugrTreeItem);
        }
    }

    private Collection<PartGroupTreeItem> sortAndCacheUnknownMgrTreeItems() {
        Collection<PartGroupTreeItem> sortedItems = unknownMgrTreeItems.stream()
            .sorted(Comparator.comparing(t -> t.getUserObject().getAggregationObject().getMgr()))
            .collect(Collectors.toList());

        for (PartGroupTreeItem sortedItem : sortedItems) {
            partGroupTreeModel.cacheTreeItem(sortedItem);
        }

        return sortedItems;
    }

    private void handleClickEvent(boolean isDoubleClick, String propertyName) {
        splitPaneEfsView = parentController.getSplitPaneEfsView();
        if (!isDoubleClick) {
            return;
        }

        EfsViewTabPaneController efsViewTabPaneController = parentController.getEfsViewTabPaneController();
        PartGroupTreeItem item = (PartGroupTreeItem) partGroupTreeTableView.getSelectionModel().getSelectedItem();

        lastSelectedColumnProperty = propertyName;

        List<EfsElementDTO> efsElementList = new ArrayList<>();
        String columnHeaderName = switch (propertyName) {
            case PartGroupTreeItemPropertyNames.WEIGHT_ALL -> {
                efsElementList = TreeItemUtil.getAggregatedChildren(item);
                yield I18N.getString("treetablecolumn.weightall");
            }
            case PartGroupTreeItemPropertyNames.HUT -> {
                efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                    .filter(e -> ApCompareGroup.HUT.containsAp(e.getAp())).toList());
                yield I18N.getString("treetablecolumn.hut");
            }
            case PartGroupTreeItemPropertyNames.SYSTEM -> {
                efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                    .filter(e -> ApCompareGroup.SYSTEM.containsAp(e.getAp())).toList());
                yield I18N.getString("treetablecolumn.system");
            }
            default -> {
                efsElementList.addAll(TreeItemUtil.getAggregatedChildren(item).stream()
                    .filter(e -> ApCompareGroup.PLATFORM.containsAp(e.getAp())).toList());
                yield I18N.getString("treetablecolumn.platform");
            }
        };

        String itemName =
            item.getValue().getId() == null ? I18N.getString("table.row.unknown") : getItemNameForSummaryView(item);
        if (splitPaneEfsView.getItems().contains(efsViewTabPaneController.getControl())) {
            handleActionReloadSummarisedView(PartGroupSummarisedTabController.class, efsElementList, columnHeaderName,
                itemName, splitPaneEfsView);

            if (splitPaneEfsView.getDividerPositions()[0] > 0.65) {
                splitPaneEfsView.setDividerPositions(0.65);
            }

            return;
        }

        if (efsElementList.isEmpty()) {
            return;
        }

        splitPaneEfsView.setDividerPositions(0.65);

        AbstractSummarisedTabController controller = handleActionShowSummarisedView(
            PartGroupSummarisedTabController.class, efsElementList, columnHeaderName, itemName, splitPaneEfsView);

        addBindedColumnHeaderListener(controller.getTableView());

        ListChangeListener<? super TableColumn<EfsElementDTO, ?>> listChangeListener = (ListChangeListener.Change<? extends TableColumn<EfsElementDTO, ?>> c) -> {
            if (c.next()) {
                notifyTableColumnChanged(
                    controller.getTableView().getVisibleLeafColumns().stream().map(TableColumnBase::getText).toList());
            }
        };

        controller.initVisibleColumnListener(listChangeListener);
    }

    private void handleDisplayNumberOfParts(Boolean newVal) {
        if (newVal) {
            addCountColumns();
        } else {
            removeCountColumns();
        }
    }

    private void addCountColumns() {
        partGroupTreeTableView.setHeaderHeight(24);

        colPlatform.getColumns().addAll(colPlatNum, colPlatWeight);
        colSystem.getColumns().addAll(colSystemNum, colSystemWeight);
        colHut.getColumns().addAll(colHutNum, colHutWeight);
        colWeightAll.getColumns().addAll(colAllNum, colAllWeight);

        setColHeaderBinding();
    }

    private void removeCountColumns() {
        colPlatform.getColumns().removeAll(colPlatNum, colPlatWeight);
        colSystem.getColumns().removeAll(colSystemNum, colSystemWeight);
        colHut.getColumns().removeAll(colHutNum, colHutWeight);
        colWeightAll.getColumns().removeAll(colAllNum, colAllWeight);

        setColHeaderBinding();
        partGroupTreeTableView.setHeaderHeight(48);
    }

    private void setColHeaderBinding() {
        partGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colPlatform);
        partGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colSystem);
        partGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colHut);
        partGroupTreeTableView.setColGraphicWidthBindingFor2NestedColumns(colWeightAll);
    }

    private AbstractSummarisedTabController handleActionShowSummarisedView(
        Class<? extends AbstractSummarisedTabController> controllerClass, List<EfsElementDTO> efsElements,
        String propertyName, String itemName, SplitPane splitPaneEfsView) {
        AbstractSummarisedTabController controller = BaseController.load(controllerClass);

        if (controller.getSelectedEfsElement() == null) {
            controller.initEfsElementList(efsElements, propertyName, itemName,
                parentController.getVehiclePartList().getVehicleConfig(), null, false);
        } else {
            controller.initEfsElementList(efsElements, controller.getSelectedEfsElement());
        }

        if (splitPaneEfsView.getItems().size() > 1) {
            splitPaneEfsView.getItems().remove(1);
        }

        splitPaneEfsView.getItems().add(controller.getControl());

        disablePropertyCloseSummary.set(false);

        ColumnSequenceChangeEvent changeEvent = new ColumnSequenceChangeEvent(
            parentController.getVehiclePartList().getVehicleConfig(), getSVPLColumnOrder(),
            SingleVehiclePartGroupController.class);
        EventBus.getInstance().post(changeEvent);

        return controller;
    }

    private void handleActionReloadSummarisedView(Class<? extends AbstractSummarisedTabController> controllerClass,
        List<EfsElementDTO> efsElements, String propertyName, String itemName, SplitPane splitPaneEfsView)
        throws ControllerException {
        if (controllerClass == null) {
            return;
        }

        handleActionShowSummarisedView(controllerClass, efsElements, propertyName, itemName, splitPaneEfsView);
    }

    private String getItemNameForSummaryView(PartGroupTreeItem item) {
        if (item.getValue().getAggregationObject().isCategory() && item.getValue().getId() >= 100) {
            return SpecPartGroupCategory.getStringForCategory(item.getValue().getId());
        }

        return PartGroupUtil.groupToString(item.getValue().getId());
    }

    private void refreshPartGroup(EfsElementDTO efsElement) {
        if (partGroupTreeModel.getTreeItems().size() > 1) {
            partGroupTreeModel.updateNode(efsElement, true, true);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void clearOldPartGroupNode(PartGroupTreeRefreshEvent event) {
        if (event.isTreeRefresh() && event.getVehiclePartListId().equals(parentController.getVehiclePartListId())) {
            partGroupTreeModel.updateNode(event.getEfsElement(), false, true);
        }

        if (lastSelectedColumnProperty != null) {
            handleClickEvent(true, lastSelectedColumnProperty);
        }
    }
}
