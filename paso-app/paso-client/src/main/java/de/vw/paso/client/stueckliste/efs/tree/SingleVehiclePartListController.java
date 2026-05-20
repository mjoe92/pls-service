package de.vw.paso.client.stueckliste.efs.tree;

import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Duration;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Table;
import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoDialog;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.breadcrumb.PasoBreadCrumbBar;
import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.control.combobox.PasoCustomComboBox;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.control.tablebase.tableconfig.ColumnSelectionResult;
import de.vw.paso.client.control.tablebase.tableconfig.TableColumnSelectionUtils;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.explorer.vehicleconfig.event.ShowCompareTabEvent;
import de.vw.paso.client.main.ribbonmenu.efs.DisplayMode;
import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.converter.QuantityUnitStringConverter;
import de.vw.paso.client.stueckliste.efs.EfsTabController;
import de.vw.paso.client.stueckliste.efs.control.AggregatTextFieldEvent;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.efs.display.strategy.AbstractDisplayStrategyForTrees;
import de.vw.paso.client.stueckliste.efs.display.strategy.FlatDisplayStrategy;
import de.vw.paso.client.stueckliste.efs.display.strategy.HierarchicalDisplayStrategy;
import de.vw.paso.client.stueckliste.efs.display.strategy.PartListGroupDisplayStrategy;
import de.vw.paso.client.stueckliste.efs.event.FzgStuecklisteGewichtEvent;
import de.vw.paso.client.stueckliste.efs.event.PartListLoadedEvent;
import de.vw.paso.client.stueckliste.efs.export.partlist.PartListExcelExporter;
import de.vw.paso.client.stueckliste.efs.header.EfsHeaderEngineAndGearboxController;
import de.vw.paso.client.stueckliste.efs.header.EfsHeaderFahrzeugController;
import de.vw.paso.client.stueckliste.efs.header.EfsHeaderGetriebeController;
import de.vw.paso.client.stueckliste.efs.header.EfsHeaderMotorController;
import de.vw.paso.client.stueckliste.efs.inspector.Inspector;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.client.stueckliste.efs.tree.cell.EfsCellFactory;
import de.vw.paso.client.stueckliste.efs.tree.model.AbstractEfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementHistoryTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItemPropertyNames;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeModel;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialog;
import de.vw.paso.client.stueckliste.efs.views.compare.ComparePartListSelectionDialogResult;
import de.vw.paso.client.stueckliste.efs.views.historie.EfsHistoryUtil;
import de.vw.paso.client.stueckliste.efs.views.historie.columns.EfsTreeTableColumn;
import de.vw.paso.client.stueckliste.efs.views.historie.event.HistorieUpdateEvent;
import de.vw.paso.client.stueckliste.efs.views.historie.model.EfsElementHistoryTreeModel;
import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorDeleteEfsElementSolutionEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.PasoPredicate;
import de.vw.paso.client.stueckliste.event.CostGroupTreeRefreshEvent;
import de.vw.paso.client.stueckliste.event.FgSetTreeRefreshEvent;
import de.vw.paso.client.stueckliste.event.PartGroupTreeRefreshEvent;
import de.vw.paso.client.stueckliste.event.SelectEfsElementOnEfsTabEvent;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.stueckliste.util.PartNumberUtil;
import de.vw.paso.client.util.AnimationUtil;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.client.util.TreeItemUtil;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.delegate.fzgkonfig.VehicleConfigRestClientHolder;
import de.vw.paso.delegate.stammdaten.tableconfig.TableConfigRestClientHolder;
import de.vw.paso.delegate.stueckliste.EfsEditLoadAdapter;
import de.vw.paso.delegate.stueckliste.efselementhistory.EfsElementHistoryRestClientHolder;
import de.vw.paso.delegate.stueckliste.efsweight.EfsWeightRestClientHolder;
import de.vw.paso.delegate.stueckliste.inspector.InspectorRestClientHolder;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.partlist.domain.AP;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.AppendToDeletedElementException;
import de.vw.paso.service.partlist.EfsEditValidations;
import de.vw.paso.service.partlist.ICopyEfsElementConsumer;
import de.vw.paso.service.partlist.IDeleteEfsElementConsumer;
import de.vw.paso.service.partlist.IMoveEfsElementConsumer;
import de.vw.paso.service.partlist.MovingHierachyConflictException;
import de.vw.paso.service.partlist.PartNumberInappropriateException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementDTOWrapper;
import de.vw.paso.service.partlist.efselementhistory.EfsElementHistoryDTO;
import de.vw.paso.service.partlist.inspector.InspectorIgnoreDTO;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.EfsElementUtil;
import de.vw.paso.utility.Pair;
import de.vw.paso.utility.StringCommonTermsUtil;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;

