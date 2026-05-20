package de.vw.paso.client.stammdaten.fzgprojekt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.cell.TableCellFactory;
import de.vw.paso.client.stammdaten.AbstractMasterDataTableViewController;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.converter.BooleanStringConverter;
import de.vw.paso.client.valueobject.VehicleProjectVMO;
import de.vw.paso.delegate.stammdaten.vehicleprojct.VehicleProjectRestClientHolder;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.UpdateVehicleProjectArchiveStateDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;

@FXController(name = "vehicle-project-tab")
public class VehicleProjectController extends AbstractMasterDataTableViewController<VehicleProjectVMO>
    implements Initializable {

    private static final String TOGGLE_ARCHIVING = "project.toggleArchiving";
    private static final String BRANDS_BOX_VALUE_SHOW_ALL = "brandsBox.showAll";

    @FXML
    private Button toggleArchivingButton;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TableColumn<VehicleProjectVMO, Boolean> colArchive;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colBrand;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colVehicleProject;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colDescription;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colProductKey;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colSetVersionName;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colSalesKey;
    @FXML
    private TableColumn<VehicleProjectVMO, Integer> colFirstModelYear;
    @FXML
    private TableColumn<VehicleProjectVMO, String> colPlatformId;

    @Override
    protected boolean disableRefresh() {
        return false;
    }

    @Override
    protected void openEditDialog(VehicleProjectVMO selectedItem, Consumer<Optional<VehicleProjectVMO>> callback) {
        if (selectedItem == null) {
            return;
        }

        VehicleProjectDialog.showDialog(selectedItem, () -> {
            doLoad(this::setItems);
            refreshTable();
        }, CacheManager.getVehicleProjects().stream().filter(
                vehicleProject -> vehicleProject.getProductDTO().getProductKey().equals(selectedItem.getProductKey()))
            .map(VehicleProjectDTO::getProjectName).toList());
    }

    @Override
    protected void doLoad(Consumer<List<VehicleProjectVMO>> callback) {
        doAsync(() -> {
            CacheManager.invalidateVehicleProjects();
            return VehicleProjectVMO.toVMOs(CacheManager.getVehicleProjects());
        }, callback);
    }

    @Override
    protected Comparator<? super VehicleProjectVMO> getItemComparator() {
        return Comparator.comparing(VehicleProjectVMO::getProjectName);
    }

    @Override
    protected boolean getFilterCriteria(VehicleProjectVMO item, PasoWildCardPattern pattern) {
        return (pattern == null || pattern.matches(item.getProjectName()) != null
            || pattern.matches(item.getDescription()) != null || pattern.matches(item.getProductKey()) != null
            || pattern.matches(item.getSalesKey()) != null || (item.getFirstModelYear() != null
            && pattern.matches(item.getFirstModelYear().toString()) != null)
            || pattern.matches(item.getPlatform()) != null || pattern.matches(item.brandNamePropertyValue()) != null)
            && (comboBox.getValue().equals(I18N.getString(BRANDS_BOX_VALUE_SHOW_ALL)) || comboBox.getValue()
            .equals(item.brandNamePropertyValue()));
    }

    @Override
    protected void initializeView() {
        initComboBox();
        initToggleArchivingButton();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem toggleArchive = new MenuItem(I18N.getString(TOGGLE_ARCHIVING));

        contextMenu.getItems().add(toggleArchive);
        toggleArchive.setOnAction(e -> toggleArchiving());

        productTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        productTableView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.hide();
                contextMenu.show(productTableView, event.getScreenX(), event.getScreenY());
            }
        });
    }

    @Override
    protected void initTableColumns() {
        colArchive.setCellValueFactory(cellData -> cellData.getValue().archiveProperty());
        colBrand.setCellValueFactory(cellData -> cellData.getValue().brandNameProperty());
        colVehicleProject.setCellValueFactory(cellData -> cellData.getValue().projectNameProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colProductKey.setCellValueFactory(cellData -> cellData.getValue().productKeyProperty());
        colSetVersionName.setCellValueFactory(cellData -> cellData.getValue().setVersionNameProperty());
        colSalesKey.setCellValueFactory(cellData -> cellData.getValue().salesKeyProperty());
        colFirstModelYear.setCellValueFactory(cellData -> cellData.getValue().firstModelYearProperty());
        colPlatformId.setCellValueFactory(cellData -> cellData.getValue().platformProperty());

        colArchive.setCellFactory(column -> {
            TextFieldTableCell<VehicleProjectVMO, Boolean> cell = new TextFieldTableCell<>();

            cell.setConverter(new BooleanStringConverter());

            return cell;
        });

        colFirstModelYear.setCellFactory(new TableCellFactory<>(new IntegerStringConverter()));
    }

    @Override
    protected void setItems(List<VehicleProjectVMO> items) {
        long[] prevSelectedIds = productTableView.getSelectionModel().getSelectedItems().stream()
            .map(VehicleProjectVMO::getId).mapToLong(Long::longValue).toArray();

        super.setItems(items);

        selectTableItems(prevSelectedIds);
    }

    @FXML
    private void toggleArchiving() {
        Collection<VehicleProjectVMO> selectedVehicleProjects = productTableView.getSelectionModel().getSelectedItems();
        if (selectedVehicleProjects.isEmpty()) {
            return;
        }

        boolean isAllArchived = selectedVehicleProjects.stream().allMatch(VehicleProjectVMO::isArchive);
        for (VehicleProjectVMO selectedVehicleProject : selectedVehicleProjects) {
            selectedVehicleProject.setArchive(!isAllArchived);
        }

        List<Long> ids = selectedVehicleProjects.stream().map(VehicleProjectVMO::getId).collect(Collectors.toList());

        doAsync(() -> {
            VehicleProjectRestClientHolder.getInstance()
                .updateVehicleProjectArchiveState(new UpdateVehicleProjectArchiveStateDTO(ids, !isAllArchived));
            CacheManager.invalidateVehicleProjects();
        }, () -> {
            productTableView.refresh();
            EventBus.getInstance().post(new UpdateVehicleProjectArchivingEvent());
        });
    }

    private void initComboBox() {
        comboBox.getItems().clear();
        comboBox.getItems().add(I18N.getString(BRANDS_BOX_VALUE_SHOW_ALL));
        comboBox.getItems().addAll(Arrays.stream(Brand.values()).map(Brand::getBrandName).toList());

        comboBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            productTableView.clearFilters();
            refreshTable();
        });

        comboBox.setValue(I18N.getString(BRANDS_BOX_VALUE_SHOW_ALL));
    }

    private void initToggleArchivingButton() {
        toggleArchivingButton.setOnAction(e -> toggleArchiving());

        KeyCombination toggleArchivingKeyComb = new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN);
        Mnemonic toggleArchivingShortcut = new Mnemonic(toggleArchivingButton, toggleArchivingKeyComb);

        GridPane gridPane = getControl();
        gridPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            Scene scene = gridPane.getScene();
            if (scene != null) {
                scene.addMnemonic(toggleArchivingShortcut);
            }
        });
    }

    private void selectTableItems(long[] prevSelectedIds) {
        if (prevSelectedIds.length == 0) {
            return;
        }

        int[] selectedIndices = new int[prevSelectedIds.length];
        List<Long> ids = productTableView.getItems().stream().map(VehicleProjectVMO::getId).toList();
        for (int i = 0; i < prevSelectedIds.length; i++) {
            selectedIndices[i] = ids.indexOf(prevSelectedIds[i]);
        }

        int firstItemIndex = selectedIndices[0];
        productTableView.getSelectionModel().selectIndices(firstItemIndex, selectedIndices);
        productTableView.scrollTo(firstItemIndex);
        productTableView.requestFocus();
    }
}
