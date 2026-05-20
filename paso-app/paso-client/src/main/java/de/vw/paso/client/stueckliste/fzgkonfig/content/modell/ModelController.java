package de.vw.paso.client.stueckliste.fzgkonfig.content.modell;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.RadioButtonTreeTableCell;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.control.textfield.PasoCustomTextFieldClearable;
import de.vw.paso.client.control.textfield.PasoNumberField;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.stueckliste.efs.display.strategy.ModelDisplayStrategy;
import de.vw.paso.client.stueckliste.fzgkonfig.content.AbstractContentController;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.RefreshDatenstandEvent;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.customfilter.CustomFilterUtil;
import de.vw.paso.client.validation.Validator;
import de.vw.paso.delegate.model.ModelRestClientHolder;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotExistingException;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;
import de.vw.paso.service.modelimport.IImportModelConsumer;
import de.vw.paso.service.modelimport.ILoadModelsConsumer;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.modelimport.ModelImportDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.StringCommonTermsUtil;
import de.vw.paso.utility.StringConstant;

@FXController(name = "model")
public class ModelController extends AbstractContentController implements IImportModelConsumer, ILoadModelsConsumer {

    @FXML
    private PasoCustomTextField<String> textFieldDistributionKey;
    @FXML
    private CheckBox checkboxOhneModell;
    @FXML
    private PasoCustomTextFieldClearable textFieldSearch;
    @FXML
    private CustomTreeTableView<ModelDTO> treeTableViewModell;
    @FXML
    private CustomTableView<ModelImportDTO> tableViewModelImport;
    @FXML
    private SplitPane tableSplitPane;

    @FXML
    private TreeTableColumn<ModelDTO, Boolean> colRadio;
    @FXML
    private TreeTableColumn<ModelDTO, String> colModel;
    @FXML
    private TreeTableColumn<ModelDTO, String> colDescription;
    @FXML
    private TreeTableColumn<ModelDTO, String> colVersion;
    @FXML
    private TreeTableColumn<ModelDTO, String> colStatus;
    @FXML
    private TableColumn<ModelImportDTO, String> colStartDate;
    @FXML
    private TableColumn<ModelImportDTO, Integer> colModelYear;
    @FXML
    private TableColumn<ModelImportDTO, String> colSalesRegion;
    @FXML
    private TableColumn<ModelImportDTO, String> colSalesKey;
    @FXML
    private TableColumn<ModelImportDTO, String> colImportStatus;

    private final ToggleGroup toggleGroupRadioButton;
    private final ObjectProperty<ModelImportDTO> selectedModellImport;
    private final ObjectProperty<ModelDTO> selectedModell;
    private final Label placeholderLabel;
    private final BooleanProperty combinedValidation;

    private ModellTreeModel modellTreeModel;
    private PasoWildCardPattern patternSearchTerm;
    private FilteredList<ModelDTO> filteredData;
    private StringProperty datenstandStatus;

    public ModelController() {
        toggleGroupRadioButton = new ToggleGroup();
        selectedModellImport = new SimpleObjectProperty<>();
        selectedModell = new SimpleObjectProperty<>();
        placeholderLabel = new Label();
        combinedValidation = new SimpleBooleanProperty();
        filteredData = new FilteredList<>(FXCollections.observableArrayList(), model -> true);
    }

    @Override
    public void onVehicleConfigChanged() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        if (vehicleConfig.getModelImport() != null) {
            selectedModellImportProperty().set(vehicleConfig.getModelImport());

            if (vehicleConfig.getModel() != null) {
                selectedModellProperty().set(vehicleConfig.getModel());
            }
        }

        checkboxOhneModell.setSelected(getVehicleConfig().getModel() == null);

        String text = getVehicleConfig().getVehicleProject().getSalesKey() == null ? "5G" :
                getVehicleConfig().getVehicleProject().getSalesKey();
        textFieldDistributionKey.setText(text);

        if (selectedModellProperty().get() != null || checkboxOhneModell.isSelected()) {
            setCombinedValidation(true);
        }

