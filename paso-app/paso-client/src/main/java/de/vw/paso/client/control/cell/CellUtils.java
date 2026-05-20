package de.vw.paso.client.control.cell;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;

import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.control.combobox.PasoCustomComboBox;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.control.textfield.PasoNumberField;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

public class CellUtils {

    private static final int CELL_HEIGHT = 21;
    private static final int CELL_MIN_WIDTH = 50;

    private static final String SEPARATOR = StringConstant.SLASH;

    private CellUtils() {
        throw new IllegalArgumentException("Util class");
    }

    public static <T> ListCell<T> createListCell(Function<T, String> converter) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                String text = empty ? null : converter.apply(item);
                setText(text);
            }
        };
    }

    public static <T> PasoCustomComboBox<T> createComboBox(AbstractTreeTableCell<?, T> cell, Collection<T> items) {
        PasoCustomComboBox<T> comboBox = new PasoCustomComboBox<>(items);
        initCustomTextField(cell, comboBox);
        return comboBox;
    }

    public static <T> void updateItem(AbstractTableCell<?, T> cell, TextInputControl textField) {
        updateItem(cell, cell.getConverter(), textField,
                editingCell -> textField.setText(getItemText(editingCell, cell.getConverter())));
    }

    public static void setSize(Region graphic) {
        graphic.setMaxHeight(CELL_HEIGHT);
        graphic.setMinHeight(CELL_HEIGHT);
        graphic.setPrefHeight(CELL_HEIGHT);
        graphic.setMinWidth(CELL_MIN_WIDTH);
        graphic.setMaxWidth(Double.MAX_VALUE);
    }

    static <T> void cancelEdit(Cell<T> cell, StringConverter<T> converter) {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(null);
    }

    static TextFlow getPrNumberRule(Collection<String> prNumbers, VehicleConfigDTO vehicleConfig) {
        TextFlow textFlow = new TextFlow();
        textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        textFlow.setMinWidth(Region.USE_PREF_SIZE);
        textFlow.setPrefHeight(20);

        for (String prNumber : prNumbers) {
            addPrNumberRuleElements(prNumber, vehicleConfig, textFlow);
        }

        return textFlow;
    }

    static Tooltip setTooltipContent(Collection<String> prNumberStrings) {
        Collection<String> allPrNumberStrings = new ArrayList<>();
        for (String prNumberString : prNumberStrings) {
            if (prNumberString.contains(SEPARATOR)) {
                String[] split = prNumberString.split(SEPARATOR);
                Collections.addAll(allPrNumberStrings, split);
            } else {
                allPrNumberStrings.add(prNumberString);
            }
        }

        List<PrNumberDTO> prNumbers = CacheManager.getPrNumbers(allPrNumberStrings);
        prNumbers.sort(Comparator.comparing(element -> element.prNumberFamily().name()));

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);

        pane.add(createLabel("FAM", true), 0, 0);
        pane.add(createLabel("PRNR", true), 1, 0);
        pane.add(createLabel("Beschreibung", true), 2, 0);
        pane.add(createLabel("Zusatzbeschreibung", true), 3, 0);

        for (int i = 0; i < prNumbers.size(); i++) {
            PrNumberDTO prNumber = prNumbers.get(i);

            int row = i + 1;
            pane.add(createLabel(prNumber.prNumberFamily().name(), false), 0, row);
            pane.add(createLabel(prNumber.name(), false), 1, row);
            pane.add(createLabel(prNumber.description(), false), 2, row);
            pane.add(createLabel(prNumber.additionalName(), false), 3, row);
        }

        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(pane);
        return tooltip;
    }

    static List<String> sortPrNumbersByFamily(Collection<String> prNumbers) {
        return prNumbers.stream().sorted(CellUtils::sortPrNumbersByFamilyImpl).toList();
    }

    static <T> void updateItem(ReadOnlyTreeTableCell<?, T> cell) {
        updateItem(cell, cell.getConverter(), null,
                editingCell -> editingCell.setText(getItemText(editingCell, cell.getConverter())));
    }

    static <T> void updateItem(AbstractTreeTableCell<?, T> cell) {
        updateItem(cell, cell.getConverter(), null,
                editingCell -> editingCell.setText(getItemText(editingCell, cell.getConverter())));
    }

    static <T> void updateItem(ReadOnlyTableCell<?, T> cell) {
        updateItem(cell, cell.getConverter(), null,
                editingCell -> editingCell.setText(getItemText(editingCell, cell.getConverter())));
    }

    static <T> PasoCustomTextField<T> createTextField(AbstractTreeTableCell<?, T> cell) {
        PasoCustomTextField<T> textField = new PasoCustomTextField<>();
        initCustomTextField(cell, textField);
        return textField;
    }

    static <T extends Number> PasoNumberField<T> createNumberField(AbstractTreeTableCell<?, T> cell) {
        PasoNumberField<T> numberField = new PasoNumberField<>();
        numberField.setConverter(cell.getConverter());
        initCustomTextField(cell, numberField);
        return numberField;
    }

    static <T> void updateItem(AbstractTreeTableCell<?, T> cell, TextInputControl textField) {
        updateItem(cell, cell.getConverter(), textField,
                editingCell -> textField.setText(getItemText(editingCell, cell.getConverter())));
    }

    static void startEdit(AbstractTreeTableCell<?, Date> cell, DatePicker datePicker) {
        Consumer<Cell<Date>> editingValue = editingCell -> {
            if (editingCell.getItem() != null) {
                datePicker.setValue(editingCell.getItem().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                datePicker.getEditor().setText(getItemText(editingCell, cell.getConverter()));
            }
        };

        Consumer<DatePicker> editingFocus = node -> {
            datePicker.getEditor().selectAll();
            datePicker.getEditor().requestFocus();
        };

        startEdit(cell, datePicker, editingValue, editingFocus);
    }

    static void updateItem(Cell<Date> cell, StringConverter<Date> converter, DatePicker datePicker) {
        updateItem(cell, converter, datePicker,
                editingCell -> datePicker.getEditor().setText(getItemText(editingCell, converter)));
    }

    static DatePicker createDatePicker(Cell<Date> cell, StringConverter<Date> converter) {
        DatePicker datePicker = new DatePicker();
        initTextField(cell, datePicker.getEditor(), converter);
        datePicker.setOnKeyPressed(createKeyEventHandler(cell, datePicker.getEditor(), converter));

        return datePicker;
    }

    static <T> void startEdit(AbstractTreeTableCell<?, T> cell, TextInputControl textField) {
        startEdit(cell, textField, cell.getConverter());
    }

    static <T> void startEdit(AbstractTreeTableCell<?, T> cell, PasoCustomComboBox<T> comboBox) {
        if (!comboBox.isEditable()) {
            return;
        }

        Consumer<Cell<T>> editingValue = editingCell -> comboBox.setText(getItemText(editingCell, cell.getConverter()));
        Consumer<PasoCustomComboBox<T>> editingFocus = node -> {
            node.requestFocus();
            node.selectAll();
        };

        startEdit(cell, comboBox, editingValue, editingFocus);
    }

    static <T> void updateItem(AbstractTreeTableCell<?, T> cell, PasoCustomComboBox<T> comboBox) {
        updateItem(cell, cell.getConverter(), comboBox,
                editingCell -> comboBox.setText(getItemText(editingCell, cell.getConverter())));
    }

    private static <T> void startEdit(Cell<T> cell, TextInputControl textField, StringConverter<T> converter) {
        Consumer<Cell<T>> editingValue = editingCell -> textField.setText(getItemText(editingCell, converter));
        Consumer<TextInputControl> editingFocus = node -> {
            node.selectAll();
            node.requestFocus();
        };

        startEdit(cell, textField, editingValue, editingFocus);
    }

    private static <T> void initCustomTextField(AbstractTreeTableCell<?, T> cell, PasoCustomTextField<T> textField) {
        textField.converterProperty().bind(cell.converterProperty());
        textField.maxTextLengthProperty().bind(cell.maxTextLengthProperty());
        textField.validationProperty().bind(cell.validationProperty());
        textField.validCharacterProperty().bind(cell.validCharacterProperty());
        textField.upperCaseProperty().bind(cell.upperCaseProperty());
        textField.clearableProperty().bind(cell.clearableProperty());
        textField.editableProperty().bind(cell.editableProperty());

        initTextField(cell, textField, textField.getConverter());
    }

    private static <T> void initTextField(Cell<T> cell, TextField textField, StringConverter<T> converter) {
        setSize(textField);
        textField.setText(getItemText(cell, converter));
        textField.setOnKeyPressed(createKeyEventHandler(cell, textField, converter));
    }

    private static <T> EventHandler<KeyEvent> createKeyEventHandler(Cell<T> cell, TextField textField,
            StringConverter<T> converter) {

        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (converter == null) {
                    throw new IllegalStateException("Attempting to convert text input into Object, but provided "
                            + "StringConverter is null. Be sure to set a StringConverter " + "in your cell factory.");
                }

                T fieldValue = converter.fromString(textField.getText());

                if (fieldValue == null) {
                    fieldValue = getAsType(textField.getUserData());
                }

                cell.commitEdit(fieldValue);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cell.cancelEdit();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T getAsType(Object obj) {
        return (T) obj;
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        if (converter == null) {
            return cell.getItem() == null ? StringConstant.EMPTY : cell.getItem().toString();
        }

        return converter.toString(cell.getItem());
    }

    private static <T, N extends Node> void startEdit(Cell<T> cell, N graphic, Consumer<Cell<T>> editingValue,
            Consumer<N> editingFocus) {
        if (graphic != null) {
            editingValue.accept(cell);
        }

        cell.setText(null);
        cell.setGraphic(graphic);
        editingFocus.accept(graphic);
    }

    private static <T> void updateItem(Cell<T> cell, StringConverter<T> converter, Node graphic,
            Consumer<Cell<T>> editingValue) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
        } else if (cell.isEditing()) {
            if (graphic != null) {
                editingValue.accept(cell);
            }
            cell.setText(null);
            cell.setGraphic(graphic);
        } else {
            cell.setText(getItemText(cell, converter));
            cell.setGraphic(null);
        }
    }

    private static void addPrNumberRuleElements(String p, VehicleConfigDTO vehicleConfig, TextFlow textFlow) {
        boolean isValid = isContainedInVehicleConfig(p, vehicleConfig);
        textFlow.getChildren().add(createPrNumberRuleElement(p, isValid));
    }

    private static boolean isContainedInVehicleConfig(String p, VehicleConfigDTO vehicleConfig) {
        String vehicleConfigPrNumberString = vehicleConfig.getPrNumberString();
        return Arrays.stream(p.split(SEPARATOR)).anyMatch(vehicleConfigPrNumberString::contains);
    }

    private static Text createPrNumberRuleElement(String prNumber, boolean isValid) {
        Text text = new Text(StringConstant.PLUS + prNumber);
        if (!isValid) {
            text.setFill(Color.RED);
        }

        return text;
    }

    private static Label createLabel(String text, boolean header) {
        String style = header ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: WHITE;" :
                "-fx-font-size: 12px; -fx-text-fill: WHITE;";

        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    private static int sortPrNumbersByFamilyImpl(String item1, String item2) {
        return item1.isEmpty() || item2.isEmpty() ? item1.compareTo(item2) :
                getPrNumberFamilyName(item1).compareTo(getPrNumberFamilyName(item2));
    }

    private static String getPrNumberFamilyName(String item) {
        String toCheck =
                item.contains(StringConstant.SLASH) ? item.substring(0, item.indexOf(StringConstant.SLASH)) : item;
        return CacheManager.getPrNumber(toCheck).prNumberFamily().name();
    }
}
