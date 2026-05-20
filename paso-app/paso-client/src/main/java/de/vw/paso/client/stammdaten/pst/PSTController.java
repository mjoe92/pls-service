package de.vw.paso.client.stammdaten.pst;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
import de.vw.paso.client.valueobject.PstVMO;
import de.vw.paso.delegate.stueckliste.pst.PstRestClientHolder;
import de.vw.paso.service.masterdata.pst.PstDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "pst-tab")
public class PSTController extends AbstractMasterDataController<GridPane> implements Initializable {

    private static final String CATEGORY_STYLE_CLASS = "font-bold";

    @FXML
    private GridPane gridPaneTab;
    @FXML
    private TextField searchTextField;
    @FXML
    private CustomTreeTableView<PstVMO> pstTreeTableView;
    @FXML
    private TreeTableColumn<PstVMO, String> colPST;
    @FXML
    private TreeTableColumn<PstVMO, String> colDescriptionDe;
    @FXML
    private TreeTableColumn<PstVMO, String> colDescriptionEng;
    @FXML
    private TreeTableColumn<PstVMO, String> colParent;

    private PasoWildCardPattern patternSearchTerm;
    private Map<Long, PstVMO> pstVMOMap;
    private Map<Long, List<FilterableTreeItem<PstVMO>>> pstDTOMapWithParents;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        pstTreeTableView.setRowFactory(param -> new TreeTableRow<>() {
            @Override
            protected void updateItem(PstVMO item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().remove(CATEGORY_STYLE_CLASS);
                if (empty) {
                    return;
                }

                if (item != null && item.getParentId() == null) {
                    getStyleClass().add(CATEGORY_STYLE_CLASS);
                }
            }
        });