        if (!isEditable()) {
            checkboxOhneModell.setDisable(true);
            textFieldDistributionKey.setEditable(false);
            textFieldDistributionKey.setStyle("-fx-opacity: 0.5;");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initTextField();
        initTable();
        initTableColumns();
        treeTableViewModell.makeFilterable();
        tableViewModelImport.makeFilterable();

        selectedModellProperty().addListener((observable, oldValue, newValue) -> setCombinedValidation(
                newValue != null || checkboxOhneModell.selectedProperty().getValue()));

        initCheckBox();
        initRadioColumn();
        initTextFields();
    }

    private void addValidators() {
        addValidator(new Validator<>(combinedValidationProperty(), r -> r, I18N.getString("validation.modell")));
    }

    @Override
    public void start() {
        super.start();

        if (isEditable()) {
            colRadio.setOnEditCommit(this::handleEditCommit);
        }

        addValidators();
    }

    private void initTextField() {
        centerTextField(textFieldDistributionKey);
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
    }

    private void centerTextField(PasoCustomTextField<?> textField) {
        textField.getStyleClass().remove(PasoNumberField.STYLE_CLASS_NUMBER_TEXT_FIELD);
        textField.getStyleClass().add(PasoNumberField.STYLE_CLASS_TEXT_FIELD_CENTER);
    }

