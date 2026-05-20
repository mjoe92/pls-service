package de.vw.paso.client.stueckliste.fzgkonfig.content.fzgprojekt;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.cell.RadioButtonTableCell;
import de.vw.paso.client.control.cell.TableCellFactory;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.control.table.DisabledSelectionModel;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.control.textfield.PasoCustomTextFieldClearable;
import de.vw.paso.client.stammdaten.validation.ValidationFactory;
import de.vw.paso.client.stueckliste.fzgkonfig.content.AbstractContentController;
import de.vw.paso.client.stueckliste.fzgkonfig.content.stueckliste.StuecklisteControl;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.validation.Validator;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigCategoryStatusDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.vehicle.VehicleConfigStatus;
import org.apache.commons.lang3.StringUtils;

@FXController(name = "fzg-projekt")
public class FzgProjektController extends AbstractContentController {

    private final String PATTERN = "dd.MM.yyyy";

    @FXML
    private PasoCustomTextFieldClearable textfieldSuche;

    @FXML
    private CustomTableView<FzgProjektItem> fzgProjektTableView;

    @FXML
    private ComboBox<String> brandsBox;

    @FXML
    private TableColumn<FzgProjektItem, Boolean> colRadio;

    @FXML
    private TableColumn<FzgProjektItem, String> colVehicleProject;

    @FXML
    private TableColumn<FzgProjektItem, String> colProjectDescription;

    @FXML
    private TableColumn<FzgProjektItem, String> colProductKey;

    @FXML
    private TableColumn<FzgProjektItem, String> colSaleskey;

    @FXML
    private TableColumn<FzgProjektItem, Integer> colFirstModelYear;

    @FXML
    private TableColumn<FzgProjektItem, String> colPlatformID;

    @FXML
    private ComboBox<UserGroupDTO> userGroupsBox;

    @FXML
    private PasoCustomTextField<String> textFieldName;

    @FXML
    private DatePicker datePickerValidDate;

    @FXML
    private TextArea textAreaDescription;

    @FXML
    private CheckBox checkBoxErstellungAuto;

    @FXML
    private StuecklisteControl stuecklisteFahrzeug;

    private final ObjectProperty<VehicleProjectDTO> selectedVehicleProject = new SimpleObjectProperty<>();

    private FilteredList<FzgProjektItem> filteredData = new FilteredList<>(FXCollections.observableArrayList(),
            p -> true);

    @Override
    public void onVehicleConfigChanged() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();

        if (vehicleConfig.getOwnerGroup() != null) {
            Optional<UserGroupDTO> foundUserGroup = userGroupsBox.getItems().stream()
                    .filter(userGroupDTO -> userGroupDTO.getId().equals(vehicleConfig.getOwnerGroup().getId()))
                    .findFirst();

            foundUserGroup.ifPresent(userGroupDTO -> userGroupsBox.setValue(userGroupDTO));
        }

        textFieldName.setText(vehicleConfig.getName());
        textAreaDescription.setText(vehicleConfig.getDescription());
        if (vehicleConfig.getValidDate() != null) {
            final Date validDate = vehicleConfig.getValidDate();
            datePickerValidDate.setValue(validDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        if (!isEditable()) {
            fzgProjektTableView.setEditable(false);
            fzgProjektTableView.setSelectionModel(new DisabledSelectionModel<>(fzgProjektTableView));
            brandsBox.setDisable(true);
            checkBoxErstellungAuto.setDisable(true);
            stuecklisteFahrzeug.setDisable(true);
            userGroupsBox.setDisable(true);
            datePickerValidDate.setDisable(true);
        }

        stuecklisteFahrzeug.updateTiWhImport();

        VehicleProjectDTO vehicleProject = vehicleConfig.getVehicleProject();
        selectedVehicleProject.set(vehicleProject);

        String brandName =
                vehicleProject == null ? Brand.VW.getBrandName() : vehicleProject.getBrandCode().getBrandName();
        brandsBox.setValue(brandName);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);
        initTable();
        initTableColumns();

        initBrandComboBox();
        initUserGroupComboBox();

        textfieldSuche.textProperty().addListener((observable, oldValue, newValue) -> handleFzgProjektSearch());

        fzgProjektTableView.setOnMousePressed(mouseEvent -> {
            fzgProjektTableView.getItems().forEach(fzgProjektItem -> fzgProjektItem.setSelected(false));

            FzgProjektItem selectedItem = fzgProjektTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedItem.setSelected(true);
            }
        });

