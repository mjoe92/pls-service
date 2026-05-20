package de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoAlert;
import de.vw.paso.client.control.status.CompletionStatusBarController;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.control.treetable.DisabledTreeSelectionModel;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.stueckliste.column.alignment.ColumnAlignment;
import de.vw.paso.client.stueckliste.fzgkonfig.content.AbstractContentController;
import de.vw.paso.client.util.FileUtil;
import de.vw.paso.client.util.TableExporter;
import de.vw.paso.client.util.TreeTableCellFactory;
import de.vw.paso.client.util.highlight.SelectionHighlightManager;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.validation.Validator;
import de.vw.paso.delegate.stammdaten.prnumber.PrNumberRestClientHolder;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberListDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberRestService;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.stage.Stage;
import de.vw.paso.utility.Pair;
import de.vw.paso.utility.PrNumberUtil;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

// todo: optimize creation of elements -> only 1x on init then using PasoPredicate
@FXController(name = "configuration")
public class ConfigurationController extends AbstractContentController {

    private static final int NULLABLE_FAMILY_COUNT = 1;
    private static final String SEPARATOR_PR_NUMBER = StringConstant.PLUS;
    private static final String SEPARATOR_PR_NUMBER_SPLIT = StringConstant.SPACE;
    private static final char SEPARATOR_PR_NUMBER_COLON = StringConstant.COLON_CHAR;
    private static final String SEPARATOR_ACTIVE_PR_NUMBER_FAMILY = StringConstant.COMMA;
    private static final String PATTERN_REPLACEABLE_SEPARATORS = "[,; ]";
    private static final int PR_NUMBER_LENGTH_WITH_FAMILY = 7;
    private static final int PR_NUMBER_INDEX_SEPARATOR = 3;
    private static final int PR_NUMBER_INDEX_START = 4;
    private static final String COL_STYLE = "highlight-col-selection";
    private static final String ROW_STYLE = "highlight-row-selection";
    private static final String SNR_FAMILY = "SNR";
    private static final String[] COMPLETE_FAMILY_NAMES = { SNR_FAMILY, "KDT" };

    private static final Collection<Pair<String, String>> ACTIVE_PR_NUMBER_FAMILIES_AND_PR_NUMBERS = List.of(
            new Pair<>("BAT", "J3Z"), new Pair<>("GEN", "8GA"), new Pair<>("BAH", "2EX"), new Pair<>("BAV", "1ZX"),
            new Pair<>("GKH", "0YZ"), new Pair<>("LGW", "IG9"), new Pair<>("VOG", "IM9"), new Pair<>("HIG", "IN9"),
            new Pair<>("STH", "0BA"), new Pair<>("STV", "0AA"));

    private static final String FILTERMODE_DEFAULT_MESSAGE = I18N.getString("filtermode.default");
    private static final String FILTERMODE_SELECTED_PR_NUMBERS_MESSAGE = I18N.getString(
            "filtermode.selected.pr.numbers");
    private static final String FILTERMODE_EMPTY_MESSAGE = I18N.getString("filtermode.emptyOnly");
    private static final String FILTERMODE_MULTISELECT_FAMILIES_MESSAGE = I18N.getString(
            "filtermode.multiSelectFamilies");
    private static final String FILTERMODE_ACTIVE_FAMILIES_MESSAGE = I18N.getString("filtermode.activeFamilies");
    private static final String FILTERMODE_INVALID_FAMILIES_MESSAGE = I18N.getString("filtermode.invalid.families");

    private static final String STYLE_CLASS_GRAY = "gray";
    private static final String STYLE_CLASS_STRIKETHROUGH = "strikethrough";

    @FXML
    private Button prDefaultFillButton;
    @FXML
    private Button prStringButton;
    @FXML
    private Button prResetButton;
    @FXML
    private Button reportButton;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private ComboBox<String> filterModeBox;
    @FXML
    private CustomTreeTableView<PrNumberTreeItemObject> tableviewPrNummern;
    @FXML
    private CompletionStatusBarController completionStatusBarController;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> prNumberFamily;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> prNumber;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> description1;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> description2;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> prNumberStatus;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, Date> prNumberBeginDate;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> prNumberBeginDateKey;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, Date> prNumberEndDate;
    @FXML
    private TreeTableColumn<PrNumberTreeItemObject, String> prNumberEndDateKey;
    // todo: revert when model sales key set (2. step) is fixed
    //    @FXML
    //    private TreeTableColumn<PrNumberTreeItemObject, String> salesSetting;
    @FXML
    private Label validDateLabel;

    private final SelectionHighlightManager<PrNumberTreeItemObject> highlightManager;
    private final Map<String, PrNumberDTO> prNumberMap;
    private final Map<String, PrNumberDTO> multiSelectFamiliesMap;
    private final Map<String, PrNumberDTO> activePrNumberFamilyPrNumberMap;
    private final Map<String, PrNumberDTO> invalidFamiliesMap;
    private final Map<String, PrNumberDTO> filteredEmptyPrNumberMap;

    private final Collection<PrNumberFamilyDTO> completePrNumberFamilyList;
    private final Collection<String> prNumberFamilyWithMultipleLSalesSettings;

