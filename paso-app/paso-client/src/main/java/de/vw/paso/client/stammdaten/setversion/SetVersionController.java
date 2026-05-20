package de.vw.paso.client.stammdaten.setversion;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.TableCellFactory;
import de.vw.paso.client.explorer.vehicleconfig.converter.DateTimeStringConverter;
import de.vw.paso.client.stammdaten.AbstractMasterDataTableViewController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.valueobject.SetVersionVMO;
import de.vw.paso.delegate.stammdaten.setversion.SetVersionRestClientHolder;
import de.vw.paso.service.masterdata.setversion.AddSetVersionRequestDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.setversion.UpdateSetVersionRequestDTO;

@FXController(name = "set-version-tab")
public class SetVersionController extends AbstractMasterDataTableViewController<SetVersionVMO>
    implements Initializable {

    @FXML
    private TableColumn<SetVersionVMO, String> colSetVersionName;
    @FXML
    private TableColumn<SetVersionVMO, String> colLastModifiedBy;
    @FXML
    private TableColumn<SetVersionVMO, Date> colLastModifiedAt;

    @Override
    protected void openAddDialog(SetVersionVMO selectedItem, Consumer<Optional<SetVersionVMO>> callback) {
        SetVersionDialog setVersionDialog = new SetVersionDialog(I18N.getString(DIALOG_TITLE_ADD),
            productTableView.getItems());
        callback.accept(setVersionDialog.showAndWait());
    }

    @Override
    protected void openEditDialog(SetVersionVMO selectedItem, Consumer<Optional<SetVersionVMO>> callback) {
        SetVersionDialog setVersionDialog = new SetVersionDialog(I18N.getString(DIALOG_TITLE_EDIT),
            productTableView.getItems(), selectedItem);
        callback.accept(setVersionDialog.showAndWait());
    }

    @Override
    protected void doAdd(SetVersionVMO newItem, Consumer<SetVersionVMO> callback) {
        doAsync(() -> addSetVersion(newItem.toAddSetVersionRequest()), callback);
    }

    @Override
    protected void doEdit(SetVersionVMO selectedItem, SetVersionVMO newItem, Consumer<SetVersionVMO> callback) {
        doAsync(() -> updateSetVersion(newItem), callback);
    }

    @Override
    protected <T> void handleTableSelection(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        super.handleTableSelection(observable, oldValue, newValue);

        if (newValue == null || DEFAULT_ITEM_NAME.equals(newValue.toString())) {
            disablePropertyEdit().set(true);
            disablePropertyRemove().set(true);
        }
    }

    @Override
    protected void doDelete(SetVersionVMO selectedItem, Runnable callback) {
        doAsync(() -> deleteSetVersion(selectedItem.getId()), callback);
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
    protected void doLoad(Consumer<List<SetVersionVMO>> callback) {
        doAsync(this::loadSetVersions, loadedSetVersions -> accept(callback, loadedSetVersions));
    }

    @Override
    protected Comparator<? super SetVersionVMO> getItemComparator() {
        return Comparator.comparing(SetVersionVMO::getSetVersionName);
    }

    @Override
    protected boolean getFilterCriteria(SetVersionVMO item, PasoWildCardPattern pattern) {
        return true;
    }

    @Override
    protected void initializeView() {
        productTableView.setEditable(true);
    }

    @Override
    protected void initTableColumns() {
        colSetVersionName.setCellValueFactory(cellData -> cellData.getValue().setVersionNameProperty());
        colLastModifiedBy.setCellValueFactory(cellData -> cellData.getValue().lastModifiedByProperty());
        colLastModifiedAt.setCellValueFactory(cellData -> cellData.getValue().lastModifiedAtProperty());
        colLastModifiedAt.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));
    }

    @Override
    protected void setItems(List<SetVersionVMO> items) {
        SetVersionVMO prevSelectedVersion = productTableView.getSelectionModel().getSelectedItem();

        super.setItems(items);

        selectTableItems(prevSelectedVersion);
    }

    private List<SetVersionVMO> loadSetVersions() {
        List<SetVersionDTO> setVersions = SetVersionRestClientHolder.getInstance().loadSetVersions().setVersions();
        return SetVersionVMO.dtosToVMOs(setVersions);
    }

    private SetVersionVMO addSetVersion(AddSetVersionRequestDTO addSetVersionRequestDTO) {
        SetVersionDTO addedSetVersion = SetVersionRestClientHolder.getInstance().addSetVersion(addSetVersionRequestDTO);
        return SetVersionVMO.dtoToVMO(addedSetVersion);
    }

    private SetVersionVMO updateSetVersion(SetVersionVMO newItem) {
        Long setVersionId = newItem.getId();
        UpdateSetVersionRequestDTO updateSetVersionRequest = newItem.toUpdateSetVersionRequest();

        SetVersionDTO updatedSetVersion = SetVersionRestClientHolder.getInstance()
            .updateSetVersion(setVersionId, updateSetVersionRequest);

        return SetVersionVMO.dtoToVMO(updatedSetVersion);
    }

    private void deleteSetVersion(Long id) {
        SetVersionRestClientHolder.getInstance().deleteSetVersion(id);
    }

    private void accept(Consumer<List<SetVersionVMO>> callback, List<SetVersionVMO> loadedSetVersions) {
        if (loadedSetVersions != null) {
            callback.accept(loadedSetVersions);
        }
    }

    private void selectTableItems(SetVersionVMO selectingItem) {
        if (selectingItem == null) {
            return;
        }

        List<SetVersionVMO> items = productTableView.getItems();
        for (int i = 0; i < items.size(); i++) {
            SetVersionVMO item = items.get(i);
            if (selectingItem.getId().equals(item.getId())) {
                productTableView.getSelectionModel().select(i);
                productTableView.scrollTo(i);
                productTableView.requestFocus();

                return;
            }
        }
    }
}