        fzgProjektTableView.setOnKeyPressed(keyEvent -> {
            fzgProjektTableView.getItems().forEach(fzgProjektItem -> fzgProjektItem.setSelected(false));

            FzgProjektItem selectedItem = fzgProjektTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedItem.setSelected(true);
            }
        });

        fzgProjektTableView.getSelectionModel().cellSelectionEnabledProperty().set(false);

        initializeStueckliste();

        initRadioColumn();
        handleFzgProjektSearch();

        datePickerValidDate.setConverter(new StringConverter<>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(PATTERN);

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : null;
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return LocalDate.parse(string, dateFormatter);
                } catch (Exception e) {
                    return null;
                }
            }
        });
        initTextFields();

        stuecklisteFahrzeug.fillTextField();
        stuecklisteFahrzeug.initMenuButton();
    }

    private void initializeStueckliste() {
        stuecklisteFahrzeug.init(this, 3, this::getTiWhImport);
        initDatenstaende();
    }

    private TiWhImportDTO getTiWhImport() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        if (vehicleConfig == null) {
            return null;
        }

        return vehicleConfig.getTiWhImportVehicle();
    }

    private void initBrandComboBox() {
        Stream<String> showAllItem = Stream.of(I18N.getString("brandsBox.showAll"));
        Stream<String> brandNameItems = Arrays.stream(Brand.values()).map(Brand::getBrandName);
        ObservableList<String> brandItems = Stream.concat(showAllItem, brandNameItems)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        brandsBox.setItems(brandItems);

        brandsBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            fzgProjektTableView.clearFilters();
            handleFzgProjektSearch();
        });
    }

    @Override
    public void start() {
        super.start();
        loadFzgProjekte();

        addValidators();
    }

    private void addValidators() {
        addValidator(new Validator<>(selectedVehicleProjectProperty(), Objects::nonNull,
                I18N.getString("validation.fzgprojekt")));
        addValidator(new Validator<>(userGroupsBox.getSelectionModel().selectedItemProperty(), Objects::nonNull,
                I18N.getString("validation.usergroup")));
        addValidator(new Validator<>(datePickerValidDate.valueProperty(), Objects::nonNull,
                I18N.getString("validation.validdate")));
        addValidator(new Validator<>(textFieldName.textProperty(), StringUtils::isNotEmpty,
                I18N.getString("validation.name")));

        stuecklisteFahrzeug.addValidators();
    }

    private void initTable() {
        fzgProjektTableView.makeHeaderWrappable();
        fzgProjektTableView.makeFilterable();
        fzgProjektTableView.getSelectionModel().setCellSelectionEnabled(true);
        fzgProjektTableView.setEditable(true);
        fzgProjektTableView.sortPolicyProperty().set(param -> false);
        fzgProjektTableView.setPlaceholder(new Label(I18N.getString("fzgprojekte.leer")));
    }

    private void initTableColumns() {
        colVehicleProject.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getProjectName()));
        colProjectDescription.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getProjectDescription()));
        colProductKey.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getProductKey()));
        colSaleskey.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getSalesKey()));
        colFirstModelYear.setCellValueFactory(e -> new SimpleObjectProperty<>(e.getValue().getFirstModelYear()));
        colFirstModelYear.setCellFactory(new TableCellFactory<>(new IntegerStringConverter()));
        colPlatformID.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getPlatform()));
    }

    private boolean progUpdate = false;

    private void initRadioColumn() {
        colRadio.setCellFactory(column -> {
            RadioButtonTableCell<FzgProjektItem> radioButtonTableCell = new RadioButtonTableCell<>() {
                @Override
                public void updateItem(Boolean selected, boolean empty) {
                    super.updateItem(selected, empty);
                    if (!progUpdate) {
                        progUpdate = true;
                        int index = getTableRow().getIndex();
                        if (Boolean.TRUE.equals(selected)) {
                            getTableView().getSelectionModel().select(index);
                            getTableRow().getItem().setSelected(true);
                        } else {
                            FzgProjektItem item = getTableRow().getItem();
                            if (item != null) {
                                getTableRow().getItem().setSelected(false);
                            }
                            getTableView().getSelectionModel().clearSelection(index);
                        }
                        progUpdate = false;
                    }
                }
            };
            VehicleConfigDTO value = getVehicleConfig();
            if (value != null && !value.isEditable()) {
                radioButtonTableCell.disable();
            }
            return radioButtonTableCell;
        });

        colRadio.setCellValueFactory(param -> param.getValue().selectedProperty());
        colRadio.setOnEditCommit(this::handleEditCommit);

        selectedVehicleProjectProperty().addListener((obs, oldValue, newValue) -> {
            VehicleConfigDTO vehicleConfigDTO = getVehicleConfig();
            if (vehicleConfigDTO != null) {
                vehicleConfigDTO.setVehicleProject(newValue);

                UserGroupDTO selectedItem = userGroupsBox.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    vehicleConfigDTO.setUserGroups(List.of(selectedItem));
                    vehicleConfigDTO.setOwnerGroup(selectedItem);
                }
            }
            stuecklisteFahrzeug.produktschluesselTextProperty().set(newValue.getProductKey());
        });
    }

    private void handleEditCommit(final CellEditEvent<FzgProjektItem, Boolean> event) {
        for (FzgProjektItem fzgProjektItem : filteredData.getSource()) {
            fzgProjektItem.setSelected(Boolean.FALSE);
        }

        event.getRowValue().setSelected(event.getNewValue());
    }

    private void handleFzgProjektSearch() {
        filteredData.setPredicate(fzgProjektItem -> {
            String sucheText = textfieldSuche.getText();
            String brand = brandsBox.getValue();

            boolean matchingBrand = false;
            if (brand != null) {
                matchingBrand = fzgProjektItem.getVehicleProject().getBrandCode().getBrandName().matches(brand);
                if (brand.matches(I18N.getString("brandsBox.showAll"))) {
                    matchingBrand = true;
                }
            }

            boolean matchInAnyColumn =
                    fzgProjektItem.getVehicleProject().getProjectName().toLowerCase().contains(sucheText.toLowerCase())
                            || fzgProjektItem.getVehicleProject().getDescription().toLowerCase()
                            .contains(sucheText.toLowerCase()) || fzgProjektItem.getVehicleProject().getProductKey()
                            .toLowerCase().contains(sucheText.toLowerCase()) || fzgProjektItem.getVehicleProject()
                            .getSalesKey().toLowerCase().contains(sucheText.toLowerCase()) || (
                            fzgProjektItem.getVehicleProject().getFirstModelYear() != null
                                    && fzgProjektItem.getVehicleProject().getFirstModelYear().toString().toLowerCase()
                                    .contains(sucheText.toLowerCase())) || fzgProjektItem.getVehicleProject()
                            .getPlatform().toLowerCase().contains(sucheText.toLowerCase());
            boolean archived = fzgProjektItem.getVehicleProject().isArchive();

            return matchingBrand && matchInAnyColumn && !archived;
        });

        if (!filteredData.isEmpty()) {
            filteredData.forEach(fzgProjektItem -> {
                if (fzgProjektItem.selectedProperty().get()) {
                    fzgProjektTableView.scrollTo(fzgProjektItem);
                }
            });
        }
    }

    private void loadFzgProjekte() {
        doAsync(CacheManager::getVehicleProjects, this::setFzgProjektItems);
    }

    private void setFzgProjektItems(Collection<VehicleProjectDTO> fzgProjekte) {
        ObservableList<FzgProjektItem> projectItems = fzgProjekte.stream().map(this::wrapToItem)
                .sorted(Comparator.comparing(FzgProjektItem::getProjectName))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        filteredData = new FilteredList<>(projectItems, p -> true);
        fzgProjektTableView.setItems(filteredData);
        handleFzgProjektSearch();
    }

    private FzgProjektItem wrapToItem(VehicleProjectDTO vehicleProject) {
        FzgProjektItem fzgProjektItem = new FzgProjektItem(vehicleProject);

        fzgProjektItem.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                selectedVehicleProject.set(fzgProjektItem.getVehicleProject());
            }
        });

        VehicleConfigDTO vehicleConfigDTO = getVehicleConfig();
        if (vehicleConfigDTO == null) {
            return fzgProjektItem;
        }

        boolean equalsId = vehicleConfigDTO.getVehicleProject() != null && fzgProjektItem.getId()
                .equals(vehicleConfigDTO.getVehicleProject().getId());

        if (equalsId) {
            fzgProjektItem.setSelected(true);
        }

        return fzgProjektItem;
    }

    private ReadOnlyObjectProperty<VehicleProjectDTO> selectedVehicleProjectProperty() {
        return selectedVehicleProject;
    }

    private void initUserGroupComboBox() {
        List<UserGroupDTO> userGroups = UserProperties.getUser().getUserGroups().stream()
                .filter(UserGroupDTO::isWriteAccess).toList();

        userGroupsBox.getItems().addAll(userGroups);

        userGroupsBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            VehicleConfigDTO vehicleConfig = getVehicleConfig();
            if (newValue == null) {
                vehicleConfig.setUserGroups(List.of());
                vehicleConfig.setOwnerGroup(null);
            } else {
                vehicleConfig.setUserGroups(List.of(newValue));
                vehicleConfig.setOwnerGroup(newValue);
            }

            setDirty();
        });
    }

    private void initTextFields() {
        textFieldName.setValidation(ValidationFactory.getStringPredicate(1));

        textFieldName.textProperty().addListener((obs, oldVal, newVal) -> updateName());
        textAreaDescription.textProperty().addListener((obs, oldVal, newVal) -> updateDescription());

        datePickerValidDate.setPromptText(PATTERN.toLowerCase());

        datePickerValidDate.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                textAreaDescription.requestFocus();
            }
        });
        datePickerValidDate.getEditor().focusedProperty()
                .addListener((observableValue, aBoolean, t1) -> updateValidDate(false));
        datePickerValidDate.valueProperty().addListener((obs, oldVal, newVal) -> updateValidDate(true));
        datePickerValidDate.chronologyProperty().addListener((obs, oldVal, newVal) -> updateValidDate(true));
    }

    private void updateName() {
        String name = textFieldName.textProperty().get();

        if (!StringUtils.isBlank(name)) {
            getVehicleConfig().setName(name);
            forceDirty();
        }
    }

    private void updateValidDate(boolean pickerUpdate) {
        if (pickerUpdate) {
            LocalDate validDate = datePickerValidDate.getValue();
            getVehicleConfig().setValidDate(DateUtil.toDate(validDate));
        } else {
            String text = datePickerValidDate.getEditor().getText();
            LocalDate localDate = parseDate(text);
            if (localDate == null) {
                getVehicleConfig().setValidDate(null);
            } else {
                datePickerValidDate.valueProperty().set(localDate);
                getVehicleConfig().setValidDate(DateUtil.toDate(localDate));
            }
        }
        setDirty();
    }

    private LocalDate parseDate(String text) {
        try {
            return datePickerValidDate.getConverter().fromString(text);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateDescription() {
        String description = textAreaDescription.textProperty().get();

        if (!StringUtils.isBlank(description)) {
            getVehicleConfig().setDescription(description);
            setDirty();
        }
    }

    private void initDatenstaende() {
        stuecklisteFahrzeug.initDatenstand();
    }

    private String getProductKeyFahrzeug() {
        String productKey = getVehicleConfig().getVehicleProject().getProductKey();
        if (productKey != null) {
            return productKey;
        } else {
            showWarningDialog();
        }
        return null;
    }

    private String getProductKeyMotor() {
        //FIXME Old solution works with Modell Aggregat, currently the concept for this is not clear and need to be solved
        showWarningDialog();
        return null;
    }

    private String getProductKeyGetriebe() {
        //FIXME Old solution works with Modell Aggregat, currently the concept for this is not clear and need to be solved
        showWarningDialog();
        return null;
    }

    private void showWarningDialog() {
        DialogUtil.showWarnDialog(I18N.getString("produkschluessel.nicht.gefunden.title"),
                I18N.getString("produkschluessel.nicht.gefunden.header"),
                I18N.getString("produkschluessel.nicht.gefunden.text"));
    }

    public void updateMenuImportStatus() {
        ImportStatus highestImportStatus = getHighestImportStatus();

        for (VehicleConfigCategoryStatusDTO categoryStatus : getVehicleConfig().getVehicleConfigCategoryStatus()) {
            if (categoryStatus.getVehicleConfigCategory().equals(vehicleConfigCategory)) {
                if (highestImportStatus == ImportStatus.IMPORTED) {
                    categoryStatus.setVehicleConfigStatus(VehicleConfigStatus.OK);
                } else if (highestImportStatus == ImportStatus.REQUESTED) {
                    categoryStatus.setVehicleConfigStatus(VehicleConfigStatus.WAIT);
                }
            }
        }

        setDirty();
    }

    private ImportStatus getHighestImportStatus() {
        ImportStatus importStatusFzg = getVehicleConfig().getTiWhImportVehicle() == null ? null :
                getVehicleConfig().getTiWhImportVehicle().getImportStatus();
        ImportStatus importStatusMot = getVehicleConfig().getTiWhImportMotor() == null ? null :
                getVehicleConfig().getTiWhImportMotor().getImportStatus();
        ImportStatus importStatusGetr = getVehicleConfig().getTiWhImportGearbox() == null ? null :
                getVehicleConfig().getTiWhImportGearbox().getImportStatus();

        // Hierarchie:
        // ERROR
        // NOT_IMPORTED
        // IMPORTED
        // WAIT
        // WARN
        // EDIT
        // OK

        if (importStatusFzg == ImportStatus.ERROR || importStatusMot == ImportStatus.ERROR
                || importStatusGetr == ImportStatus.ERROR) {
            return ImportStatus.ERROR;
        } else if (importStatusFzg == ImportStatus.NO_DATA || importStatusMot == ImportStatus.NO_DATA
                || importStatusGetr == ImportStatus.NO_DATA) {
            return ImportStatus.NO_DATA;
        } else if (importStatusFzg == ImportStatus.IMPORTED || importStatusMot == ImportStatus.IMPORTED
                || importStatusGetr == ImportStatus.IMPORTED) {
            return ImportStatus.IMPORTED;
        }

        return ImportStatus.REQUESTED;
    }

    public CustomTableView<FzgProjektItem> getFzgProjektTableView() {
        return fzgProjektTableView;
    }
}
