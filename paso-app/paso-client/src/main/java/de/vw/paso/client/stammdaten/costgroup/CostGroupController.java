package de.vw.paso.client.stammdaten.costgroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.GridPane;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.control.treetable.TreeFilteringUpdateEvent;
import de.vw.paso.client.stammdaten.AbstractMasterDataController;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.valueobject.CostGroupVMO;
import de.vw.paso.delegate.stueckliste.costgroup.CostGroupRestClientHolder;
import de.vw.paso.exception.ServerException;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupsDTO;
import de.vw.paso.service.partlist.costgroup.UpdateCostGroupDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "cost-group-tab")
public class CostGroupController extends AbstractMasterDataController<GridPane> implements Initializable {

    private static final String DIALOG_TITLE_ADD = "dialog.costgroup.add.title";
    private static final String DIALOG_TITLE_EDIT = "dialog.costgroup.edit.title";
    private static final String CATEGORY_STYLE_CLASS = "font-bold";

    private final ChangeListener<Long> comboBoxChangeListener = (ov, oldValue, newValue) -> fillComboBox(newValue);

    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBox<Long> comboBox;
    @FXML
    private TreeTableColumn<CostGroupVMO, String> colCostGroup;
    @FXML
    private TreeTableColumn<CostGroupVMO, String> colDescription;
    @FXML
    private TreeTableColumn<CostGroupVMO, String> colParent;
    @FXML
    private GridPane gridPaneTab;
    @FXML
    private CustomTreeTableView<CostGroupVMO> costGroupTreeTableView;

    private PasoWildCardPattern patternSearchTerm;
    private Map<String, List<FilterableTreeItem<CostGroupVMO>>> costGroupsWithParent;
    private Collection<FilterableTreeItem<CostGroupVMO>> costGroupVMOS;

    public CostGroupController() {
        costGroupsWithParent = new HashMap<>();
        costGroupVMOS = new HashSet<>();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> key) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();

