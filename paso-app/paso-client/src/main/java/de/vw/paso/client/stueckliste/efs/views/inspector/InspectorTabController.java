package de.vw.paso.client.stueckliste.efs.views.inspector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.stammdaten.costgroup.CostGroupChangedEvent;
import de.vw.paso.client.stammdaten.setkey.SetKeyChangedEvent;
import de.vw.paso.client.stueckliste.efs.inspector.Inspector;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.client.stueckliste.efs.views.AbstractEfsViewTabController;
import de.vw.paso.client.stueckliste.efs.views.EfsViewTabType;
import de.vw.paso.client.stueckliste.efs.views.inspector.comparator.InspectorItemComparator;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorIgnoreEntriesChangeEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorJumpToElementEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolver;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.InspectorUtil;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.SolverPanel;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.SolverProvider;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorEntryTreeItemObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorProblemGroupTreeItemObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeTableRow;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTypeCell;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTypeTreeItemObject;
import de.vw.paso.client.util.FileUtil;
import de.vw.paso.client.util.TableExporter;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.delegate.stueckliste.inspector.InspectorRestClientHolder;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.inspector.InspectorIgnoreDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.IEfsElementResolverListener;
import de.vw.paso.utility.StringConstant;

/**
 * The FX {@link AbstractEfsViewTabController} for inspecting.
 */
@FXController(name = "inspector-tab")
public class InspectorTabController extends AbstractEfsViewTabController implements IEfsElementResolverListener {

    private static final Collection<InspectorEntryType> INSPECTOR_ENTRY_TYPES = Set.of(InspectorEntryType.GWS_INCORRECT,
        InspectorEntryType.GWS_INCORRECT_NO_WEIGHT, InspectorEntryType.WEIGHT_DIFFERENCE,
        InspectorEntryType.GWS_BAUKASTEN, InspectorEntryType.BAUKASTEN);

    private final ObjectProperty<Boolean> showHiddenProperty;
    private final FilterableTreeItem<InspectorTreeItemObject> root;
    private final InspectorItemComparator inspectorItemComparator;
    private final DoubleStringConverter doubleStringConverter;
    private final Map<AbstractSolver, Button> solverToButton;

    @FXML
    private Tab tabInspector;
    @FXML
    private SplitPane errorDescriptionSplitPane;
    @FXML
    private CustomTreeTableView<InspectorTreeItemObject> errorTree;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, Integer> colNumberOfIssues;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colType;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colError;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorPRNumber;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorAdditionalDescription;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorAP;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorSET;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorCostGroup;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorGWS;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, Double> colErrorTotalWeight;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, String> colErrorWeightUnit;
    @FXML
    private TreeTableColumn<InspectorTreeItemObject, Long> colTisSort;
    @FXML
    private WebView descriptionArea;
    @FXML
    private HBox solutionArea;
    @FXML
    private GridPane gridPane;

    private EventHandler<InspectorJumpToElementEvent> onElementClickedHandler;
    private Inspector inspector;
    private VehicleConfigDTO vehicleConfig;

    public InspectorTabController() {
        showHiddenProperty = new SimpleObjectProperty<>(false);
        root = new FilterableTreeItem<>(null);
        inspectorItemComparator = new InspectorItemComparator();
        doubleStringConverter = new DoubleStringConverter();
        solverToButton = new HashMap<>(13);
    }

    @Override
    protected EfsViewTabType getType() {
        return EfsViewTabType.INSPECTOR;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initTree();
        initDescriptionArea();
        inspector = new Inspector();
        showHiddenProperty.addListener((observable, oldValue, newValue) -> filter());
        EfsElementResolver.addListener(this);

        errorTree.makeFilterable();
        errorTree.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);

