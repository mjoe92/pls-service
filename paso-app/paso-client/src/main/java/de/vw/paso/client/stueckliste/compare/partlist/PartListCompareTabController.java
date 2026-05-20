package de.vw.paso.client.stueckliste.compare.partlist;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.main.ribbonmenu.compare.partlist.RibbonMenuComparePartlistListener;
import de.vw.paso.client.stueckliste.column.alignment.ColumnAlignment;
import de.vw.paso.client.stueckliste.compare.ReopenCompareTabsEvent;
import de.vw.paso.client.stueckliste.compare.partlist.combine.DefaultCombineStrategy;
import de.vw.paso.client.stueckliste.compare.partlist.combine.ITreeCombineStrategy;
import de.vw.paso.client.stueckliste.compare.partlist.combine.nodematcher.ReflectionPathNodeIdentityProvider;
import de.vw.paso.client.stueckliste.compare.partlist.combine.selection.MethodWrapper;
import de.vw.paso.client.stueckliste.compare.partlist.combine.selection.NodeIdSelectionDialog;
import de.vw.paso.client.stueckliste.compare.partlist.combine.selection.SelectionResult;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.ExpandCollapseUtil;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.client.util.converter.NumberStringConverter;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

@FXController(name = "partlist-compare-tab")
public class PartListCompareTabController extends BaseController<Tab> implements RibbonMenuComparePartlistListener {

  public static final String COMPARE_STATUS_CHANGED_ROW = "compare-status-changed";
  public static final String COMPARE_STATUS_CHANGED = "compare-status-changed-cell";
  public static final String COMPARE_STATUS_ADDED = "compare-status-added";
  public static final String COMPARE_STATUS_DELETED = "compare-status-deleted";

  private static final String COL_STYLE = "highlight-col-selection";
  private static final String ROW_STYLE = "highlight-row-selection";

  private BooleanProperty navigationBackProperty = new SimpleBooleanProperty(true);
  private BooleanProperty navigationForwardProperty = new SimpleBooleanProperty(true);

  private boolean isResettingSort = false;

  @FXML
  private Tab partlistTab;

  @FXML
  private BorderPane partlistTabContent;

  private CustomTreeTableView<PartListCompareRow> treeTableView;

  private ITreeCombineStrategy combineStrategy;

  private List<VehicleConfigDTO> vehicleConfigs;
  private List<VehiclePartListDTO> partLists;

  private SelectionResult lastResult;

  private SelectionHighlightManager<PartListCompareRow> highlightManager;

  private Map<Integer, TreeTableColumn<PartListCompareRow, ?>> deltaColumnMap = new HashMap<>();
  private BooleanProperty toggleDisplayDeltaColumnsProperty;