        colPST.setCellValueFactory(cellData -> cellData.getValue().getValue().getNameAsObservableValue());
        colDescriptionDe.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getValue().getDescriptionDe()));
        colDescriptionEng.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getValue().getDescriptionEng()));
        colParent.setCellValueFactory(
            cellData -> Optional.ofNullable(pstVMOMap.get(cellData.getValue().getValue().getParentId()))
                .map(PstVMO::getNameAsObservableValue).orElse(new SimpleStringProperty(StringConstant.EMPTY)));
        pstTreeTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelection);
        pstTreeTableView.makeFilterable();
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
    public GridPane getControl() {
        return gridPaneTab;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    @Override
    public void handleActionAdd() {
        TreeItem<PstVMO> selectedTreeItem = getSelectedTreeItem();
        PstVMO pstVMO = selectedTreeItem == null ? new PstVMO() : selectedTreeItem.getValue();

        PstDialog dialog = new PstDialog(I18N.getString("dialog.pst.add.title"), pstVMOMap, pstVMO, false);
        dialog.showAndWait().ifPresent(pst -> doAsync(() -> PstRestClientHolder.getInstance().addPst(pst),
            pstRes -> load(() -> selectTreeNode(new PstVMO(pst)))));
    }

    @Override
    public void handleActionEdit() {
        TreeItem<PstVMO> selectedTreeItem = getSelectedTreeItem();
        PstVMO pstVMO = selectedTreeItem != null ? selectedTreeItem.getValue() : new PstVMO();

        PstDialog dialog = new PstDialog(I18N.getString("dialog.pst.edit.title"), pstVMOMap, pstVMO, true);
        dialog.showAndWait().ifPresent(pst -> doAsync(() -> PstRestClientHolder.getInstance().editPst(pst),
            pstRes -> load(() -> selectTreeNode(new PstVMO(pst)))));
    }

    @Override
    public void handleActionDelete() {
        TreeItem<PstVMO> selectedTreeItem = getSelectedTreeItem();
        if (selectedTreeItem == null || selectedTreeItem.getValue() == null) {
            return;
        }

        doAsync(() -> PstRestClientHolder.getInstance().deletePst(selectedTreeItem.getValue().getId()), this::load);
    }

    @Override
    public void handleActionRefresh() {
        load();
    }

    @Override
    public void handleActionResetFilters() {
        disablePropertyResetFilters().set(true);
        pstTreeTableView.clearFilters();
    }

    @Override
    public void start() {
        load();
    }

    @FXML
    public void handleSearch() {
        setPatternSearchTerm(searchTextField.getText().trim());
        FilterableTreeItem<PstVMO> root = (FilterableTreeItem<PstVMO>) pstTreeTableView.getRoot();
        root.predicateProperty().setValue(pstVMOTreeItem -> {
            PstVMO vmo = pstVMOTreeItem.getValue();
            return (patternSearchTerm.matches(vmo.getNameAsObservableValue().get()) != null) || (
                patternSearchTerm.matches(vmo.getDescription().get()) != null) || (vmo.getParentId() != null && (
                patternSearchTerm.matches(pstVMOMap.get(vmo.getParentId()).getNameAsObservableValue().get()) != null));
        });
        pstTreeTableView.refresh();
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private void load() {
        load(() -> {
        });
    }

    private void load(Runnable uiCallback) {
        doAsync(() -> PstRestClientHolder.getInstance().getPsts().pstDTOS(), pstDTOs -> {
            pstVMOMap = pstDTOs.stream().collect(Collectors.toMap(PstDTO::getId, PstVMO::new));

            load(pstVMOMap.values());
        }, uiCallback);
    }

    private void load(Collection<PstVMO> pstVmos) {
        FilterableTreeItem<PstVMO> root = new FilterableTreeItem<>(new PstVMO());

        pstDTOMapWithParents = pstVmos.stream().map(FilterableTreeItem::new).collect(Collectors.groupingBy(pst -> {
            Long parentId = pst.getValue().getPstProperty().getParentId();
            return parentId == null ? 0L : parentId;
        }));

        pstDTOMapWithParents.getOrDefault(0L, List.of()).stream()
            .sorted(Comparator.comparing(t -> t.getValue().getNameAsObservableValue().get())).forEach(item -> {
                root.getSourceChildren().add(item);
                setChildren(item);
            });

        setChildren(root);

        TreeItem<PstVMO> selectedTreeItem = pstTreeTableView.getSelectionModel().getSelectedItem();

        pstTreeTableView.setShowRoot(false);
        pstTreeTableView.setRoot(root);

        selectItems(selectedTreeItem);
    }

    private void selectItems(TreeItem<PstVMO> selectedTreeItem) {
        if (selectedTreeItem == null) {
            return;
        }

        Long selectedItemId = selectedTreeItem.getValue().getId();
        for (TreeItem<PstVMO> treeItem : pstTreeTableView.getRoot().getChildren()) {
            Long id = treeItem.getValue().getId();
            if (id.equals(selectedItemId)) {
                pstTreeTableView.getSelectionModel().select(treeItem);

                return;
            }
        }

        TreeItem<PstVMO> firstSelected = pstTreeTableView.getRoot().getChildren().getFirst();
        pstTreeTableView.scrollToCenter(firstSelected);
        pstTreeTableView.requestFocus();
    }

    private void setChildren(FilterableTreeItem<PstVMO> root) {
        Long currentRootSetKey = root.getValue().getId();
        Collection<FilterableTreeItem<PstVMO>> items = pstDTOMapWithParents.get(currentRootSetKey);
        if (items == null) {
            return;
        }

        List<FilterableTreeItem<PstVMO>> toSort = new ArrayList<>(items);
        toSort.sort(Comparator.comparing(t -> t.getValue().getNameAsObservableValue().get()));
        for (FilterableTreeItem<PstVMO> it : toSort) {
            setChildren(it);
            root.getSourceChildren().add(it);
        }
    }

    private void selectTreeNode(PstVMO vmo) {
        Long parent = vmo.getParentId() == null ? 0L : pstVMOMap.get(vmo.getParentId()).getParentId();
        Optional<FilterableTreeItem<PstVMO>> itemToSelect = pstDTOMapWithParents.get(parent).stream()
            .filter(cg -> cg.getValue().getNameAsObservableValue().equals(vmo.getNameAsObservableValue())).findAny();

        itemToSelect.ifPresent(item -> {
            TreeItem<PstVMO> itemToExpand = item.getParent();
            while (itemToExpand != null) {
                itemToExpand.setExpanded(true);
                itemToExpand = itemToExpand.getParent();
            }

            pstTreeTableView.getSelectionModel().select(item);
        });
    }

    private TreeItem<PstVMO> getSelectedTreeItem() {
        return pstTreeTableView.getSelectionModel().getSelectedItem();
    }

    @Subscribe
    private void handleFilterUpdate(TreeFilteringUpdateEvent event) {
        disablePropertyResetFilters().setValue(pstTreeTableView.getColumnToPredicateMap().isEmpty());
    }
}