        colNumberOfIssues.getGraphic().setOnMouseReleased(null);
    }

    @Override
    public void onEfsElementUpdate(Collection<EfsElementDTO> efsElements) {
        boolean createTree = false;
        for (EfsElementDTO element : efsElements) {
            if (element.getVehiclePartListId().equals(getVehiclePartListId())) {
                createTree = true;
                break;
            }
        }

        if (createTree) {
            refreshInspectorItems();
        }
    }

    public void refreshInspectorItems(EfsElementDTO root, Collection<InspectorIgnoreDTO> toIgnore) {
        if (toIgnore == null) {
            // todo: replace with ui ignore filtering -> no backend call!
            toIgnore = InspectorRestClientHolder.getInstance()
                .loadIgnoreEntries(vehicleConfig.getVehiclePartList().getId()).inspectorIgnoredList();
        }

        Collection<EfsElementDTO> elementsInPartList =
            root == null ? EfsElementResolver.getElementsInPartList(getVehiclePartListId()) : root.getAllChildren();
        inspectEfsElements(elementsInPartList, root, toIgnore);
    }

    public void refreshInspectorItems() {
        // todo: replace with ui ignore filtering -> no backend call!
        Collection<InspectorIgnoreDTO> ignoreList = InspectorRestClientHolder.getInstance()
            .loadIgnoreEntries(vehicleConfig.getVehiclePartList().getId()).inspectorIgnoredList();
        refreshInspectorItems(null, ignoreList);
    }

    @Override
    public Tab getControl() {
        return tabInspector;
    }

    @Override
    public Parent getStyleableParent() {
        return gridPane;
    }

    @Override
    public void stop() {
        super.stop();
        EfsElementResolver.removeListener(this);
    }

    public void setOnJumpToElementHandler(EventHandler<InspectorJumpToElementEvent> handler) {
        onElementClickedHandler = handler;
    }

    @Subscribe
    private void updateSetKeys(SetKeyChangedEvent event) {
        refreshInspectorItems();
    }

    @Subscribe
    private void handleInspectorIgnoreEntriesChangeEvent(InspectorIgnoreEntriesChangeEvent event) {
        filter();
    }

    @Subscribe
    private void updateCostGroups(CostGroupChangedEvent event) {
        refreshInspectorItems();
    }

    private void initDescriptionArea() {
        String stylePath = getClass().getResource("description.css").toExternalForm();
        descriptionArea.getEngine().setUserStyleSheetLocation(stylePath);
        errorDescriptionSplitPane.setDividerPositions(0.6);
    }

    private void initTree() {
        errorTree.setShowRoot(false);
        errorTree.getSelectionModel().selectionModeProperty().set(SelectionMode.MULTIPLE);

        initCellValueFactories();
        initCellFactories();

        errorTree.setRowFactory(tableView -> createInspectorTreeTableRow());
        errorTree.setRoot(root);
        errorTree.setOnMouseClicked(this::onTableMouseClick);

        setReorderable(colType);
    }

    private void onTableMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
            ContextMenu popup = createPopup();
            errorTree.setContextMenu(popup);
        }
    }

    private TreeTableRow<InspectorTreeItemObject> createInspectorTreeTableRow() {
        TreeTableRow<InspectorTreeItemObject> row = new InspectorTreeTableRow();
        row.setOnMouseClicked(event -> onRowMouseClick(event, row));
        row.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateSolutionArea();
            }
        });

        return row;
    }

    private void updateSolutionArea() {
        solutionArea.getChildren().clear();
        descriptionArea.getEngine().loadContent(StringConstant.EMPTY);

        updateSolvers();
    }

    private void setReorderable(TreeTableColumn<InspectorTreeItemObject, String> colType) {
        colType.setReorderable(false);
    }

    private Button createSolverButton(AbstractSolver solver) {
        Button solutionButton = solverToButton.get(solver);
        if (solutionButton == null) {
            String title = I18N.getString(solver.getTitleKey());
            solutionButton = new Button(title);
            solutionButton.getStyleClass().add("solution-button");
            solutionButton.setOnAction(event -> solve(solver));

            solverToButton.put(solver, solutionButton);
        }

        solutionButton.setDisable(solver.disable());
        return solutionButton;
    }

    private void solve(AbstractSolver solutionController) {
        try {
            if (solutionController.solve()) {
                updateSolutionPanel();
            }
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    //todo: decrease vo size and complex logic for call value factoring
    private void initCellValueFactories() {
        colType.setCellValueFactory(this::createProblemTypeValue);
        colNumberOfIssues.setCellValueFactory(this::countChildren);
        colError.setCellValueFactory(param -> createCellStringProperty(param, EfsElementDTO::getDescription1));
        colErrorAdditionalDescription.setCellValueFactory(
            param -> createCellStringProperty(param, EfsElementDTO::getDescription2));
        colErrorPRNumber.setCellValueFactory(
            param -> createCellStringProperty(param, EfsElementDTO::getFormattedPartNumber));
        colErrorAP.setCellValueFactory(param -> createCellStringProperty(param, EfsElementDTO::getAp));
        colErrorSET.setCellValueFactory(param -> createCellStringProperty(param, EfsElementDTO::getSetKey));
        colErrorCostGroup.setCellValueFactory(param -> createCellStringProperty(param, EfsElementDTO::getCostGroup));
        colErrorGWS.setCellValueFactory(param -> createCellStringProperty(param, this::getWeightControlFlag));
        colErrorWeightUnit.setCellValueFactory(
            param -> createCellStringProperty(param, EfsElementDTO::getQuantityUnit));
        colErrorTotalWeight.setCellValueFactory(param -> createCellObjectProperty(param, EfsElementDTO::getNodeWeight));
        colTisSort.setCellValueFactory(param -> createCellObjectProperty(param, EfsElementDTO::getTisSort));
    }

    private String getWeightControlFlag(EfsElementDTO efsElementDTO) {
        WeightControlFlag weightControlFlag = efsElementDTO.getWeightControlFlag();
        return weightControlFlag == null ? StringConstant.EMPTY : weightControlFlag.getValue();
    }

    private SimpleStringProperty createProblemTypeValue(CellDataFeatures<InspectorTreeItemObject, String> param) {
        FilterableTreeItem<InspectorTreeItemObject> treeItem = (FilterableTreeItem<InspectorTreeItemObject>) param.getValue();
        if (treeItem == null) {
            return null;
        }

        InspectorTreeItemObject value = treeItem.getValue();
        if (value == null) {
            return null;
        }

        InspectorEntryType type = value.getType();
        if (value.isTypeNode()) {
            return new SimpleStringProperty(getLabelForType(type));
        }

        if (!value.isGroupNode()) {
            return null;
        }

        if (InspectorEntryType.DUPLICATE == type) {
            String label = treeItem.getSourceChildren().getFirst().getValue().getEntry().getElement().getNodeLabel();
            return createSimpleStringProperty(type, label);
        }

        String groupName = value.toString();
        if (groupName == null) {
            groupName = I18N.getString("inspector.tree.group.empty");
        }

        return createSimpleStringProperty(type, groupName);
    }

    private SimpleStringProperty createSimpleStringProperty(InspectorEntryType type, String label) {
        return new SimpleStringProperty(
            getLabelForType(type) + StringConstant.SPACE_LEFT_PARENTHESIS + label + StringConstant.RIGHT_PARENTHESIS);
    }

    private SimpleStringProperty createCellStringProperty(CellDataFeatures<InspectorTreeItemObject, String> param,
        Function<EfsElementDTO, String> textFunction) {
        TreeItem<InspectorTreeItemObject> item = param.getValue();
        if (item == null) {
            return null;
        }

        InspectorTreeItemObject value = item.getValue();
        if (value == null) {
            return null;
        }

        if (value.isEntryNode()) {
            InspectorEntry entry = value.getEntry();
            String text = textFunction.apply(entry.getElement());
            return new SimpleStringProperty(text);
        }

        return shouldShowParentDetails(value) ? getParentStringValue(param, textFunction) : null;
    }

    private <T> SimpleObjectProperty<T> createCellObjectProperty(CellDataFeatures<InspectorTreeItemObject, T> param,
        Function<EfsElementDTO, T> numberFunction) {
        TreeItem<InspectorTreeItemObject> item = param.getValue();
        if (item == null) {
            return null;
        }

        InspectorTreeItemObject value = item.getValue();
        if (value == null) {
            return null;
        }

        if (value.isEntryNode()) {
            InspectorEntry entry = value.getEntry();
            T text = numberFunction.apply(entry.getElement());
            return text == null ? null : new SimpleObjectProperty<>(text);
        }

        return shouldShowParentDetails(value) ? getParentObjectValue(param, numberFunction) : null;
    }

    private SimpleObjectProperty<Integer> countChildren(CellDataFeatures<InspectorTreeItemObject, Integer> param) {
        TreeItem<InspectorTreeItemObject> value = param.getValue();
        if (value == null || value.getChildren().isEmpty()) {
            return null;
        }

        Collection<TreeItem<InspectorTreeItemObject>> entries = InspectorUtil.getEntries(value.getChildren(),
            showHiddenProperty.get());
        return entries.isEmpty() ? null : new SimpleObjectProperty<>(entries.size());
    }

    private boolean shouldShowParentDetails(InspectorTreeItemObject value) {
        return value.isGroupNode() && INSPECTOR_ENTRY_TYPES.contains(value.getType());
    }

    private SimpleStringProperty getParentStringValue(
        TreeTableColumn.CellDataFeatures<InspectorTreeItemObject, String> param,
        Function<EfsElementDTO, String> getter) {
        FilterableTreeItem<InspectorTreeItemObject> value = (FilterableTreeItem<InspectorTreeItemObject>) param.getValue();
        if (value.getSourceChildren().isEmpty()) {
            return null;
        }

        TreeItem<InspectorTreeItemObject> child = value.getSourceChildren().getFirst();
        EfsElementDTO parent = child.getValue().getEntry().getElement().getParent();

        return parent == null ? null : new SimpleStringProperty(getter.apply(parent));
    }

    private <T> SimpleObjectProperty<T> getParentObjectValue(
        TreeTableColumn.CellDataFeatures<InspectorTreeItemObject, T> param, Function<EfsElementDTO, T> getter) {
        ObservableList<TreeItem<InspectorTreeItemObject>> children = ((FilterableTreeItem<InspectorTreeItemObject>) param.getValue()).getSourceChildren();
        if (children.isEmpty()) {
            return null;
        }

        TreeItem<InspectorTreeItemObject> child = children.getFirst();
        EfsElementDTO parent = child.getValue().getEntry().getElement().getParent();
        if (parent == null) {
            return null;
        }

        T number = getter.apply(parent);
        return number == null ? null : new SimpleObjectProperty<>(number);
    }

    private void initCellFactories() {
        colType.setCellFactory(param -> new InspectorTypeCell());
        colErrorPRNumber.setCellFactory(column -> createInspectorCell(String.class, null));
        colErrorTotalWeight.setCellFactory(column -> createInspectorCell(Double.class, doubleStringConverter));
        colError.setCellFactory(column -> new ReadOnlyTreeTableCell<>(String.class));
    }

    private <T> ReadOnlyTreeTableCell<InspectorTreeItemObject, T> createInspectorCell(Class<T> dataType,
        StringConverter<T> converter) {
        ReadOnlyTreeTableCell<InspectorTreeItemObject, T> cell = new ReadOnlyTreeTableCell<>(dataType);
        cell.setAlignment(Pos.CENTER_RIGHT);
        if (converter != null) {
            cell.setConverter(converter);
        }

        return cell;
    }

    private void onRowMouseClick(MouseEvent event, TreeTableRow<InspectorTreeItemObject> row) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            InspectorTreeItemObject rowItem = row.getItem();
            if (rowItem.isEntryNode()) {
                onElementClickedHandler.handle(new InspectorJumpToElementEvent(rowItem.getEntry().getElement()));
            }
        }
    }

    private void updateSolutionPanel() {
        updateSolutionArea();

        errorTree.refresh();
    }

    private void updateSolvers() {
        List<TreeItem<InspectorTreeItemObject>> selectedItems = errorTree.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }

        boolean show = showHiddenProperty.get();

        updateSolverPanel(selectedItems, show);
        updateSolverButtons(selectedItems, show);
    }

    private void updateSolverPanel(List<TreeItem<InspectorTreeItemObject>> selectedItems, boolean show) {
        InspectorEntryType type = selectedItems.getFirst().getValue().getType();
        boolean mixed = selectedItems.stream().anyMatch(item -> item.getValue().getType() != type);
        if (mixed) {
            return;
        }

        SolverPanel solverPanel = SolverProvider.getSolverArea(type);
        solverPanel.setVehicleConfig(vehicleConfig);
        solverPanel.setEntries(selectedItems, show);

        for (AbstractSolver solver : solverPanel.getSolvers()) {
            Button solverButton = createSolverButton(solver);

            solutionArea.getChildren().add(solverButton);
        }

        if (!solverPanel.getEntries().isEmpty()) {
            descriptionArea.getEngine().loadContent(solverPanel.getDescription());
        }
    }

    private void updateSolverButtons(Collection<TreeItem<InspectorTreeItemObject>> selectedItems, boolean show) {
        for (AbstractSolver solver : SolverProvider.getGeneralSolvers()) {
            Collection<TreeItem<InspectorTreeItemObject>> entries = InspectorUtil.getEntries(selectedItems, show);
            solver.setEntries(entries, show);
            solver.setVehicleConfig(vehicleConfig);

            Button solverButton = createSolverButton(solver);
            solutionArea.getChildren().add(solverButton);
        }
    }

    private ContextMenu createPopup() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(createShowHideMenuItem(), createExportMenuItem());

        return contextMenu;
    }

    private MenuItem createShowHideMenuItem() {
        CheckMenuItem hideMenuItem = new CheckMenuItem(I18N.getString("inspector.context.show_ignored"));
        hideMenuItem.setSelected(showHiddenProperty.get());
        hideMenuItem.setOnAction(actionEvent -> showOrHideProperty());
        return hideMenuItem;
    }

    private void showOrHideProperty() {
        showHiddenProperty.setValue(!showHiddenProperty.get());
        errorTree.refresh();
    }

    private MenuItem createExportMenuItem() {
        MenuItem exportMenuItem = new MenuItem(I18N.getString("inspector.context.export"));
        exportMenuItem.setOnAction(this::exportTable);
        return exportMenuItem;
    }

    private void exportTable(ActionEvent actionEvent) {
        File file = FileUtil.openSaveExcelDialog("inspector_export", errorTree.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            TableExporter.TableExporterConfig config = new TableExporter.TableExporterConfig(
                (treeItem, column) -> column.getTreeTableView().getColumns().indexOf(column) == 0
                    && treeItem.getChildren().isEmpty());
            TableExporter.export(errorTree, out, config);
            FileUtil.openFileWithAssociatedProgram(file);
        } catch (IOException e) {
            ExceptionHandler.instance().handleException(e);
        }
    }

    private void filter() {
        //todo: this should be: PasoPredicate<TreeItem<InspectorTreeItemObject>> pasoPredicate = showHidden ? null : this::filterInspections;
        // -> but table sorting is not called on null predicate and also on table refresh
        errorTree.setCustomPredicate(this::filterInspections);
    }

    private boolean filterInspections(TreeItem<InspectorTreeItemObject> item) {
        if (showHiddenProperty.get()) {
            return true;
        }

        InspectorTreeItemObject value = item.getValue();
        if (value.isEntryNode()) {
            return !value.isIgnored();
        }

        Collection<TreeItem<InspectorTreeItemObject>> entries = InspectorUtil.getEntries(item.getChildren(), false);
        if (entries.isEmpty()) {
            return false;
        }

        if (shouldShowParentDetails(value)) {
            return true;
        }

        return value.isTypeNode() || !item.getChildren().isEmpty();
    }

    private Long getVehiclePartListId() {
        return vehicleConfig.getVehiclePartList().getId();
    }

    private void inspectEfsElements(Collection<EfsElementDTO> elements, EfsElementDTO rootElem,
        Collection<InspectorIgnoreDTO> listToIgnore) {
        if (elements == null) {
            return;
        }

        ListMultimap<InspectorEntryType, InspectorEntry> inspectorTypeToEntryMap = inspector.checkElements(elements,
            rootElem, vehicleConfig);

        Collection<InspectorEntry> inspectorEntries = inspectorTypeToEntryMap.get(InspectorEntryType.GAP);
        Collection<Long> gapIds = new HashSet<>(inspectorEntries.size());
        for (InspectorEntry entry : inspectorEntries) {
            gapIds.add(entry.getElement().getId());
        }

        Collection<Long> elementIds = elements.stream().map(EfsElementDTO::getId).toList();
        ListMultimap<Long, InspectorEntry> efsIdToInspectorEntryMap = MultimapBuilder.ListMultimapBuilder.hashKeys()
            .arrayListValues().build();
        for (InspectorEntry inspectorEntry : inspectorTypeToEntryMap.values()) {
            efsIdToInspectorEntryMap.put(inspectorEntry.getElement().getId(), inspectorEntry);
        }

        //todo: maybe map to value then check with contains?
        Collection<TreeItem<InspectorTreeItemObject>> prevSelectedItems = errorTree.getSelectionModel()
            .getSelectedItems().stream().toList();

        root.getChildren().clear();
        root.getSourceChildren().clear();
        root.getFilteredChildren().clear();

        Map<InspectorEntryType, Collection<Long>> typeToIgnoredEntryIds = new EnumMap<>(InspectorEntryType.class);
        if (listToIgnore != null) {
            for (InspectorIgnoreDTO toIgnore : listToIgnore) {
                typeToIgnoredEntryIds.computeIfAbsent(toIgnore.type(), entry -> new HashSet<>())
                    .add(toIgnore.efsElementId());
            }
        }

        for (Long efsId : elementIds) {
            Collection<InspectorEntry> errorsForEfsId = efsIdToInspectorEntryMap.get(efsId);
            // if element is a gap, ignore other problems.
            if (gapIds.contains(efsId)) {
                errorsForEfsId.removeIf(element -> InspectorEntryType.GAP != element.getType());
            }

            addToTree(errorsForEfsId, typeToIgnoredEntryIds);
        }

        sortTree(root.getSourceChildren());

        filter();

        updateSolutionPanel();

        MultipleSelectionModel<TreeItem<InspectorTreeItemObject>> selectionModel = errorTree.getSelectionModel();
        selectionModel.clearSelection();

        for (TreeItem<InspectorTreeItemObject> toSelect : prevSelectedItems) {
            select(toSelect);
        }
    }

    private void select(TreeItem<InspectorTreeItemObject> toSelect) {
        InspectorTreeItemObject value = toSelect.getValue();
        InspectorEntryType type = value.getType();

        for (TreeItem<InspectorTreeItemObject> typeItem : root.getChildren()) {
            if (typeItem.getValue().getType() != type) {
                continue;
            }

            if (value.isTypeNode()) {
                errorTree.getSelectionModel().select(typeItem);
                return;
            }

            typeItem.setExpanded(true);
            if (value.isGroupNode()) {
                for (TreeItem<InspectorTreeItemObject> groupItem : typeItem.getChildren()) {
                    if (Objects.equals(groupItem.getValue().toString(), value.toString())) {
                        errorTree.getSelectionModel().select(groupItem);
                        return;
                    }
                }
            } else if (value.isEntryNode()) {
                String groupName = toSelect.getParent().getValue().toString();
                for (TreeItem<InspectorTreeItemObject> groupItem : typeItem.getChildren()) {
                    if (!Objects.equals(groupItem.getValue().toString(), groupName)) {
                        continue;
                    }

                    groupItem.setExpanded(true);
                    for (TreeItem<InspectorTreeItemObject> entryItem : groupItem.getChildren()) {
                        if (Objects.equals(entryItem.getValue().toString(), value.toString())) {
                            errorTree.getSelectionModel().select(entryItem);
                            return;
                        }
                    }
                }
            }

            return;
        }
    }

    private FilterableTreeItem<InspectorTreeItemObject> findTreeItemForType(InspectorEntryType typeToFind) {
        ObservableList<TreeItem<InspectorTreeItemObject>> children = root.getSourceChildren();
        for (TreeItem<InspectorTreeItemObject> item : children) {
            InspectorEntryType type = item.getValue().getType();
            if (type.equals(typeToFind)) {
                return (FilterableTreeItem<InspectorTreeItemObject>) item;
            }
        }

        return null;
    }

    private FilterableTreeItem<InspectorTreeItemObject> findTreeItemForProblemGroupId(String groupId,
        FilterableTreeItem<InspectorTreeItemObject> treeItemForType) {
        ObservableList<TreeItem<InspectorTreeItemObject>> children = treeItemForType.getSourceChildren();
        for (TreeItem<InspectorTreeItemObject> item : children) {
            if (item.getValue() instanceof InspectorProblemGroupTreeItemObject value && value.groupId()
                .equals(groupId)) {
                return (FilterableTreeItem<InspectorTreeItemObject>) item;
            }
        }

        return null;
    }

    private void addToTree(Collection<InspectorEntry> entries,
        Map<InspectorEntryType, Collection<Long>> typeToIgnoredEntryIds) {
        Collection<FilterableTreeItem<InspectorTreeItemObject>> typeItems = new ArrayList<>(entries.size());
        for (InspectorEntry entry : entries) {
            InspectorEntryType type = entry.getType();

            FilterableTreeItem<InspectorTreeItemObject> typeItem = findTreeItemForType(type);
            if (typeItem == null) {
                InspectorTypeTreeItemObject typeItemObject = new InspectorTypeTreeItemObject(type);

                typeItem = new FilterableTreeItem<>(typeItemObject);
                typeItems.add(typeItem);
            }

            Collection<Long> ignoredEntryIds = typeToIgnoredEntryIds.get(type);
            boolean ignored = ignoredEntryIds != null && ignoredEntryIds.contains(entry.getElement().getId());
            if (!ignored) {
                typeItem.getValue().setIgnored(false);
            }

            InspectorEntryTreeItemObject entryTreeItemObject = new InspectorEntryTreeItemObject(entry);
            entryTreeItemObject.setIgnored(ignored);

            FilterableTreeItem<InspectorTreeItemObject> entryItem = new FilterableTreeItem<>(entryTreeItemObject);
            addToProblemGroupItem(type, entryItem, typeItem);
        }

        root.getSourceChildren().addAll(typeItems);
    }

    private void sortTree(List<TreeItem<InspectorTreeItemObject>> types) {
        types.sort(inspectorItemComparator);

        for (var item : types) {
            var groups = ((FilterableTreeItem<InspectorTreeItemObject>) item).getSourceChildren();
            groups.sort(inspectorItemComparator);

            for (var group : groups) {
                FilterableTreeItem<InspectorTreeItemObject> entry = (FilterableTreeItem<InspectorTreeItemObject>) group;
                entry.getSourceChildren().sort(inspectorItemComparator);
            }
        }
    }

    private void addToProblemGroupItem(InspectorEntryType type, FilterableTreeItem<InspectorTreeItemObject> entryItem,
        FilterableTreeItem<InspectorTreeItemObject> typeItem) {
        InspectorTreeItemObject entryItemObject = entryItem.getValue();
        InspectorEntry entry = entryItemObject.getEntry();
        String problemGroupId = entry.getGroupIdentifier();

        FilterableTreeItem<InspectorTreeItemObject> groupItem = findTreeItemForProblemGroupId(problemGroupId, typeItem);
        if (groupItem != null || !type.isGroupsEnabled()) {
            Objects.requireNonNullElse(groupItem, typeItem).getSourceChildren().add(entryItem);
            if (groupItem != null && !entryItemObject.isIgnored()) {
                groupItem.getValue().setIgnored(false);
            }

            return;
        }

        InspectorProblemGroupTreeItemObject group = new InspectorProblemGroupTreeItemObject(type,
            entry.getProblemGroup(), problemGroupId);
        if (!entryItemObject.isIgnored()) {
            group.setIgnored(false);
        }

        FilterableTreeItem<InspectorTreeItemObject> newGroupItem = new FilterableTreeItem<>(group);
        newGroupItem.getSourceChildren().add(entryItem);
        typeItem.getSourceChildren().add(newGroupItem);
    }

    private String getLabelForType(InspectorEntryType type) {
        return I18N.getString("inspector.type." + type.name().toLowerCase() + ".label");
    }

    public void setVehicleConfig(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig = vehicleConfig;
    }
}
