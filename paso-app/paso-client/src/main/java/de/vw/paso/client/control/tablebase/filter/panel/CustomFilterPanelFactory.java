package de.vw.paso.client.control.tablebase.filter.panel;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.control.treetable.CustomTreeTableView;
import de.vw.paso.client.exception.ExceptionHandler;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.PasoPredicate;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.converter.BooleanStringConverter;
import de.vw.paso.client.util.customfilter.CustomFilterSortUtil;
import de.vw.paso.client.util.customfilter.CustomFilterUtil;
import de.vw.paso.client.util.customfilter.DateToLocalDateConverter;
import de.vw.paso.client.util.customfilter.FilterPanelPredicateData;
import de.vw.paso.client.util.customfilter.FilterStrings;
import de.vw.paso.client.util.customfilter.IFilterableColumn;
import de.vw.paso.client.util.customfilter.PredicateData;
import de.vw.paso.exception.ServiceConsumer;
import de.vw.paso.utility.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFilterPanelFactory<S> implements ServiceConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFilterPanelFactory.class);

    private static final String PARSE_EXCEPTION_MESSAGE = "Could not parse date";

    private CustomFilterPanel filterPanel;

    private Map<TableColumnBase<S, ?>, PredicateData> columnToPredicateDataMap;
    private Map<TableColumnBase<S, ?>, FilterPanelPredicateData> columnToFilterPanelPredicateDataMap;
    private Control tableView;

    private Map<TableColumnBase<S, ?>, Map<String, CustomTableFilterValue>> columnToFilterValueMapMap = new HashMap<>();
    private Map<String, CustomTableFilterValue> stringToFilterValueMap = new HashMap<>();

    private ListView<CustomTableFilterValue> checkListView;
    private FilteredList<CustomTableFilterValue> filterableListViewItemList;
    private PasoPredicate checkListViewPredicate = null;
    private ObservableList<CustomTableFilterValue> tableFilterValues = FXCollections.observableArrayList();
    private ObservableList<String> tableValuesStringBackupList = FXCollections.observableArrayList();
    private Collection<S> itemCollection;

    private VBox searchArea;
    private HBox searchAreaTop;
    private HBox searchAreaMid;
    private HBox searchAreaBottom;
    private Boolean disableApplyFilter = false;

    private final String buttonApply;
    private final String buttonNone;
    private final String buttonAll;
    private final String buttonReset;

    private CustomFilterPanelFactory(Control tableView, TableColumnBase<S, ?> column, ContextMenu contextMenu,
            Collection<S> tableItems, Map<TableColumnBase<S, ?>, PredicateData> columnPredicateDataMap,
            Map<TableColumnBase<S, ?>, FilterPanelPredicateData> columnFilterPanelPredicateDataMap) {

        buttonApply = I18N.getString("button.apply");
        buttonNone = I18N.getString("button.none");
        buttonAll = I18N.getString("button.all");
        buttonReset = I18N.getString("button.reset");
        columnToPredicateDataMap = columnPredicateDataMap;
        itemCollection = tableItems;
        this.tableView = tableView;
        columnToFilterPanelPredicateDataMap = columnFilterPanelPredicateDataMap;

        final ListView<CustomTableFilterValue> listView = initChecklistView(column);
        final HBox buttons = initButtons(contextMenu, column);
        final ObservableList<CustomTableFilterValue> items = listView.getItems();
        searchArea = new VBox();
        if (items.size() > 1) {
            final Class<?> actualOriginClass = CustomFilterSortUtil.determineActualOriginClass(items.get(0),
                    items.get(1));
            searchArea = initSearchArea(column, contextMenu, actualOriginClass);
        }
        filterPanel = new CustomFilterPanel(searchArea, listView, buttons);
        reapplyPreviousFilterPanelPredicateData(column);

        filterPanel.setPadding(new Insets(3));

        if (tableView instanceof CustomTreeTableView) {
            itemCollection.clear();
        }
        registerEventBus();

        buttonEnableManaging(null);
    }

    public static <S> void createMenuItem(TableColumnBase<S, ?> column, Collection<S> items, Control tableView,
            Map<TableColumnBase<S, ?>, PredicateData> columnPredicateDataMap,
            Map<TableColumnBase<S, ?>, FilterPanelPredicateData> columnFilterPanelPredicateDataMap) {
        if (column.getContextMenu() != null) {
            column.getContextMenu().getItems().clear();
            column.getContextMenu().hide();
        }

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("custom-column-filter");
        column.setContextMenu(contextMenu);

        CustomFilterPanel filterPanel = new CustomFilterPanelFactory<>(tableView, column, contextMenu, items,
                columnPredicateDataMap, columnFilterPanelPredicateDataMap).filterPanel;

        CustomMenuItem menuItem = new CustomMenuItem();
        menuItem.setContent(filterPanel);
        menuItem.setHideOnClick(false);

        contextMenu.getItems().add(menuItem);

        // runLater() so we request the focus when the window is actually visible.
        contextMenu.setOnShown(e -> Platform.runLater(filterPanel.getCheckListView()::requestFocus));
    }

    private VBox initSearchArea(TableColumnBase column, ContextMenu contextMenu, Class<?> actualOriginClass) {
        VBox searchArea = new VBox();
        searchAreaTop = new HBox();
        searchAreaTop.setPadding(new Insets(5, 10, 5, 0));
        searchAreaTop.setSpacing(5);
        searchAreaMid = new HBox();
        searchAreaMid.setPadding(new Insets(5, 10, 5, 0));
        searchAreaMid.setSpacing(5);
        searchAreaBottom = new HBox();
        searchAreaBottom.setPadding(new Insets(5, 10, 5, 0));
        searchAreaBottom.setSpacing(5);

        Label lowerRangeLimitLabel = new Label();
        Label upperRangeLimitLabel = new Label();

        TextField mainTextfield = new TextField();
        mainTextfield.setLayoutX(contextMenu.getWidth());
        TextField secondaryTextField = new TextField();

        DatePicker mainTextfieldDatePicker = new DatePicker();

        ComboBox<String> templates = new ComboBox<>();
        ObservableList<String> searchMode = FXCollections.observableArrayList();

        if (actualOriginClass.equals(String.class) || actualOriginClass.equals(CogCoordinates.class)) {
            searchMode.addAll(FilterStrings.getLocalizedContainsString(),
                    FilterStrings.getLocalizedDoesNotContainString());
        } else {
            if (isaNumber(actualOriginClass)) {
                searchMode.addAll(FilterStrings.getLocalizedEqualsString(),
                        FilterStrings.getLocalizedWithinRangeString(), FilterStrings.getLocalizedOutsideOfRangeString(),
                        FilterStrings.getLocalizedTopString());
            } else if (actualOriginClass.equals(Date.class) || actualOriginClass.equals(LocalDate.class)) {
                searchMode.addAll(FilterStrings.getLocalizedWithinRangeString(),
                        FilterStrings.getLocalizedOutsideOfRangeString(), FilterStrings.getLocalizedInYearString());
            }
        }

        initTextFields(column, actualOriginClass, mainTextfield, secondaryTextField, templates, contextMenu);

        if (actualOriginClass.equals(CogCoordinates.class)) {
            searchAreaTop.getChildren().addAll(lowerRangeLimitLabel, mainTextfield);
            searchAreaBottom.getChildren().addAll(upperRangeLimitLabel, secondaryTextField);
        } else if (actualOriginClass.equals(LocalDate.class) || actualOriginClass.equals(Date.class)) {
            mainTextfieldDatePicker = new DatePicker();
            DatePicker secondaryTextfieldDatePicker = new DatePicker();

            DatePicker finalMainTextfieldDatePicker = mainTextfieldDatePicker;
            initDatePickers(column, actualOriginClass, mainTextfieldDatePicker, templates, secondaryTextfieldDatePicker,
                    finalMainTextfieldDatePicker, contextMenu);

            searchAreaTop.getChildren().addAll(lowerRangeLimitLabel, mainTextfieldDatePicker);
            searchAreaBottom.getChildren().addAll(upperRangeLimitLabel, secondaryTextfieldDatePicker);
        } else {
            searchAreaTop.getChildren().addAll(lowerRangeLimitLabel, mainTextfield);
            searchAreaBottom.getChildren().addAll(upperRangeLimitLabel, secondaryTextField);
        }

        templates.setItems(searchMode);
        DatePicker finalMainTextfieldDatePicker = mainTextfieldDatePicker;
        templates.valueProperty().addListener(
                (observable, oldValue, newValue) -> handleTemplateSelectionChange(lowerRangeLimitLabel,
                        upperRangeLimitLabel, mainTextfield, secondaryTextField, searchAreaTop, searchAreaBottom,
                        oldValue, newValue, actualOriginClass, finalMainTextfieldDatePicker));
        if (actualOriginClass.equals(String.class) || actualOriginClass.equals(CogCoordinates.class)) {
            templates.setValue(FilterStrings.getLocalizedContainsString());
            secondaryTextField.setVisible(false);
        } else {
            if (!searchMode.isEmpty()) {
                templates.setValue(searchMode.getFirst());
            }
        }

        if (actualOriginClass.equals(String.class) || actualOriginClass.equals(CogCoordinates.class)) {
            searchAreaTop.setSpacing(0);
            searchArea.getChildren().addAll(templates, searchAreaTop);
        } else {
            searchArea.getChildren().addAll(templates, searchAreaTop, searchAreaBottom);
        }

        return searchArea;
    }

    private void initTextFields(TableColumnBase column, Class actualOriginClass, TextField mainTextfield,
            TextField secondaryTextField, ComboBox templates, ContextMenu contextMenu) {
        mainTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!actualOriginClass.equals(String.class) && !actualOriginClass.equals(CogCoordinates.class)) {

                String numbersOnlyString = newValue.replaceAll("[^\\d.]", "");
                if (!numbersOnlyString.matches(newValue) && !newValue.trim().isEmpty()) {
                    mainTextfield.textProperty().set(numbersOnlyString);
                    return;
                }
            }
            handleAdvancedSearch((String) templates.getValue(), mainTextfield.getText(), secondaryTextField.getText(),
                    actualOriginClass, column);
        });
        mainTextfield.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !disableApplyFilter) {
                filterContent(column, mainTextfield.getText(), tableFilterValues);
                Event.fireEvent(contextMenu, new WindowEvent(contextMenu, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        secondaryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!actualOriginClass.equals(String.class) && !actualOriginClass.equals(CogCoordinates.class)) {
                String numbersOnlyString = newValue.replaceAll("[^\\d.]", "");
                if (!numbersOnlyString.matches(newValue) && !newValue.trim().isEmpty()) {
                    secondaryTextField.textProperty().set(numbersOnlyString);
                    return;
                }
            }
            handleAdvancedSearch((String) templates.getValue(), mainTextfield.getText(), secondaryTextField.getText(),
                    actualOriginClass, column);
        });
        secondaryTextField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !disableApplyFilter) {
                filterContent(column, secondaryTextField.getText(), tableFilterValues);
                Event.fireEvent(contextMenu, new WindowEvent(contextMenu, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    private void initDatePickers(TableColumnBase column, Class actualOriginClass, DatePicker mainTextfieldDatePicker,
            ComboBox templates, DatePicker secondaryTextfieldDatePicker, DatePicker finalMainTextfieldDatePicker,
            ContextMenu contextMenu) {

        mainTextfieldDatePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

            String numbersOnlyString = newValue.replaceAll("[^\\d.]", "");
            if (!numbersOnlyString.matches(newValue) && !newValue.trim().isEmpty()) {
                mainTextfieldDatePicker.getEditor().textProperty().set(numbersOnlyString);
                return;
            }

            if (isTextFieldsDateContentParsable(newValue, "")) {

                LocalDate localDate = null;
                try {
                    localDate = DateToLocalDateConverter.convertToLocalDate(DateUtil.parseDefaultString(newValue));
                } catch (ParseException e) {
                    LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
                }
                if (localDate != null && !templates.getValue().toString()
                        .matches(FilterStrings.getLocalizedInYearString()) && !secondaryTextfieldDatePicker.getEditor()
                        .getText().trim().isEmpty()) {

                    LocalDate secondarylocalDate = null;
                    try {
                        secondarylocalDate = DateToLocalDateConverter.convertToLocalDate(
                                DateUtil.parseDefaultString(secondaryTextfieldDatePicker.getEditor().getText()));
                    } catch (ParseException e) {
                        //do nothing
                    }
                    if (secondarylocalDate != null) {

                        createAndSetPredicateforWithinOrOutsideRangeRule(actualOriginClass, templates,
                                secondaryTextfieldDatePicker, finalMainTextfieldDatePicker, localDate,
                                secondarylocalDate);
                    } else {
                        createAndApplyAdvancedFilterPredicate(actualOriginClass, templates,
                                secondaryTextfieldDatePicker, finalMainTextfieldDatePicker);
                        // createFilter with date of first datepicker incl. templateValue
                    }
                } else if (localDate != null) {
                    // createFilter with date of first datepicker incl. templateValue
                    createAndApplyAdvancedFilterPredicate(actualOriginClass, templates, secondaryTextfieldDatePicker,
                            finalMainTextfieldDatePicker);
                } else if (!templates.getValue().toString().matches(FilterStrings.getLocalizedInYearString())) {
                    createAndApplyAdvancedFilterPredicate(actualOriginClass, templates, secondaryTextfieldDatePicker,
                            finalMainTextfieldDatePicker);
                }
            }
            if (filterableListViewItemList.getPredicate() == null && columnToPredicateDataMap.containsKey(column)) {
                reapplyPreviousFilterSettings(column);
            }
        });
        mainTextfieldDatePicker.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !disableApplyFilter) {
                filterContent(column, finalMainTextfieldDatePicker.getEditor().getText(), tableFilterValues);
                Event.fireEvent(contextMenu, new WindowEvent(contextMenu, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });

        secondaryTextfieldDatePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

            String numbersOnlyString = newValue.replaceAll("[^\\d.]", "");
            if (!numbersOnlyString.matches(newValue) && !newValue.trim().isEmpty()) {
                secondaryTextfieldDatePicker.getEditor().textProperty().set(numbersOnlyString);
                return;
            }

            if (isTextFieldsDateContentParsable("", newValue)) {

                LocalDate secondarylocalDate = null;
                try {
                    secondarylocalDate = DateToLocalDateConverter.convertToLocalDate(
                            DateUtil.parseDefaultString(newValue));
                } catch (ParseException e) {
                    LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
                }
                if (secondarylocalDate != null && !templates.getValue().toString()
                        .matches(FilterStrings.getLocalizedInYearString()) && !finalMainTextfieldDatePicker.getEditor()
                        .getText().trim().isEmpty()) {
                    LocalDate mainlocalDate = null;
                    try {
                        mainlocalDate = DateToLocalDateConverter.convertToLocalDate(
                                DateUtil.parseDefaultString(finalMainTextfieldDatePicker.getEditor().getText()));
                    } catch (ParseException e) {
                        LOG.warn(PARSE_EXCEPTION_MESSAGE, e);
                    }
                    if (mainlocalDate != null) {

                        createAndSetPredicateforWithinOrOutsideRangeRule(actualOriginClass, templates,
                                secondaryTextfieldDatePicker, finalMainTextfieldDatePicker, mainlocalDate,
                                secondarylocalDate);
                    }
                } else if (secondarylocalDate != null) {
                    createAndApplyAdvancedFilterPredicate(actualOriginClass, templates, secondaryTextfieldDatePicker,
                            finalMainTextfieldDatePicker);
                } else if (!templates.getValue().toString().matches(FilterStrings.getLocalizedInYearString())) {
                    createAndApplyAdvancedFilterPredicate(actualOriginClass, templates, secondaryTextfieldDatePicker,
                            finalMainTextfieldDatePicker);
                }
            }
            if (filterableListViewItemList.getPredicate() == null && columnToPredicateDataMap.containsKey(column)) {
                reapplyPreviousFilterSettings(column);
            }
        });
        secondaryTextfieldDatePicker.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !disableApplyFilter) {
                filterContent(column, secondaryTextfieldDatePicker.getEditor().getText(), tableFilterValues);
                Event.fireEvent(contextMenu, new WindowEvent(contextMenu, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    private boolean isaNumber(Class actualOriginClass) {
        return actualOriginClass.equals(Double.class) || actualOriginClass.equals(Float.class)
                || actualOriginClass.equals(Long.class) || actualOriginClass.equals(Integer.class);
    }

    private void createAndApplyAdvancedFilterPredicate(Class actualOriginClass, ComboBox templates,
            DatePicker secondaryTextfieldDatePicker, DatePicker finalMainTextfieldDatePicker) {

        if (finalMainTextfieldDatePicker.getEditor().getText().trim().isEmpty()
                && secondaryTextfieldDatePicker.getEditor().getText().trim().isEmpty()) {
            filterableListViewItemList.setPredicate(null);
        } else {
            checkListViewPredicate = CustomFilterUtil.createAdvancedFilterPredicate(templates.getValue().toString(),
                    finalMainTextfieldDatePicker.getEditor().getText(),
                    secondaryTextfieldDatePicker.getEditor().getText(), actualOriginClass, tableFilterValues);
            filterableListViewItemList.setPredicate(checkListViewPredicate);
        }

    }

    private void createAndSetPredicateforWithinOrOutsideRangeRule(Class actualOriginClass, ComboBox templates,
            DatePicker secondaryTextfieldDatePicker, DatePicker finalMainTextfieldDatePicker, LocalDate localDate,
            LocalDate secondarylocalDate) {

        if (templates.getValue().toString().matches(FilterStrings.getLocalizedWithinRangeString()) && (
                localDate.isBefore(secondarylocalDate) || localDate.isEqual(secondarylocalDate))) {
            //create Filter
            createAndApplyAdvancedFilterPredicate(actualOriginClass, templates, secondaryTextfieldDatePicker,
                    finalMainTextfieldDatePicker);
        } else if (templates.getValue().toString().matches(FilterStrings.getLocalizedOutsideOfRangeString())
                && localDate.isBefore(secondarylocalDate)) {
            createAndApplyAdvancedFilterPredicate(actualOriginClass, templates, secondaryTextfieldDatePicker,
                    finalMainTextfieldDatePicker);
        }
    }

    private void handleTemplateSelectionChange(Label lowerRangeLimitLabel, Label upperRangeLimitLabel,
            TextField mainTextfield, TextField secondaryTextField, HBox searchFieldArea1, HBox searchFieldArea2,
            Object oldValue, Object newValue, Class actualOriginClass, DatePicker finalMainTextfieldDatePicker) {
        if (newValue != oldValue) {
            searchFieldArea1.setSpacing(5);

            if (newValue.toString().matches(FilterStrings.getLocalizedInYearString())) {
                DatePicker[] datePickerToReplace = { null };
                searchFieldArea1.setSpacing(0);

                // recreate searchArea1

                final int[] index = { 0 };
                searchFieldArea1.getChildren().forEach(child -> {
                    if (child instanceof DatePicker) {
                        index[0] = searchFieldArea1.getChildren().indexOf(child);
                        datePickerToReplace[0] = (DatePicker) child;
                    }
                });

                searchFieldArea1.getChildren().add(index[0], mainTextfield);
                String yearString = "";
                final String dateText = datePickerToReplace[0].getEditor().getText();
                if (dateText.contains(".")) {
                    final String[] split = dateText.split("\\.");
                    for (int i = 0; i < split.length; i++) {
                        if (i == split.length - 1) {
                            yearString = split[i];
                        }
                    }
                }

                mainTextfield.setText(yearString);
                (datePickerToReplace[0]).setValue(null);
                searchFieldArea1.getChildren().remove(datePickerToReplace[0]);
                lowerRangeLimitLabel.setText("");

                if (searchFieldArea2 != null) {

                    searchFieldArea2.getChildren().forEach(child -> {
                        if (child instanceof DatePicker) {
                            ((DatePicker) child).setValue(null);
                            child.setVisible(false);
                        }
                    });
                    searchFieldArea2.setVisible(false);
                    upperRangeLimitLabel.setText("");
                }
            } else if (oldValue != null && oldValue.toString().matches(FilterStrings.getLocalizedInYearString())) {
                final int[] index = { 0 };
                final TextField[] textFieldToReplace = new TextField[1];

                searchFieldArea1.getChildren().forEach(child -> {
                    if (child instanceof TextField) {
                        index[0] = searchFieldArea1.getChildren().indexOf(child);
                        textFieldToReplace[0] = (TextField) child;
                    }
                });
                searchFieldArea1.getChildren().add(index[0], finalMainTextfieldDatePicker);
                if ((textFieldToReplace[0]).getText().trim().length() > 0) {

                    finalMainTextfieldDatePicker.setValue(
                            LocalDate.of(Integer.parseInt((textFieldToReplace[0]).getText().trim()), 1, 1));
                }
                (textFieldToReplace[0]).clear();
                searchFieldArea1.getChildren().remove(textFieldToReplace[0]);

                if (searchFieldArea2 != null) {
                    searchFieldArea2.getChildren().forEach(child -> {
                        if (child instanceof DatePicker) {

                            ((DatePicker) child).setValue(null);
                            child.setVisible(true);
                        }
                    });
                    searchFieldArea2.setVisible(true);
                }
            }

            if (newValue.toString().matches(FilterStrings.getLocalizedContainsString()) || newValue.toString()
                    .matches(FilterStrings.getLocalizedDoesNotContainString()) || newValue.toString()
                    .matches(FilterStrings.getLocalizedTopString()) || newValue.toString()
                    .matches(FilterStrings.getLocalizedEqualsString())) {

                lowerRangeLimitLabel.setText(StringUtils.EMPTY);
                upperRangeLimitLabel.setText(StringUtils.EMPTY);

                searchFieldArea1.setSpacing(0);

                if (searchFieldArea2 != null) {
                    searchFieldArea2.getChildren().forEach(child -> {
                        if (child instanceof DatePicker) {
                            ((DatePicker) child).setValue(null);
                        }
                    });

                    searchFieldArea2.setVisible(false);
                    secondaryTextField.clear();
                }

                secondaryTextField.setVisible(false);
            } else if (!secondaryTextField.isVisible() || (searchFieldArea2 != null && !searchFieldArea2.isVisible())) {
                secondaryTextField.setVisible(true);

                if (searchFieldArea2 != null) {
                    searchFieldArea2.setVisible(true);
                }
            }

            if (newValue.toString().matches(FilterStrings.getLocalizedWithinRangeString()) || newValue.toString()
                    .matches(FilterStrings.getLocalizedOutsideOfRangeString())) {
                lowerRangeLimitLabel.setText(FilterStrings.getLocalizedLowerLimitString());
                upperRangeLimitLabel.setText(FilterStrings.getLocalizedUpperLimitString());
            } else if (!newValue.toString().matches(FilterStrings.getLocalizedWithinRangeString())
                    && !newValue.toString().matches(FilterStrings.getLocalizedOutsideOfRangeString())
                    && searchFieldArea2 != null) {

                searchFieldArea2.getChildren().forEach(child -> {
                    if (child instanceof DatePicker) {

                        ((DatePicker) child).setValue(null);
                    }
                });
            }

            if (!mainTextfield.getText().isEmpty() || !secondaryTextField.getText().isEmpty()) {
                checkListViewPredicate = CustomFilterUtil.createAdvancedFilterPredicate(newValue.toString(),
                        mainTextfield.getText(), secondaryTextField.getText(), actualOriginClass, tableFilterValues);
                filterableListViewItemList.setPredicate(checkListViewPredicate);
            } else {
                updateDatpickerTextField(searchFieldArea1);

                if (searchFieldArea2 != null) {
                    updateDatpickerTextField(searchFieldArea2);
                }
            }
        }
    }

    private void updateDatpickerTextField(HBox searchFieldArea1) {
        searchFieldArea1.getChildren().forEach(child -> {
            if (child instanceof DatePicker) {

                final LocalDate pickedDate = ((DatePicker) child).getValue();
                if (pickedDate != null) {
                    ((DatePicker) child).getEditor().setText(((DatePicker) child).getEditor().getText().trim() + " ");
                }
            }
        });
    }

    private void handleAdvancedSearch(String templateString, String firstTextfieldContent,
            String secondTextfieldContent, Class actualOriginClass, TableColumnBase column) {
        if (firstTextfieldContent.trim().isEmpty() && secondTextfieldContent.trim().isEmpty()) {
            applyAdvancedFilterCriteriaToTableValueList("", "", "", null, column);
        } else {
            applyAdvancedFilterCriteriaToTableValueList(templateString, firstTextfieldContent, secondTextfieldContent,
                    actualOriginClass, column);
        }
    }

    private void applyAdvancedFilterCriteriaToTableValueList(String templateString, String firstTextfield,
            String secondTextfield, Class actualOriginClass, TableColumnBase column) {

        checkListViewPredicate = null;
        if (templateString.matches(FilterStrings.getLocalizedContainsString()) && String.class.equals(
                actualOriginClass)) {
            checkListViewPredicate = CustomFilterUtil.createAdvancedFilterPredicate(templateString, firstTextfield);

        } else if (!templateString.trim().isEmpty()) {
            checkListViewPredicate = CustomFilterUtil.createAdvancedFilterPredicate(templateString, firstTextfield,
                    secondTextfield, actualOriginClass, tableFilterValues);

        }
        filterableListViewItemList.setPredicate(checkListViewPredicate);
        if (checkListViewPredicate == null) {
            reapplyPreviousFilterSettings(column);
        }
        checkListView.setItems(filterableListViewItemList);
        EventBus.getInstance().post(new FilterSelectionChangeEvent());
    }

    private boolean isTextFieldsDateContentParsable(String firstTextfieldContent, String secondTextfieldContent) {
        if (!firstTextfieldContent.trim().isEmpty()) {
            try {
                DateToLocalDateConverter.convertToLocalDate(DateUtil.parseDefaultString(firstTextfieldContent.trim()));
            } catch (ParseException e) {
                return false;
            }
        }

        if (!secondTextfieldContent.trim().isEmpty()) {
            try {
                DateToLocalDateConverter.convertToLocalDate(DateUtil.parseDefaultString(secondTextfieldContent.trim()));
            } catch (ParseException e) {
                return false;
            }
        }

        return true;
    }

    private ListView<CustomTableFilterValue> initChecklistView(TableColumnBase column) {
        addAllItemsFromCollection(column);

        checkListView = new ListView<>();
        checkListView.setFixedCellSize(24);

        reapplyPreviousFilterSettings(column);
        sortTableValues();

        filterableListViewItemList = new FilteredList(tableFilterValues);
        checkListView.setItems(filterableListViewItemList);
        filterableListViewItemList.predicateProperty()
                .addListener((observable, oldValue, newValue) -> updateSelectionOfFilterValues());

        return checkListView;
    }

    private void updateSelectionOfFilterValues() {
        tableFilterValues.forEach(item -> {
            if (checkListView.getItems().contains(item)) {
                item.selectedProperty().set(true);
            } else {
                item.selectedProperty().set(false);
            }
        });
    }

    private void sortTableValues() {
        if (tableFilterValues.size() > 1) {
            final CustomTableFilterValue tableFilterValue = tableFilterValues.get(0);
            final CustomTableFilterValue otherTableFilterValue = tableFilterValues.get(1);

            if (tableFilterValue != null && otherTableFilterValue != null) {
                final Class originClass = CustomFilterSortUtil.determineActualOriginClass(tableFilterValue,
                        otherTableFilterValue);

                CustomFilterSortUtil.sort(tableFilterValues, originClass);
            }
        }
    }

    private void reapplyPreviousFilterSettings(TableColumnBase column) {
        if (!columnToPredicateDataMap.isEmpty()) {
            final PredicateData predicateData = columnToPredicateDataMap.get(column);

            if (predicateData != null && predicateData.getValueItems() != null && (tableFilterValues != null
                    && !tableFilterValues.isEmpty())) {

                tableFilterValues.forEach(filterValueItem -> {
                    if (!predicateData.getValueItems().contains(filterValueItem.getUnformattedLabelText())) {
                        filterValueItem.selectedProperty().set(false);
                    }
                });
            }
        }
    }

    private void reapplyPreviousFilterPanelPredicateData(TableColumnBase column) {
        if (!columnToFilterPanelPredicateDataMap.isEmpty()) {
            final FilterPanelPredicateData predicateData = columnToFilterPanelPredicateDataMap.get(column);

            if (predicateData != null && !predicateData.getRule().isEmpty() && searchAreaTop != null
                    && searchAreaBottom != null && (!predicateData.getUpperField().isEmpty()
                    || !predicateData.getLowerField().isEmpty())) {

                searchArea.getChildren().forEach(node -> {
                    if (node instanceof ComboBox) {
                        ((ComboBox) node).setValue(predicateData.getRule());
                    }
                });

                searchAreaTop.getChildren().forEach(child -> {
                    if (child instanceof TextField) {
                        ((TextField) child).textProperty().set(predicateData.getUpperField());
                    } else if (child instanceof DatePicker) {
                        ((DatePicker) child).getEditor().textProperty().set(predicateData.getUpperField());
                    }
                });
                searchAreaBottom.getChildren().forEach(child -> {
                    if (child instanceof TextField) {
                        ((TextField) child).textProperty().set(predicateData.getLowerField());
                    } else if (child instanceof DatePicker) {
                        ((DatePicker) child).getEditor().textProperty().set(predicateData.getLowerField());
                    }
                });
            }
        }
    }

    private void addAllItemsFromCollection(TableColumnBase column) {
        itemCollection.forEach(item -> {
            if (column.getCellObservableValue(item) != null) {

                Object value = column.getCellObservableValue(item).getValue();
                if (value != null) {
                    if (!tableValuesStringBackupList.contains(value.toString().toLowerCase())) {
                        addValuesToCollections(value, column, item);
                    } else if (value.toString().trim().isEmpty() && !tableValuesStringBackupList.contains(
                            CustomFilterUtil.getLocalizedEmptyString())) {
                        addValuesToCollections(null, column, item);
                    }
                } else if (!tableValuesStringBackupList.contains(CustomFilterUtil.getLocalizedEmptyString())) {
                    if (column instanceof TreeTableColumn) {
                        if (((TreeTableColumn) column).getTreeTableView().getRoot().equals(item)) {
                            return;
                        }
                    }

                    addValuesToCollections(null, column, item);
                }
            }
        });
    }

    private void addValuesToCollections(Object value, TableColumnBase column, S treeItem) {
        Class columnDataCategory;
        final ObservableValue observableValue = column.getCellObservableValue(treeItem);

        if (treeItem instanceof TreeItem && ((TreeItem) treeItem).getValue() instanceof InspectorTreeItemObject
                && !(observableValue.getValue() instanceof Double)) {
            columnDataCategory = String.class;
        } else {
            if (observableValue.getValue() != null) {
                columnDataCategory = observableValue.getValue().getClass();
            } else {
                columnDataCategory = observableValue.getClass();
            }
        }

        if (columnToPredicateDataMap.isEmpty()) {
            if (value == null && !tableValuesStringBackupList.contains(CustomFilterUtil.getLocalizedEmptyString())
                    && !tableValuesStringBackupList.contains("")) {

                CustomTableFilterValue newFilterValue = new CustomTableFilterValue("", null, null, -999999999999999999L,
                        tableValuesStringBackupList);
                tableValuesStringBackupList.addAll(CustomFilterUtil.getLocalizedEmptyString(), "");
                tableFilterValues.add(newFilterValue);
            } else if (value != null && !tableValuesStringBackupList.contains(value.toString())) {
                CustomTableFilterValue newFilterValue;
                final Object valueObject = observableValue.getValue();
                newFilterValue = createTableFilterValue(value, column, columnDataCategory, null, valueObject);

                if (columnToFilterValueMapMap.get(column) == null) {
                    columnToFilterValueMapMap.put(column, stringToFilterValueMap);
                } else {
                    columnToFilterValueMapMap.get(column).put(value.toString(), newFilterValue);
                }
                if (newFilterValue != null) {
                    tableFilterValues.add(newFilterValue);
                }
            }
        } else if (value == null && !tableValuesStringBackupList.contains(CustomFilterUtil.getLocalizedEmptyString())
                && !tableValuesStringBackupList.contains("")) {

            CustomTableFilterValue newFilterValue = new CustomTableFilterValue(
                    CustomFilterUtil.getLocalizedEmptyString(), null, columnDataCategory, -999999999999999999L,
                    tableValuesStringBackupList);
            tableValuesStringBackupList.addAll(CustomFilterUtil.getLocalizedEmptyString(), "");
            tableFilterValues.add(newFilterValue);
        } else {
            Map<String, List<TableColumnBase>> tempStorageMap = new HashMap<>();
            List<TableColumnBase> columnsList = new ArrayList<>();

            columnToPredicateDataMap.keySet().forEach(key -> {
                if (!key.equals(column) && value != null && treeItem != null) {
                    final ObservableValue cellObservableValue = key.getCellObservableValue(treeItem);

                    columnsList.add(key);
                    if (isCellValueInExistingFilterForColumn(value, key, cellObservableValue)) {
                        saveValueInTemporaryMap(value, tempStorageMap, key);
                    }
                } else if (key.equals(column) && value != null && treeItem != null
                        && columnToPredicateDataMap.size() == 1) {
                    if (!tableValuesStringBackupList.contains(value.toString())) {
                        columnsList.add(column);
                        saveValueInTemporaryMap(value, tempStorageMap, key);
                    }
                }
            });

            if ((value != null ? value.toString() : null) != null
                    && tempStorageMap.get(value.toString().toLowerCase()) != null
                    && tempStorageMap.get(value.toString().toLowerCase()).size() == columnsList.size()) {

                CustomTableFilterValue newFilterValue;
                final Object valueObject = observableValue.getValue();
                newFilterValue = createTableFilterValue(value, column, columnDataCategory, null, valueObject);

                if (newFilterValue != null) {
                    tableFilterValues.add(newFilterValue);
                }
            }

            tempStorageMap.clear();
            columnsList.clear();
        }
    }

    private CustomTableFilterValue createTableFilterValue(Object value, TableColumnBase column,
            Class columnDataCategory, CustomTableFilterValue newFilterValue, Object valueObject) {
        if (columnDataCategory.equals(LocalDate.class)
                || columnDataCategory.equals(Date.class) && valueObject instanceof Date) {

            final long time = (DateToLocalDateConverter.convertToLocalDate((Date) valueObject)).toEpochDay();
            LocalDate localdate = LocalDate.ofEpochDay(time);
            if (!tableValuesStringBackupList.contains(localdate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")))) {

                newFilterValue = new CustomTableFilterValue(value.toString(), null, columnDataCategory, time,
                        tableValuesStringBackupList);
            }
        } else if (columnDataCategory.equals(LocalDate.class)
                || columnDataCategory.equals(Date.class) && valueObject instanceof String) {

            try {
                final long time = DateToLocalDateConverter.convertToLocalDate(
                        DateUtil.parseDefaultString(valueObject.toString())).toEpochDay();
                LocalDate localdate = LocalDate.ofEpochDay(time);
                if (!tableValuesStringBackupList.contains(
                        localdate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")))) {
                    newFilterValue = new CustomTableFilterValue(value.toString(), null, columnDataCategory, time,
                            tableValuesStringBackupList);
                }
            } catch (ParseException e) {
                ExceptionHandler.instance().handleException(e, this);
            }
        } else if (columnDataCategory.equals(Boolean.class)) {
            String text = new BooleanStringConverter().toString((Boolean) value);
            newFilterValue = new CustomTableFilterValue(value.toString(), text, columnDataCategory,
                    -999999999999999999L, tableValuesStringBackupList);
        } else {
            String labelText = value.toString();
            if (column instanceof IFilterableColumn) {
                IFilterableColumn fc = (IFilterableColumn) column;
                if (fc.getConverter() != null) {
                    labelText = fc.getConverter().toString(value);

                }
            }

            newFilterValue = new CustomTableFilterValue(value.toString(), labelText, columnDataCategory,
                    -999999999999999999L, tableValuesStringBackupList);
        }
        return newFilterValue;
    }

    private boolean isCellValueInExistingFilterForColumn(Object value, TableColumnBase key,
            ObservableValue cellObservableValue) {
        if (cellObservableValue != null && cellObservableValue.getValue() != null) {
            if ((cellObservableValue.getValue() instanceof Date)) {

                final LocalDate localDate = DateToLocalDateConverter.convertToLocalDate(
                        (Date) cellObservableValue.getValue());
                String dateString = localDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
                return (!columnToPredicateDataMap.get(key).getValueItems().isEmpty() && columnToPredicateDataMap.get(
                        key).getValueItems().contains(dateString) && !tableValuesStringBackupList.contains(
                        value.toString()));
            }

            final Object cellcontentValue = cellObservableValue.getValue();

            return (!columnToPredicateDataMap.get(key).getValueItems().isEmpty()) && (
                    (columnToPredicateDataMap.get(key).getValueItems().contains(cellcontentValue.toString()) || (
                            (cellcontentValue instanceof Double || cellcontentValue instanceof Float)
                                    && columnToPredicateDataMap.get(key).getValueItems()
                                    .contains(cellcontentValue.toString().replace(".", ",")))) || (
                            cellcontentValue.toString().isEmpty() && columnToPredicateDataMap.get(key).getValueItems()
                                    .contains(CustomFilterUtil.getLocalizedEmptyString())))
                    && !tableValuesStringBackupList.contains(value.toString());
        } else if (cellObservableValue != null) {

            return (!columnToPredicateDataMap.get(key).getValueItems().isEmpty()) && columnToPredicateDataMap.get(key)
                    .getValueItems().contains(CustomFilterUtil.getLocalizedEmptyString())
                    && !tableValuesStringBackupList.contains(value.toString());
        }
        return false;
    }

    private void saveValueInTemporaryMap(Object value, Map<String, List<TableColumnBase>> tempStorageMap,
            TableColumnBase key) {
        final String valueToLowerCase = value.toString().toLowerCase();
        if (tempStorageMap.get(valueToLowerCase) == null) {
            List<TableColumnBase> list = new ArrayList<>();
            list.add(key);
            tempStorageMap.put(valueToLowerCase, list);
        } else {
            tempStorageMap.get(valueToLowerCase).add(key);
        }
    }

    private HBox initButtons(ContextMenu contextMenu, TableColumnBase column) {

        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(5, 0, 5, 0));
        buttonBox.setSpacing(10);

        Button applyBttn = new Button(buttonApply);
        HBox.setHgrow(applyBttn, Priority.ALWAYS);

        applyBttn.setOnAction(e -> {

            unregisterEventBus();
            filterContent(column, "", tableFilterValues);
            Event.fireEvent(contextMenu, new WindowEvent(contextMenu, WindowEvent.WINDOW_CLOSE_REQUEST));
            column.setContextMenu(null);
        });

        Button unselectAllButton = new Button(buttonNone);
        HBox.setHgrow(unselectAllButton, Priority.ALWAYS);
        unselectAllButton.setOnAction(e -> {
            // deselect all items
            unregisterEventBus();
            tableFilterValues.forEach(filterValueItem -> filterValueItem.selectedProperty().set(false));
            registerEventBus();
            EventBus.getInstance().post(new FilterSelectionChangeEvent());

        });

        Button selectAllButton = new Button(buttonAll);
        HBox.setHgrow(selectAllButton, Priority.ALWAYS);

        selectAllButton.setOnAction(e -> {
            // select all items
            unregisterEventBus();
            tableFilterValues.forEach(filterValueItem -> {
                filterValueItem.selectedProperty().set(true);
            });
            registerEventBus();
            EventBus.getInstance().post(new FilterSelectionChangeEvent());
        });

        Button clearFilterButton = new Button(buttonReset);

        HBox.setHgrow(clearFilterButton, Priority.ALWAYS);
        clearFilterButton.setOnAction(e -> {

            unregisterEventBus();
            if (tableView instanceof CustomTreeTableView) {
                ((CustomTreeTableView) tableView).filterContent(column, StringUtils.EMPTY, null,
                        checkListView.getItems());
                columnToFilterPanelPredicateDataMap.remove(column);
            } else if (tableView instanceof CustomTableView) {
                ((CustomTableView) tableView).filterContent(column, StringUtils.EMPTY, null,
                        ((CustomTableView) tableView).getFilteredList().getSource());
            }
            Event.fireEvent(contextMenu, new WindowEvent(contextMenu, WindowEvent.WINDOW_CLOSE_REQUEST));
            column.setContextMenu(null);
        });

        if (columnToPredicateDataMap.get(column) == null) {
            clearFilterButton.setDisable(true);
        }

        buttonBox.getChildren().addAll(applyBttn, selectAllButton, unselectAllButton, clearFilterButton);

        buttonBox.setAlignment(Pos.BASELINE_CENTER);

        return buttonBox;
    }

    private void filterContent(TableColumnBase column, String text, ObservableList<CustomTableFilterValue> values) {
        final ObservableList checkListViewItems = checkListView.getItems();

        if (tableView instanceof CustomTreeTableView) {
            ((CustomTreeTableView) tableView).filterContent(column, text, values, checkListViewItems);
        } else if (tableView instanceof CustomTableView) {
            ((CustomTableView) tableView).filterContent(column, text, values, checkListViewItems);
        }

        if (CustomFilterUtil.allItemsInScopeSelected(checkListView.getItems())) {
            columnToFilterPanelPredicateDataMap.put(column,
                    CustomFilterUtil.createFilterPanelPredicate(searchArea, searchAreaTop, searchAreaBottom));
        }
    }

    public void clear() {
        //clean up
        checkListView.getItems().clear();
        tableFilterValues.clear();
        tableValuesStringBackupList.clear();
        filterPanel.getChildren().clear();
    }

    @Subscribe
    private void buttonEnableManaging(FilterSelectionChangeEvent changeEvent) {

        if (filterPanel != null) {
            if (filterableListViewItemList.isEmpty()) {
                filterPanel.getButtonBox().getChildren().forEach(button -> {
                    if (((Button) button).getText().matches(buttonApply)) {
                        button.setDisable(true);
                        disableApplyFilter = true;
                    }
                });
            } else {
                if (!CustomFilterUtil.allItemsInScopeSelected(checkListView.getItems())) {
                    if (CustomFilterUtil.noSelectedItems(tableFilterValues)) {
                        filterPanel.getButtonBox().getChildren().forEach(button -> {
                            if (((Button) button).getText().matches(buttonApply)) {

                                button.setDisable(true);
                                disableApplyFilter = true;
                            } else if (((Button) button).getText().matches(buttonNone)) {

                                button.setDisable(true);
                            } else if (((Button) button).getText().matches(buttonAll)) {
                                button.setDisable(false);
                            }
                        });
                    } else {
                        filterPanel.getButtonBox().getChildren().forEach(button -> {
                            if (((Button) button).getText().matches(buttonApply)) {

                                button.setDisable(false);
                                disableApplyFilter = false;
                            } else if (((Button) button).getText().matches(buttonNone)) {

                                button.setDisable(false);
                            } else if (((Button) button).getText().matches(buttonAll)) {
                                button.setDisable(false);
                            }
                        });
                    }
                } else {
                    filterPanel.getButtonBox().getChildren().forEach(button -> {
                        if (((Button) button).getText().matches(buttonApply)) {

                            button.setDisable(false);
                            disableApplyFilter = false;
                        } else if (((Button) button).getText().matches(buttonNone)) {

                            button.setDisable(false);
                        } else if (((Button) button).getText().matches(buttonAll)) {
                            button.setDisable(true);
                        }
                    });
                }
            }
        }

    }

    private void registerEventBus() { // NO_UCD (use private)
        EventBus.getInstance().register(this);
    }

    private void unregisterEventBus() { // NO_UCD (use private)
        EventBus.getInstance().unregister(this);
    }
}