    private final Map<String, PrNumberDTO> selectedPrNumbers;

    private final Map<String, ToggleGroup> familyToToggleGroup;
    private final BooleanProperty isAllRequiredCorrectlyFilled;
    private final BooleanProperty isActivePrNumberNotSet;

    /* PASO-275 Fix */
    private final Map<String, Collection<PrNumberDTO>> familyPrNumberMap;

    private PrNumberTreeModel prNumberTreeModel;
    private Date validDate;

    public ConfigurationController() {
        highlightManager = new SelectionHighlightManager<>();
        prNumberMap = new HashMap<>();
        multiSelectFamiliesMap = new HashMap<>();
        activePrNumberFamilyPrNumberMap = new HashMap<>();
        invalidFamiliesMap = new HashMap<>();
        filteredEmptyPrNumberMap = FXCollections.observableHashMap();
        completePrNumberFamilyList = new ArrayList<>();
        prNumberFamilyWithMultipleLSalesSettings = new ArrayList<>();
        selectedPrNumbers = FXCollections.observableHashMap();
        familyToToggleGroup = new HashMap<>();
        isAllRequiredCorrectlyFilled = new SimpleBooleanProperty();
        isActivePrNumberNotSet = new SimpleBooleanProperty();
        familyPrNumberMap = new HashMap<>();
    }

    public CustomTreeTableView<PrNumberTreeItemObject> getTableviewPrNummern() {
        return tableviewPrNummern;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        initButtons();
        initTable();

        if (PasoClientProperties.get().getStage().equals(Stage.PROD)) {
            prDefaultFillButton.setVisible(false);
        }

        initStatusBar();
    }

    @Override
    public void stop() {
        super.stop();
        highlightManager.removeFromTable();
    }

    @Override
    public void onVehicleConfigChanged() {
        if (!isEditable()) {
            prDefaultFillButton.setDisable(true);
            prResetButton.setDisable(true);
            reportButton.setDisable(true);

            tableviewPrNummern.setEditable(false);
            tableviewPrNummern.setSelectionModel(new DisabledTreeSelectionModel<>(tableviewPrNummern));
        }
    }

    @Override
    public void start() {
        super.start();

        doAsync(this::loadPrNumbers, this::setPrNummern);

        String toSelect = isEditable() ? FILTERMODE_DEFAULT_MESSAGE : FILTERMODE_SELECTED_PR_NUMBERS_MESSAGE;
        filterModeBox.getSelectionModel().select(toSelect);

        addValidators();
    }

    public void sortAndSetTree(PrNumberTreeItem root) {
        root.getChildren().sort(Comparator.comparing(e -> e.getValue().getPrNumberFamily().name()));
        for (TreeItem<PrNumberTreeItemObject> fam : root.getChildren()) {
            fam.getChildren().sort(Comparator.comparing(e -> e.getValue().getPrNumber().name()));
        }

        tableviewPrNummern.setRoot(root);
        updateStatusBarWithCurrentState();
        setDisableDefaultButton();
    }

    private PrNumberListDTO loadPrNumbers() {
        PrNumberRestService service = PrNumberRestClientHolder.getInstance();
        Long configId = getVehicleConfig().getId();

        return service.loadPrNumbersForConfig(configId);
    }

    private void addValidators() {
        addValidator(new Validator<>(isAllRequiredCorrectlyFilled, r -> r, StringConstant.EMPTY));
    }

    private int calcSelections(Collection<String> selectedKeys) {
        int counter = 0;

        for (String key : selectedKeys) {
            PrNumberDTO prNumber = selectedPrNumbers.get(key);
            if (prNumber != null) {
                counter++;
            }
        }

        return counter;
    }

    private int calculateEmptyFamilies() {
        if (completePrNumberFamilyList.isEmpty()) {
            return 0;
        }

        int counter = 0;

        for (PrNumberFamilyDTO family : completePrNumberFamilyList) {
            PrNumberDTO prNumber = selectedPrNumbers.get(family.name());
            for (String familyName : COMPLETE_FAMILY_NAMES) {
                if (!family.name().matches(familyName) && prNumber == null) {
                    counter++;
                }
            }
        }

        return counter;
    }

    private void setDisableDefaultButton() {
        prDefaultFillButton.setDisable(familyToToggleGroup.size() == calcSelections(familyToToggleGroup.keySet())
                && prNumberFamilyWithMultipleLSalesSettings.size() == calcSelections(
                prNumberFamilyWithMultipleLSalesSettings));
    }