    private void initTable() {
        treeTableViewModell.getSelectionModel().setCellSelectionEnabled(false);
        treeTableViewModell.setEditable(true);
        treeTableViewModell.sortPolicyProperty().set(param -> false);
        treeTableViewModell.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tableViewModelImport.getSelectionModel().setCellSelectionEnabled(false);
        tableViewModelImport.setEditable(false);
        tableViewModelImport.sortPolicyProperty().set(param -> false);
        tableViewModelImport.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        placeholderLabel.textProperty().bind(datenstandStatusProperty());
        treeTableViewModell.setPlaceholder(placeholderLabel);

        tableViewModelImport.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setSelectedModellImport(newValue);

                        loadModels(newValue.getId());
                    }
                });
    }

    private void initTableColumns() {
        colModel.setCellValueFactory(cellData -> ((ModellItem) cellData.getValue()).propertyModelKey());
        CustomFilterUtil.setGraphicForColumn(colModel, I18N.getString("tablecolumn.modell"));
        colDescription.setCellValueFactory(cellData -> ((ModellItem) cellData.getValue()).propertyDescription());
        CustomFilterUtil.setGraphicForColumn(colDescription, I18N.getString("tablecolumn.bezeichnung"));
        colVersion.setCellValueFactory(cellData -> ((ModellItem) cellData.getValue()).propertyModelVersion());
        CustomFilterUtil.setGraphicForColumn(colVersion, I18N.getString("tablecolumn.version"));
        colStatus.setCellValueFactory(cellData -> ((ModellItem) cellData.getValue()).propertyStatus());
        CustomFilterUtil.setGraphicForColumn(colStatus, I18N.getString("tablecolumn.status"));

        colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                DateUtil.formatDate(cellData.getValue().getTimestampChange(), "dd.MM.yyyy")));
        colModelYear.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getModelYear()));
        colSalesRegion.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getSalesRegion().id()));
        colSalesKey.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSalesKey()));
        colImportStatus.setCellValueFactory(
                cellData -> new SimpleStringProperty(getPlaceholderStatusText(cellData.getValue().getImportStatus())));
    }

    public ModellTreeModel getModelTreeModel() {
        if (modellTreeModel == null) {
            modellTreeModel = new ModellTreeModel();
        }

        return modellTreeModel;
    }

    private void initRadioColumn() {
        colRadio.setCellFactory(column -> new RadioButtonTreeTableCell<>(toggleGroupRadioButton));
        colRadio.setCellValueFactory(param -> ((ModellItem) param.getValue()).selectedProperty());
        CustomFilterUtil.setGraphicForColumn(colRadio, I18N.getString("tablecolumn.radio"));
    }

    private void handleEditCommit(TreeTableColumn.CellEditEvent<ModelDTO, Boolean> event) {
        event.getTableColumn().setUserData(event.getRowValue().getValue());

        saveModel(event.getRowValue().getValue());
    }

    private void initCheckBox() {
        checkboxOhneModell.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getVehicleConfig().setModel(null);
                setCombinedValidation(true);
            } else if (selectedModellProperty().get() == null) {
                setCombinedValidation(false);
            }

            setDirty();
        });

        tableSplitPane.disableProperty().bindBidirectional(checkboxOhneModell.selectedProperty());
        textFieldDistributionKey.disableProperty().bindBidirectional(checkboxOhneModell.selectedProperty());
        textFieldSearch.disableProperty().bindBidirectional(checkboxOhneModell.selectedProperty());
    }

    private void saveModel(ModelDTO model) {
        if (model.getDescription() != null) {
            setSelectedModell(model);
        }
    }

    private String getPlaceholderStatusText(ImportStatus importStatus) {
        return switch (importStatus) {
            case REQUESTED -> I18N.getString("import.angefordert");
            case IMPORTED -> I18N.getString("import.importiert");
            case NO_DATA -> I18N.getString("import.keine.daten");
            case ERROR -> I18N.getString("import.fehler");
        };
    }

    @FXML
    private void handleSearch() {
        try {
            patternSearchTerm = new PasoWildCardPattern(textFieldSearch.textProperty().get().toUpperCase());
        } catch (Exception exception) {
            handleException(exception);
        }

        Collection<ModelDTO> models = filteredData.stream()
                .filter(model -> patternSearchTerm == null || (model.getModelKey() != null
                        && patternSearchTerm.matches(model.getModelKey().toUpperCase()) != null) || (
                        model.getModelVersion() != null
                                && patternSearchTerm.matches(model.getModelVersion().toUpperCase()) != null) || (
                        model.getDescription() != null
                                && patternSearchTerm.matches(model.getDescription().toUpperCase()) != null) || (
                        model.getStatus() != null
                                && patternSearchTerm.matches(model.getStatus().toUpperCase()) != null)).toList();

        FilteredList<ModelDTO> patternMatchedModels = new FilteredList<>(FXCollections.observableArrayList(models),
                p -> true);

        modellTreeModel = new ModelDisplayStrategy().createDisplayModel(patternMatchedModels);

        treeTableViewModell.setRoot(modellTreeModel.getRoot());
        treeTableViewModell.reapplyFilters();
    }

    private void initTextFields() {
        textFieldDistributionKey.textProperty().addListener((obs, oldVal, newVal) -> loadModellImporte());
    }

    @Subscribe
    public void handle(RefreshDatenstandEvent event) {
        loadModellImporte();
    }

    private void loadModellImporte() {
        String textDistributionKey = textFieldDistributionKey.getText().trim();

        if (textDistributionKey.isEmpty()
                || textDistributionKey.length() < textFieldDistributionKey.maxTextLengthProperty().get()) {
            return;
        }

        doAsync(() -> ModelRestClientHolder.getInstance()
                        .loadModelImports(textFieldDistributionKey.textProperty().get(), null, null).modelImportDTOList(),
                this::setModellImportItems);
    }

    private void setModellImportItems(List<ModelImportDTO> modelImports) {
        modelImports.sort(Comparator.<ModelImportDTO, Integer>comparing(
                        modelImport -> modelImport.getImportStatus().getSortOrder())
                .thenComparing(ModelImportDTO::getTimestampChange, Collections.reverseOrder()));

        tableViewModelImport.getItems().clear();
        tableViewModelImport.getItems().addAll(modelImports);

        ModelImportDTO selectedModelImport = getSelectedModellImport();

        if (selectedModelImport == null) {
            selectedModelImport = modelImports.stream()
                    .filter(modelImport -> modelImport.getImportStatus().equals(ImportStatus.IMPORTED))
                    .max(Comparator.comparing(ModelImportDTO::getTimestampChange)).orElse(null);
        }

        tableViewModelImport.getSelectionModel().select(selectedModelImport);
    }

    @Override
    public void loadModels(Long id) {
        doAsync(() -> ModelRestClientHolder.getInstance().loadModels(id).modelDTOSet(), this::setModellItems);
    }

    private void setModellItems(Set<ModelDTO> modelle) {
        Collection<String> parentModelKeys = modelle.stream().map(ModelDTO::getModelKey)
                .map(modelKey -> modelKey.substring(0, 3) + StringConstant.STAR + modelKey.substring(4))
                .collect(Collectors.toSet());

        Collection<ModelDTO> models = new ArrayList<>(modelle);
        for (String parentModelKey : parentModelKeys) {
            ModelDTO parentModel = new ModelDTO();
            parentModel.setModelKey(parentModelKey);
            models.add(parentModel);
        }

        filteredData = new FilteredList<>(FXCollections.observableArrayList(models), p -> true);
        modellTreeModel = new ModelDisplayStrategy().createDisplayModel(filteredData);

        treeTableViewModell.setRoot(getModelTreeModel().getRoot());
        treeTableViewModell.getRoot().setExpanded(true);
        treeTableViewModell.setShowRoot(false);

        if (getVehicleConfig().getModel() != null) {
            selectSavedModel();
        }
    }

    private void selectSavedModel() {
        ObservableList<TreeItem<ModelDTO>> children = treeTableViewModell.getRoot().getChildren();

        for (TreeItem<ModelDTO> model : children) {
            for (TreeItem<ModelDTO> modelTreeItem : model.getChildren()) {
                if (modelTreeItem.getValue().getId().equals(getSelectedModell().getId())) {
                    colRadio.setUserData(modelTreeItem.getValue());

                    model.setExpanded(true);

                    treeTableViewModell.getSelectionModel().select(modelTreeItem);

                    return;
                }
            }
        }
    }

    private ObjectProperty<ModelImportDTO> selectedModellImportProperty() {
        return this.selectedModellImport;
    }

    public final ModelImportDTO getSelectedModellImport() {
        return this.selectedModellImportProperty().get();
    }

    public final void setSelectedModellImport(ModelImportDTO modelImport) {
        getVehicleConfig().setModelImport(modelImport);

        selectedModellImportProperty().set(modelImport);
    }

    private ObjectProperty<ModelDTO> selectedModellProperty() {
        return this.selectedModell;
    }

    public final ModelDTO getSelectedModell() {
        return this.selectedModellProperty().get();
    }

    private void setSelectedModell(ModelDTO model) {
        getVehicleConfig().setModel(model);

        selectedModellProperty().set(model);

        setDirty();
    }

    private StringProperty datenstandStatusProperty() {
        if (datenstandStatus == null) {
            datenstandStatus = new SimpleStringProperty(I18N.getString("modelle.leer"));
        }

        return this.datenstandStatus;
    }

    private ReadOnlyBooleanProperty combinedValidationProperty() {
        return combinedValidation;
    }

    private void setCombinedValidation(boolean combinedValidation) {
        this.combinedValidation.set(combinedValidation);
    }

    public CustomTreeTableView<ModelDTO> getTreeTableViewModell() {
        return treeTableViewModell;
    }

    public CustomTableView<ModelImportDTO> getTableViewModelImport() {
        return tableViewModelImport;
    }

    @Override
    public void handle(SalesRegionNotExistingException exception) {
        String messageKey = exception.getMessageKey();
        DialogUtil.showWarnDialog(
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TITLE_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.HEADER_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TEXT_LOW_CASE));
    }

    @Override
    public void handle(SalesRegionNotRelevantException exception) {
        String messageKey = exception.getMessageKey();
        DialogUtil.showWarnDialog(
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TITLE_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.HEADER_LOW_CASE),
                I18N.getString(messageKey + StringConstant.DOT + StringCommonTermsUtil.TEXT_LOW_CASE));

    }

    @Override
    public void importModels(String distributionKey, Integer modelYear, String salesRegionLicensePlate) {
        //		ServiceController<ModelImport> serviceController = new ServiceController<>();
        //		serviceController.setOnSucceeded(e -> refreshMbvData(serviceController.getValue()));
        //		serviceController.setOnFailed(e -> handleException(serviceController.getException()));
        //		serviceController.setExecutionTime(1000);
        //		serviceController.start(() -> new ModelBD().importModel(salesRegionLicensePlate, modellJahr, vertriebsregionKz));
    }
}
