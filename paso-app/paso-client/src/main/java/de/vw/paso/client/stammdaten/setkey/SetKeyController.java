package de.vw.paso.client.stammdaten.setkey;

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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.control.treetable.TreeFilteringUpdateEvent;
import de.vw.paso.client.stammdaten.AbstractMasterDataController;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.valueobject.SetKeyVMO;
import de.vw.paso.delegate.stammdaten.setversion.SetVersionRestClientHolder;
import de.vw.paso.delegate.stueckliste.setkey.SetKeyRestClientHolder;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.partlist.setkey.SetKeysDTO;
import de.vw.paso.service.partlist.setkey.UpdateSetKeyDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "set-key-tab")
public class SetKeyController extends AbstractMasterDataController<GridPane> implements Initializable {

    private static final String DIALOG_TITLE_ADD = "dialog.set.add.title";
    private static final String DIALOG_TITLE_EDIT = "dialog.set.edit.title";
    private static final String CATEGORY_STYLE_CLASS = "font-bold";

    @FXML
    private ComboBox<SetVersionDTO> comboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TreeTableColumn<SetKeyVMO, String> colSetKey;
    @FXML
    private TreeTableColumn<SetKeyVMO, String> colDescription;
    @FXML
    private TreeTableColumn<SetKeyVMO, String> colParent;
    @FXML
    private GridPane gridPaneTab;
    @FXML
    private CustomTreeTableView<SetKeyVMO> setKeyTreeTableView;

    private PasoWildCardPattern patternSearchTerm;
    private Map<String, List<FilterableTreeItem<SetKeyVMO>>> setKeysWithParent;
    private Set<FilterableTreeItem<SetKeyVMO>> setKeyVMOS;

