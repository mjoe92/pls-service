package de.vw.paso.client.stammdaten.partgroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.GridPane;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.control.treetable.TreeFilteringUpdateEvent;
import de.vw.paso.client.stammdaten.AbstractMasterDataController;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.delegate.partgroup.PartGroupRestClientHolder;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.utility.SpecPartGroupCategory;
import de.vw.paso.utility.StringConstant;

@FXController(name = "part-group-tab")
public class PartGroupController extends AbstractMasterDataController<GridPane> {

    private static final String DIALOG_TITLE_ADD = "dialog.partgroup.add.title";
    private static final String DIALOG_TITLE_EDIT = "dialog.partgroup.edit.title";

    private static final Integer NO_UGR = -1;

    private static final String CATEGORY_STYLE_CLASS = "font-bold";

    @FXML
    private TextField searchTextField;
    @FXML
    private GridPane gridPaneTab;
    @FXML
    private CustomTreeTableView<PartGroupVMO> partGroupTreeTableView;
    @FXML
    private TreeTableColumn<PartGroupVMO, Integer> colMgr;
    @FXML
    private TreeTableColumn<PartGroupVMO, Integer> colUgr;
    @FXML
    private TreeTableColumn<PartGroupVMO, String> colDescription;

    private PasoWildCardPattern patternSearchTerm;

    private List<PartGroupVMO> allPartGroupVMOs = new ArrayList<>();

    private final Set<Long> expandedNodes;
    private final Map<Integer, FilterableTreeItem<PartGroupVMO>> categoryTreeItemMap;
    private final Table<Integer, Integer, FilterableTreeItem<PartGroupVMO>> mgrTreeItemTable;