        return t -> map.putIfAbsent(key.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        costGroupTreeTableView.setRowFactory(param -> new TreeTableRow<>() {
            @Override
            protected void updateItem(CostGroupVMO item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().remove(CATEGORY_STYLE_CLASS);
                if (empty) {
                    return;
                }

                if (item != null && item.getParent() == null) {
                    getStyleClass().add(CATEGORY_STYLE_CLASS);
                }
            }
        });

        colCostGroup.setCellValueFactory(param -> param.getValue().getValue().costGroupProperty());
        colParent.setCellValueFactory(cellData -> cellData.getValue().getValue().parentProperty());
        colDescription.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());
        costGroupTreeTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelection);
        costGroupTreeTableView.makeFilterable();
    }

    @Override
    protected boolean disableAdd() {
        return false;
    }

    @Override
    protected boolean disableRefresh() {
        return false;
    }

    @Override
    public void start() {
        load();
    }

    @Override
    public GridPane getControl() {
        return gridPaneTab;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    @Override
    public void handleActionAdd() {
        TreeItem<CostGroupVMO> selectedTreeItem = getSelectedTreeItem();
        CostGroupVMO defaultVMO = new CostGroupVMO();
        defaultVMO.setVersion(comboBox.getValue());
        CostGroupVMO costGroupVMO = selectedTreeItem != null ? selectedTreeItem.getValue() : defaultVMO;

        List<String> costGroupParents = getCostGroupParents();

        List<CostGroupVMO> costGroups = costGroupsWithParent.values().stream()
            .map(e -> e.stream().map(FilterableTreeItem::getValue).toList()).flatMap(List::stream)
            .collect(Collectors.toCollection(ArrayList::new));

        CostGroupDialog costGroupDialog = new CostGroupDialog(I18N.getString(DIALOG_TITLE_ADD), costGroupVMO,
            costGroupParents, costGroups);

        Optional<CostGroupVMO> dialogRes = costGroupDialog.showAndWait();

        dialogRes.ifPresent(newCostGroup -> doAsync(() -> {
            CostGroupsDTO result = CostGroupRestClientHolder.getInstance()
                .saveCostGroup(new CostGroupsDTO(List.of(CostGroupVMO.toCostGroupDTO(newCostGroup))));

            for (CostGroupDTO costGroup : result.costGroups()) {
                return CostGroupVMO.toVMO(costGroup);
            }

            throw new ServerException("An exception occurred during cost group save");
        }, saveResult -> load(() -> selectTreeNode(newCostGroup))));
    }

    @Override
    public void handleActionEdit() {
        TreeItem<CostGroupVMO> selectedTreeItem = getSelectedTreeItem();
        CostGroupVMO costGroupVMO = selectedTreeItem != null ? selectedTreeItem.getValue() : new CostGroupVMO();

        List<String> costGroupParents = getCostGroupParents();

        List<CostGroupVMO> costGroups = costGroupsWithParent.values().stream()
            .map(e -> e.stream().map(FilterableTreeItem::getValue).toList()).flatMap(List::stream)
            .collect(Collectors.toCollection(ArrayList::new));

        CostGroupDialog costGroupDialog = new CostGroupDialog(I18N.getString(DIALOG_TITLE_EDIT), costGroupVMO,
            costGroupParents, costGroups);

        Optional<CostGroupVMO> dialogRes = costGroupDialog.showAndWait();

        dialogRes.ifPresent(newCostGroup -> doAsync(() -> {
            CostGroupDTO oldCostGroupDTO = CostGroupVMO.toCostGroupDTO(costGroupVMO);
            CostGroupDTO updatedCostGroupDTO = CostGroupVMO.toCostGroupDTO(newCostGroup);
            UpdateCostGroupDTO updateCostGroupDTO = new UpdateCostGroupDTO(oldCostGroupDTO, updatedCostGroupDTO);

            return CostGroupVMO.toVMO(CostGroupRestClientHolder.getInstance().updateCostGroup(updateCostGroupDTO));
        }, saveResult -> load(() -> selectTreeNode(newCostGroup))));
    }

    @Override
    public void handleActionDelete() {
        TreeItem<CostGroupVMO> selectedTreeItem = getSelectedTreeItem();
        doAsync(() -> {
            CostGroupDTO costGroupDTO = CostGroupVMO.toCostGroupDTO(selectedTreeItem.getValue());
            Long version = costGroupDTO.getVersion();
            String costGroupName = costGroupDTO.getCostGroupName();
            CostGroupRestClientHolder.getInstance().removeCostGroup(version, costGroupName);
        }, () -> selectedTreeItem.getParent().getChildren().remove(selectedTreeItem));
    }

    @Override
    public void handleActionRefresh() {
        load();
    }

    @Override
    public void handleActionResetFilters() {
        disablePropertyResetFilters().set(true);
        costGroupTreeTableView.clearFilters();
    }

    @FXML
    private void handleSearch() {
        setPatternSearchTerm(searchTextField.getText().trim());
        FilterableTreeItem<CostGroupVMO> root = (FilterableTreeItem<CostGroupVMO>) costGroupTreeTableView.getRoot();
        root.predicateProperty().setValue(partGroupVMOTreeItem -> {
            CostGroupVMO vmo = partGroupVMOTreeItem.getValue();
            return patternSearchTerm.matches(vmo.getCostGroup()) != null
                || patternSearchTerm.matches(vmo.getDescription()) != null || (vmo.getParent() != null && (
                patternSearchTerm.matches(vmo.getParent()) != null))
                || patternSearchTerm.matches(Long.toString(vmo.getVersion())) != null;
        });

        costGroupTreeTableView.refresh();
    }

    @Subscribe
    private void handleFilterUpdate(TreeFilteringUpdateEvent event) {
        disablePropertyResetFilters().setValue(costGroupTreeTableView.getColumnToPredicateMap().isEmpty());
    }

    private void load() {
        load(() -> {
        });
    }

    private void load(Runnable uiCallback) {
        doAsync(() -> CostGroupVMO.toVMOs(CostGroupRestClientHolder.getInstance().loadCostGroups().costGroupDTOs()),
            loadedCostGroups -> {
                costGroupsWithParent.clear();
                costGroupVMOS.clear();

                loadComboBox(loadedCostGroups);

                costGroupVMOS = loadedCostGroups.stream().parallel().map(FilterableTreeItem::new)
                    .collect(Collectors.toSet());

                load(costGroupVMOS.stream().filter(cg -> cg.getValue().getVersion().equals(comboBox.getValue()))
                    .collect(Collectors.toSet()));
            }, uiCallback);
    }

    private void load(Collection<FilterableTreeItem<CostGroupVMO>> costGroupVMOS) {
        FilterableTreeItem<CostGroupVMO> root = new FilterableTreeItem<>(new CostGroupVMO());

        costGroupsWithParent = costGroupVMOS.stream().parallel()
            .filter(costGroup -> Objects.equals(costGroup.getValue().getVersion(), comboBox.getValue()))
            .collect(Collectors.groupingBy(cg -> {
                String parent = cg.getValue().getParent();
                return parent == null ? StringConstant.EMPTY : parent;
            }));

        costGroupVMOS.stream().parallel().forEach(vmo -> vmo.getSourceChildren().clear());

        List<FilterableTreeItem<CostGroupVMO>> costGroups = costGroupsWithParent.get(StringConstant.EMPTY);
        if (costGroups != null) {
            costGroups.sort(Comparator.comparing(t -> t.getValue().getCostGroup()));
            for (FilterableTreeItem<CostGroupVMO> item : costGroups) {
                root.getSourceChildren().add(item);
                setChildren(item);
            }
        }

        TreeItem<CostGroupVMO> selectedTreeItem = costGroupTreeTableView.getSelectionModel().getSelectedItem();

        costGroupTreeTableView.setShowRoot(false);
        costGroupTreeTableView.setRoot(root);

        if (selectedTreeItem != null) {
            selectTreeItem(selectedTreeItem.getValue().getCostGroup(), root);
        }
    }

    private void selectTreeItem(String selectedKey, TreeItem<CostGroupVMO> currentTreeItem) {
        if (currentTreeItem == null) {
            return;
        }

        for (TreeItem<CostGroupVMO> childTree : currentTreeItem.getChildren()) {
            String key = childTree.getValue().getCostGroup();
            if (!selectedKey.equals(key)) {
                expandItem(childTree);

                costGroupTreeTableView.getSelectionModel().select(childTree);
                costGroupTreeTableView.scrollToCenter(childTree);
                costGroupTreeTableView.requestFocus();

                return;
            }

            selectTreeItem(selectedKey, childTree);
        }
    }

    private void expandItem(TreeItem<CostGroupVMO> child) {
        TreeItem<CostGroupVMO> parent = child.getParent();
        if (parent == null) {
            return;
        }

        parent.setExpanded(true);
        expandItem(parent);
    }

    private void setChildren(FilterableTreeItem<CostGroupVMO> root) {
        List<FilterableTreeItem<CostGroupVMO>> items = costGroupsWithParent.get(root.getValue().getCostGroup());
        if (items == null) {
            return;
        }

        items.sort(Comparator.comparing(item -> item.getValue().getCostGroup()));
        for (FilterableTreeItem<CostGroupVMO> item : items) {
            setChildren(item);
            root.getSourceChildren().add(item);
        }
    }

    private void fillComboBox(Long newValue) {
        costGroupTreeTableView.clearFilters();

        Collection<FilterableTreeItem<CostGroupVMO>> filteredTreeItems = costGroupVMOS.stream()
            .filter(vmo -> vmo.getValue().getVersion().equals(newValue)).distinct().toList();
        load(filteredTreeItems);
    }

    private void loadComboBox(List<CostGroupVMO> costGroups) {
        Long lastVersion = comboBox.getValue();

        comboBox.valueProperty().removeListener(comboBoxChangeListener);

        ObservableList<Long> versions = costGroups.stream().filter(distinctByKey(CostGroupVMO::getVersion))
            .sorted(Comparator.comparing(CostGroupVMO::getVersion)).map(CostGroupVMO::getVersion).distinct()
            .collect(Collectors.toCollection(FXCollections::observableArrayList));
        comboBox.setItems(versions);

        if (lastVersion == null) {
            comboBox.getSelectionModel().selectLast();
        } else {
            comboBox.getSelectionModel().select(lastVersion);
        }

        comboBox.valueProperty().addListener(comboBoxChangeListener);
    }

    private TreeItem<CostGroupVMO> getSelectedTreeItem() {
        return costGroupTreeTableView.getSelectionModel().getSelectedItem();
    }

    private void selectTreeNode(CostGroupVMO costGroup) {
        String parent = costGroup.getParent() == null ? StringConstant.EMPTY : costGroup.getParent();
        Optional<FilterableTreeItem<CostGroupVMO>> itemToSelect = costGroupsWithParent.get(parent).stream()
            .filter(cg -> cg.getValue().getCostGroup().equals(costGroup.getCostGroup())).findAny();

        itemToSelect.ifPresent(item -> {
            TreeItem<CostGroupVMO> itemToExpand = item.getParent();
            while (itemToExpand != null) {
                itemToExpand.setExpanded(true);
                itemToExpand = itemToExpand.getParent();
            }

            costGroupTreeTableView.getSelectionModel().select(item);
        });
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private List<String> getCostGroupParents() {
        return Stream.concat(costGroupsWithParent.get(StringConstant.EMPTY).stream(),
                costGroupsWithParent.get(StringConstant.EMPTY).stream()
                    .map(costGroup -> costGroupsWithParent.getOrDefault(costGroup.getValue().getCostGroup(), List.of()))
                    .flatMap(List::stream)).map(costGroup -> costGroup.getValue().getCostGroup())
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