    public SetKeyController() {
        setKeysWithParent = new HashMap<>();
        setKeyVMOS = new HashSet<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setKeyTreeTableView.setRowFactory(param -> new TreeTableRow<>() {
            @Override
            protected void updateItem(SetKeyVMO item, boolean empty) {
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

        colSetKey.setCellValueFactory(cellData -> cellData.getValue().getValue().setKeyProperty().asString());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().getValue().descriptionProperty());
        colParent.setCellValueFactory(cellData -> cellData.getValue().getValue().parentProperty());
        setKeyTreeTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelection);
        setKeyTreeTableView.makeFilterable();
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
        TreeItem<SetKeyVMO> selectedTreeItem = getSelectedTreeItem();
        SetKeyVMO setKeyVMO = selectedTreeItem != null ? selectedTreeItem.getValue() : new SetKeyVMO();

        Long setVersionId = comboBox.getValue().getId();

        List<String> setKeyParentsName = getSetKeyParents();
        List<SetKeyVMO> setKeys = setKeysWithParent.values().stream()
            .map(e -> e.stream().map(FilterableTreeItem::getValue).toList()).flatMap(List::stream)
            .collect(Collectors.toCollection(ArrayList::new));

        SetKeyDialog setKeyDialog = new SetKeyDialog(I18N.getString(DIALOG_TITLE_ADD), setKeyVMO, setKeyParentsName,
            setKeys, setVersionId);

        Optional<SetKeyVMO> dialogRes = setKeyDialog.showAndWait();
        dialogRes.ifPresent(newSetKey -> doAsync(() -> {
            SetKeysDTO setKeysDTO = SetKeyRestClientHolder.getInstance()
                .saveSetKeys(new SetKeysDTO(List.of(SetKeyVMO.toDTO(newSetKey))));
            for (SetKeyDTO setKey : setKeysDTO.setKeys()) {
                return SetKeyVMO.toVMO(setKey);
            }

            return SetKeyVMO.toVMO(null);
        }, saveResult -> load(() -> selectTreeNode(newSetKey))));
    }

    @Override
    public void handleActionEdit() {
        TreeItem<SetKeyVMO> selectedTreeItem = getSelectedTreeItem();
        SetKeyVMO setKeyVMO = selectedTreeItem != null ? selectedTreeItem.getValue() : new SetKeyVMO();

        Long setVersionId = comboBox.getValue().getId();

        List<String> setKeyParentsName = getSetKeyParents();

        List<SetKeyVMO> setKeys = setKeysWithParent.values().stream()
            .map(e -> e.stream().map(FilterableTreeItem::getValue).toList()).flatMap(List::stream)
            .collect(Collectors.toCollection(ArrayList::new));

        SetKeyDialog setKeyDialog = new SetKeyDialog(I18N.getString(DIALOG_TITLE_EDIT), setKeyVMO, setKeyParentsName,
            setKeys, setVersionId);

        Optional<SetKeyVMO> dialogRes = setKeyDialog.showAndWait();

        dialogRes.ifPresent(newSetKey -> doAsync(() -> {
            SetKeyDTO oldSetKeyDTO = SetKeyVMO.toDTO(setKeyVMO);
            SetKeyDTO newSetKeyDTO = SetKeyVMO.toDTO(newSetKey);
            UpdateSetKeyDTO updateSetKeyDTO = new UpdateSetKeyDTO(oldSetKeyDTO, newSetKeyDTO);
            return SetKeyVMO.toVMO(SetKeyRestClientHolder.getInstance().updateSetKey(updateSetKeyDTO));
        }, saveResult -> load(() -> selectTreeNode(newSetKey))));
    }

    @Override
    public void handleActionDelete() {
        TreeItem<SetKeyVMO> selectedTreeItem = getSelectedTreeItem();
        doAsync(() -> {
            SetKeyDTO setKey = SetKeyVMO.toSetKey(selectedTreeItem.getValue());
            SetKeyRestClientHolder.getInstance().removeSetKey(setKey.getSetVersionId(), setKey.getSetKeyName());
            //todo: must remove the node children too without refresh!!!
            CacheManager.removeSetKey(setKey);
        }, () -> selectedTreeItem.getParent().getChildren().remove(selectedTreeItem));
    }

    @Override
    public void handleActionRefresh() {
        load();
    }

    @Override
    public void handleActionResetFilters() {
        disablePropertyResetFilters().set(true);
        setKeyTreeTableView.clearFilters();
    }

    @FXML
    private void handleSearch() {
        setPatternSearchTerm(searchTextField.getText().trim());
        FilterableTreeItem<SetKeyVMO> root = (FilterableTreeItem<SetKeyVMO>) setKeyTreeTableView.getRoot();
        root.predicateProperty().setValue(partGroupVMOTreeItem -> {
            SetKeyVMO vmo = partGroupVMOTreeItem.getValue();
            return patternSearchTerm.matches(vmo.getSetKeyString()) != null
                || patternSearchTerm.matches(vmo.getDescription()) != null
                || patternSearchTerm.matches(Long.toString(vmo.getVersion())) != null
                || vmo.getParent() != null && patternSearchTerm.matches(vmo.getParent()) != null;
        });
        setKeyTreeTableView.refresh();
    }

    private void load() {
        load(() -> {
        });
    }

    private void load(Runnable uiCallback) {
        doAsync(() -> {
            List<SetKeyDTO> setKeys = SetKeyRestClientHolder.getInstance().loadSetKeys().setKeys();
            CacheManager.updateSetKeys(setKeys);
            return SetKeyVMO.toVMOs(setKeys);
        }, loadedSetKeys -> {

            setKeysWithParent.clear();
            setKeyVMOS.clear();

            loadComboBox();
            initComboBox();

            setKeyVMOS = loadedSetKeys.parallelStream().map(FilterableTreeItem::new).collect(Collectors.toSet());

            Collection<FilterableTreeItem<SetKeyVMO>> setKeyItems = setKeyVMOS.stream()
                .filter(setKey -> setKey.getValue().getVersion().equals(comboBox.valueProperty().get().getId()))
                .collect(Collectors.toSet());
            setTableItems(setKeyItems);
        }, uiCallback);
    }

    private void setTableItems(Collection<FilterableTreeItem<SetKeyVMO>> setKeyVMOS) {
        FilterableTreeItem<SetKeyVMO> root = new FilterableTreeItem<>(null);

        setKeysWithParent = setKeyVMOS.stream().parallel()
            .filter(sk -> Objects.equals(sk.getValue().getVersion(), comboBox.getValue().getId()))
            .collect(Collectors.groupingBy(setKey -> {
                String parent = setKey.getValue().getParent();
                return parent == null ? StringConstant.EMPTY : parent;
            }));

        setKeyVMOS.stream().parallel().forEach(vmo -> vmo.getSourceChildren().clear());

        List<FilterableTreeItem<SetKeyVMO>> setKeys = setKeysWithParent.get(StringConstant.EMPTY);
        if (setKeys != null) {
            setKeys.sort(Comparator.comparing(t -> t.getValue().getSetKeyString()));
            for (FilterableTreeItem<SetKeyVMO> setKey : setKeys) {
                root.getSourceChildren().add(setKey);
                setChildren(setKey);
            }
        }

        TreeItem<SetKeyVMO> selectedTreeItem = setKeyTreeTableView.getSelectionModel().getSelectedItem();

        setKeyTreeTableView.setShowRoot(false);
        setKeyTreeTableView.setRoot(root);

        if (selectedTreeItem != null) {
            selectTreeItem(selectedTreeItem.getValue().getSetKeyString(), root);
        }
    }

    private void selectTreeItem(String selectedKey, TreeItem<SetKeyVMO> currentTreeItem) {
        if (currentTreeItem == null) {
            return;
        }

        for (TreeItem<SetKeyVMO> childTree : currentTreeItem.getChildren()) {
            String key = childTree.getValue().getSetKeyString();
            if (selectedKey.equals(key)) {
                expandItem(childTree);

                setKeyTreeTableView.getSelectionModel().select(childTree);
                setKeyTreeTableView.scrollToCenter(childTree);
                setKeyTreeTableView.requestFocus();

                return;
            }

            selectTreeItem(selectedKey, childTree);
        }
    }

    private void expandItem(TreeItem<SetKeyVMO> child) {
        TreeItem<SetKeyVMO> parent = child.getParent();
        if (parent == null) {
            return;
        }

        parent.setExpanded(true);
        expandItem(parent);
    }

    private void setChildren(FilterableTreeItem<SetKeyVMO> root) {
        String currentRootSetKey = root.getValue().getSetKeyString();
        List<FilterableTreeItem<SetKeyVMO>> items = setKeysWithParent.get(currentRootSetKey);
        if (items == null) {
            return;
        }

        items.sort(Comparator.comparing(t -> t.getValue().getSetKeyString()));
        for (FilterableTreeItem<SetKeyVMO> item : items) {
            setChildren(item);
            root.getSourceChildren().add(item);
        }
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private void initComboBox() {
        comboBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            setKeyTreeTableView.getSelectionModel().clearSelection();
            setKeyTreeTableView.clearFilters();

            setTableItems(setKeyVMOS.stream().filter(vmo -> vmo.getValue().getVersion().equals(newValue.getId()))
                .collect(Collectors.toSet()));

            for (TreeItem<SetKeyVMO> child : setKeyTreeTableView.getRoot().getChildren()) {
                child.setExpanded(false);
            }

            setKeyTreeTableView.refresh();
        });
    }