    public PartGroupController() {
        expandedNodes = new HashSet<>();
        categoryTreeItemMap = new HashMap<>();
        mgrTreeItemTable = HashBasedTable.create();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        partGroupTreeTableView.setRowFactory(param -> new TreeTableRow<>() {
            @Override
            protected void updateItem(PartGroupVMO item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().remove(CATEGORY_STYLE_CLASS);
                if (empty) {
                    return;
                }

                if (item != null && item.isCategory()) {
                    getStyleClass().add(CATEGORY_STYLE_CLASS);
                }
            }
        });

        colMgr.setCellValueFactory(param -> {
            PartGroupVMO value = param.getValue().getValue();
            return value.getMgr() == null ? value.categoryProperty() : value.mgrProperty();
        });
        colMgr.setCellFactory(param -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);

                TreeTableRow<PartGroupVMO> row = getTableRow();
                if (row == null || row.getTreeItem() == null || row.getTreeItem().getValue() == null) {
                    return;
                }

                PartGroupVMO value = row.getTreeItem().getValue();
                String text;
                if (value.getMgr() == null) {
                    text = Objects.requireNonNullElseGet(SpecPartGroupCategory.getStringForCategory(item),
                        () -> item + StringConstant.EMPTY);
                } else {
                    text = PartGroupUtil.groupToString(value.getMgr());
                    if (value.getMgrEnd() != null) {
                        text += StringConstant.SPACE_DASH_SPACE + PartGroupUtil.groupToString(value.getMgrEnd());
                    }
                }

                setText(text);
            }
        });

        colUgr.setCellValueFactory(param -> param.getValue().getValue().ugrProperty());
        colUgr.setCellFactory(param -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                setText(null);

                TreeTableRow<PartGroupVMO> row = getTableRow();
                if (row != null && row.getTreeItem() != null && row.getTreeItem().getValue() != null) {
                    setText(PartGroupUtil.groupToString(item));
                }
            }
        });
        colDescription.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());

        partGroupTreeTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelection);
        partGroupTreeTableView.makeFilterable();
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
        TreeItem<PartGroupVMO> selectedTreeItem = getSelectedTreeItem();
        PartGroupVMO value = selectedTreeItem == null ? new PartGroupVMO() : selectedTreeItem.getValue();
        PartGroupDialog partGroupDialog = new PartGroupDialog(I18N.getString(DIALOG_TITLE_ADD),
            new PartGroupVMO(value.getCategory(), value.getMgr(), value.getUgr()), allPartGroupVMOs);
        Optional<PartGroupVMO> dialogResult = partGroupDialog.showAndWait();
        dialogResult.ifPresent(e -> doAsync(
            () -> PartGroupVMO.toVMO(PartGroupRestClientHolder.getInstance().addPartGroup(PartGroupVMO.toPartGroup(e))),
            result -> load(() -> selectTreeNode(result))));
    }

    @Override
    public void handleActionEdit() {
        TreeItem<PartGroupVMO> selectedTreeItem = getSelectedTreeItem();
        PartGroupVMO existingVMO = selectedTreeItem.getValue();
        PartGroupDialog partGroupDialog = new PartGroupDialog(I18N.getString(DIALOG_TITLE_EDIT), existingVMO,
            allPartGroupVMOs);
        partGroupDialog.showAndWait().ifPresent(
            result -> doAsync(() -> PartGroupRestClientHolder.getInstance().update(PartGroupVMO.toPartGroup(result)),
                saveResult -> load(() -> selectTreeNode(result))));

    }

    @Override
    public void handleActionDelete() {
        TreeItem<PartGroupVMO> selectedTreeItem = getSelectedTreeItem();
        doAsync(() -> {
            PartGroupDTO partGroup = PartGroupVMO.toPartGroup(selectedTreeItem.getValue());
            PartGroupRestClientHolder.getInstance()
                .delete(partGroup.isMgr(), partGroup.getMgr(), partGroup.isUgr(), partGroup.getUgr());
        }, () -> selectedTreeItem.getParent().getChildren().remove(selectedTreeItem));
    }

    @Override
    public <T> void handleTableSelection(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        super.handleTableSelection(observable, oldValue, newValue);
        if (newValue instanceof FilterableTreeItem<?> filterItem && filterItem.getValue() instanceof PartGroupVMO) {
            disablePropertyRemove().set(PartGroupUtil.isCategory((PartGroupVMO) filterItem.getValue()));
        }
    }

    @Override
    public void handleActionRefresh() {
        load();
    }

    @Override
    public void handleActionResetFilters() {
        disablePropertyResetFilters().set(true);
        partGroupTreeTableView.clearFilters();
    }

    @Override
    public String getCustomDeleteMessageKey() {
        return getSelectedTreeItem().getChildren().isEmpty() ? super.getCustomDeleteMessageKey()
            : "dialog.delete.children.msg";
    }

    @Override
    protected boolean disableAdd() {
        return false;
    }

    @Override
    protected boolean disableRefresh() {
        return false;
    }

    @FXML
    private void handleSearch() {
        setPatternSearchTerm(searchTextField.getText());
        FilterableTreeItem<PartGroupVMO> root = (FilterableTreeItem<PartGroupVMO>) partGroupTreeTableView.getRoot();
        root.predicateProperty().setValue(partGroupVMOTreeItem -> {
            if (partGroupVMOTreeItem.getValue().getCategory() != null) {
                PartGroupVMO vmo = partGroupVMOTreeItem.getValue();
                return patternSearchTerm.matches(vmo.getCategory() + StringConstant.EMPTY) != null
                    || patternSearchTerm.matches(vmo.getMgr() + StringConstant.EMPTY) != null
                    || patternSearchTerm.matches(vmo.getUgr() + StringConstant.EMPTY) != null
                    || patternSearchTerm.matches(vmo.getDescription()) != null;
            }

            return false;
        });
        partGroupTreeTableView.refresh();
    }

    @Subscribe
    private void handleFilterUpdate(TreeFilteringUpdateEvent event) {
        disablePropertyResetFilters().setValue(partGroupTreeTableView.getColumnToPredicateMap().isEmpty());
    }

    private void load() {
        load(() -> {
        });
    }

    private void load(Runnable uiCallback) {
        doAsync(() -> PartGroupVMO.toVMOs(PartGroupRestClientHolder.getInstance().loadPartGroups().partGroupDTOs()),
            loadedPartGroups -> {
                FilterableTreeItem<PartGroupVMO> root = new FilterableTreeItem<>(new PartGroupVMO());

                PartGroupUtil.sortByParent(loadedPartGroups);

                categoryTreeItemMap.clear();
                mgrTreeItemTable.clear();

                allPartGroupVMOs = loadedPartGroups;

                Collection<PartGroupVMO> elementsWithoutParent = new ArrayList<>();
                for (PartGroupVMO item : loadedPartGroups) {
                    FilterableTreeItem<PartGroupVMO> ti = new FilterableTreeItem<>(item);
                    ti.expandedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            expandedNodes.add(item.getId());
                        } else {
                            expandedNodes.remove(item.getId());
                        }
                    });

                    if (item.isCategory()) {
                        root.getSourceChildren().add(ti);
                        categoryTreeItemMap.put(item.getCategory(), ti);
                    } else if (item.isMgr()) {
                        FilterableTreeItem<PartGroupVMO> categoryItem = categoryTreeItemMap.get(item.getCategory());
                        if (categoryItem == null) {
                            elementsWithoutParent.add(item);
                        } else {
                            categoryItem.getSourceChildren().add(ti);
                            if (!(item.getCategory() >= 100 && item.getMgr() != 52)) {
                                if (item.getMgrEnd() != null) {
                                    for (int i = item.getMgr(); i <= item.getMgrEnd(); i++) {
                                        mgrTreeItemTable.put(i, NO_UGR, ti);
                                    }
                                } else {
                                    mgrTreeItemTable.put(item.getMgr(), NO_UGR, ti);
                                }
                            }
                        }
                    } else {
                        FilterableTreeItem<PartGroupVMO> mgrItem = mgrTreeItemTable.get(item.getMgr(), NO_UGR);
                        if (mgrItem == null) {
                            elementsWithoutParent.add(item);
                        } else {
                            mgrItem.getSourceChildren().add(ti);
                            mgrTreeItemTable.put(item.getMgr(), item.getUgr(), ti);
                        }
                    }
                    if (expandedNodes.contains(item.getId())) {
                        ti.setExpanded(true);
                    }
                }

                if (!elementsWithoutParent.isEmpty()) {
                    throw new ParentNotFoundException("Found missing Parents for Part Group");
                }

                TreeItem<PartGroupVMO> selectedTreeItem = partGroupTreeTableView.getSelectionModel().getSelectedItem();

                partGroupTreeTableView.setShowRoot(false);
                partGroupTreeTableView.setRoot(root);

                if (selectedTreeItem != null) {
                    selectTreeItem(selectedTreeItem.getValue().getId(), root);
                }
            }, uiCallback);
    }

    private void selectTreeNode(PartGroupVMO vmo) {
        FilterableTreeItem<PartGroupVMO> itemToSelect = mgrTreeItemTable.get(vmo.getMgr(),
            vmo.getUgr() == null ? NO_UGR : vmo.getUgr());
        TreeItem<PartGroupVMO> itemToExpand = itemToSelect.getParent();
        while (itemToExpand != null) {
            itemToExpand.setExpanded(true);
            itemToExpand = itemToExpand.getParent();
        }

        partGroupTreeTableView.getSelectionModel().select(itemToSelect);
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private TreeItem<PartGroupVMO> getSelectedTreeItem() {
        return partGroupTreeTableView.getSelectionModel().getSelectedItem();
    }

    private void selectTreeItem(Long selectedKey, TreeItem<PartGroupVMO> currentTreeItem) {
        if (currentTreeItem == null) {
            return;
        }

        for (TreeItem<PartGroupVMO> childTree : currentTreeItem.getChildren()) {
            Long key = childTree.getValue().getId();
            if (selectedKey.equals(key)) {
                expandItem(childTree);

                partGroupTreeTableView.getSelectionModel().select(childTree);
                partGroupTreeTableView.scrollToCenter(childTree);
                partGroupTreeTableView.requestFocus();

                return;
            }

            selectTreeItem(selectedKey, childTree);
        }
    }

    private void expandItem(TreeItem<PartGroupVMO> child) {
        TreeItem<PartGroupVMO> parent = child.getParent();
        if (parent == null) {
            return;
        }

        parent.setExpanded(true);
        expandItem(parent);
    }
}