    private void checkForActivePrNumberFamiliesAndPrNumbers() {
        Collection<String> activePrNumberSelectedFamilies = new ArrayList<>();
        activePrNumberFamilyPrNumberMap.clear();

        for (Pair<String, String> pair : ACTIVE_PR_NUMBER_FAMILIES_AND_PR_NUMBERS) {
            String key = pair.first();
            if (!selectedPrNumbers.containsKey(key)) {
                continue;
            }

            if (selectedPrNumbers.get(key).name().equals(pair.second())) {
                activePrNumberSelectedFamilies.add(key);
            }
        }

        if (activePrNumberSelectedFamilies.isEmpty()) {
            isActivePrNumberNotSet.set(true);
            createTreeItems();
            return;
        }

        fillActivePrNumberFamiliesMap(activePrNumberSelectedFamilies);
        isActivePrNumberNotSet.set(false);
        filterModeBox.setValue(FILTERMODE_ACTIVE_FAMILIES_MESSAGE);
        createTreeItems();

        StringBuilder stringBuffer = new StringBuilder();
        for (String prNumberFamily : activePrNumberSelectedFamilies) {
            if (!stringBuffer.isEmpty()) {
                stringBuffer.append(SEPARATOR_ACTIVE_PR_NUMBER_FAMILY);
            }

            stringBuffer.append(prNumberFamily);
        }

        Alert alert = new PasoAlert(Alert.AlertType.ERROR);
        alert.setTitle(I18N.getString("alert.wrong.pr.number.selection.title"));
        alert.setHeaderText(I18N.getString("alert.wrong.pr.number.selection.header"));
        alert.setContentText(
                I18N.getString("alert.wrong.pr.number.selection.content") + StringConstant.SPACE + stringBuffer);
        alert.showAndWait();
    }