    private void loadComboBox() {
        SetVersionDTO alreadySelectedSetVersion = comboBox.getValue() == null ? null : comboBox.getValue();

        if (comboBox.getValue() != null) {
            alreadySelectedSetVersion = comboBox.getValue();
        }

        List<SetVersionDTO> setVersions = SetVersionRestClientHolder.getInstance().loadSetVersions().setVersions();

        List<SetVersionDTO> setVersionsComboBox = setVersions.stream()
            .filter(setVersion -> !DEFAULT_ITEM_NAME.equals(setVersion.getName()))
            .sorted(Comparator.comparing(SetVersionDTO::getName)).toList();
        comboBox.getItems().setAll(setVersionsComboBox);

        Long recentlyUsedSetVersionId = UserProperties.getRecentlyUsedSetVersionId();

        SetVersionDTO preSelectedSetVersion = alreadySelectedSetVersion == null ? setVersions.stream()
            .filter(setVersion -> Objects.equals(setVersion.getId(), recentlyUsedSetVersionId)).findFirst()
            .orElse(setVersions.getLast()) : alreadySelectedSetVersion;

        comboBox.getSelectionModel().select(preSelectedSetVersion);
    }

    private TreeItem<SetKeyVMO> getSelectedTreeItem() {
        return setKeyTreeTableView.getSelectionModel().getSelectedItem();
    }

    private void selectTreeNode(SetKeyVMO vmo) {
        String parent = vmo.getParent() == null ? StringConstant.EMPTY : vmo.getParent();
        Optional<FilterableTreeItem<SetKeyVMO>> itemToSelect = setKeysWithParent.get(parent).stream()
            .filter(cg -> cg.getValue().getSetKeyString().equals(vmo.getSetKeyString())).findAny();

        itemToSelect.ifPresent(item -> {
            TreeItem<SetKeyVMO> itemToExpand = item.getParent();
            while (itemToExpand != null) {
                itemToExpand.setExpanded(true);
                itemToExpand = itemToExpand.getParent();
            }

            setKeyTreeTableView.getSelectionModel().select(item);
        });
    }

    private List<String> getSetKeyParents() {
        return Stream.concat(setKeysWithParent.getOrDefault(StringConstant.EMPTY, List.of()).stream(),
                setKeysWithParent.getOrDefault(StringConstant.EMPTY, List.of()).stream()
                    .map(setKey -> setKeysWithParent.getOrDefault(setKey.getValue().getSetKeyString(), List.of()))
                    .flatMap(List::stream)).map(setKey -> setKey.getValue().getSetKeyString())
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Subscribe
    private void handleFilterUpdate(TreeFilteringUpdateEvent event) {
        disablePropertyResetFilters().setValue(setKeyTreeTableView.getColumnToPredicateMap().isEmpty());
    }
}