  private TreeTableColumn<PartListCompareRow, Long> colTisSort;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);

    Method method;
    Method method2;
    try {
      method = EfsElementDTO.class.getMethod("getNodeValue");
      method2 = EfsElementDTO.class.getMethod("getTisSort");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }

    lastResult = new SelectionResult();
    lastResult.setSelectedProperties(Arrays.asList(new MethodWrapper(method), new MethodWrapper(method2)));
    lastResult.setCombineAll(true);
    lastResult.setCheckPath(true);
    combineStrategy = new DefaultCombineStrategy(
      new ReflectionPathNodeIdentityProvider(Arrays.asList(method, method2)));
    initTreeTable();
  }

  @Override
  public void stop() {
    super.stop();
    highlightManager.removeFromTable();
  }

  private void initTreeTable() {
    treeTableView = new CustomTreeTableView<>();
    treeTableView.setId("partlist-compare-treetableview");
    treeTableView.setShowRoot(false);

    //Temporary columns
    treeTableView.getColumns().add(createColumn(null, EfsProperty.NODE_LABEL, 150));
    treeTableView.getColumns().add(createColumn(null, EfsProperty.NODE_VALUE, 100));
    colTisSort = createLongColumn(null, EfsProperty.TI_SORT, 80);
    treeTableView.getColumns().add(colTisSort);

    treeTableView.makeHeaderWrappable();

    partlistTabContent.setCenter(treeTableView);

    highlightManager = new SelectionHighlightManager<>();
    highlightManager.initTable(treeTableView, ROW_STYLE, COL_STYLE);

    treeTableView.getSortOrder().addListener((ListChangeListener<TreeTableColumn<PartListCompareRow, ?>>) change -> {
      if (!isResettingSort) {
        disablePropertyResetSorting().set(false);
      }
    });

    treeTableView.getColumns().forEach(column -> {
      column.sortTypeProperty().addListener((observableValue, sortType, t1) -> {
        if (!isResettingSort) {
          disablePropertyResetSorting().set(false);
        }
      });
    });

    treeTableView.getColumns().forEach(this::initColumnAlignment);
  }

  private <S, T> void initColumnAlignment(TreeTableColumn<S, T> column) {
    ColumnAlignment columnAlignment = ColumnAlignment.findByColumnName(column.getId());
    column.setStyle(columnAlignment.getAlignment());
  }

  public void setVehicleConfigs(List<VehicleConfigDTO> vehicleConfigs) {
    this.vehicleConfigs = vehicleConfigs;
    this.partLists = vehicleConfigs.stream().map(VehicleConfigDTO::getVehiclePartList).toList();

    createTree();
    addColumnsForPartLists();
    highlightManager.setStyleToTreeTableColumnGroups(treeTableView, false);
    treeTableView.makeFilterable();
  }

  private void addColumnsForPartLists() {
    boolean lastColumnIsDiff = false;
    for (int i = 0; i < partLists.size(); i++) {
      lastColumnIsDiff = false;
      VehiclePartListDTO partList = partLists.get(i);
      Long partListId = partList.getId();
      VehicleConfigDTO vehicleConfig = partList.getVehicleConfig();
      TreeTableColumn<PartListCompareRow, ?> groupColumn = new TreeTableColumn<>(getName(vehicleConfig));

      groupColumn.getColumns().add(createColumn(partListId, EfsMaraProperties.PARTNUMBER_FORMATTED, 120));
      groupColumn.getColumns().add(createColumn(partListId, EfsProperty.AP, 50));
      groupColumn.getColumns().add(createColumn(partListId, EfsProperty.SET_KEY, 55));
      groupColumn.getColumns().add(createColumn(partListId, EfsProperty.COST_GROUP, 60));
      groupColumn.getColumns().add(createIntegerColumn(partListId, EfsProperty.QUANTITY, 60));
      groupColumn.getColumns().add(createColumn(partListId, EfsProperty.QUANTITY_UNIT, 52));
      groupColumn.getColumns().add(createColumn(partListId, EfsProperty.WEIGHT_CONTROL_FLAG, 50));
      groupColumn.getColumns().add(createDoubleColumn(partListId, EfsProperty.NODE_WEIGHT, 75));
      groupColumn.getColumns().add(createDoubleColumn(partListId, EfsProperty.WEIGHT, 75));
      groupColumn.getColumns().add(createDoubleColumn(partList.getId(), EfsMaraProperties.WEIGHT_PRIO, 75));
      groupColumn.getColumns().add(createIntegerColumn(partList.getId(), EfsMaraProperties.WEIGHT_QUALITY, 70));
      groupColumn.getColumns().add(createColumn(partListId, EfsProperty.PR_NUMBER_RULE, 120));

      groupColumn.getColumns().forEach(c -> treeTableView.makeHeaderWrappable(c));

      groupColumn.getColumns().forEach(this::initColumnAlignment);

      treeTableView.getColumns().add(groupColumn);
      //Add diff columns for every two part lists.
      if (i % 2 == 1) {
        VehiclePartListDTO firstPartList = partLists.get(i - 1);

        TreeTableColumn<PartListCompareRow, Double> difColumn = createDifferenceColumn(firstPartList, partList, 150);
        treeTableView.getColumns().add(difColumn);
        deltaColumnMap.put(treeTableView.getColumns().size() - 1, difColumn);
        lastColumnIsDiff = true;
      }
    }
    if (!lastColumnIsDiff) {
      TreeTableColumn<PartListCompareRow, Double> difColumn = createDifferenceColumn(
        partLists.get(partLists.size() - 2), partLists.get(partLists.size() - 1), 150);
      treeTableView.getColumns().add(difColumn);
      deltaColumnMap.put(treeTableView.getColumns().size() - 1, difColumn);
    }
  }

  private void createTree() {
    PartlistCompareTreeItem root = combineStrategy.createTree(partLists);
    treeTableView.setRoot(root);
    initSorting();

    root.setExpanded(true);
    root.getChildren().forEach(e -> e.setExpanded(true));
  }

  @Override
  public Tab getControl() {
    return partlistTab;
  }

  @Override
  public Parent getStyleableParent() {
    return partlistTabContent;
  }

  private TreeItem<PartListCompareRow> getSelectedTreeItem() {
    return treeTableView.getSelectionModel().getSelectedItem();
  }

  @Override
  public void openPathSelectionDialog() {
    Dialog<SelectionResult> d = new NodeIdSelectionDialog(lastResult);

    if (d.showAndWait().isPresent()) {
      SelectionResult result = d.getResult();
      List<MethodWrapper> selectedProperties = result.getSelectedProperties();
      if (!selectedProperties.isEmpty()) {
        lastResult = result;

        List<Method> methods = selectedProperties.stream().map(MethodWrapper::method).collect(Collectors.toList());

        ReflectionPathNodeIdentityProvider newIdenityPRovider = new ReflectionPathNodeIdentityProvider(methods);
        newIdenityPRovider.setCheckPath(result.isCheckPath());
        newIdenityPRovider.setCombineAll(result.isCombineAll());
        combineStrategy = new DefaultCombineStrategy(newIdenityPRovider);
        createTree();
      }
    }
  }

  @Override
  public void handleActionNavigateBack() {

  }

  @Override
  public void handleActionNavigateForward() {

  }

  @Override
  public void handleActionCollapseTree() {
    if (getSelectedTreeItem() != null) {
      ExpandCollapseUtil.setExpanded(treeTableView, getSelectedTreeItem(), false, true);
    } else {
      ExpandCollapseUtil.setExpanded(treeTableView, treeTableView.getRoot(), false, false);
    }
  }

  @Override
  public void handleActionCollapseAllTree() {
    ExpandCollapseUtil.collapseAll(treeTableView.getRoot());

  }

  @Override
  public void handleActionExpandTree() {
    if (getSelectedTreeItem() != null) {
      ExpandCollapseUtil.setExpanded(treeTableView, getSelectedTreeItem(), true, true);
    } else {
      ExpandCollapseUtil.setExpanded(treeTableView, treeTableView.getRoot(), true, false);
    }
  }

  @Override
  public void handleActionExpandAllTree() {
    ExpandCollapseUtil.expandAll(treeTableView.getRoot());
  }

  @Override
  public void handleActionResetSorting() {
    isResettingSort = true;
    initSorting();

    disablePropertyResetSorting().set(true);
    isResettingSort = false;
  }

  private void initSorting() {
    treeTableView.getSortOrder().setAll(colTisSort);
  }

  public void handleActionClearFilters() {
    treeTableView.clearFilters();
    disablePropertyClearFilters().set(true);
  }

  @Override
  public BooleanProperty disablePropertyNavigateBack() {
    return navigationBackProperty;
  }

  @Override
  public BooleanProperty disablePropertyNavigateForward() {
    return navigationForwardProperty;
  }

  @Override
  public void actionReopenCompareTabs() {
    EventBus.getInstance().post(new ReopenCompareTabsEvent(vehicleConfigs));
  }

  private TreeTableColumn<PartListCompareRow, String> createColumn(Long partListId, EfsProperty<String> property,
    double width) {
    TreeTableColumn<PartListCompareRow, String> column = new TreeTableColumn<>(
      I18N.getString(property.getPropertyId()));
    column.setId(property.getColumnId());
    column.setCellValueFactory(data -> {

      PartListCompareRow row = data.getValue().getValue();
      if (row != null) {
        EfsElementDTO element = row.getBaseElement();
        if (partListId != null) {
          element = row.getElement(partListId);
        }
        if (element != null) {
          return new SimpleStringProperty(property.getGetter().apply(element));
        }
      }
      return null;
    });
    column.setCellFactory(createCellFactory(property));
    return column;
  }

  private TreeTableColumn<PartListCompareRow, Double> createDoubleColumn(Long vehiclePartListId,
    EfsProperty<Double> property, double width) {
    TreeTableColumn<PartListCompareRow, Double> column = new TreeTableColumn<>(property.getPropertyName());
    column.setId(property.getColumnId());
    column.setCellValueFactory(data -> {
      PartListCompareRow row = data.getValue().getValue();
      if (row != null) {
        EfsElementDTO element = row.getBaseElement();
        if (vehiclePartListId != null) {
          element = row.getElement(vehiclePartListId);
        }
        if (element != null) {
          Double value = property.getGetter().apply(element);
          return new SimpleObjectProperty<>(value);
        }
      }
      return null;
    });
    column.setCellFactory(createNumberCellFactory(property, new DoubleStringConverter()));
    return column;
  }

  private TreeTableColumn<PartListCompareRow, Integer> createIntegerColumn(Long vehiclePartListId,
    EfsProperty<Integer> property, double width) {
    TreeTableColumn<PartListCompareRow, Integer> column = new TreeTableColumn<>(property.getPropertyName());
    column.setId(property.getColumnId());
    column.setCellValueFactory(data -> {
      PartListCompareRow row = data.getValue().getValue();
      if (row != null) {
        EfsElementDTO element = row.getBaseElement();
        if (vehiclePartListId != null) {
          element = row.getElement(vehiclePartListId);
        }
        if (element != null) {
          Integer value = property.getGetter().apply(element);
          return new SimpleObjectProperty<>(value);
        }
      }
      return null;
    });
    column.setCellFactory(createNumberCellFactory(property, new IntegerStringConverter()));
    return column;
  }

  private TreeTableColumn<PartListCompareRow, Long> createLongColumn(Long vehiclePartListId, EfsProperty<Long> property,
    double width) {
    TreeTableColumn<PartListCompareRow, Long> column = new TreeTableColumn<>(property.getPropertyName());
    column.setId(property.getColumnId());
    column.setCellValueFactory(data -> {
      PartListCompareRow row = data.getValue().getValue();
      if (row != null) {
        EfsElementDTO element = row.getBaseElement();
        if (vehiclePartListId != null) {
          element = row.getElement(vehiclePartListId);
        }
        if (element != null) {
          Long value = property.getGetter().apply(element);
          return new SimpleObjectProperty<>(value);
        }
      }
      return null;
    });
    column.setCellFactory(createNumberCellFactory(property, new LongStringConverter()));
    return column;
  }

  private TreeTableColumn<PartListCompareRow, Double> createDifferenceColumn(VehiclePartListDTO firstPartList,
    VehiclePartListDTO secondPartList, double width) {
    VehicleConfigDTO vehicleConfig1 = firstPartList.getVehicleConfig();
    VehicleConfigDTO vehicleConfig2 = secondPartList.getVehicleConfig();
    TreeTableColumn<PartListCompareRow, Double> column = new TreeTableColumn<>(
      "Differenz\n" + getName(vehicleConfig1) + "\n zu \n" + getName(vehicleConfig2));
    column.setId("difference");
    column.setCellValueFactory(data -> {
      PartListCompareRow row = data.getValue().getValue();
      if (row != null) {
        EfsElementDTO firstElement = row.getElement(firstPartList.getId());
        double weightFirst = firstElement != null ? firstElement.getNodeWeight() : 0d;

        EfsElementDTO secondElement = row.getElement(secondPartList.getId());
        double weightSecond = secondElement != null ? secondElement.getNodeWeight() : 0d;

        double diff = weightSecond - weightFirst;
        return new SimpleObjectProperty<>(diff);
      }
      return null;
    });
    column.setCellFactory(createNumberCellFactory(null, new DoubleStringConverter()));
    return column;
  }

  private String getName(VehicleConfigDTO vehicleConfig1) {
    return vehicleConfig1 == null ? "" : vehicleConfig1.getName();
  }

  private Callback<TreeTableColumn<PartListCompareRow, String>, TreeTableCell<PartListCompareRow, String>> createCellFactory(
    EfsProperty<?> property) {
    return new Callback<>() {
      @Override
      public TreeTableCell<PartListCompareRow, String> call(TreeTableColumn<PartListCompareRow, String> column) {
        return new TreeTableCell<>() {
          @Override
          protected void updateItem(String s, boolean b) {
            super.updateItem(s, b);
            setText(s);
            getStyleClass().removeAll(COMPARE_STATUS_ADDED, COMPARE_STATUS_DELETED, COMPARE_STATUS_CHANGED,
              COMPARE_STATUS_CHANGED_ROW);

            TreeTableRow<PartListCompareRow> row = getTreeTableRow();
            if (row != null && !row.isSelected()) {
              TreeItem<PartListCompareRow> treeItem = row.getTreeItem();
              if (treeItem != null) {
                PartListCompareRow value = treeItem.getValue();
                if (PartListCompareStatus.CHANGED.equals(value.getRowStatus())) {
                  setChangedCellStyle(property, value, this);
                }
              }
            }
          }
        };
      }
    };
  }

  private <T extends Number> Callback<TreeTableColumn<PartListCompareRow, T>, TreeTableCell<PartListCompareRow, T>> createNumberCellFactory(
    EfsProperty<?> property, NumberStringConverter<T> converter) {
    return new Callback<>() {
      @Override
      public TreeTableCell<PartListCompareRow, T> call(TreeTableColumn<PartListCompareRow, T> column) {
        return new ReadOnlyTreeTableCell<>(converter, false, true) {
          @Override
          public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            getStyleClass().removeAll(COMPARE_STATUS_ADDED, COMPARE_STATUS_DELETED, COMPARE_STATUS_CHANGED,
              COMPARE_STATUS_CHANGED_ROW);

            TreeTableRow<PartListCompareRow> row = getTreeTableRow();
            if (row != null && !row.isSelected()) {
              TreeItem<PartListCompareRow> treeItem = row.getTreeItem();
              if (treeItem != null) {
                PartListCompareRow value = treeItem.getValue();
                if (PartListCompareStatus.CHANGED.equals(value.getRowStatus())) {
                  setChangedCellStyle(property, value, this);
                }
              }
            }
          }
        };
      }
    };
  }

  private void setChangedCellStyle(EfsProperty<?> property, PartListCompareRow value, TreeTableCell<?, ?> cell) {
    if (property == null) {
      cell.getStyleClass().add(COMPARE_STATUS_CHANGED_ROW);
    } else {
      PartListCompareStatus propChanged = value.getPropertyStatus(property);
      switch (propChanged) {
        case CHANGED:
          cell.getStyleClass().add(COMPARE_STATUS_CHANGED);
          break;
        case ADDED:
          cell.getStyleClass().add(COMPARE_STATUS_ADDED);
          break;
        case DELETED:
          cell.getStyleClass().add(COMPARE_STATUS_DELETED);
          break;
        default:
          cell.getStyleClass().add(COMPARE_STATUS_CHANGED_ROW);
      }
    }
  }

  @Override
  public BooleanProperty toggleDisplayDeltaColumnsProperty() {
    if (toggleDisplayDeltaColumnsProperty == null) {
      toggleDisplayDeltaColumnsProperty = new SimpleBooleanProperty(true);

      toggleDisplayDeltaColumnsProperty.addListener((obs, oldVal, newVal) -> displayDeltaColumns(newVal));
    }

    return toggleDisplayDeltaColumnsProperty;
  }

  private void displayDeltaColumns(boolean showDelta) {
    if (showDelta) {
      for (Map.Entry<Integer, TreeTableColumn<PartListCompareRow, ?>> difEntry : deltaColumnMap.entrySet()) {
        treeTableView.getColumns().add(difEntry.getKey(), difEntry.getValue());
      }
    } else {
      treeTableView.getColumns().removeAll(deltaColumnMap.values());
    }
  }
}