    private void clearOnMouseClick(MouseEvent event, String message) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            filterModeBox.setValue(message);
            tableviewPrNummern.clearFilters();
            createTreeItems();
        }
    }

    private void createTreeItems() {
        ensureFilteredMapsInitialized();

        PrNumberTreeItem root = new PrNumberTreeItem(null);
        prNumberTreeModel = new PrNumberTreeModel(root.getUserObject());
        prNumberTreeModel.setRoot(root);

        Map<String, PrNumberTreeItem> familyMap = new HashMap<>();
        Collection<PrNumberDTO> currentPrNumbers = getCurrentPrNumbers();

        for (PrNumberDTO prNumber : currentPrNumbers) {
            if (!matchesSearch(prNumber)) {
                continue;
            }

            addPrNumberToTree(root, familyMap, prNumber);
        }

        sortAndSetTree(root);
        tableviewPrNummern.reapplyFilters();
        setDisableDefaultButton();
    }

    private void ensureFilteredMapsInitialized() {
        if (prNumberFamilyWithMultipleLSalesSettings.isEmpty()) {
            prNumberFamilyWithMultipleLSalesSettings.add(SNR_FAMILY);
            filterMultiSelectPrNumberFamilies();
        }

        if (invalidFamiliesMap.isEmpty()) {
            filterInvalidPrNumberFamilies();
        }

        if (!filterModeBox.getValue().matches(FILTERMODE_DEFAULT_MESSAGE)) {
            filterEmptyPrNumberFamiliesAndFillMap();
        }
    }

    private void addPrNumberToTree(PrNumberTreeItem root, Map<String, PrNumberTreeItem> familyMap, PrNumberDTO numb) {
        String familyName = numb.prNumberFamily().name();
        PrNumberTreeItem familyItem = familyMap.get(familyName);

        if (familyItem == null) {
            familyItem = new PrNumberTreeItem(new PrNumberTreeItemObject(numb.prNumberFamily()));
            familyItem.setExpanded(true);
            familyMap.put(familyName, familyItem);
            prNumberTreeModel.addElement(root, familyItem);

            if (!prNumberFamilyWithMultipleLSalesSettings.contains(familyName)) {
                familyToToggleGroup.put(familyName, new ToggleGroup());
            }
        }

        PrNumberTreeItemObject itemObject = new PrNumberTreeItemObject(numb);
        PrNumberDTO selected = selectedPrNumbers.get(familyName);
        if (selected != null) {
            itemObject.setSelected(selected.equals(numb));
        }

        prNumberTreeModel.addElement(familyItem, new PrNumberTreeItem(itemObject));
    }

    private Collection<PrNumberDTO> getCurrentPrNumbers() {
        String filterBoxValue = filterModeBox.getValue();
        if (filterBoxValue.matches(FILTERMODE_DEFAULT_MESSAGE)) {
            return prNumberMap.values();
        } else if (filterBoxValue.matches(FILTERMODE_MULTISELECT_FAMILIES_MESSAGE)) {
            return multiSelectFamiliesMap.values();
        } else if (filterBoxValue.matches(FILTERMODE_ACTIVE_FAMILIES_MESSAGE)) {
            return activePrNumberFamilyPrNumberMap.values();
        } else if (filterBoxValue.matches(FILTERMODE_INVALID_FAMILIES_MESSAGE)) {
            return invalidFamiliesMap.values();
        } else if (filterBoxValue.matches(FILTERMODE_SELECTED_PR_NUMBERS_MESSAGE)) {
            return selectedPrNumbers.values();
        }

        return filteredEmptyPrNumberMap.values();
    }

    private void fillActivePrNumberFamiliesMap(Collection<String> prNumberFamilies) {
        for (String family : prNumberFamilies) {
            for (PrNumberDTO prNumber : familyPrNumberMap.get(family)) {
                String prNumberName = prNumber.name();
                if (prNumberMap.containsKey(prNumberName) && !activePrNumberFamilyPrNumberMap.containsKey(
                        prNumberName)) {
                    activePrNumberFamilyPrNumberMap.put(prNumberName, prNumber);
                }
            }
        }
    }

    private void fillStatusBar(String summary, String linkText, EventHandler<MouseEvent> actionLink) {
        completionStatusBarController.setSummary(summary);
        completionStatusBarController.setMessage(StringConstant.EMPTY);
        completionStatusBarController.setActionLink(linkText);
        completionStatusBarController.setActionLinkEvent(actionLink);
    }

    private void filterEmptyPrNumberFamiliesAndFillMap() {
        filteredEmptyPrNumberMap.clear();

        //    emptyPrNumberMapProperty.setValue(filteredEmptyPrNumberMap);

        for (PrNumberFamilyDTO family : completePrNumberFamilyList) {
            String familyName = family.name();
            if ((selectedPrNumbers.get(familyName) != null && selectedPrNumbers.get(familyName) != null)
                    || filteredEmptyPrNumberMap.get(familyName) == null) {
                continue;
            }

            for (PrNumberDTO prNumber : familyPrNumberMap.get(familyName)) {
                String prNumberName = prNumber.name();
                if (prNumberMap.containsKey(prNumberName) && !filteredEmptyPrNumberMap.containsKey(prNumberName)) {
                    filteredEmptyPrNumberMap.put(prNumberName, prNumber);
                }
            }
        }
    }

    private void filterMultiSelectPrNumberFamilies() {
        if (!multiSelectFamiliesMap.isEmpty()) {
            return;
        }

        for (PrNumberFamilyDTO family : completePrNumberFamilyList) {
            String familyString = family.name();
            if (!prNumberFamilyWithMultipleLSalesSettings.contains(familyString)
                    || multiSelectFamiliesMap.get(familyString) != null) {
                continue;
            }

            for (PrNumberDTO prNumber : familyPrNumberMap.get(familyString)) {
                String prNumberName = prNumber.name();
                if (prNumberMap.containsKey(prNumberName) && !multiSelectFamiliesMap.containsKey(prNumberName)) {
                    multiSelectFamiliesMap.put(prNumberName, prNumber);
                }
            }
        }
    }

    private void filterInvalidPrNumberFamilies() {
        for (PrNumberFamilyDTO family : completePrNumberFamilyList) {
            Collection<PrNumberDTO> prNumbers = familyPrNumberMap.get(family.name());
            if (!allPrNumbersInvalid(prNumbers)) {
                continue;
            }

            for (PrNumberDTO prNumber : prNumbers) {
                invalidFamiliesMap.put(prNumber.name(), prNumber);
            }
        }
    }

    private void initButtons() {
        prDefaultFillButton.setOnAction(e -> {
            selectFirstChildOfUnfilledPrFamilies();
            updateStatusBarWithCurrentState();
        });

        filterModeBox.getItems()
                .addAll(FILTERMODE_DEFAULT_MESSAGE, FILTERMODE_SELECTED_PR_NUMBERS_MESSAGE, FILTERMODE_EMPTY_MESSAGE,
                        FILTERMODE_MULTISELECT_FAMILIES_MESSAGE, FILTERMODE_ACTIVE_FAMILIES_MESSAGE,
                        FILTERMODE_INVALID_FAMILIES_MESSAGE);

        filterModeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (String mode : filterModeBox.getItems()) {
                if (mode.matches(newValue)) {
                    createTreeItems();
                    tableviewPrNummern.clearFilters();
                }
            }
        });
        prStringButton.setOnAction(e -> showPrNumberDialog());
        prResetButton.setOnAction(e -> {
            Alert alert = new PasoAlert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(I18N.getString("alert.reset.title"));
            alert.setHeaderText(null);
            alert.setContentText(I18N.getString("alert.reset.text"));
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                updateSelectedPrNumbersFromModel();
                createTreeItems();
            }
        });
        reportButton.setOnAction(e -> report());
    }

    private <S, T> void initColumnAlignment(TreeTableColumn<S, T> column) {
        ColumnAlignment columnAlignment = ColumnAlignment.findByColumnName(column.getId());
        column.setStyle(columnAlignment.getAlignment());
    }

    private void initStatusBar() {
        registerSubController(completionStatusBarController);
    }

    private void initTable() {
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isEmpty(newValue)) {
                tableviewPrNummern.clearLastStoredFilter();
            }

            createTreeItems();
        });

        highlightManager.initTable(tableviewPrNummern, ROW_STYLE, COL_STYLE, this::setRowStyle);

        tableviewPrNummern.setShowRoot(false);
        tableviewPrNummern.getSelectionModel().setCellSelectionEnabled(true);
        tableviewPrNummern.setEditable(false);
        tableviewPrNummern.sortPolicyProperty().set(param -> false);
        tableviewPrNummern.setPlaceholder(new Label(I18N.getString("prnr.familien.leer")));

        prNumberFamily.setCellFactory((param -> new SelectionCell(!isEditable())));
        prNumberFamily.setCellValueFactory(data -> {
            PrNumberTreeItemObject value = data.getValue().getValue();
            if (value == null) {
                return null;
            }

            return value.isFamily() ? new SimpleStringProperty(value.getPrNumberFamily().name()) : null;
        });

        prNumber.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            return value == null || value.isFamily() ? null : new SimpleStringProperty(value.getPrNumber().name());
        });

        description1.setCellValueFactory(data -> {
            PrNumberTreeItemObject value = data.getValue().getValue();
            if (value == null) {
                return null;
            }

            String text =
                    value.isFamily() ? value.getPrNumberFamily().description() : value.getPrNumber().description();
            return new SimpleStringProperty(text);
        });

        description2.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            return value == null || value.isFamily() ? null :
                    new SimpleStringProperty(value.getPrNumber().additionalName());
        });

        prNumberStatus.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            if (value == null || value.isFamily()) {
                return null;
            }

            Integer status = value.getPrNumber().status();
            return status == null ? null : new SimpleStringProperty(status.toString());
        });

        prNumberBeginDate.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            return value == null || value.isFamily() ? null :
                    new SimpleObjectProperty<>(value.getPrNumber().startDate());
        });

        prNumberBeginDate.setCellFactory(TreeTableCellFactory.forDateColumn());
        prNumberBeginDateKey.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            return value == null || value.isFamily() ? null : new SimpleStringProperty(value.getPrNumber().startKey());
        });

        prNumberEndDate.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            return value == null || value.isFamily() ? null : new SimpleObjectProperty<>(value.getPrNumber().endDate());
        });

        prNumberEndDate.setCellFactory(TreeTableCellFactory.forDateColumn());
        prNumberEndDateKey.setCellValueFactory(e -> {
            PrNumberTreeItemObject value = e.getValue().getValue();
            return value == null || value.isFamily() ? null : new SimpleStringProperty(value.getPrNumber().endKey());
        });

        // todo: revert maybe when prNumbersForSelectedModel will be fixed
        //        salesSetting.setCellValueFactory(e -> {
        //            PrNumberTreeItemObject value = e.getValue().getValue();
        //            return value == null || value.isFamily() ? null : new SimpleStringProperty(StringConstant.DASH);
        //        });

        tableviewPrNummern.makeHeaderWrappable();
        tableviewPrNummern.makeFilterable();
        for (TreeTableColumn<PrNumberTreeItemObject, ?> column : tableviewPrNummern.getColumns()) {
            initColumnAlignment(column);
        }
    }

    private void setRowStyle(TreeTableRow<PrNumberTreeItemObject> row, boolean empty) {
        row.getStyleClass().removeAll(STYLE_CLASS_GRAY, STYLE_CLASS_STRIKETHROUGH);
        row.setDisable(false);

        if (empty) {
            return;
        }

        PrNumberTreeItemObject itemObject = row.getItem();
        if (itemObject == null || itemObject.isFamily()) {
            return;
        }

        if (validDate == null) {
            validDate = getVehicleConfig().getValidDate();
        }

        if (validDateLabel.textProperty().get().isBlank()) {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String vehicleConfigValidDate = dateFormat.format(validDate);
            String validDateStr = I18N.getString("valid.date");
            validDateLabel.setText(validDateStr + StringConstant.COLON_SPACE + vehicleConfigValidDate);
        }

        PrNumberDTO itemPrNumber = itemObject.getPrNumber();
        Date einsatzDate = itemPrNumber.startDate();
        Date entfallDate = itemPrNumber.endDate();
        if (!PrNumberValidator.isStartDateBeforeEndDate(einsatzDate, entfallDate)) {
            row.getStyleClass().addAll(STYLE_CLASS_GRAY, STYLE_CLASS_STRIKETHROUGH);
            row.setDisable(true);
            return;
        }

        if (!PrNumberValidator.isPrNumberValid(einsatzDate, entfallDate, validDate)) {
            row.getStyleClass().addAll(STYLE_CLASS_GRAY, STYLE_CLASS_STRIKETHROUGH);
            return;
        }

        if (validDate.before(einsatzDate)) {
            row.getStyleClass().add(STYLE_CLASS_GRAY);
            return;
        }

        if (validDate.after(entfallDate)) {
            row.getStyleClass().add(STYLE_CLASS_GRAY);
        }
    }

    private boolean allPrNumbersInvalid(Collection<PrNumberDTO> prNumbers) {
        Date validDate = getVehicleConfig().getValidDate();
        return prNumbers.stream().noneMatch(
                prNumber -> PrNumberValidator.isPrNumberValid(prNumber.startDate(), prNumber.endDate(), validDate));
    }

    private boolean isValidPrNumber(String prNumber) {
        return prNumber != null && prNumber.length() == 3;
    }

    private void setPrNummern(PrNumberListDTO result) {
        for (PrNumberDTO prNumber : result.prNumberDTOList()) {
            prNumberMap.put(prNumber.name(), prNumber);

            PrNumberFamilyDTO prNumberFamily = prNumber.prNumberFamily();
            familyPrNumberMap.computeIfAbsent(prNumberFamily.name(), value -> new ArrayList<>()).add(prNumber);

            if (!completePrNumberFamilyList.contains(prNumberFamily)) {
                completePrNumberFamilyList.add(prNumberFamily);
            }
        }

        String prNumberString = getVehicleConfig().getPrNumberString();
        if (prNumberString == null) {
            updateSelectedPrNumbersFromModel();
        } else {
            updateSelectedPrNumbersFrom(prNumberString);
        }

        checkForActivePrNumberFamiliesAndPrNumbers();
    }

    private boolean matchesSearch(PrNumberDTO numb) {
        String search = textFieldSearch.getText();
        if (StringUtils.isEmpty(search)) {
            return true;
        }

        return StringUtils.containsIgnoreCase(numb.name(), search) || StringUtils.containsIgnoreCase(
                numb.prNumberFamily().name(), search) || StringUtils.containsIgnoreCase(numb.description(), search);
    }

    private void prNumberStringCharacterReplace(TextArea textArea) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasString()) {
            return;
        }

        String text = clipboard.getString();
        if (text == null) {
            return;
        }

        String inputText = Arrays.stream(clipboard.getString().split(SEPARATOR_PR_NUMBER_SPLIT))
                .filter(x -> (x.length() == PR_NUMBER_LENGTH_WITH_FAMILY) && (x.charAt(PR_NUMBER_INDEX_SEPARATOR)
                        == SEPARATOR_PR_NUMBER_COLON)).map(a -> a.substring(PR_NUMBER_INDEX_START))
                .collect(Collectors.joining(SEPARATOR_PR_NUMBER));

        String replacing =
                StringUtils.isEmpty(inputText) ? text.replaceAll(PATTERN_REPLACEABLE_SEPARATORS, SEPARATOR_PR_NUMBER) :
                        SEPARATOR_PR_NUMBER + inputText;
        textArea.replaceText(textArea.getSelection(), replacing);
    }

    private void prNumberStringPasteEvent(KeyEvent event, TextArea textArea, KeyCombination pasteKeyCombination) {
        if (!pasteKeyCombination.match(event)) {
            return;
        }

        prNumberStringCharacterReplace(textArea);

        event.consume();
    }

    private void prNumberStringTypeEvent(KeyEvent event, TextArea textArea) {
        if (!event.getCharacter().matches(PATTERN_REPLACEABLE_SEPARATORS)) {
            return;
        }

        textArea.replaceText(textArea.getSelection(), SEPARATOR_PR_NUMBER);

        event.consume();
    }

    private void report() {
        File file = FileUtil.openSaveExcelDialog("pr_konfiguration", getControl().getScene().getWindow());
        if (file == null) {
            return;
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            TableExporter.export(tableviewPrNummern, out, TableExporter.TableExporterConfig.createDefault());
            FileUtil.openFileWithAssociatedProgram(file);
        } catch (IOException e) {
            ExceptionHandler.instance().handleException(e);
        }
    }

    private void savePrNumbers() {
        String prNumberString = PrNumberUtil.sortJoin(selectedPrNumbers.values());
        VehicleConfigDTO config = getVehicleConfig();
        config.setPrNumberString(prNumberString);

        Map<Long, Collection<Long>> prFamilyIdToPrNumberIds = mapToPrNumberMap();
        config.setPrFamilyToNumberIds(prFamilyIdToPrNumberIds);

        setDisableDefaultButton();
        checkForActivePrNumberFamiliesAndPrNumbers();

        setDirty();
    }

    private Map<Long, Collection<Long>> mapToPrNumberMap() {
        Map<Long, Collection<Long>> result = new HashMap<>();
        Collection<PrNumberDTO> prNumbers = selectedPrNumbers.values();
        for (PrNumberDTO prNumber : prNumbers) {
            result.computeIfAbsent(prNumber.prNumberFamily().id(), id -> new ArrayList<>())
                    .add(prNumber.assignmentId());
        }

        return result;
    }

    private void selectFirstChildOfUnfilledPrFamilies() {
        StringBuilder prNumberStr = new StringBuilder();
        for (TreeItem<PrNumberTreeItemObject> familyTreeItem : tableviewPrNummern.getRoot().getChildren()) {
            PrNumberTreeItemObject prNumberTreeItemObject = familyTreeItem.getValue();
            if (prNumberTreeItemObject.isFamily() && !selectedPrNumbers.containsKey(
                    prNumberTreeItemObject.getPrNumberFamily().name())
                    || selectedPrNumbers.get(prNumberTreeItemObject.getPrNumberFamily().name()) == null) {
                prNumberStr.append(getValidPrNumber(familyTreeItem));
            }
        }

        String prNumberString = PrNumberUtil.sortJoin(selectedPrNumbers.values());
        updateSelectedPrNumbersFrom(prNumberString + prNumberStr);

        tableviewPrNummern.refresh();
        if (filterModeBox.getValue().matches(FILTERMODE_EMPTY_MESSAGE)) {
            filterModeBox.setValue(FILTERMODE_DEFAULT_MESSAGE);
            createTreeItems();
        }

        prDefaultFillButton.setDisable(true);

        savePrNumbers();
    }

    private String getValidPrNumber(TreeItem<PrNumberTreeItemObject> familyTreeItem) {
        StringBuilder validPrNumber = new StringBuilder();
        ObservableList<TreeItem<PrNumberTreeItemObject>> treeItems = familyTreeItem.getChildren();
        for (TreeItem<PrNumberTreeItemObject> treeItem : treeItems) {
            PrNumberDTO prNumber = treeItem.getValue().getPrNumber();
            Date einsatz = prNumber.startDate();
            Date entfall = prNumber.endDate();
            Date validDate = getVehicleConfig().getValidDate();
            if (PrNumberValidator.isPrNumberValid(einsatz, entfall, validDate)) {
                validPrNumber.append(SEPARATOR_PR_NUMBER);
                validPrNumber.append(prNumber.name());
                break;
            }
        }

        return validPrNumber.toString();
    }

    private void showPrNumberDialog() {
        boolean editable = isEditable();
        ButtonType buttonType = editable ? ButtonType.APPLY : ButtonType.CLOSE;
        Alert alert = new PasoAlert(Alert.AlertType.INFORMATION, null, buttonType);

        alert.setTitle(I18N.getString("prnumber.dialog.title"));
        alert.setHeaderText(I18N.getString("prnumber.dialog.header"));

        VBox dialogPaneContent = new VBox();
        Font font = Font.font("monospace", 16);

        KeyCombination pasteKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        String prNumberString = PrNumberUtil.sortJoin(selectedPrNumbers.values());

        TextArea textArea = new TextArea(prNumberString);
        textArea.setFont(font);
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(60);
        textArea.addEventHandler(KeyEvent.KEY_TYPED, event -> prNumberStringTypeEvent(event, textArea));
        textArea.addEventHandler(KeyEvent.KEY_PRESSED,
                event -> prNumberStringPasteEvent(event, textArea, pasteKeyCombination));

        HBox buttonBox = new HBox();
        Button copyButton = new Button();
        Button pasteButton = new Button();

        copyButton.setText(I18N.getString("copy"));
        copyButton.setGraphic(new ImageView(ActionIcon.COPY_16X16.getImage()));
        copyButton.addEventHandler(ActionEvent.ACTION, event -> {
            if (textArea.getSelectedText().isEmpty()) {
                textArea.selectAll();
            }

            textArea.copy();
        });

        if (!editable) {
            textArea.setEditable(false);
            textArea.setStyle("-fx-text-fill: grey;");
            pasteButton.setDisable(true);
        }

        pasteButton.setGraphic(new ImageView(ActionIcon.PASTE_16X16.getImage()));
        pasteButton.setText(I18N.getString("paste"));
        pasteButton.addEventHandler(ActionEvent.ACTION, event -> prNumberStringCharacterReplace(textArea));

        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(0, 0, 10, 0));
        buttonBox.getChildren().add(copyButton);
        buttonBox.getChildren().add(pasteButton);

        dialogPaneContent.getChildren().add(buttonBox);
        dialogPaneContent.getChildren().add(textArea);

        alert.getDialogPane().setContent(dialogPaneContent);
        alert.setHeight(300);
        alert.setWidth(500);

        Optional<ButtonType> selectedButton = alert.showAndWait();
        if (selectedButton.isPresent() && selectedButton.get() == ButtonType.APPLY) {
            updateSelectedPrNumbersFrom(textArea.getText());
            filterEmptyPrNumberFamiliesAndFillMap();
            createTreeItems();
            savePrNumbers();
        }
    }

    private void updateSelectedPrNumbersFromModel() {
        selectedPrNumbers.clear();

        setDisableDefaultButton();
        updateStatusBarWithCurrentState();
    }

    private void updateSelectedPrNumbersFrom(String prNumberString) {
        selectedPrNumbers.clear();
        setDisableDefaultButton();

        if (StringUtils.isEmpty(prNumberString)) {
            return;
        }

        Collection<String> splitPrNumbers = PrNumberUtil.split(prNumberString);
        for (String prNumberStr : splitPrNumbers) {
            if (!isValidPrNumber(prNumberStr)) {
                continue;
            }

            PrNumberDTO prNumber = prNumberMap.get(prNumberStr.toUpperCase());
            if (prNumber == null) {
                continue;
            }

            String selectedPrNumberFamilyKey = prNumber.prNumberFamily().name();
            selectedPrNumbers.put(selectedPrNumberFamilyKey, prNumber);
        }
    }

    private void updateStatusBar(String summaryString, String statusMessage, String filterModeMessage) {
        fillStatusBar(summaryString, statusMessage, event -> clearOnMouseClick(event, filterModeMessage));
    }

    private void updateStatusBarWithCurrentState() {
        int numberOfEmptyFamilies = calculateEmptyFamilies();

        String summaryString = (completePrNumberFamilyList.size() - NULLABLE_FAMILY_COUNT) - numberOfEmptyFamilies
                + StringConstant.SLASH + (completePrNumberFamilyList.size() - NULLABLE_FAMILY_COUNT);

        boolean isRequiredFilled;
        String statusMessage, filterModeMessage;
        if (numberOfEmptyFamilies == completePrNumberFamilyList.size()) {
            // no families filled
            statusMessage = I18N.getString("status.message.noSelection");
            filterModeMessage = FILTERMODE_DEFAULT_MESSAGE;

            isRequiredFilled = false;
        } else if (numberOfEmptyFamilies > 0) {
            // partially filled
            statusMessage = I18N.getString("status.message.partialSelection", numberOfEmptyFamilies);
            filterModeMessage = FILTERMODE_EMPTY_MESSAGE;

            // temporarily disabled this check by setting it to true (mainly to update prod for Audi colleagues onboarding)
            isRequiredFilled = true;
        } else {
            // all Filled
            statusMessage = I18N.getString("status.message.fullSelection");
            filterModeMessage = FILTERMODE_MULTISELECT_FAMILIES_MESSAGE;

            isRequiredFilled = isActivePrNumberNotSet.get();
        }

        updateStatusBar(summaryString, statusMessage, filterModeMessage);
        isAllRequiredCorrectlyFilled.set(isRequiredFilled);
    }

    private class SelectionCell extends TreeTableCell<PrNumberTreeItemObject, String> {

        private final boolean disabled;

        public SelectionCell(boolean disabled) {
            this.disabled = disabled;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            setStyle(null);
            setText(null);
            setGraphic(null);

            if (empty) {
                return;
            }

            TreeItem<PrNumberTreeItemObject> treeItem = getTableRow().getTreeItem();
            if (treeItem == null) {
                setDisableDefaultButton();
                updateStatusBarWithCurrentState();
                return;
            }

            PrNumberTreeItemObject value = treeItem.getValue();
            HBox box = new HBox(4);
            box.setAlignment(Pos.CENTER);
            if (value.isFamily()) {
                setText(item);

                setGraphic(box);

                setDisableDefaultButton();
                updateStatusBarWithCurrentState();

                return;
            }

            RadioButton radioButton = new RadioButton();
            if (disabled) {
                radioButton.setDisable(true);
            }

            PrNumberDTO prNumber = value.getPrNumber();
            String prNumberFamilyName = prNumber.prNumberFamily().name();
            radioButton.setOnMouseClicked(
                    mouseEvent -> afterSelected(mouseEvent, radioButton, prNumberFamilyName, prNumber));

            radioButton.setOnAction(event -> onSelect(prNumber));

            radioButton.setUserData(prNumber);
            box.getChildren().add(radioButton);

            if (!prNumberFamilyWithMultipleLSalesSettings.contains(prNumberFamilyName)) {
                radioButton.setToggleGroup(familyToToggleGroup.get(prNumberFamilyName));
            }

            Collection<PrNumberDTO> values = selectedPrNumbers.values();
            for (PrNumberDTO selected : values) {
                if (selected.name().matches(prNumber.name())) {
                    radioButton.setSelected(true);
                    break;
                }
            }

            setGraphic(box);

            setDisableDefaultButton();
            updateStatusBarWithCurrentState();
        }

        private void afterSelected(MouseEvent mouseEvent, RadioButton radioButton, String prNumberFamilyName,
                PrNumberDTO prNumber) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY && radioButton.isSelected()) {
                radioButton.setSelected(false);

                PrNumberDTO selected = selectedPrNumbers.get(prNumberFamilyName);
                if (selected.name().matches(prNumber.name())) {
                    selectedPrNumbers.remove(prNumberFamilyName);
                }

                updateStatusBarWithCurrentState();
            } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                radioButton.setSelected(true);
                updateStatusBarWithCurrentState();
            }

            savePrNumbers();
        }

        private void onSelect(PrNumberDTO prNumber) {
            selectPrNumber();

            if (PrNumberValidator.isPrNumberValid(prNumber.startDate(), prNumber.endDate(),
                    getVehicleConfig().getValidDate())) {
                return;
            }

            Alert alert = new PasoAlert(Alert.AlertType.WARNING);
            alert.setTitle(I18N.getString("alert.invalid.pr.number.title"));
            alert.setHeaderText(null);

            String invalidationReasonMessageKey = getInvalidationReasonMessageKey(prNumber);
            alert.setContentText(
                    I18N.getString("alert.invalid.pr.number.name") + StringConstant.SPACE_COLON_SPACE + prNumber.name()
                            + "\n" + I18N.getString(invalidationReasonMessageKey));

            alert.showAndWait();
        }

        private String getInvalidationReasonMessageKey(PrNumberDTO prNumber) {
            Date validDate = getVehicleConfig().getValidDate();
            if (validDate.before(prNumber.startDate())) {
                return "valid.date.before.start.date";
            }

            return validDate.after(prNumber.endDate()) ? "valid.date.after.end.date" : "alert.invalid.pr.number.title";
        }

        private void selectPrNumber() {
            PrNumberDTO currentPrNumber = getTableRow().getTreeItem().getValue().getPrNumber();
            String familyKey = currentPrNumber.prNumberFamily().name();
            if (selectedPrNumbers.containsKey(familyKey)) {
                boolean alreadyExistedInSelectionMap = false;

                PrNumberDTO prNumber = selectedPrNumbers.get(familyKey);
                if (prNumber.name().matches(currentPrNumber.name())) {
                    alreadyExistedInSelectionMap = true;
                }

                if (!alreadyExistedInSelectionMap && !prNumberFamilyWithMultipleLSalesSettings.contains(familyKey)) {
                    selectedPrNumbers.remove(familyKey);
                }
            } else {
                selectedPrNumbers.put(familyKey, currentPrNumber);
            }

            savePrNumbers();
        }
    }
}