@FXController(name = "efs-tree")
public class SingleVehiclePartListController extends SingleVehicleBaseController<EfsElementDTO>
        implements ICopyEfsElementConsumer, IDeleteEfsElementConsumer, IMoveEfsElementConsumer {

    private static final String TREE_HIGHLIGHT_COL_SELECTION = "tree-highlight-col-selection";
    private static final String TREE_HIGHLIGHT_ROW_SELECTION = "tree-highlight-row-selection";
    private static final int EXEC_TIME_SAVE_EFS = 1500;
    private static final int EXEC_TIME_DELETE_EFS = 1500;
    private static final int EXEC_TIME_COPY_EFS = 1500;
    private static final int EXEC_TIME_MOVE_EFS = 1500;
    private static final int EXEC_TIME_LOAD_HISTORIE = 1000;

    private final ObjectProperty<VehicleConfigDTO> vehicleConfig;
    private final ObjectProperty<Label> tablePlaceholderProperty;
    private final ObjectProperty<List<DisplayMode>> availableDisplayModes;
    private final ObjectProperty<DisplayMode> selectedDisplayMode;
    private final ObjectProperty<EventHandler<HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>>>> efsSelectionProperty;
    private final Map<PartListViewMode, EfsHeaderFahrzeugController> mapHeaderController;
    private final ObservableList<String> setKeys;
    private final ObservableList<String> costGroups;
    private final SelectionHighlightManager<EfsElementDTO> highlightManager;

    @FXML
    private PasoBreadCrumbBar<EfsElementDTO> breadCrumbBar;
    @FXML
    private CustomTreeTableView<EfsElementDTO> efsTreeTableView;
    @FXML
    private EfsTreeTableColumn<EfsElementDTO, String> colPartNumber;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPartNumberVornummer;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPartNumberMittelgruppe;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPartNumberEndNumber;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPartNumberIndex;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDescription1;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDescription2;
    @FXML
    private TreeTableColumn<EfsElementDTO, Integer> colBomNumber;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colProduct;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPartType;
    @FXML
    private TreeTableColumn<EfsElementDTO, Integer> colNodeLevel;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colNodeType;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colNodeLabel;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colNodeValueParent;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colNodeValue;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colAp;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colSetKey;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colCostGroup;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colConstructionsGroup;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colProductStructure;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDeletionFlag;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPositionVariant;
    @FXML
    private TreeTableColumn<EfsElementDTO, Integer> colQuantity;
    @FXML
    private TreeTableColumn<EfsElementDTO, QuantityUnit> colQuantityUnit;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colQuantityUnitExtended;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colWeightControlFlag;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightNode;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightAll;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightPrio;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightWeightedTe;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colWeightWeightedTeDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightCalculatedTe;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colWeightCalculatedTeDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightEstimatedTe;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colWeightEstimatedTeDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colWeightWeightedProd;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colWeightWeightedProdDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colBeginDateKey;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colDrawingDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDrawingStatus;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colBeginDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colEndDateKey;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colEndDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colAssemblyIndicator;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colConstructionsState;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colQuality;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colMatThickness;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colSeeDrawing;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colRespConstr1;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colRespConstr2;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colBuildSampleApproval;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colBuildSampleApprovalDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colTechnicallyOkay;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colRelDateSoll;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDesignerName;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDesignerCostGroup;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDesignerPhone;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colKStandRelDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colTioFreiRelDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colMFPStatus;
    @FXML
    private TreeTableColumn<EfsElementDTO, Double> colMFPThickness;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colKseKz;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colWeightAcceptedFromEpis;
    @FXML
    private TreeTableColumn<EfsElementDTO, Integer> colBaukastenFlag;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colBaukastenStatus;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colBaukastenNodeId;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colDmuRelevant;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colProcessStatus;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colMaterialType;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colEarliestPvs;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colEarliestNs;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colEarliestSop;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colPActivationDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, Date> colConstructureDate;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colAvonStatus;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colPrNumberRule;
    @FXML
    private TreeTableColumn<EfsElementDTO, Long> colTisSort;
    @FXML
    private TreeTableColumn<EfsElementDTO, CogCoordinates> colCog;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colNodeId;
    @FXML
    private TreeTableColumn<EfsElementDTO, String> colWahlweiseFall;
    @FXML
    private TreeTableColumn<EfsElementDTO, Integer> colWahlweiseNr;

    private EfsElementTreeModel efsElementTreeModel;
    private EfsTabController parentController;

    private BooleanPropertyAdapter disablePropertyNewEfsElement;
    private BooleanPropertyAdapter disablePropertyDeleteEfsElemente;
    private BooleanPropertyAdapter disablePropertyPasteEfsElemente;
    private BooleanPropertyAdapter disablePropertyCopyEfsElemente;
    private BooleanPropertyAdapter disablePropertyCutEfsElemente;
    private BooleanProperty disablePropertyCollapseTree;
    private BooleanProperty disablePropertyCollapseAllTree;
    private BooleanProperty disablePropertyExpandTree;
    private BooleanProperty disablePropertyExpandAllTree;
    private BooleanProperty disablePropertyShowPartProperties;
    private BooleanProperty disablePropertyCompare;
    private BooleanProperty disablePropertyShowHistory;
    private BooleanProperty disablePropertyShowRevision;
    private BooleanProperty disablePropertyShowChanges;
    private BooleanProperty disablePropertyExcelExport;
    private BooleanProperty togglePropertyChangeView;
    private ObjectProperty<PartListViewMode> viewModeEfsProperty;
    private boolean isCrumbAction;
    private PasoWildCardPattern patternSearchTerm;
    private GridPane paneEfsHeader;
    private EfsHeaderFahrzeugController efsHeaderController;
    private Collection<EfsElementDTOWrapper> history;
    private Runnable selectTabAction;
    private EfsElementTreeItem motor;
    private EfsElementTreeItem gearbox;
    private ListChangeListener<? super TreeTableColumn<EfsElementDTO, ?>> colSequenceChangeListener;
    private Collection<TreeItem<EfsElementDTO>> treeItemsToCopy;
    private Collection<TreeItem<EfsElementDTO>> treeItemsToCut;
    private ContextMenu contextMenu;
    private AbstractDisplayStrategyForTrees<EfsElementDTO> displayStrategy;

    public SingleVehiclePartListController() {
        vehicleConfig = new SimpleObjectProperty<>();
        tablePlaceholderProperty = new SimpleObjectProperty<>();
        availableDisplayModes = new SimpleObjectProperty<>();
        selectedDisplayMode = new SimpleObjectProperty<>();
        efsSelectionProperty = new SimpleObjectProperty<>(this, "EfsElement");
        mapHeaderController = new HashMap<>();
        setKeys = FXCollections.observableArrayList();
        costGroups = FXCollections.observableArrayList();
        highlightManager = new SelectionHighlightManager<>();

        history = new ArrayList<>();
        displayStrategy = HierarchicalDisplayStrategy.getStrategyWithoutDeletion();
    }

    private final ObjectProperty<InspectorItemCounter> inspectorItemCountProperty = new SimpleObjectProperty<>(
            new InspectorItemCounter(0, 0, new HashSet<>()));

    public ObjectProperty<InspectorItemCounter> getInspectorItemCountProperty() {
        return inspectorItemCountProperty;
    }

    public EfsHeaderFahrzeugController getEfsHeaderController() {
        return efsHeaderController;
    }

    public CustomTreeTableView<EfsElementDTO> getEfsTreeTableView() {
        return efsTreeTableView;
    }

    public ObjectProperty<List<DisplayMode>> availableDisplayModesProperty() {
        return availableDisplayModes;
    }

    public ObjectProperty<DisplayMode> selectedDisplayModeProperty() {
        return selectedDisplayMode;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        efsTreeTableView.getSortOrder()
                .addListener((ListChangeListener<TreeTableColumn<EfsElementDTO, ?>>) change -> enableResetSorting());

        for (TreeTableColumn<EfsElementDTO, ?> column : efsTreeTableView.getColumns()) {
            column.sortTypeProperty().addListener((observableValue, sortType, t1) -> enableResetSorting());
        }

        initBreadCrumbBar();

        highlightManager.initTable(efsTreeTableView, TREE_HIGHLIGHT_ROW_SELECTION, TREE_HIGHLIGHT_COL_SELECTION);
        selectedDisplayMode.addListener((observableValue, displayMode, t1) -> useDisplayMode(t1));
    }

    public void setPaneEfsHeader(GridPane paneEfsHeader) {
        this.paneEfsHeader = paneEfsHeader;
    }

    public void setParentController(EfsTabController parentController) {
        this.parentController = parentController;
    }

    private void enableResetSorting() {
        if (!isResettingSort) {
            disablePropertyResetSorting().set(false);
        }
    }

    @Override
    protected void initSorting() {
        efsTreeTableView.getSortOrder().setAll(colTisSort);
    }

    public final ObjectProperty<VehicleConfigDTO> vehicleConfigProperty() {
        return vehicleConfig;
    }

    public void initializeEfsHeader(ObjectProperty<PartListViewMode> viewModeEfsProperty) {
        this.viewModeEfsProperty = viewModeEfsProperty;
        viewModeEfsProperty.addListener((observable, oldValue, newViewMode) -> updateEfsHeader(newViewMode));

        loadEfsHeaderController(PartListViewMode.VEHICLE_ALL);
    }

    private void updateEfsHeader(PartListViewMode newViewMode) {
        loadEfsHeaderController(newViewMode);

        if (newViewMode.equals(PartListViewMode.GEARBOX) || newViewMode.equals(PartListViewMode.ENGINE)
                || newViewMode.equals(PartListViewMode.ENGINE_AND_GEARBOX)) {
            availableDisplayModes.set(Arrays.asList(DisplayMode.values()));
            resetDisplayMode();
        } else if (newViewMode.equals(PartListViewMode.VEHICLE)) {
            availableDisplayModes.set(Arrays.asList(DisplayMode.TREE, DisplayMode.LIST));
            if (selectedDisplayMode.get() == DisplayMode.GROUP) {
                selectedDisplayMode.set(DisplayMode.TREE);
            }
        } else {
            availableDisplayModes.set(Arrays.asList(DisplayMode.TREE, DisplayMode.LIST));
            if (selectedDisplayMode.get() == DisplayMode.GROUP) {
                selectedDisplayMode.set(DisplayMode.TREE);
            }
        }
    }

    public void handleActionNewEfsElement() {
        EfsElementDTO parent = displayStrategy.getParentForCreatingNewElement(getSelectedEfsElements());
        if (parent == null) {
            return;
        }

        EfsElementDTO efsElement = createNewEfsElement(parent);
        if (efsElement == null) {
            return;
        }

        String userId = UserProperties.getUserId();
        efsElement.setChange(userId);
        efsElement.getEfsElementMara().setChange(userId);
        efsElement.getEfsElementMara().setEntityChange(false);

        saveEfsElement(efsElement);
    }

    public void handleActionDeleteEfsElemente() {
        Collection<TreeItem<EfsElementDTO>> treeItems = getSelectedTreeItems();
        showDeleteDialog(treeItems);
    }

    private void showDeleteDialog(Collection<TreeItem<EfsElementDTO>> treeItems) {
        ButtonType resultButton = DialogUtil.showDeleteDialog(treeItems.size());
        if (resultButton == ButtonType.YES) {
            List<EfsElementDTO> efsElemente = treeItems.stream().map(TreeItem::getValue).toList();
            deleteEfsElement(efsElemente);
        }
    }

    public void handleActionCopyEfsElemente() {
        clearCopyCut();
        getTreeItemsToCopy().addAll(getSelectedTreeItems());
        setActionStates();
    }

    public void handleActionCutEfsElemente() {
        clearCopyCut();
        getTreeItemsToCut().addAll(getSelectedTreeItems());
        setActionStates();
    }

    public void handleActionPasteEfsElemente() {
        if (!getTreeItemsToCopy().isEmpty()) {
            copyEfsElemente(getSelectedEfsElementTreeItem(), getTreeItemsToCopy());
        } else if (!getTreeItemsToCut().isEmpty()) {
            moveEfsElemente(getSelectedEfsElementTreeItem(), getTreeItemsToCut());
        } else {
            setActionStates();
        }
    }

    public void handleActionExcelExport() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        if (vehicleConfig == null) {
            handleException(new IllegalStateException("Vehicle Config is null"));
            return;
        }

        //todo: refactor -> can be traversed AND the cells created at the same time in PartListExcelExporter
        Collection<EfsElementDTO> sortedElements = traverseElements(efsTreeTableView.getRoot(), new ArrayList<>());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String formattedDate = LocalDate.now().format(formatter);

        String title =
                vehicleConfig.getVehicleProject().getProjectName() + StringConstant.UNDERLINE + vehicleConfig.getName()
                        + "_GD" + formattedDate;
        title = title.replaceAll("\\\\+", StringConstant.UNDERLINE).replaceAll("/+", StringConstant.UNDERLINE);

        try {
            for (EfsElementDTO element : sortedElements) {
                new PartListExcelExporter(title, sortedElements, vehicleConfig.getVehiclePartList()).export(
                        I18N.getString("part.list.excel.sheet.name", element.getProduct()));
                return;
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private Collection<EfsElementDTO> traverseElements(TreeItem<EfsElementDTO> item,
            Collection<EfsElementDTO> accumulator) {
        if (item == null) {
            return accumulator;
        }

        if (item.getParent() != null) {
            accumulator.add(item.getValue());
        }

        for (TreeItem<EfsElementDTO> child : item.getChildren()) {
            traverseElements(child, accumulator);
        }

        return accumulator;
    }

    public void handleActionShowCompareDialog() {
        Collection<VehicleConfigDTO> vehicleConfigs = VehicleConfigRestClientHolder.getInstance()
                .loadNonDeletedVehicleConfigs().vehicleConfigDTOList();

        List<VehicleConfigDTO> finalVehicleConfigs = vehicleConfigs.stream()
                .filter(config -> config.getVehiclePartList() != null).toList();

        Collection<VehicleConfigDTO> selectedConfigsList = new ArrayList<>();
        selectedConfigsList.add(getVehicleConfig());

        ComparePartListSelectionDialog dialog = new ComparePartListSelectionDialog(selectedConfigsList, null,
                finalVehicleConfigs, parentController.getMainTabPaneController().getOpenPartListIDs());

        Optional<ComparePartListSelectionDialogResult> result = dialog.showAndWait();
        dialog.unregisterEventBus();
        result.ifPresent(comparePartListSelectionDialogResult -> EventBus.getInstance()
                .post(new ShowCompareTabEvent(comparePartListSelectionDialogResult.getSelectedVehicleConfigs(),
                        comparePartListSelectionDialogResult.getReferenceVehicleConfig())));
    }

    @Override
    public void copyEfsElements(IPartListChildDTO toParent, List<EfsElementDTO> copyEfsElemente) {
        ServiceController<List<EfsElementDTO>> serviceController = new ServiceController<>();

        serviceController.setOnSucceeded(event -> updateEfsTree(serviceController));
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        // EXEC_TIME_COPY_EFS -> Anzahl EfsElemente beachten
        serviceController.setExecutionTime(EXEC_TIME_COPY_EFS);
        serviceController.start(() -> new EfsEditLoadAdapter().copyEfsElements(toParent, copyEfsElemente));
    }

    @Subscribe
    private void deleteEfsElement(InspectorDeleteEfsElementSolutionEvent event) {
        if (!getVehicleConfig().getId().equals(event.getVehicleConfigId())) {
            return;
        }

        deleteEfsElement(event.getDeletingElements());
    }

    private void updateEfsTree(ServiceController<List<EfsElementDTO>> serviceController) {
        List<EfsElementDTO> copiedEfsElements = serviceController.getValue();
        EfsElementResolver.registerElements(copiedEfsElements);
        EventBus.getInstance().post(new VehicleConfigChangedEvent(getVehicleConfig()));

        fireHistoryUpdateEvent(copiedEfsElements.getFirst());
    }

    @Override
    public void handle(MovingHierachyConflictException exception) {
        String messageKey = exception.getMessageKey();
        DialogUtil.showWarnDialog(
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TITLE_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.HEADER_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TEXT_LOW_CASE)
                        + StringConstant.COLON_SPACE + exception.getEfsElement().getPartNumber());
    }

    @Override
    public void moveEfsElements(IPartListChildDTO toParent, List<EfsElementDTO> moveEfsElements) {
        ServiceController<List<EfsElementDTO>> serviceController = new ServiceController<>();

        serviceController.setOnSucceeded(event -> moveEfsElements(serviceController));
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        // EXEC_TIME_MOVE_EFS -> Note the number of Efs elements
        serviceController.setExecutionTime(EXEC_TIME_MOVE_EFS);
        serviceController.start(() -> new EfsEditLoadAdapter().moveEfsElements(toParent, moveEfsElements));
    }

    @Override
    public void handle(EmptyListException arg0) {
        assert false;
    }

    @Override
    public void handle(NullElementException arg0) {
        assert false;
    }

    @Override
    public void handle(AppendToDeletedElementException exception) {
        String messageKey = exception.getMessageKey();
        DialogUtil.showWarnDialog(
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TITLE_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.HEADER_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TEXT_LOW_CASE)
                        + StringConstant.COLON_SPACE + exception.getEfsElement().getPartNumber());
    }

    @Override
    public void onEfsElementUpdate(Collection<EfsElementDTO> elements) {
        if (efsTreeTableView.getRoot() == null || CollectionUtils.isEmpty(elements)) {
            return;
        }

        EfsElementDTO first = elements.iterator().next();
        VehiclePartListDTO vehiclePartList = getVehiclePartList();
        if (vehiclePartList == null || !vehiclePartList.getId().equals(first.getVehiclePartListId())) {
            return;
        }

        updateInspectorItemCount(getVehicleConfig());
        EfsElementDTO rootElement = null;
        for (EfsElementDTO element : elements) {
            if (element.getParentId() == null) {
                rootElement = element;
            }

            displayStrategy.updateNode(getEfsElementTreeModel(), element);
            if (element.isDeleted()) {
                clearCopyCut();
                efsTreeTableView.getSelectionModel().clearSelection();
                resetNavigation();
            }

            efsTreeTableView.requestFocus();
            refreshTreeItemInCrumbBar();
        }

        if (rootElement != null) {
            EventBus.getInstance()
                    .post(new FzgStuecklisteGewichtEvent(getVehiclePartList().getId(), rootElement.getNodeWeight()));
        }
    }

    public List<EfsElementDTO> searchEfsElements(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception e) {
            handleException(e);
        }

        Collection<EfsElementTreeItem> treeItems = getEfsElementTreeModel().getTreeItems();
        List<EfsElementDTO> efsElements = new ArrayList<>();
        for (EfsElementTreeItem efsElementTreeItem : treeItems) {
            EfsElementDTO efsElement = efsElementTreeItem.getUserObject();
            if (matchEfsElement(efsElementTreeItem)) {
                efsElements.add(efsElement);
            }
        }

        return efsElements;
    }

    public EfsElementTreeModel getEfsElementTreeModel() {
        if (efsElementTreeModel == null) {
            EfsElementDTO efs = PartListFactory.createEfsElement();
            EfsElementMaraDTO efsElementMara = PartListFactory.createEfsElementMara();
            efs.setEfsElementMara(efsElementMara);

            efsElementTreeModel = new EfsElementTreeModel(efs);
        }

        return efsElementTreeModel;
    }

    public EfsElementTreeItem getMotor() {
        return getEfsElementTreeModel().getMotor();
    }

    public EfsElementTreeItem getGetriebe() {
        return getEfsElementTreeModel().getGetriebe();
    }

    public Double getWeight() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        return vehicleConfig == null || vehicleConfig.getVehiclePartList() == null ? 0.0 :
                vehicleConfig.getVehiclePartList().getWeight();
    }

    public BooleanPropertyAdapter disablePropertyNewEfsElement() {
        if (disablePropertyNewEfsElement == null) {
            boolean isEditable = getVehicleConfig().isEditAllowed();
            disablePropertyNewEfsElement = new BooleanPropertyAdapter(new SimpleBooleanProperty(!isEditable));
        }

        return disablePropertyNewEfsElement;
    }

    public BooleanPropertyAdapter disablePropertyDeleteEfsElemente() {
        if (disablePropertyDeleteEfsElemente == null) {
            boolean isEditable = getVehicleConfig().isEditAllowed();
            disablePropertyDeleteEfsElemente = new BooleanPropertyAdapter(new SimpleBooleanProperty(!isEditable));
        }

        return disablePropertyDeleteEfsElemente;
    }

    public BooleanPropertyAdapter disablePropertyCopyEfsElemente() {
        if (disablePropertyCopyEfsElemente == null) {
            boolean isEditable = getVehicleConfig().isEditAllowed();
            disablePropertyCopyEfsElemente = new BooleanPropertyAdapter(new SimpleBooleanProperty(!isEditable));
        }

        return disablePropertyCopyEfsElemente;
    }

    public BooleanPropertyAdapter disablePropertyCutEfsElemente() {
        if (disablePropertyCutEfsElemente == null) {
            boolean isEditable = getVehicleConfig().isEditAllowed();
            disablePropertyCutEfsElemente = new BooleanPropertyAdapter(new SimpleBooleanProperty(!isEditable));
        }

        return disablePropertyCutEfsElemente;
    }

    public BooleanPropertyAdapter disablePropertyPasteEfsElemente() {
        if (disablePropertyPasteEfsElemente == null) {
            boolean isEditable = getVehicleConfig().isEditAllowed();
            disablePropertyPasteEfsElemente = new BooleanPropertyAdapter(new SimpleBooleanProperty(!isEditable));
        }

        return disablePropertyPasteEfsElemente;
    }

    public BooleanProperty disablePropertyCollapseTree() {
        if (disablePropertyCollapseTree == null) {
            disablePropertyCollapseTree = new SimpleBooleanProperty(false);
        }
        return disablePropertyCollapseTree;
    }

    public BooleanProperty disablePropertyCollapseAllTree() {
        if (disablePropertyCollapseAllTree == null) {
            disablePropertyCollapseAllTree = new SimpleBooleanProperty(false);
        }
        return disablePropertyCollapseAllTree;
    }

    public BooleanProperty disablePropertyExpandTree() {
        if (disablePropertyExpandTree == null) {
            disablePropertyExpandTree = new SimpleBooleanProperty(false);
        }
        return disablePropertyExpandTree;
    }

    public BooleanProperty disablePropertyExpandAllTree() {
        if (disablePropertyExpandAllTree == null) {
            disablePropertyExpandAllTree = new SimpleBooleanProperty(false);
        }
        return disablePropertyExpandAllTree;
    }

    public BooleanProperty disablePropertyShowHistory() {
        if (disablePropertyShowHistory == null) {
            disablePropertyShowHistory = new SimpleBooleanProperty(false);
        }

        return disablePropertyShowHistory;
    }

    public BooleanProperty disablePropertyShowRevision() {
        if (disablePropertyShowRevision == null) {
            disablePropertyShowRevision = new SimpleBooleanProperty(false);
        }

        return disablePropertyShowRevision;
    }

    public BooleanProperty toggleChangeViewProperty() {
        if (togglePropertyChangeView == null) {
            togglePropertyChangeView = new SimpleBooleanProperty(false);

            togglePropertyChangeView.addListener((obs, oldVal, newVal) -> handleChangeViewDisplay(newVal));
        }

        return togglePropertyChangeView;
    }

    public BooleanProperty disablePropertyShowChanges() {
        if (disablePropertyShowChanges == null) {
            disablePropertyShowChanges = new SimpleBooleanProperty(true);
        }

        return disablePropertyShowChanges;
    }

    public BooleanProperty disablePropertyShowPartProperties() {
        if (disablePropertyShowPartProperties == null) {
            disablePropertyShowPartProperties = new SimpleBooleanProperty(true);
        }

        return disablePropertyShowPartProperties;
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

    public void scrollToFirstColumn() {
        efsTreeTableView.scrollToColumnIndex(0);
    }

    public void scrollToLastColumn() {
        int size = efsTreeTableView.getColumns().size();
        efsTreeTableView.scrollToColumnIndex(size - 1);
    }

    @Override
    public void selectElementById(Long efsElementId) {
        EfsElementTreeItem treeItem = getEfsElementTreeModel().getTreeItem(efsElementId);
        if (treeItem == null) {
            return;
        }

        efsTreeTableView.getSelectionModel().clearSelection();

        TreeItem<EfsElementDTO> parent = treeItem.getParent();
        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        int row = getRow(treeItem);

        efsTreeTableView.scrollToCenter(row);
        efsTreeTableView.getSelectionModel().select(row, colPartNumber);

        efsTreeTableView.requestFocus();
    }

    /**
     * TreeTableView.getRow() is not working correctly. Use this one instead. TreeItem has to be visible for this to work.
     */
    private int getRow(TreeItem<EfsElementDTO> item) {
        if (item == efsElementTreeModel.getRoot()) {
            return 0;
        }

        int row = 0;
        while (item != efsElementTreeModel.getRoot()) {
            TreeItem<EfsElementDTO> parent = item.getParent();
            List<TreeItem<EfsElementDTO>> siblings = parent.getChildren();
            int posOfItem = siblings.indexOf(item);
            row++;
            for (int i = 0; i < posOfItem; i++) {
                row++;
                row += getExpandedCount(siblings.get(i));
            }

            item = item.getParent();
        }

        return efsTreeTableView.showRootProperty().get() ? row : row - 1;
    }

    private int getExpandedCount(TreeItem<?> item) {
        int count = 0;
        if (item.isExpanded()) {
            for (TreeItem<?> child : item.getChildren()) {
                count++; // add one for child
                count += getExpandedCount(child); // add expanded items
            }
        }

        return count;
    }

    private void useDisplayMode(DisplayMode displayMode) {
        if (DisplayMode.TREE == displayMode) {
            changeCollapseExpandDisableProperty(false);

            if (toggleChangeViewProperty().get()) {
                setDisplayStrategy(HierarchicalDisplayStrategy.getStrategyWithDeletion(
                        viewModeEfsProperty.get().equals(PartListViewMode.VEHICLE_ALL)));
                loadVehiclePartListRevisionen();
            } else {
                setDisplayStrategy(HierarchicalDisplayStrategy.getStrategyWithoutDeletion(
                        viewModeEfsProperty.get().equals(PartListViewMode.VEHICLE_ALL)));
            }
        } else if (DisplayMode.LIST == displayMode) {
            changeCollapseExpandDisableProperty(true);

            if (toggleChangeViewProperty().get()) {
                setDisplayStrategy(FlatDisplayStrategy.getStrategyWithDeletion(getColumnToFilterMap().values()));
                loadVehiclePartListRevisionen();
            } else {
                setDisplayStrategy(FlatDisplayStrategy.getStrategyWithoutDeletion(getColumnToFilterMap().values()));
            }
        } else if (DisplayMode.GROUP == displayMode) {
            EfsElementDTO engine = null;
            if (motor != null) {
                engine = motor.getUserObject();
            }

            EfsElementDTO gearboxE = null;
            if (gearbox != null) {
                gearboxE = gearbox.getUserObject();
            }

            if (toggleChangeViewProperty().get()) {
                setDisplayStrategy(PartListGroupDisplayStrategy.getStrategyWithDeletion(getColumnToFilterMap().values(),
                        viewModeEfsProperty.get(), engine, gearboxE));

                loadVehiclePartListRevisionen();
            } else {
                setDisplayStrategy(
                        PartListGroupDisplayStrategy.getStrategyWithoutDeletion(getColumnToFilterMap().values(),
                                viewModeEfsProperty.get(), engine, gearboxE));
            }
        }

        reapplyFiltersOnTreeTableView();
    }

    private void moveEfsElements(ServiceController<List<EfsElementDTO>> serviceController) {
        List<EfsElementDTO> movedEfsElements = serviceController.getValue();

        EfsElementResolver.registerElements(movedEfsElements);
        EventBus.getInstance().post(new VehicleConfigChangedEvent(getVehicleConfig()));

        handleEfsElementeMoved(movedEfsElements);
    }

    @Subscribe
    private void selectEfsElementExtern(SelectEfsElementOnEfsTabEvent event) {
        if (!event.isClearFilter()) {
            selectElementById(event.getEfsElementId());
            return;
        }

        selectTabAction.run();
        handleActionClearFilters();
        selectElementById(event.getEfsElementId());
    }

    public void setActionSelectTab(Runnable action) {
        selectTabAction = action;
    }

    public void setSetKeys(Collection<String> setKeys) {
        this.setKeys.setAll(setKeys);
    }

    public void setCostGroups(Collection<String> costGroups) {
        this.costGroups.setAll(costGroups);
    }

    @Override
    public void handle(SameMaraInHierachyException exception) {
        String messageKey = exception.getMessageKey();
        DialogUtil.showWarnDialog(
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TITLE_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.HEADER_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TEXT_LOW_CASE));
    }

    // FIXME getSelectedTreeItems() should be handled another way
    @Override
    public void deleteEfsElement(List<EfsElementDTO> efsElements) {
        ServiceController<List<EfsElementDTO>> serviceController = new ServiceController<>();
        serviceController.start(() -> new EfsEditLoadAdapter().deleteEfsElements(efsElements));
        serviceController.setOnSucceeded(event -> {
            Collection<EfsElementDTO> deletedElements = serviceController.getValue();

            EfsElementResolver.registerElements(deletedElements);

            EventBus.getInstance().post(new VehicleConfigChangedEvent(getVehicleConfig()));
            removeTreeItems(getSelectedTreeItems());

            showInfoCountDeleted(deletedElements.size());
            updateAllHeader();
            fireHistoryUpdateEvent(efsElements.getFirst());

            Collection<Long> ids = deletedElements.stream().map(EfsElementDTO::getVehiclePartListId)
                    .collect(Collectors.toSet());
            for (Long id : ids) {
                refreshVehiclePartListWeight(id, getVehicleConfig());
            }

            efsTreeTableView.refresh();
        });

        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        // EXEC_TIME_DELETE_EFS -> Anzahl EfsElemente beachten
        serviceController.setExecutionTime(EXEC_TIME_DELETE_EFS);
    }

    // Todo ZsN - Aggregated view - Delete
    public final void setEfsSelectionAction(
            EventHandler<HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>>> handler) {
        efsSelectionProperty().set(handler);
    }

    public EfsElementDTO getSelectedEfsElement() {
        TreeItem<EfsElementDTO> selectedItem = efsTreeTableView.getSelectionModel().getSelectedItem();
        return selectedItem == null ? null : selectedItem.getValue();
    }

    @Override
    protected void setActionStates() {
        super.setActionStates();

        EfsEditValidations efsEditValidations = new EfsEditValidations(EfsElementResolver::getElement,
                EfsElementResolver::getAllElementsInHierarchy);
        List<EfsElementDTO> efsElements = getSelectedEfsElements();

        disablePropertyNewEfsElement().run(() -> displayStrategy.getParentForCreatingNewElement(efsElements));
        disablePropertyDeleteEfsElemente().run(() -> efsEditValidations.deleteEfsElement(efsElements));

        if (displayStrategy.allowsCopy()) {
            disablePropertyCopyEfsElemente().run(() -> efsEditValidations.prepareCopyEfsElements(efsElements));
        } else {
            disablePropertyCopyEfsElemente.set(true);
        }

        if (displayStrategy.allowsMove()) {
            disablePropertyCutEfsElemente().run(() -> efsEditValidations.prepareCopyEfsElements(efsElements));
        } else {
            disablePropertyCutEfsElemente.set(true);
        }

        if (displayStrategy.allowsCopy() || displayStrategy.allowsMove()) {
            setPasteDisableProperty();
        } else {
            disablePropertyPasteEfsElemente().set(true);
        }
    }

    @Override
    protected void stop() {
        super.stop();

        saveConfigOnClose();

        if (treeItemsToCopy != null) {
            treeItemsToCopy.clear();
        }

        if (treeItemsToCut != null) {
            treeItemsToCut.clear();
        }

        getEfsElementTreeModel().removeAllElements();

        EfsElementResolver.removeListener(this);
        highlightManager.removeFromTable();

        for (EfsHeaderFahrzeugController efsHeaderFahrzeugController : mapHeaderController.values()) {
            efsHeaderFahrzeugController.stop();
        }
    }

    public void saveConfigOnClose() {
        TableConfigDTO currentTableConfigDTO = getTreeTableView().getColumnConfig();
        if (currentTableConfigDTO == null) {
            return;
        }

        // columns in order, in case the user changes it
        List<String> currentColumnIds = getTreeTableView().getVisibleLeafColumns().stream().map(TableColumnBase::getId)
                .toList();
        List<String> currentColumnNames = getTreeTableView().getVisibleLeafColumns().stream()
                .map(TableColumnBase::getText).toList();
        // we only save this if the user is the owner of the config
        if (currentTableConfigDTO.getUserId().equals(UserProperties.getUserId())
                && !currentTableConfigDTO.getSelectedColumnIds().equals(currentColumnIds)) {
            currentTableConfigDTO.setSelectedColumnIds(currentColumnIds);
            currentTableConfigDTO.setSelectedColumns(currentColumnNames);

            ServiceController<List<TableConfigDTO>> service = saveOnExitServiceController();
            service.start(() -> TableConfigRestClientHolder.getInstance().getConfigurationsForUser().tableConfigDTOs());
        }
    }

    public void setTablePlaceholderPropertyText(boolean isLoading) {
        String placeholderKey = isLoading ? "message.laden.stueckliste" : "no.data";
        Label label = new Label(I18N.getString(placeholderKey));
        tablePlaceholderProperty.setValue(label);
    }

    public void populatePartListTable() {
        setDisplayStrategy(displayStrategy);

        motor = getEfsElementTreeModel().getMotor();
        gearbox = getEfsElementTreeModel().getGetriebe();

        /* we subtract the root element */
        int numberOfItems = TreeItemUtil.getChildTreeObjects(getTreeTableView().getRoot()).size() - 1;

        updateHeader(efsHeaderController);
        getEfsHeaderController().setPositions(numberOfItems);

        initSorting();
    }

    @Override
    protected void initTreeTable() {
        efsTreeTableView.showRootProperty().set(false);
        efsTreeTableView.setEditable(true);
        efsTreeTableView.placeholderProperty().bind(tablePlaceholderProperty);
        efsTreeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        efsTreeTableView.getSelectionModel().setCellSelectionEnabled(true);
        efsTreeTableView.getSelectionModel().selectedItemProperty()
                .addListener(this::treeTableSelectionChangedListener);
        efsTreeTableView.addFilterChangeListener(this::headerChangeEventListener);
        efsTreeTableView.makeHeaderWrappable();
        efsTreeTableView.makeFilterable();
        efsTreeTableView.setTableMenuButtonVisible(true);

        efsTreeTableView.getVisibleLeafColumns()
                .addListener((ListChangeListener<TreeTableColumn<EfsElementDTO, ?>>) c -> {
                    if (isColumnChanging) {
                        return;
                    }

                    isColumnChanging = true;
                    List<String> columns = efsTreeTableView.getVisibleLeafColumns().stream()
                            .map(TableColumnBase::getText).collect(Collectors.toList());
                    EventBus.getInstance().post(new VisibleColumnsChangedEvent(vehicleConfig.get(), columns));
                    notifyTableColumnChanged(
                            efsTreeTableView.getVisibleLeafColumns().stream().map(TableColumnBase::getText).toList());
                    isColumnChanging = false;
                });

        colSequenceChangeListener = change -> {
            if (change.next() && change.wasRemoved() && !change.getRemoved().equals(change.getList())) {
                Collection<TreeTableColumn<EfsElementDTO, ?>> columns = new ArrayList<>(efsTreeTableView.getColumns());
                columns.removeAll(List.of(colWeightNode, colCog));

                List<String> texts = columns.stream().map(TableColumnBase::getText).toList();
                EventBus.getInstance().post(new ColumnSequenceChangeEvent(getVehicleConfig(), texts, getClass()));
            }
        };

        efsTreeTableView.getColumns().addListener(colSequenceChangeListener);

        efsTreeTableView.setOnMouseClicked(this::handleActionMouseClicked);

        addDisabledEditKeys();
    }

    @Override
    protected void initTreeTableColumns() {
        initColumn(colPartNumber, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPartNumber(),
                EfsCellFactory.forColumnPartNumber(), AbstractEfsElementDTO::getPartNumber,
                EfsElementTreeItemPropertyNames.PART_NUMBER);
        colPartNumber.setConverter(new SeparatedPartNumberStringConverter());
        initColumn(colPartNumberVornummer,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPartNumberVornummer(),
                EfsCellFactory.forStringColumn(3, EfsElementTreeItemPropertyNames.PART_NUMBER_VORNUMMER),
                node -> node.getEfsElementMara().getPartNumberVornummer(),
                EfsElementTreeItemPropertyNames.PART_NUMBER_VORNUMMER);
        initColumn(colPartNumberMittelgruppe,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPartNumberMittelgruppe(),
                EfsCellFactory.forStringColumn(3, EfsElementTreeItemPropertyNames.PART_NUMBER_MITTELGRUPPE),
                node -> node.getEfsElementMara().getPartNumberMittelgruppe(),
                EfsElementTreeItemPropertyNames.PART_NUMBER_MITTELGRUPPE);
        initColumn(colPartNumberEndNumber,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPartNumberEndNumber(),
                EfsCellFactory.forStringColumn(3, EfsElementTreeItemPropertyNames.PART_NUMBER_END_NUMBER),
                node -> node.getEfsElementMara().getPartNumberEndNumber(),
                EfsElementTreeItemPropertyNames.PART_NUMBER_END_NUMBER);
        initColumn(colPartNumberIndex, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPartNumberIndex(),
                EfsCellFactory.forStringColumn(2, EfsElementTreeItemPropertyNames.PART_NUMBER_INDEX),
                node -> node.getEfsElementMara().getPartNumberIndex(),
                EfsElementTreeItemPropertyNames.PART_NUMBER_INDEX);
        initColumn(colDescription1, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDescription1(),
                EfsCellFactory.forColumnDescription1(), AbstractEfsElementDTO::getDescription1,
                EfsElementTreeItemPropertyNames.DESCRIPTION1);
        initColumn(colDescription2, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDescription2(),
                EfsCellFactory.forColumnDescription2(), AbstractEfsElementDTO::getDescription2,
                EfsElementTreeItemPropertyNames.DESCRIPTION2);

        initColumn(colBomNumber, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBomNumber(),
                EfsCellFactory.forColumnSet(), AbstractEfsElementDTO::getBomNumber,
                EfsElementTreeItemPropertyNames.BOM_NUMBER);
        initColumn(colProduct, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyProduct(),
                EfsCellFactory.forStringColumn(3, EfsElementTreeItemPropertyNames.PRODUCT),
                AbstractEfsElementDTO::getProduct, EfsElementTreeItemPropertyNames.PRODUCT);
        initColumn(colPartType, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPartType(),
                EfsCellFactory.forStringColumn(24, EfsElementTreeItemPropertyNames.PART_TYPE),
                AbstractEfsElementDTO::getPartType, EfsElementTreeItemPropertyNames.PART_TYPE);

        initColumn(colNodeLevel, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyNodeLevel(),
                EfsCellFactory.forColumnSet(), AbstractEfsElementDTO::getNodeLevel,
                EfsElementTreeItemPropertyNames.NODE_LEVEL);
        initColumn(colNodeType, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyNodeType(),
                EfsCellFactory.forStringColumn(8, EfsElementTreeItemPropertyNames.NODE_TYPE),
                AbstractEfsElementDTO::getNodeType, EfsElementTreeItemPropertyNames.NODE_TYPE);
        initColumn(colNodeLabel, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyNodeLabel(),
                EfsCellFactory.forStringColumn(60, EfsElementTreeItemPropertyNames.NODE_LABEL),
                AbstractEfsElementDTO::getNodeLabel, EfsElementTreeItemPropertyNames.NODE_LABEL);

        initColumn(colNodeValueParent, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyNodeValueParent(),
                EfsCellFactory.forStringColumn(40, EfsElementTreeItemPropertyNames.NODE_VALUE_PARENT),
                AbstractEfsElementDTO::getNodeValueParent, EfsElementTreeItemPropertyNames.NODE_VALUE_PARENT);

        initColumn(colNodeValue, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyNodeValue(),
                EfsCellFactory.forStringColumn(40, EfsElementTreeItemPropertyNames.NODE_VALUE),
                AbstractEfsElementDTO::getNodeValue, EfsElementTreeItemPropertyNames.NODE_VALUE);

        initColumn(colAp, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyAp(),
                EfsCellFactory.forColumnAp(), AbstractEfsElementDTO::getAp, EfsElementTreeItemPropertyNames.AP);
        initColumn(colSetKey, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertySetKey(),
                EfsCellFactory.forColumnSetKey(setKeys), AbstractEfsElementDTO::getSetKey,
                EfsElementTreeItemPropertyNames.SET_KEY);
        initColumn(colCostGroup, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyCostGroup(),
                EfsCellFactory.forColumnCostGroup(costGroups), AbstractEfsElementDTO::getCostGroup,
                EfsElementTreeItemPropertyNames.COST_GROUP);
        initColumn(colConstructionsGroup,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyConstructionsGroup(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.CONSTRUCTIONS_GROUP),
                AbstractEfsElementDTO::getConstructionsGroup, EfsElementTreeItemPropertyNames.CONSTRUCTIONS_GROUP);
        initColumn(colProductStructure,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyProductStructure(),
                EfsCellFactory.forStringColumn(3, EfsElementTreeItemPropertyNames.PRODUCT_STRUCTURE),
                AbstractEfsElementDTO::getProductStructure, EfsElementTreeItemPropertyNames.PRODUCT_STRUCTURE);
        initColumn(colPositionVariant,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPositionVariantProperty(),
                EfsCellFactory.forStringColumn(8, EfsElementTreeItemPropertyNames.POSITION_VARIANT),
                AbstractEfsElementDTO::getPositionVariant, EfsElementTreeItemPropertyNames.POSITION_VARIANT);
        initColumn(colDeletionFlag, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDeletionFlag(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.DELETION_FLAG),
                AbstractEfsElementDTO::getDeletionFlag, EfsElementTreeItemPropertyNames.DELETION_FLAG);

        initColumn(colQuantity, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyQuantity(),
                EfsCellFactory.forColumnSet(), AbstractEfsElementDTO::getQuantity,
                EfsElementTreeItemPropertyNames.QUANTITY);
        initColumn(colQuantityUnit, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyQuantityUnit(),
                EfsCellFactory.forColumnQuantityUnit(), AbstractEfsElementDTO::getQuantityUnit,
                EfsElementTreeItemPropertyNames.QUANTITY_UNIT);
        initColumn(colQuantityUnitExtended,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyQuantityUnitExtended(),
                EfsCellFactory.forStringColumn(3, EfsElementTreeItemPropertyNames.QUANTITY_UNIT_EXTENDED),
                AbstractEfsElementDTO::getQuantityUnitExtended, EfsElementTreeItemPropertyNames.QUANTITY_UNIT_EXTENDED);
        initColumn(colWeightControlFlag,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightControlFlag(),
                EfsCellFactory.forColumnWeightCode(), AbstractEfsElementDTO::getWeightControlFlag,
                EfsElementTreeItemPropertyNames.WEIGHT_CONTROL_FLAG);
        initColumn(colWeightNode, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightNode(),
                EfsCellFactory.forColumnReadOnly(EfsElementTreeItemPropertyNames.WEIGHT_NODE, false),
                EfsElementDTO::getNodeWeight, EfsElementTreeItemPropertyNames.WEIGHT_NODE);
        initColumn(colWeightAll, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightAll(),
                EfsCellFactory.forColumnReadOnly(EfsElementTreeItemPropertyNames.WEIGHT_ALL, false),
                EfsElementDTO::getWeight, EfsElementTreeItemPropertyNames.WEIGHT_ALL);
        initColumn(colWeightPrio, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightPrio(),
                EfsCellFactory.forColumnWeightPrioES(EfsElementTreeItemPropertyNames.WEIGHT_PRIO),
                node -> node.getEfsElementMara().getPrioritizedWeight(), EfsElementTreeItemPropertyNames.WEIGHT_PRIO);
        initColumn(colWeightWeightedTe,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightWeightedTe(),
                EfsCellFactory.forColumnWeightES(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE),
                node -> node.getEfsElementMara().getWeightWeightedTe(),
                EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE);
        initColumn(colWeightWeightedTeDate,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightWeightedTeDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE_DATE),
                node -> node.getEfsElementMara().getWeightWeightedTeDate(),
                EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE_DATE);
        initColumn(colWeightCalculatedTe,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightCalculatedTe(),
                EfsCellFactory.forColumnWeightES(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE),
                node -> node.getEfsElementMara().getWeightCalculatedTe(),
                EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE);
        initColumn(colWeightCalculatedTeDate,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightCalculatedTeDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE_DATE),
                node -> node.getEfsElementMara().getWeightCalculatedTeDate(),
                EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE_DATE);
        initColumn(colWeightEstimatedTe,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightEstimatedTe(),
                EfsCellFactory.forColumnWeightES(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE),
                node -> node.getEfsElementMara().getWeightEstimatedTe(),
                EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE);
        initColumn(colWeightEstimatedTeDate,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightEstimatedTeDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE_DATE),
                node -> node.getEfsElementMara().getWeightEstimatedTeDate(),
                EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE_DATE);
        initColumn(colWeightWeightedProd,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightWeightedProd(),
                EfsCellFactory.forColumnWeightES(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD),
                node -> node.getEfsElementMara().getWeightWeightedProd(),
                EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD);
        initColumn(colWeightWeightedProdDate,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightWeightedProdDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD_DATE),
                node -> node.getEfsElementMara().getWeightWeightedProdDate(),
                EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD_DATE);
        initColumn(colBeginDateKey, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBeginDateKey(),
                EfsCellFactory.forColumnStartEndKey(11, EfsElementTreeItemPropertyNames.BEGIN_DATE_KEY),
                AbstractEfsElementDTO::getBeginDateKey, EfsElementTreeItemPropertyNames.BEGIN_DATE_KEY);
        initColumn(colDrawingDate, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDrawingDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.DRAWING_DATE),
                node -> node.getEfsElementMara().getDrawingDate(), EfsElementTreeItemPropertyNames.DRAWING_DATE);
        initColumn(colDrawingStatus, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDrawingStatus(),
                EfsCellFactory.forStringColumn(2, EfsElementTreeItemPropertyNames.DRAWING_STATUS),
                node -> node.getEfsElementMara().getDrawingStatus(), EfsElementTreeItemPropertyNames.DRAWING_STATUS);
        initColumn(colBeginDate, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBeginDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.BEGIN_DATE),
                AbstractEfsElementDTO::getBeginDate, EfsElementTreeItemPropertyNames.BEGIN_DATE);
        initColumn(colEndDateKey, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyEndDateKey(),
                EfsCellFactory.forColumnStartEndKey(11, EfsElementTreeItemPropertyNames.END_DATE_KEY),
                AbstractEfsElementDTO::getEndDateKey, EfsElementTreeItemPropertyNames.END_DATE_KEY);
        initColumn(colEndDate, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyEndDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.END_DATE),
                AbstractEfsElementDTO::getEndDate, EfsElementTreeItemPropertyNames.END_DATE);

        initColumn(colAssemblyIndicator,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyAssemblyIndicator(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.ASSEMBLY_INDICATOR),
                node -> node.getEfsElementMara().getAssemblyIndicator(),
                EfsElementTreeItemPropertyNames.ASSEMBLY_INDICATOR);
        initColumn(colConstructionsState,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyConstructionsState(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.CONSTRUCTIONS_STATE),
                node -> node.getEfsElementMara().getConstructionsState(),
                EfsElementTreeItemPropertyNames.CONSTRUCTIONS_STATE);

        initColumn(colQuality, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyQuality(),
                EfsCellFactory.forStringColumn(40, EfsElementTreeItemPropertyNames.QUALITY),
                node -> node.getEfsElementMara().getQuality(), EfsElementTreeItemPropertyNames.QUALITY);

        initColumn(colMatThickness, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyMaterialThickness(),
                EfsCellFactory.forColumnReadOnly(EfsElementTreeItemPropertyNames.MATERIAL_THICKNESS),
                node -> node.getEfsElementMara().getMaterialThickness(),
                EfsElementTreeItemPropertyNames.MATERIAL_THICKNESS);

        initColumn(colSeeDrawing, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertySeeDrawing(),
                EfsCellFactory.forStringColumn(12, EfsElementTreeItemPropertyNames.SEE_DRAWING),
                node -> node.getEfsElementMara().getSeeDrawing(), EfsElementTreeItemPropertyNames.SEE_DRAWING);

        initColumn(colRespConstr1, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyRespConstr1(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.RESPONSIBLE_CONSTR_1),
                node -> node.getEfsElementMara().getResponsibleConstr1(),
                EfsElementTreeItemPropertyNames.RESPONSIBLE_CONSTR_1);

        initColumn(colRespConstr2, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyRespConstr2(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.RESPONSIBLE_CONSTR_2),
                node -> node.getEfsElementMara().getResponsibleConstr2(),
                EfsElementTreeItemPropertyNames.RESPONSIBLE_CONSTR_2);

        initColumn(colBuildSampleApproval,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBuildSampleApproval(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.BUILD_SAMPLE_APPROVAL),
                node -> node.getEfsElementMara().getBuildSampleApproval(),
                EfsElementTreeItemPropertyNames.BUILD_SAMPLE_APPROVAL);

        initColumn(colBuildSampleApprovalDate,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBuildSampleApprovalDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.BUILD_SAMPLE_APPROVAL_DATE),
                node -> node.getEfsElementMara().getBuildSampleApprovalTargetDate(),
                EfsElementTreeItemPropertyNames.BUILD_SAMPLE_APPROVAL_DATE);

        initColumn(colTechnicallyOkay, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyTechnicallyOkay(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.TECHNICALLY_OKAY),
                node -> node.getEfsElementMara().getTechnicallyOkay(),
                EfsElementTreeItemPropertyNames.TECHNICALLY_OKAY);

        initColumn(colRelDateSoll, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyRelDateSoll(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.RELEASE_DATE_SOLL),
                node -> node.getEfsElementMara().getReleaseDateSoll(),
                EfsElementTreeItemPropertyNames.RELEASE_DATE_SOLL);

        initColumn(colDesignerName, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDesignerName(),
                EfsCellFactory.forStringColumn(20, EfsElementTreeItemPropertyNames.DESIGNER_NAME),
                node -> node.getEfsElementMara().getDesignerName(), EfsElementTreeItemPropertyNames.DESIGNER_NAME);
        initColumn(colDesignerCostGroup,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDesignerCostGroup(),
                EfsCellFactory.forStringColumn(5, EfsElementTreeItemPropertyNames.DESIGNER_COST_GROUP),
                node -> node.getEfsElementMara().getDesignerCostGroup(),
                EfsElementTreeItemPropertyNames.DESIGNER_COST_GROUP);
        initColumn(colDesignerPhone,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDesignerPhoneNumber(),
                EfsCellFactory.forStringColumn(15, EfsElementTreeItemPropertyNames.DESIGNER_PHONE_NUMBER),
                node -> node.getEfsElementMara().getDesignerPhoneNumber(),
                EfsElementTreeItemPropertyNames.DESIGNER_PHONE_NUMBER);

        initColumn(colKStandRelDate, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyKStandRelDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.K_STAND_RELEASE_DATE),
                node -> node.getEfsElementMara().getKStandReleaseDate(),
                EfsElementTreeItemPropertyNames.K_STAND_RELEASE_DATE);
        initColumn(colTioFreiRelDate, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyTioFreiRelDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.TIO_FREI_RELEASE_DATE),
                node -> node.getEfsElementMara().getTioFreiReleaseDate(),
                EfsElementTreeItemPropertyNames.TIO_FREI_RELEASE_DATE);

        initColumn(colMFPStatus, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyMfpStatus(),
                EfsCellFactory.forStringColumn(4, EfsElementTreeItemPropertyNames.MFP_STATUS),
                node -> node.getEfsElementMara().getMfpStatus(), EfsElementTreeItemPropertyNames.MFP_STATUS);
        initColumn(colMFPThickness, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyMfpThickness(),
                EfsCellFactory.forColumnReadOnly(EfsElementTreeItemPropertyNames.MFP_THICKNESS),
                node -> node.getEfsElementMara().getMfpThickness(), EfsElementTreeItemPropertyNames.MFP_THICKNESS);

        initColumn(colKseKz, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyKseKz(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.KSE_KZ),
                node -> node.getEfsElementMara().getKseKz(), EfsElementTreeItemPropertyNames.KSE_KZ);
        initColumn(colWeightAcceptedFromEpis,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWeightAcceptedFromEpis(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.WEIGHT_ACCEPTED_FROM_EPIS),
                node -> node.getEfsElementMara().getWeightAcceptedFromEPIS(),
                EfsElementTreeItemPropertyNames.WEIGHT_ACCEPTED_FROM_EPIS);

        initColumn(colBaukastenFlag, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBaukastenFlag(),
                EfsCellFactory.forColumnSet(), AbstractEfsElementDTO::getBaukasten,
                EfsElementTreeItemPropertyNames.BAUKASTEN_FLAG);
        initColumn(colBaukastenStatus, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBaukastenStatus(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.BAUKASTEN_STATUS),
                AbstractEfsElementDTO::getBaukastenStatus, EfsElementTreeItemPropertyNames.BAUKASTEN_STATUS);
        initColumn(colBaukastenNodeId, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyBaukastenNodeId(),
                EfsCellFactory.forStringColumn(32, EfsElementTreeItemPropertyNames.BAUKASTEN_NODE_ID),
                AbstractEfsElementDTO::getBaukastenNodeId, EfsElementTreeItemPropertyNames.BAUKASTEN_NODE_ID);
        initColumn(colProcessStatus, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyProcessStatus(),
                EfsCellFactory.forStringColumn(1, EfsElementTreeItemPropertyNames.PROCESS_STATUS),
                AbstractEfsElementDTO::getProcessStatus, EfsElementTreeItemPropertyNames.PROCESS_STATUS);
        initColumn(colDmuRelevant, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyDmuRelevant(),
                EfsCellFactory.forStringColumn(2, EfsElementTreeItemPropertyNames.DMU_RELEVANT),
                AbstractEfsElementDTO::getDmuRelevant, EfsElementTreeItemPropertyNames.DMU_RELEVANT);
        initColumn(colMaterialType, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyMaterialType(),
                EfsCellFactory.forStringColumn(4, EfsElementTreeItemPropertyNames.MATERIAL_TYPE),
                AbstractEfsElementDTO::getMaterialType, EfsElementTreeItemPropertyNames.MATERIAL_TYPE);

        initColumn(colEarliestPvs, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyEarliestPvs(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.EARLIEST_PVS),
                AbstractEfsElementDTO::getEarliestPvs, EfsElementTreeItemPropertyNames.EARLIEST_PVS);
        initColumn(colEarliestNs, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyEarliestNs(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.EARLIEST_NS),
                AbstractEfsElementDTO::getEarliestNs, EfsElementTreeItemPropertyNames.EARLIEST_NS);
        initColumn(colEarliestSop, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyEarliestSop(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.EARLIEST_SOP),
                AbstractEfsElementDTO::getEarliestSop, EfsElementTreeItemPropertyNames.EARLIEST_SOP);

        initColumn(colPActivationDate, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPActivationDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.P_ACTIVATION_DATE),
                AbstractEfsElementDTO::getPActivationDate, EfsElementTreeItemPropertyNames.P_ACTIVATION_DATE);
        initColumn(colConstructureDate,
                cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyConstructureDate(),
                EfsCellFactory.forDateColumn(EfsElementTreeItemPropertyNames.KONSTRUCTURE_DATE),
                AbstractEfsElementDTO::getKonstructureDate, EfsElementTreeItemPropertyNames.KONSTRUCTURE_DATE);

        initColumn(colAvonStatus, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyAvonStatus(),
                EfsCellFactory.forStringColumn(4, EfsElementTreeItemPropertyNames.AVON_STATUS),
                AbstractEfsElementDTO::getAvonStatus, EfsElementTreeItemPropertyNames.AVON_STATUS);

        initColumn(colPrNumberRule, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyPrNumberRule(),
                EfsCellFactory.forColumnPrNumberRule(200, EfsElementTreeItemPropertyNames.PR_NUMBER_RULE),
                AbstractEfsElementDTO::getPrNumberRule, EfsElementTreeItemPropertyNames.PR_NUMBER_RULE);

        initColumn(colTisSort, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyTisSort(),
                EfsCellFactory.forColumnSort(), AbstractEfsElementDTO::getTisSort,
                EfsElementTreeItemPropertyNames.TIS_SORT);

        initColumn(colCog, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyCog(),
                EfsCellFactory.forColumnCog(), element -> {
                    if (element == null) {
                        return null;
                    }

                    return new CogCoordinates(element).toString();
                }, EfsElementTreeItemPropertyNames.COG);

        initColumn(colNodeId, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyNodeId(),
                EfsCellFactory.forStringColumnReadOnly(EfsElementTreeItemPropertyNames.NODE_ID),
                AbstractEfsElementDTO::getNodeId, EfsElementTreeItemPropertyNames.NODE_ID);

        initColumn(colWahlweiseFall, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWahlweiseFall(),
                EfsCellFactory.forStringColumnReadOnly(EfsElementTreeItemPropertyNames.WAHLWEISE_FALL),
                AbstractEfsElementDTO::getWahlweiseFall, EfsElementTreeItemPropertyNames.WAHLWEISE_FALL);

        initColumn(colWahlweiseNr, cellData -> ((EfsElementTreeItem) cellData.getValue()).propertyWahlweiseNr(),
                EfsCellFactory.forOptionalNr(), AbstractEfsElementDTO::getWahlweiseNr,
                EfsElementTreeItemPropertyNames.WAHLWEISE_NR);

        colNodeId.setEditable(false);
        colWahlweiseFall.setEditable(false);
        colWahlweiseNr.setEditable(false);
    }

    public void setDefaultVisibleColumns() {
        TableConfigDTO tableConfigDTO = TableConfigRestClientHolder.getInstance().getConfigurationsForUser()
                .tableConfigDTOs().stream()
                // we'll only provide the default table config
                .filter(TableConfigDTO::isDefault).findAny().orElse(null);

        isColumnChanging = true;

        if (tableConfigDTO != null) {
            ColumnSelectionResult columnSelection = new ColumnSelectionResult(tableConfigDTO.getSelectedColumnIds(),
                    tableConfigDTO.getSelectedColumns());

            TableColumnSelectionUtils.applyLayout(efsTreeTableView.getColumns(), columnSelection);
            return;
        }

        colPartNumberVornummer.setVisible(false);
        colPartNumberMittelgruppe.setVisible(false);
        colPartNumberEndNumber.setVisible(false);
        colPartNumberIndex.setVisible(false);
        colBomNumber.setVisible(false);
        colProduct.setVisible(false);
        colPartType.setVisible(false);
        colNodeLabel.setVisible(false);
        colNodeValueParent.setVisible(false);
        colNodeValue.setVisible(false);
        colNodeLevel.setVisible(false);
        colNodeType.setVisible(false);
        colWeightWeightedTeDate.setVisible(false);
        colWeightCalculatedTeDate.setVisible(false);
        colWeightEstimatedTeDate.setVisible(false);
        colWeightWeightedProdDate.setVisible(false);
        colAssemblyIndicator.setVisible(false);
        colConstructionsGroup.setVisible(false);
        colPositionVariant.setVisible(false);
        colDeletionFlag.setVisible(false);
        colQuality.setVisible(false);
        colMatThickness.setVisible(false);
        colSeeDrawing.setVisible(false);
        colRespConstr1.setVisible(false);
        colRespConstr2.setVisible(false);
        colBuildSampleApproval.setVisible(false);
        colBuildSampleApprovalDate.setVisible(false);
        colTechnicallyOkay.setVisible(false);
        colRelDateSoll.setVisible(false);
        colDesignerName.setVisible(false);
        colDesignerCostGroup.setVisible(false);
        colDesignerPhone.setVisible(false);
        colKStandRelDate.setVisible(false);
        colTioFreiRelDate.setVisible(false);
        colMFPStatus.setVisible(false);
        colMFPThickness.setVisible(false);
        colKseKz.setVisible(false);
        colWeightAcceptedFromEpis.setVisible(false);
        colBaukastenFlag.setVisible(false);
        colBaukastenStatus.setVisible(false);
        colBaukastenNodeId.setVisible(false);
        colDmuRelevant.setVisible(false);
        colProcessStatus.setVisible(false);
        colMaterialType.setVisible(false);
        colEarliestNs.setVisible(false);
        colEarliestPvs.setVisible(false);
        colEarliestSop.setVisible(false);
        colPActivationDate.setVisible(false);
        colConstructureDate.setVisible(false);
        colAvonStatus.setVisible(false);
        colConstructionsState.setVisible(false);
        colProductStructure.setVisible(false);
        colQuantityUnitExtended.setVisible(false);
        colWahlweiseFall.setVisible(false);
        colWahlweiseNr.setVisible(false);
        colNodeId.setVisible(false);
        isColumnChanging = false;
    }

    public void bindTableColumnHeader() {
        addBindedColumnHeaderListener(efsTreeTableView);
    }

    @Override
    protected <T> void initColumn(TreeTableColumn<EfsElementDTO, T> column,
            Callback<TreeTableColumn.CellDataFeatures<EfsElementDTO, T>, ObservableValue<T>> cellValueFactory,
            Callback<TreeTableColumn<EfsElementDTO, T>, TreeTableCell<EfsElementDTO, T>> cellFactory,
            Function<EfsElementDTO, Object> filterFunction, String columnIdentifier) {
        super.initColumn(column, cellValueFactory, cellFactory, filterFunction, columnIdentifier);

        column.setEditable(true);
        column.setOnEditCommit(this::handleEditCommit);
    }

    @Override
    protected void handleEfsSelected(TreeItem<EfsElementDTO> newValue) {
        super.handleEfsSelected(newValue);

        EfsElementDTO efsElement = newValue == null || newValue.getValue() == null ? null : newValue.getValue();
        efsSelectionProperty.get()
                .handle(new HistorieUpdateEvent<>(this, HistorieUpdateEvent.EFS_ELEMENT_SELECTED, efsElement));
    }

    @Override
    protected CustomTreeTableView<EfsElementDTO> getTreeTableView() {
        return efsTreeTableView;
    }

    @Override
    protected AbstractTreeModel<EfsElementTreeItem, EfsElementDTO> getTreeModel() {
        return getEfsElementTreeModel();
    }

    @Subscribe
    private void changePartListWeight(FzgStuecklisteGewichtEvent event) {
        Long vehiclePartListId = vehicleConfig.get().getVehiclePartList().getId();
        if (event.vehiclePartListId().equals(vehiclePartListId)) {
            getEfsHeaderController().setWeight(event.gewicht());
        }
    }

    @Subscribe
    private void postPartListLoadedEvent(PartListLoadedEvent event) {
        updateAllHeader();
        updateInspectorItemCount(event.getVehicleConfig());
    }

    @Subscribe
    private void onVisibleColumnsChangedEvent(VisibleColumnsChangedEvent event) {
        if (isColumnChanging) {
            return;
        }

        isColumnChanging = true;
        for (TreeTableColumn<EfsElementDTO, ?> e : efsTreeTableView.getColumns()) {
            e.setVisible(event.getColumns().contains(e.getText()));
        }

        isColumnChanging = false;
    }

    @Subscribe
    private void columnSequenceChanged(ColumnSequenceChangeEvent event) {
        if (event.getSenderClass().equals(getClass()) || event.getVehicleConfig() == null || !event.getVehicleConfig()
                .equals(getVehicleConfig())) {
            return;
        }

        ObservableList<TreeTableColumn<EfsElementDTO, ?>> columns = efsTreeTableView.getColumns();
        columns.removeListener(colSequenceChangeListener);

        List<TreeTableColumn<EfsElementDTO, ?>> tableColumns = new ArrayList<>(columns);
        tableColumns.remove(colWeightNode);
        tableColumns.remove(colCog);

        List<String> changedCols = event.getColumns();
        Collection<TreeTableColumn<EfsElementDTO, ?>> cols = new ArrayList<>();
        for (int index = 0; index < tableColumns.size(); index++) {
            if (!changedCols.get(index).equals(tableColumns.get(index).getText())) {
                cols.add(tableColumns.get(index));
            }
        }

        for (TreeTableColumn<EfsElementDTO, ?> col : cols) {
            columns.remove(col);

            int colIndex = changedCols.indexOf(col.getText());
            int cogIndex = columns.indexOf(colCog) - 1;
            int weightNodeIndex = columns.indexOf(colWeightNode) - 1;

            if (colIndex > weightNodeIndex) {
                colIndex += 1;
            } else if (colIndex > cogIndex) {
                colIndex += 2;
            }

            columns.add(colIndex, col);
        }

        columns.addListener(colSequenceChangeListener);
    }

    private ServiceController<List<TableConfigDTO>> saveOnExitServiceController() {
        ServiceController<List<TableConfigDTO>> service = new ServiceController<>();
        service.setOnSucceeded(event -> {
            TableConfigDTO currentTableConfigDTO = getTreeTableView().getColumnConfig();
            if (currentTableConfigDTO.getId() == null || currentTableConfigDTO.getName() == null) {
                return;
            }

            TableConfigDTO tableConfigDTO = new TableConfigDTO();
            tableConfigDTO.setId(currentTableConfigDTO.getId());
            tableConfigDTO.setUserId(UserProperties.getUserId());
            tableConfigDTO.setName(currentTableConfigDTO.getName());
            tableConfigDTO.setSelectedColumnIds(currentTableConfigDTO.getSelectedColumnIds());
            tableConfigDTO.setSelectedColumns(currentTableConfigDTO.getSelectedColumns());
            tableConfigDTO.setDefault(currentTableConfigDTO.isDefault());
            tableConfigDTO.setPublic(currentTableConfigDTO.isPublic());

            TableConfigRestClientHolder.getInstance().saveConfiguration(tableConfigDTO);
        });
        service.setOnFailed(event -> ExceptionHandler.instance().handleException(service.getException()));
        return service;
    }

    private void handleActionShowPartPropertiesView() {
        parentController.handleActionShowPartPropertiesView();
    }

    private void handleActionMouseClicked(MouseEvent event) {
        hideContextMenu();
        if (event.getPickResult() == null || !isRow(event.getPickResult().getIntersectedNode())) {
            return;
        }

        if (event.getButton().equals(MouseButton.SECONDARY) && event.getClickCount() == 1
                && efsTreeTableView.getSelectionModel().getSelectedItem() != null) {
            showContextMenu(efsTreeTableView, event.getScreenX(), event.getScreenY());
        }
    }

    private boolean isRow(Node node) {
        if (node == null) {
            return false;
        }

        Node parent = node;
        do {
            if (parent instanceof TreeTableCell || parent instanceof TreeTableRow) {
                return true;
            }

            parent = parent.getParent();
        } while (parent != null);

        return false;
    }

    private void showContextMenu(CustomTreeTableView<EfsElementDTO> efsTreeTableView, double screenX, double screenY) {
        contextMenu = new ContextMenu();
        MenuItem showPartProperties = new MenuItem(I18N.getString("show.partproperties.view"));
        showPartProperties.setOnAction(e -> handleActionShowPartPropertiesView());
        contextMenu.getItems().add(showPartProperties);

        MenuItem showInspector = new MenuItem(I18N.getString("show.inspector"));
        showInspector.setOnAction(e -> {
            updateInspectorItemCount(getVehicleConfig(), true);
            parentController.handleActionShowInInspector(getSelectedEfsElement());
        });

        contextMenu.getItems().add(showInspector);
        contextMenu.show(efsTreeTableView, screenX, screenY);
    }

    private void hideContextMenu() {
        if (contextMenu != null) {
            contextMenu.hide();
        }
    }

    private void initBreadCrumbBar() {
        selectTreeItemInCrumbBar(getEfsElementTreeModel().getRoot());
        efsTreeTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> selectTreeItemInCrumbBar(newValue));
        breadCrumbBar.setOnCrumbAction(this::handleCrumbAction);
    }

    private void handleCrumbAction(EfsElementDTO element) {
        try {
            isCrumbAction = true;
            selectElementById(element.getId());
            breadCrumbBar.setSelectedCrumb(element);
        } finally {
            isCrumbAction = false;
        }
    }

    private void selectTreeItemInCrumbBar(TreeItem<EfsElementDTO> newValue) {
        if (isCrumbAction) {
            return;
        }

        EfsElementDTO selectedElement =
                newValue == null ? getEfsElementTreeModel().getRoot().getUserObject() : getSelectedEfsElement();
        breadCrumbBar.setSelectedCrumb(selectedElement);
    }

    private void refreshTreeItemInCrumbBar() {
        breadCrumbBar.setSelectedCrumb(getEfsElementTreeModel().getRoot().getUserObject());
        breadCrumbBar.setSelectedCrumb(getSelectedEfsElement());
    }

    private <T> void handleEditCommit(CellEditEvent<EfsElementDTO, T> event) {
        if (!isValueChange(event)) {
            efsTreeTableView.requestFocus();

            return;
        }

        EfsElementTreeItem treeItem = (EfsElementTreeItem) event.getRowValue();

        // we have to work on the real one because we are editing via tablecell/treeItem and want to see the changes immediately
        EfsElementDTO efsElement = treeItem.getUserObject();
        EfsElementDTO oldState = EfsElementUtil.copyEfsElement(efsElement);

        TreeTableColumn<EfsElementDTO, T> column = event.getTableColumn();

        treeItem.setChange(column.getId(), toggleChangeViewProperty().get());

        if (column == colPartNumber) {
            handleEditCommitPartNumber(treeItem, event.getNewValue().toString());
        } else if (isColumnPartNumber(column)) {
            handleEditCommitPartNumberColumns(column, treeItem, event.getNewValue().toString());
        } else if (isColumnMara(column)) {
            handleEditCommitMara(column, treeItem, event.getNewValue());
        } else {
            handleEditCommitEfsElement(column, treeItem, event.getNewValue());
        }

        ServiceController<EfsElementDTO> serviceController = new ServiceController<>();
        serviceController.setOnFailed(e -> {
            onEfsElementUpdate(Collections.singleton(oldState));
            handleException(serviceController.getException());
        });
        serviceController.setExecutionTime(EXEC_TIME_SAVE_EFS);
        serviceController.start(() -> {
            EfsElementDTO copyToSave = EfsElementUtil.copyEfsElement(efsElement);
            // save will remove parent and children from the element for performance reasons
            // we need to save a copy so that the old one won't be changed
            return new EfsEditLoadAdapter().saveEfsElement(copyToSave);
        });
        serviceController.setOnSucceeded(e -> {
            EfsElementDTO savedEfsElement = serviceController.getValue();
            if (savedEfsElement.isMotor() || savedEfsElement.isGetriebe()) {
                updateHeader(efsHeaderController);
                updateMotorAndEngine();
            }

            registerAndRefresh(savedEfsElement, oldState);
        });
        serviceController.setOnFailed(stateEvent -> handleException(stateEvent.getSource().getException()));
    }

    private void registerAndRefresh(EfsElementDTO savedEfsElement, EfsElementDTO oldState) {
        EfsElementResolver.registerElements(Collections.singletonList(savedEfsElement));
        EventBus.getInstance().post(new VehicleConfigChangedEvent(getVehicleConfig()));

        refreshFgSetTree(savedEfsElement, oldState);
        refreshCostGroupTree(savedEfsElement, oldState);
        refreshPartGroupTree(savedEfsElement, oldState);
        refreshEfsTree(savedEfsElement);
    }

    private <T> boolean isValueChange(CellEditEvent<EfsElementDTO, T> event) {
        return (event.getNewValue() == null && event.getOldValue() != null) || (event.getNewValue() != null
                && !event.getNewValue().equals(event.getOldValue()));
    }

    private void handleEditCommitPartNumber(EfsElementTreeItem treeItem, String newPartNumber) {
        AbstractEfsElementMaraDTO oldMara = treeItem.getEfsElementMara();
        EfsElementMaraDTO newMara = PartListFactory.createEfsElementMara(oldMara.getDescription1De(),
                StringUtils.remove(newPartNumber, StringConstant.DOT).length() == 10 ?
                        StringUtils.remove(newPartNumber, StringConstant.DOT) + StringConstant.SPACE :
                        StringUtils.remove(newPartNumber, StringConstant.DOT));
        newMara.setVehiclePartListId(oldMara.getVehiclePartListId());

        treeItem.setPartNumber(StringUtils.remove(newPartNumber, StringConstant.DOT).length() == 10 ?
                StringUtils.remove(newPartNumber, StringConstant.DOT) + StringConstant.SPACE :
                StringUtils.remove(newPartNumber, StringConstant.DOT));
        treeItem.setPartNumberVornummer(newMara.getPartNumberVornummer());
        treeItem.setPartNumberMittelgruppe(newMara.getPartNumberMittelgruppe());
        treeItem.setPartNumberEndNumber(newMara.getPartNumberEndNumber());
        treeItem.setPartNumberIndex(newMara.getPartNumberIndex());

        treeItem.updateMaraProperties(newMara);
        treeItem.getUserObject().setChange(UserProperties.getUserId());
    }

    private <T> void handleEditCommitPartNumberColumns(TreeTableColumn<EfsElementDTO, T> column,
            EfsElementTreeItem treeItem, String newValue) {
        String partNumber = null;
        String currentPartNumber = treeItem.getPartNumber();

        if (column.equals(colPartNumberVornummer)) {
            partNumber = newValue + currentPartNumber.substring(3);
        } else if (column.equals(colPartNumberMittelgruppe)) {
            partNumber = currentPartNumber.substring(0, 3) + newValue + currentPartNumber.substring(6);
        } else if (column.equals(colPartNumberEndNumber)) {
            partNumber = currentPartNumber.substring(0, 6);
            if (currentPartNumber.length() > 9) {
                partNumber += newValue + currentPartNumber.substring(9);
            }
        } else if (column.equals(colPartNumberIndex)) {
            partNumber = currentPartNumber.substring(0, 9) + newValue;
        }

        handleEditCommitPartNumber(treeItem, partNumber);
    }

    private boolean isColumnPartNumber(TreeTableColumn<EfsElementDTO, ?> column) {
        return colPartNumberVornummer.equals(column) || colPartNumberMittelgruppe.equals(column)
                || colPartNumberEndNumber.equals(column) || colPartNumberIndex.equals(column);
    }

    private boolean isColumnMara(TreeTableColumn<EfsElementDTO, ?> column) {
        return column == colDescription1 || column == colDescription2 || column == colWeightCalculatedTe
                || column == colWeightCalculatedTeDate || column == colWeightEstimatedTe
                || column == colWeightEstimatedTeDate || column == colWeightWeightedTe
                || column == colWeightWeightedTeDate || column == colWeightWeightedProd
                || column == colWeightWeightedProdDate || column == colAssemblyIndicator || column == colDrawingDate
                || column == colDrawingStatus || column == colQuality || column == colMatThickness
                || column == colSeeDrawing || column == colRespConstr1 || column == colRespConstr2
                || column == colBuildSampleApproval || column == colTechnicallyOkay || column == colRelDateSoll
                || column == colDesignerName || column == colDesignerCostGroup || column == colDesignerPhone
                || column == colKStandRelDate || column == colTioFreiRelDate || column == colBuildSampleApprovalDate
                || column == colMFPStatus || column == colMFPThickness || column == colKseKz
                || column == colWeightAcceptedFromEpis;
    }

    private <T> void handleEditCommitMara(TreeTableColumn<EfsElementDTO, T> column, EfsElementTreeItem treeItem,
            T newValue) {
        if (column == colDescription1) {
            treeItem.setDescription1((String) newValue);
        } else if (column == colDescription2) {
            treeItem.setDescription2((String) newValue);
        } else if (column == colWeightCalculatedTe) {
            treeItem.setWeightCalculatedTe((Double) newValue);
        } else if (column == colWeightCalculatedTeDate) {
            treeItem.setWeightCalculatedTeDate((Date) newValue);
        } else if (column == colWeightEstimatedTe) {
            treeItem.setWeightEstimatedTe((Double) newValue);
        } else if (column == colWeightEstimatedTeDate) {
            treeItem.setWeightEstimatedTeDate((Date) newValue);
        } else if (column == colWeightWeightedTe) {
            treeItem.setWeightWeightedTe((Double) newValue);
        } else if (column == colWeightWeightedTeDate) {
            treeItem.setWeightWeightedTeDate((Date) newValue);
        } else if (column == colWeightWeightedProd) {
            treeItem.setWeightWeightedProd((Double) newValue);
        } else if (column == colWeightWeightedProdDate) {
            treeItem.setWeightWeightedProdDate((Date) newValue);
        } else if (column == colAssemblyIndicator) {
            treeItem.setAssemblyIndicator((String) newValue);
        } else if (column == colDrawingDate) {
            treeItem.setDrawingDate((Date) newValue);
        } else if (column == colDrawingStatus) {
            treeItem.setDrawingStatus((String) newValue);
        } else if (column == colQuality) {
            treeItem.setQuality((String) newValue);
        } else if (column == colMatThickness) {
            treeItem.setMaterialThickness((Double) newValue);
        } else if (column == colSeeDrawing) {
            treeItem.setSeeDrawing((String) newValue);
        } else if (column == colRespConstr1) {
            treeItem.setRespConstr1((String) newValue);
        } else if (column == colRespConstr2) {
            treeItem.setRespConstr2((String) newValue);
        } else if (column == colBuildSampleApproval) {
            treeItem.setBuildSampleApproval((String) newValue);
        } else if (column == colTechnicallyOkay) {
            treeItem.setTechnicallyOkay((String) newValue);
        } else if (column == colRelDateSoll) {
            treeItem.setRelDateSoll((Date) newValue);
        } else if (column == colDesignerName) {
            treeItem.setDesignerName((String) newValue);
        } else if (column == colDesignerCostGroup) {
            treeItem.setDesignerCostGroup((String) newValue);
        } else if (column == colDesignerPhone) {
            treeItem.setDesignerPhoneNumber((String) newValue);
        } else if (column == colKStandRelDate) {
            treeItem.setKStandRelDate((Date) newValue);
        } else if (column == colTioFreiRelDate) {
            treeItem.setTioFreiRelDate((Date) newValue);
        } else if (column == colBuildSampleApprovalDate) {
            treeItem.setBuildSampleApprovalDate((Date) newValue);
        } else if (column == colMFPStatus) {
            treeItem.setMfpStatus((String) newValue);
        } else if (column == colMFPThickness) {
            treeItem.setMfpThickness((Double) newValue);
        } else if (column == colKseKz) {
            treeItem.setKseKz((String) newValue);
        } else if (column == colWeightAcceptedFromEpis) {
            treeItem.setWeightAcceptedFromEpis((String) newValue);
        }

        treeItem.getEfsElementMara().setChange(UserProperties.getUserId());
    }

    private <T> void handleEditCommitEfsElement(TreeTableColumn<EfsElementDTO, T> column, EfsElementTreeItem treeItem,
            T newValue) {
        if (column == colNodeLabel) {
            treeItem.setNodeLabel((String) newValue);
        } else if (column == colNodeLevel) {
            treeItem.setNodeLevel((Integer) newValue);
        } else if (column == colNodeType) {
            treeItem.setNodeType((String) newValue);
        } else if (column == colBomNumber) {
            treeItem.setBomNumber((Integer) newValue);
        } else if (column == colProduct) {
            treeItem.setProduct((String) newValue);
        } else if (column == colPositionVariant) {
            treeItem.propertyPositionVariantProperty().set((String) newValue);
        } else if (column == colDeletionFlag) {
            treeItem.propertyDeletionFlag().set((String) newValue);
        } else if (column == colQuantity) {
            treeItem.setQuantity((Integer) newValue);
        } else if (column == colQuantityUnit) {
            treeItem.setQuantityUnit(((QuantityUnit) newValue));
        } else if (column == colQuantityUnitExtended) {
            treeItem.setQuantityUnitExtended((String) newValue);
        } else if (column == colWeightControlFlag) {
            treeItem.setWeightControlFlag(WeightControlFlag.getType((String) newValue));
        } else if (column == colBeginDateKey) {
            treeItem.setBeginDateKey((String) newValue);
        } else if (column == colEndDateKey) {
            treeItem.setEndDateKey((String) newValue);
        } else if (column == colBeginDate) {
            treeItem.setBeginDate((Date) newValue);
        } else if (column == colEndDate) {
            treeItem.setEndDate((Date) newValue);
        } else if (column == colPartType) {
            treeItem.setProduct((String) newValue);
        } else if (column == colDrawingDate) {
            treeItem.setDrawingDate((Date) newValue);
        } else if (column == colDrawingStatus) {
            treeItem.setDrawingStatus((String) newValue);
        } else if (column == colPrNumberRule) {
            treeItem.setPrNumberRule((String) newValue);
        } else if (column == colTisSort) {
            treeItem.setTisSort((Long) newValue);
        } else if (column == colAp) {
            treeItem.setAp((String) newValue);
        } else if (column == colSetKey) {
            treeItem.setSetKey((String) newValue);
        } else if (column == colCostGroup) {
            treeItem.setCostGroup((String) newValue);
        } else if (column == colCog) {
            treeItem.setCogCoordinates((CogCoordinates) newValue);
        } else if (column == colBaukastenStatus) {
            treeItem.setBaukastenStatus((String) newValue);
        } else if (column == colBaukastenNodeId) {
            treeItem.setBaukastenNodeId((String) newValue);
        } else if (column == colProcessStatus) {
            treeItem.setProcessStatus((String) newValue);
        } else if (column == colDmuRelevant) {
            treeItem.setDmuRelevant((String) newValue);
        } else if (column == colMaterialType) {
            treeItem.setMaterialType((String) newValue);
        } else if (column == colEarliestNs) {
            treeItem.setEarliestNs((Date) newValue);
        } else if (column == colEarliestPvs) {
            treeItem.setEarliestPvs((Date) newValue);
        } else if (column == colEarliestSop) {
            treeItem.setEarliestSop((Date) newValue);
        } else if (column == colPActivationDate) {
            treeItem.setPActivationDate((Date) newValue);
        } else if (column == colConstructureDate) {
            treeItem.setConstructureDate((Date) newValue);
        } else if (column == colAvonStatus) {
            treeItem.setAvonStatus((String) newValue);
        } else if (column == colConstructionsGroup) {
            treeItem.setConstructionsGroup((String) newValue);
        } else if (column == colConstructionsState) {
            treeItem.setConstructionsState((String) newValue);
        } else if (column == colProductStructure) {
            treeItem.setProductStructure((String) newValue);
        }

        treeItem.getUserObject().setChange(UserProperties.getUserId());
    }

    private void copyEfsElemente(TreeItem<EfsElementDTO> treeItem, Collection<TreeItem<EfsElementDTO>> elementsToCopy) {
        IPartListChildDTO toParent = treeItem == null ? getVehicleConfig().getVehiclePartList() : treeItem.getValue();
        List<EfsElementDTO> copyEfsElemente = elementsToCopy.stream().map(TreeItem::getValue).toList();
        copyEfsElements(toParent, copyEfsElemente);
    }

    private void moveEfsElemente(TreeItem<EfsElementDTO> treeItem, Collection<TreeItem<EfsElementDTO>> list) {
        IPartListChildDTO toParent = treeItem == null ? getVehicleConfig().getVehiclePartList() : treeItem.getValue();
        List<EfsElementDTO> moveEfsElemente = list.stream().map(TreeItem::getValue).toList();
        moveEfsElements(toParent, moveEfsElemente);
    }

    private void handleEfsElementeMoved(List<EfsElementDTO> movedEfsElements) {
        if (!movedEfsElements.isEmpty()) {
            clearCopyCut();
            fireHistoryUpdateEvent(movedEfsElements.getFirst());
        }
    }

    private void saveEfsElement(EfsElementDTO efsElement) {
        ServiceController<EfsElementDTO> serviceController = new ServiceController<>();

        serviceController.setOnSucceeded(e -> onNewCreated(serviceController.getValue()));
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(EXEC_TIME_SAVE_EFS);
        serviceController.start(() -> new EfsEditLoadAdapter().saveEfsElement(efsElement));
    }

    private void onNewCreated(EfsElementDTO value) {
        EfsElementResolver.registerElements(List.of(value));
        EventBus.getInstance().post(new VehicleConfigChangedEvent(getVehicleConfig()));

        refreshEfsTree(value);
    }

    private void refreshFgSetTree(EfsElementDTO newState, EfsElementDTO oldState) {
        if (!Objects.equals(newState.getSetKey(), oldState.getSetKey())) {
            EventBus.getInstance()
                    .post(new FgSetTreeRefreshEvent(getVehicleConfig().getVehiclePartList().getId(), oldState, true));
        }
    }

    // Todo ZsN - Aggregated view - Delete
    private void refreshCostGroupTree(EfsElementDTO newState, EfsElementDTO oldState) {
        if (!Objects.equals(newState.getCostGroup(), oldState.getCostGroup())) {
            EventBus.getInstance()
                    .post(new CostGroupTreeRefreshEvent(getVehicleConfig().getVehiclePartList().getId(), oldState,
                            true));
        }
    }

    private void refreshPartGroupTree(EfsElementDTO newState, EfsElementDTO oldState) {
        if (oldState.getPartNumber() != null && !Objects.equals(newState.getPartNumber(), oldState.getPartNumber())) {
            EventBus.getInstance()
                    .post(new PartGroupTreeRefreshEvent(getVehicleConfig().getVehiclePartList().getId(), oldState,
                            true));
        }
    }

    private void refreshEfsTree(EfsElementDTO efsElement) {
        efsSelectionProperty.get()
                .handle(new HistorieUpdateEvent<>(this, HistorieUpdateEvent.EFS_ELEMENT_SELECTED, efsElement));

        fireHistoryUpdateEvent(efsElement);

        Long vehiclePartListId = getVehiclePartList().getId();
        VehicleConfigDTO vehicleConfig = getVehicleConfig();

        refreshVehiclePartListWeight(vehiclePartListId, vehicleConfig);
    }

    private void refreshVehiclePartListWeight(Long vehiclePartListId, VehicleConfigDTO vehicleConfig) {
        ServiceController<Double> serviceController = new ServiceController<>();
        serviceController.start(
                () -> EfsWeightRestClientHolder.getInstance().updateVehiclePartListWeight(vehiclePartListId));
        serviceController.setOnSucceeded(e -> updatePartList(vehiclePartListId, vehicleConfig, serviceController));
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
    }

    private void updatePartList(Long vehiclePartListId, VehicleConfigDTO vehicleConfig,
            ServiceController<Double> serviceController) {
        EventBus.getInstance().post(new FzgStuecklisteGewichtEvent(vehiclePartListId, serviceController.getValue()));

        if (vehicleConfig != null) {
            updateInspectorItemCount(vehicleConfig);
        }
    }

    private void removeTreeItems(Collection<TreeItem<EfsElementDTO>> items) {
        if (toggleChangeViewProperty().get()) {
            setEfsElementsDeleted(items);
            return;
        }

        clearCopyCut();
        efsTreeTableView.getSelectionModel().clearSelection();
        resetNavigation();

        for (TreeItem<EfsElementDTO> treeItem : items) {
            getEfsElementTreeModel().removeElement((EfsElementTreeItem) treeItem);
        }
    }

    private EfsElementDTO createNewEfsElement(EfsElementDTO parent) {
        Optional<ImmutableTriple<EfsElementMaraDTO, Integer, String>> result = openEfsElementMaraSelectionDialog(
                parent);

        if (result.isPresent()) {
            Long parentId = parent.getId();
            return PartListFactory.createEfsElement(parentId, result.get().getLeft(), result.get().getMiddle(),
                    QuantityUnit.getByName(result.get().getRight()).getShortName(),
                    getVehicleConfig() != null ? getVehicleConfig().getVehiclePartList().getId() : null);
        }

        return null;
    }

    private Optional<ImmutableTriple<EfsElementMaraDTO, Integer, String>> openEfsElementMaraSelectionDialog(
            EfsElementDTO parent) {
        String newPosition = I18N.getString("efs.neue.position");

        Dialog<ImmutableTriple<EfsElementMaraDTO, Integer, String>> maraSetting = new PasoDialog<>();
        maraSetting.setTitle(I18N.getString("efs.neue.mara.title"));
        maraSetting.setHeaderText(formatResourceBundleMessage("efs.neue.mara.parent", parent.getFormattedPartNumber()));
        maraSetting.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasoCustomTextField<String> partNumber = new PasoCustomTextField<>();
        PasoCustomTextField<Integer> quantity = new PasoCustomTextField<>();
        ObservableList<QuantityUnit> quantityUnits = FXCollections.observableArrayList(QuantityUnit.values());
        PasoCustomComboBox<QuantityUnit> quantityUnit = new PasoCustomComboBox<>(quantityUnits);

        partNumber.setValidCharacter("[a-zA-Z0-9 ]*");
        partNumber.setValidation(isPartNumberAppropriate());
        partNumber.setMaxTextLength(14);
        partNumber.setUpperCase(true);
        partNumber.addEventFilter(KeyEvent.ANY, event -> PartNumberUtil.keyEventFilter(event, partNumber));

        quantity.setConverter(new IntegerStringConverter());

        quantityUnits.sort(Comparator.comparing(QuantityUnit::getBezeichnung));
        quantityUnit.setConverter(new QuantityUnitStringConverter());

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label(I18N.getString("efs.neu.mara.partnumber")), 0, 0);
        grid.add(partNumber, 1, 0);
        grid.add(new Label(I18N.getString("efs.neu.quantity")), 0, 1);
        grid.add(quantity, 1, 1);
        grid.add(new Label(I18N.getString("efs.neu.quantity.unit")), 0, 2);
        grid.add(quantityUnit, 1, 2);
        maraSetting.getDialogPane().setContent(grid);

        maraSetting.setResultConverter(
                confirmButton -> setEfsMaraConfiguration(confirmButton, newPosition, partNumber, quantity,
                        quantityUnit));

        return maraSetting.showAndWait();
    }

    private ImmutableTriple<EfsElementMaraDTO, Integer, String> setEfsMaraConfiguration(ButtonType confirmButton,
            String newPosition, PasoCustomTextField<String> partNumber, PasoCustomTextField<Integer> quantity,
            PasoCustomComboBox<QuantityUnit> quantityUnit) {
        if (confirmButton != ButtonType.OK) {
            return null;
        }

        EfsElementMaraDTO efsElementMara = PartListFactory.createEfsElementMara(newPosition,
                (StringUtils.remove(partNumber.getText(), StringConstant.DOT).length() == 10) ?
                        StringUtils.remove(partNumber.getText(), StringConstant.DOT) + StringConstant.SPACE :
                        StringUtils.remove(partNumber.getText(), StringConstant.DOT));
        efsElementMara.setVehiclePartListId(getVehicleConfig().getVehiclePartList().getId());
        return new ImmutableTriple<>(efsElementMara, Integer.parseInt(quantity.getText()), quantityUnit.getText());
    }

    private Predicate<String> isPartNumberAppropriate() {
        return partNumber -> {
            try {
                return EfsEditValidations.evaluateMaraIsAppropriate(partNumber);
            } catch (NullElementException | PartNumberInappropriateException e) {
                return false;
            }
        };
    }

    private void showInfoCountDeleted(Integer countDeleted) {
        String message;
        if (countDeleted == null || countDeleted == 0) {
            message = I18N.getString("dialog.position.loeschen.ergebnis.geloescht.none");
        } else if (countDeleted == 1) {
            message = I18N.getString("dialog.position.loeschen.ergebnis.geloescht.single");
        } else {
            message = formatResourceBundleMessage("dialog.position.loeschen.ergebnis.geloescht.multi",
                    countDeleted.longValue());
        }

        String title = I18N.getString("dialog.position.loeschen.ergebnis.title");
        DialogUtil.showConfirmationDialog(AlertType.INFORMATION, title, StringConstant.EMPTY, message);
    }

    private String formatResourceBundleMessage(String key, Object... params) {
        return params.length == 0 ? I18N.getString(key) : formatMessage(I18N.getString(key), params);
    }

    private String formatMessage(String message, Object... params) {
        return MessageFormat.format(message, params);
    }

    private List<EfsElementDTO> getSelectedEfsElements() {
        return efsTreeTableView.getSelectionModel().getSelectedItems().stream().filter(Objects::nonNull)
                .map(TreeItem::getValue).toList();
    }

    private TreeItem<EfsElementDTO> getSelectedEfsElementTreeItem() {
        var selectionModel = efsTreeTableView.getSelectionModel();
        if (selectionModel.isEmpty()) {
            return null;
        }

        return selectionModel.getSelectedItem();
    }

    private Collection<TreeItem<EfsElementDTO>> getSelectedTreeItems() {
        var selectionModel = efsTreeTableView.getSelectionModel();
        if (selectionModel.isEmpty()) {
            return List.of();
        }

        return selectionModel.getSelectedItems();
    }

    private void clearCopyCut() {
        getTreeItemsToCopy().clear();
        getTreeItemsToCut().clear();
    }

    private void setPasteDisableProperty() {
        if (getVehicleConfig() == null || (getTreeItemsToCopy().isEmpty() && getTreeItemsToCut().isEmpty())) {
            disablePropertyPasteEfsElemente().set(true);
            return;
        }

        TreeItem<EfsElementDTO> selectedTreeItem = getSelectedEfsElementTreeItem();
        IPartListChildDTO parent =
                selectedTreeItem == null || selectedTreeItem.getValue() == null ? getVehiclePartList() :
                        selectedTreeItem.getValue();
        EfsEditValidations efsEditValidations = new EfsEditValidations(EfsElementResolver::getElement,
                EfsElementResolver::getAllElementsInHierarchy);

        List<EfsElementDTO> copyClipboard = getTreeItemsToCopy().stream().map(TreeItem::getValue).toList();
        if (!copyClipboard.isEmpty()) {
            disablePropertyPasteEfsElemente().run(
                    () -> efsEditValidations.preparePasteAfterCopy(parent, copyClipboard));
        }

        Collection<EfsElementDTO> cutClipboard = getTreeItemsToCut().stream().map(TreeItem::getValue).toList();
        if (!cutClipboard.isEmpty()) {
            disablePropertyPasteEfsElemente().run(() -> efsEditValidations.preparePasteAfterCut(parent, cutClipboard));
        }
    }

    private VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig.get();
    }

    private VehiclePartListDTO getVehiclePartList() {
        return getVehicleConfig().getVehiclePartList();
    }

    private void handleChangeViewDisplay(Boolean newVal) {
        if (newVal) {
            loadVehiclePartListRevisionen();
        } else {
            hideChangeView();
        }
    }

    private Collection<TreeItem<EfsElementDTO>> getTreeItemsToCopy() {
        if (treeItemsToCopy == null) {
            treeItemsToCopy = new ArrayList<>();
        }

        return treeItemsToCopy;
    }

    private Collection<TreeItem<EfsElementDTO>> getTreeItemsToCut() {
        if (treeItemsToCut == null) {
            treeItemsToCut = new ArrayList<>();
        }

        return treeItemsToCut;
    }
    // FIXME Do not use EfsElement (add all elements or partlist)

    private ObjectProperty<EventHandler<HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>>>> efsSelectionProperty() { // NO_UCD (use private)
        return efsSelectionProperty;
    }

    private void fireHistoryUpdateEvent(EfsElementDTO efsElement) {
        HistorieUpdateEvent<AbstractTreeItem<AbstractEfsElementDTO>> updateEvent = new HistorieUpdateEvent<>(this,
                HistorieUpdateEvent.EFS_ELEMENTS_CHANGED, efsElement);
        efsSelectionProperty().get().handle(updateEvent);
    }

    private void loadVehiclePartListRevisionen() {
        ServiceController<List<EfsElementDTOWrapper>> serviceController = new ServiceController<>();
        serviceController.setOnSucceeded(event -> showChangeView(serviceController.getValue()));
        serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        serviceController.setExecutionTime(EXEC_TIME_LOAD_HISTORIE);
        serviceController.start(() -> EfsElementHistoryRestClientHolder.getInstance()
                .loadRevisions(getVehicleConfig().getVehiclePartList().getId()).convertToEfsElementHistoryDTO());
    }

    private void setDisplayStrategy(AbstractDisplayStrategyForTrees<EfsElementDTO> displayStrategy) {
        Collection<Pair<EfsElementDTO, TableColumnBase>> rowColPairs = efsTreeTableView.getSelectionModel()
                .getSelectedCells().stream()
                .map(e -> new Pair<>(e.getTreeItem().getValue(), (TableColumnBase) e.getTableColumn())).toList();

        this.displayStrategy = displayStrategy;
        efsElementTreeModel = (EfsElementTreeModel) displayStrategy.createDisplayModel(
                EfsElementResolver.getElementsInPartList(getVehiclePartList()));

        efsTreeTableView.setRoot(efsElementTreeModel.getRoot());

        TreeItem<EfsElementDTO> root = efsTreeTableView.getRoot();
        root.setExpanded(true);
        for (TreeItem<EfsElementDTO> child : root.getChildren()) {
            child.setExpanded(true);
        }

        efsTreeTableView.getSelectionModel().clearSelection();
        for (var rowColPair : rowColPairs) {
            TreeItem<EfsElementDTO> treeItem = efsElementTreeModel.getTreeItem(rowColPair.first().getId());
            expandParent(treeItem);

            int row = getRow(treeItem);
            efsTreeTableView.getSelectionModel().select(row, rowColPair.second());
        }

        efsTreeTableView.requestFocus();
    }

    private void expandParent(TreeItem<EfsElementDTO> item) {
        if (item != null && item.getParent() != null) {
            expandParent(item.getParent());
            item.getParent().setExpanded(true);
        }
    }

    private void reapplyFiltersOnTreeTableView() {
        TreeItem<EfsElementDTO> root = efsTreeTableView.getRoot();
        if (!(root instanceof FilterableTreeItem<EfsElementDTO> rootItem)) {
            return;
        }

        for (var key : efsTreeTableView.getColumnToPredicateMap().keySet()) {
            var predicate = efsTreeTableView.getColumnToPredicateMap().get(key);
            if (predicate instanceof PasoPredicate<TreeItem<EfsElementDTO>> pasoPredicate) {
                rootItem.predicateProperty().set(pasoPredicate.andAll(efsTreeTableView.getColumnToPredicateMap()));
            }
        }
    }

    private void changeCollapseExpandDisableProperty(boolean isDisabled) {
        disablePropertyExpandTree.set(isDisabled);
        disablePropertyExpandAllTree.set(isDisabled);
        disablePropertyCollapseTree.set(isDisabled);
        disablePropertyCollapseAllTree.set(isDisabled);
    }

    private void showChangeView(Collection<EfsElementDTOWrapper> historyElements) {
        this.history = historyElements;

        Collection<Integer> rowNumbers;
        if (selectedDisplayMode.get() == DisplayMode.LIST) {
            rowNumbers = new ArrayList<>();
            setDisplayStrategy(FlatDisplayStrategy.getStrategyWithDeletion(getColumnToFilterMap().values()));
        } else {
            rowNumbers = getAllExpandedRowNumbers();
            setDisplayStrategy(HierarchicalDisplayStrategy.getStrategyWithDeletion(
                    viewModeEfsProperty.get().equals(PartListViewMode.VEHICLE_ALL)));
        }

        Collection<EfsElementDTOWrapper> efsElementHistories = historyElements.stream()
                .filter(e -> e.getEfsElement() instanceof EfsElementHistoryDTO).toList();
        Collection<EfsElementHistoryTreeItem> historyTreeItems = createHistoryElements(efsElementHistories);

        // calculate TreeItem change
        for (EfsElementTreeItem treeItem : getEfsElementTreeModel().getTreeItems()) {
            EfsElementDTO abstractEfsElement = treeItem.getUserObject();

            if (abstractEfsElement.isDeleted()) {
                continue;
            }

            EfsHistoryUtil.compareAbstractEfsElements(historyTreeItems, treeItem);
        }

        for (int rowNumber : rowNumbers) {
            efsTreeTableView.getTreeItem(rowNumber).setExpanded(true);
        }

        initSorting();
    }

    private Collection<EfsElementHistoryTreeItem> createHistoryElements(
            Collection<EfsElementDTOWrapper> historyElements) {
        // Dummy als Root
        // FIXME We should remove the root node and every root EfsElement should be "root node"
        EfsElementDTO efs = PartListFactory.createEfsElement();
        EfsElementMaraDTO efsMara = PartListFactory.createEfsElementMara();

        efs.setEfsElementMara(efsMara);
        EfsElementDTOWrapper efsDTO = new EfsElementDTOWrapper(efs, efsMara);

        EfsElementHistoryTreeModel model = new EfsElementHistoryTreeModel(efsDTO);
        for (EfsElementDTOWrapper historyElement : historyElements) {
            model.addElement(historyElement, true);
        }

        return new ArrayList<>(model.getTreeItems());
    }

    private void hideChangeView() {
        Collection<Integer> rowNumbers = getAllExpandedRowNumbers();
        List<EfsElementDTOWrapper> historyElements = history.stream()
                .filter(e -> e.getEfsElement() instanceof EfsElementHistoryDTO).collect(Collectors.toList());

        if (!historyElements.isEmpty()) {
            EfsHistoryUtil.removeCellHighlightFromChanges(getEfsElementTreeModel().getTreeItems(), historyElements);
        }

        resetDisplayMode();

        for (int rowNumber : rowNumbers) {
            efsTreeTableView.getTreeItem(rowNumber).setExpanded(true);
        }
    }

    private void resetDisplayMode() {
        useDisplayMode(selectedDisplayMode.get());
    }

    private Collection<Integer> getAllExpandedRowNumbers() {
        Collection<TreeItem<EfsElementDTO>> expandedTreeItems = ExpandCollapseUtil.findAllExpandedItems(
                efsTreeTableView.getRoot());

        Collection<Integer> rowNumbers = new ArrayList<>(expandedTreeItems.size());
        for (TreeItem<EfsElementDTO> treeItem : expandedTreeItems) {
            rowNumbers.add(efsTreeTableView.getRow(treeItem));
        }

        return rowNumbers;
    }

    private void setEfsElementsDeleted(Collection<TreeItem<EfsElementDTO>> list) {
        efsTreeTableView.getSelectionModel().clearSelection();

        for (TreeItem<EfsElementDTO> treeItem : list) {
            treeItem.getValue().setDeleted(1);
            for (String propertyName : AbstractEfsElementTreeItem.getPropertyNamesCompare()) {
                ((AbstractTreeItem<EfsElementDTO>) treeItem).setChange(propertyName, true);
            }

            setEfsElementsDeleted(treeItem.getChildren());
        }
    }

    private boolean matchEfsElement(EfsElementTreeItem efsElementTreeItem) {
        if (patternSearchTerm == null) {
            return true;
        }

        boolean isMatching = false;
        for (TreeTableColumn<EfsElementDTO, ?> column : efsTreeTableView.getVisibleLeafColumns()) {
            ObservableValue<?> observableValue = column.getCellObservableValue(efsElementTreeItem);
            if (observableValue != null && observableValue.getValue() != null) {
                Object value = observableValue.getValue();
                isMatching = switch (value) {
                    case String string -> patternSearchTerm.matches(string) != null;
                    case Number number -> patternSearchTerm.matches(number.toString()) != null;
                    case QuantityUnit qu -> patternSearchTerm.matches(qu.getBezeichnung().toUpperCase()) != null;
                    case AP ap -> patternSearchTerm.matches(ap.getApAbbreviation().toUpperCase()) != null;
                    case Date date -> patternSearchTerm.matches(DateUtil.formatDate(date, "dd.MM.yyyy")) != null;
                    default -> patternSearchTerm.matches(value.toString()) != null;
                };
            }

            if (isMatching) {
                return true;
            }
        }

        return false;
    }

    private void slideHeaders(EfsHeaderFahrzeugController currentEfsHeaderController,
            EfsHeaderFahrzeugController efsHeaderController) {
        if (currentEfsHeaderController != null) {
            if (efsHeaderController instanceof EfsHeaderGetriebeController) {
                AnimationUtil.getInstance().slideTopToBottom(paneEfsHeader, efsHeaderController.getControl(),
                        currentEfsHeaderController.getControl(), Duration.millis(1000));
            } else if (efsHeaderController instanceof EfsHeaderMotorController
                    && currentEfsHeaderController instanceof EfsHeaderGetriebeController) {
                AnimationUtil.getInstance().slideBottomToTop(paneEfsHeader, efsHeaderController.getControl(),
                        currentEfsHeaderController.getControl(), Duration.millis(1000));
            } else if (efsHeaderController instanceof EfsHeaderMotorController) {
                AnimationUtil.getInstance().slideTopToBottom(paneEfsHeader, efsHeaderController.getControl(),
                        currentEfsHeaderController.getControl(), Duration.millis(1000));
            } else if (efsHeaderController != null) {
                AnimationUtil.getInstance().slideBottomToTop(paneEfsHeader, efsHeaderController.getControl(),
                        currentEfsHeaderController.getControl(), Duration.millis(1000));
            } else {
                handleException(new IllegalArgumentException("Invalid EfsHeader specified"));
            }
        }

        paneEfsHeader.getChildren().add(efsHeaderController.getControl());
    }

    private void loadEfsHeaderController(PartListViewMode viewMode) {
        EfsHeaderFahrzeugController currentEfsHeaderController = efsHeaderController;
        if (mapHeaderController.containsKey(viewMode)) {
            efsHeaderController = mapHeaderController.get(viewMode);
        } else {
            efsHeaderController = createHeaderController(viewMode);
            mapHeaderController.put(viewMode, efsHeaderController);

            efsHeaderController.setAggregatAction(this::handleActionShowAggregate);
            updateHeader(efsHeaderController);
        }

        if (currentEfsHeaderController != null) {
            efsHeaderController.setSelectedPartList(currentEfsHeaderController.getSelectedWeight(),
                    currentEfsHeaderController.getSelectedPositions());
            efsHeaderController.setFilteredPartList(currentEfsHeaderController.getFilteredWeight(),
                    currentEfsHeaderController.getFilteredPositions());
            efsHeaderController.setPositions(currentEfsHeaderController.getPartListPositions());
            efsHeaderController.setWeight(currentEfsHeaderController.getPartListWeight());
        }

        slideHeaders(currentEfsHeaderController, efsHeaderController);

        efsHeaderController.setVehicleConfig(vehicleConfig.get());
    }

    private EfsHeaderFahrzeugController createHeaderController(PartListViewMode partListViewMode)
            throws ControllerException {
        return switch (partListViewMode) {
            case VEHICLE_ALL, VEHICLE -> BaseController.load(EfsHeaderFahrzeugController.class);
            case ENGINE -> BaseController.load(EfsHeaderMotorController.class);
            case GEARBOX -> BaseController.load(EfsHeaderGetriebeController.class);
            case ENGINE_AND_GEARBOX -> BaseController.load(EfsHeaderEngineAndGearboxController.class);
        };
    }

    private void treeTableSelectionChangedListener(ObservableValue<? extends TreeItem<EfsElementDTO>> observable,
            TreeItem<EfsElementDTO> oldValue, TreeItem<EfsElementDTO> newValue) {
        handleEfsSelected(newValue);

        Collection<TreeItem<EfsElementDTO>> selectedTreeItems = getSelectedTreeItems();
        if (newValue == null || selectedTreeItems.isEmpty()) {
            reloadEfsHeaderSelectedContent(0, 0);
            return;
        }

        double selectedItemsWeight = selectedTreeItems.stream().mapToDouble(item -> item.getValue().getWeight()).sum();
        reloadEfsHeaderSelectedContent(selectedItemsWeight, selectedTreeItems.size());

        disablePropertyShowPartProperties().set(false);
        parentController.handleActionReloadPartPropertiesView(newValue.getValue());
    }

    private void handleActionShowAggregate(AggregatTextFieldEvent event) {
        if (event.getAggregat() != null) {
            selectElementById(event.getAggregat().getUserObject().getId());
        }
    }

    private void headerChangeEventListener() {
        CustomTreeTableView<EfsElementDTO> treeTableView = getTreeTableView();
        int numberOfFilteredItems;
        double filteredItemsWeight;
        if (treeTableView.isFiltered()) {
            numberOfFilteredItems = TreeItemUtil.getFilteredCount(treeTableView);
            filteredItemsWeight = TreeItemUtil.getChildTreeObjects(treeTableView.getRoot()).stream()
                    .mapToDouble(EfsElementDTO::getWeight).sum();
        } else {
            numberOfFilteredItems = 0;
            filteredItemsWeight = 0;
        }

        reloadEfsHeaderFilteredContent(filteredItemsWeight, numberOfFilteredItems);
    }

    private void reloadEfsHeaderSelectedContent(double selectedWeight, int numberOfSelectedElements) {
        efsHeaderController.setSelectedPartList(selectedWeight, numberOfSelectedElements);
    }

    public void reloadEfsHeaderFilteredContent(double filteredWeight, int numberOfFilteredElements) {
        efsHeaderController.setFilteredPartList(filteredWeight, numberOfFilteredElements);
    }

    private void updateInspectorItemCount(VehicleConfigDTO vehicleConfig) {
        updateInspectorItemCount(vehicleConfig, false);
    }

    private void updateInspectorItemCount(VehicleConfigDTO vehicleConfig, boolean filtered) {
        if (!vehicleConfig.getId().equals(getVehicleConfig().getId())) {
            return;
        }

        EfsElementDTO currentlySelected = getSelectedEfsElement();

        Collection<InspectorIgnoreDTO> ignoredEntries = InspectorRestClientHolder.getInstance()
                .loadIgnoreEntries(vehicleConfig.getVehiclePartList().getId()).inspectorIgnoredList();

        Table<InspectorEntryType, Long, InspectorIgnoreDTO> ignoredEntriesTable = HashBasedTable.create();
        for (InspectorIgnoreDTO entry : ignoredEntries) {
            ignoredEntriesTable.put(entry.type(), entry.efsElementId(), entry);
        }

        Collection<EfsElementDTO> elementsInPartList = EfsElementResolver.getElementsInPartList(
                vehicleConfig.getVehiclePartList());
        EfsElementDTO currRoot = null;
        if (filtered) {
            currRoot = elementsInPartList.stream().filter(elem -> elem.getId().equals(currentlySelected.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("EfsElement with this id doesn't exist"));

            elementsInPartList = currRoot.getAllChildren();
        }

        ListMultimap<InspectorEntryType, InspectorEntry> result = new Inspector().checkElements(elementsInPartList,
                currRoot, vehicleConfig);

        Set<Long> elementIdsInInspector = new HashSet<>();
        if (currRoot != null) {
            elementIdsInInspector.add(currRoot.getId());
        }

        int completeCount = 0;
        int aggregateCount = 0;
        for (InspectorEntryType key : result.keySet()) {
            Collection<InspectorEntry> inspectorEntries = result.get(key);
            for (InspectorEntry entry : inspectorEntries) {
                if (ignoredEntriesTable.contains(entry.getType(), entry.getElement().getId())) {
                    continue;
                }

                completeCount++;
                elementIdsInInspector.add(entry.getElement().getId());
                if (InspectorEntryType.MISSING_AGGREGATE_ENGINE.equals(key)
                        || InspectorEntryType.MISSING_AGGREGATE_GEARBOX.equals(key)) {
                    aggregateCount++;
                }
            }
        }

        if (filtered) {
            completeCount = inspectorItemCountProperty.get().getCompleteCount();
            aggregateCount = inspectorItemCountProperty.get().getAggregateCount();
        }

        inspectorItemCountProperty.set(new InspectorItemCounter(completeCount, aggregateCount, elementIdsInInspector));
    }

    private void updateAllHeader() {
        for (EfsHeaderFahrzeugController efsHeaderFahrzeugController : mapHeaderController.values()) {
            updateHeader(efsHeaderFahrzeugController);
        }
    }

    private void updateHeader(EfsHeaderFahrzeugController header) {
        header.setAggregateAndWeight(getMotor(), getGetriebe(), getWeight());
    }

    private void updateMotorAndEngine() {
        VehicleConfigDTO vehicleConfigDTO = vehicleConfig.get();
        EfsElementTreeItem engine = getGetriebe();
        EfsElementTreeItem motor = getMotor();
        String productKeyGearbox = vehicleConfigDTO.getVehiclePartList().getProductKeyGearbox();
        String productKeyMotor = vehicleConfigDTO.getVehiclePartList().getProductKeyMotor();

        boolean changed = false;
        if (engine != null && (productKeyGearbox == null || !engine.getUserObject().getDescription2()
                .equals(productKeyGearbox))) {
            vehicleConfigDTO.getVehiclePartList().setProductKeyGearbox(engine.getUserObject().getDescription2());
            changed = true;
        }

        if (motor != null && (productKeyMotor == null || !motor.getUserObject().getDescription2()
                .equals(productKeyMotor))) {
            vehicleConfigDTO.getVehiclePartList().setProductKeyMotor(motor.getUserObject().getDescription2());
            changed = true;
        }

        if (changed) {
            VehicleConfigRestClientHolder.getInstance().saveFzgKonfig(vehicleConfigDTO);
        }
    }

    public void handleActionResetSorting() {
        isResettingSort = true;

        initSorting();
        disablePropertyResetSorting().set(true);

        isResettingSort = false;
    }
}
