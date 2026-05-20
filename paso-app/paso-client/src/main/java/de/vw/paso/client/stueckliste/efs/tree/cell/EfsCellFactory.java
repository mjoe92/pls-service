package de.vw.paso.client.stueckliste.efs.tree.cell;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;

import de.vw.paso.client.control.cell.AbstractTreeTableCell;
import de.vw.paso.client.control.cell.CogCoordinates;
import de.vw.paso.client.control.cell.CogTreeTableCell;
import de.vw.paso.client.control.cell.ComboBoxTreeTableCell;
import de.vw.paso.client.control.cell.DatePickerTreeTableCell;
import de.vw.paso.client.control.cell.NumberFieldTreeTableCell;
import de.vw.paso.client.control.cell.PrNumberRuleTreeTableCell;
import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.cell.TextFieldTreeTableCell;
import de.vw.paso.client.stueckliste.converter.CostGroupStringConverter;
import de.vw.paso.client.stueckliste.converter.QuantityUnitStringConverter;
import de.vw.paso.client.stueckliste.converter.SetKeyStringConverter;
import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.client.stueckliste.efs.converter.EinsatzEntfallConverter;
import de.vw.paso.client.stueckliste.efs.converter.SeparatedPartNumberStringConverter;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItemPropertyNames;
import de.vw.paso.client.stueckliste.teilenummer.TeilenummerTreeTableCell;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.partlist.domain.AP;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class EfsCellFactory {

    private EfsCellFactory() {
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnPartNumber() {
        return column -> {
            TeilenummerTreeTableCell cell = new TeilenummerTreeTableCell();
            cell.setPropertyName(EfsElementTreeItemPropertyNames.PART_NUMBER);
            cell.setConverter(new SeparatedPartNumberStringConverter());
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnDescription1() {
        return column -> {
            TextFieldTreeTableCell<EfsElementDTO, String> cell = new TextFieldTreeTableCell<>();
            cell.setMaxTextLength(60);
            cell.setValidation(text -> text != null && !text.trim().isEmpty());
            cell.setPropertyName(EfsElementTreeItemPropertyNames.DESCRIPTION1);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnDescription2() {
        return column -> {
            TextFieldTreeTableCell<EfsElementDTO, String> cell = new TextFieldTreeTableCell<>();
            cell.setMaxTextLength(60);
            cell.setPropertyName(EfsElementTreeItemPropertyNames.DESCRIPTION2);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnAp() {
        ObservableList<String> aps = FXCollections.observableArrayList(AP.toStrList());

        return column -> {
            javafx.scene.control.cell.ComboBoxTreeTableCell<EfsElementDTO, String> cell = new javafx.scene.control.cell.ComboBoxTreeTableCell<>();
            cell.getItems().addAll(aps);
            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnSetKey(
            ObservableList<String> setKeys) {
        return column -> {
            ComboBoxTreeTableCell<EfsElementDTO, String> cell = new ComboBoxTreeTableCell<>(setKeys);

            cell.setConverter(new SetKeyStringConverter(false));
            cell.setPopupItemConverter(new SetKeyStringConverter(true));
            cell.setMaxTextLength(3);
            cell.setValidation(setKey -> StringUtils.isBlank(setKey) || !setKey.trim().isEmpty());
            cell.setPropertyName(EfsElementTreeItemPropertyNames.SET_KEY);
            cell.setUpperCase(true);

            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnCostGroup(
            ObservableList<String> costGroups) {
        return column -> {
            ComboBoxTreeTableCell<EfsElementDTO, String> cell = new ComboBoxTreeTableCell<>(costGroups);

            cell.setConverter(new CostGroupStringConverter(false));
            cell.setPopupItemConverter(new CostGroupStringConverter(true));
            cell.setMaxTextLength(4);
            cell.setValidation(costGroup -> StringUtils.isBlank(costGroup) || !costGroup.trim().isEmpty());
            cell.setPropertyName(EfsElementTreeItemPropertyNames.COST_GROUP);
            cell.setUpperCase(true);

            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Integer>, TreeTableCell<EfsElementDTO, Integer>> forColumnSet() {
        return column -> {
            NumberFieldTreeTableCell<EfsElementDTO, Integer> cell = new NumberFieldTreeTableCell<>(
                    new IntegerStringConverter());
            cell.setValidation(set -> Objects.nonNull(set) && set >= 0);
            cell.setPropertyName(EfsElementTreeItemPropertyNames.QUANTITY);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, QuantityUnit>, TreeTableCell<EfsElementDTO, QuantityUnit>> forColumnQuantityUnit() {
        ObservableList<QuantityUnit> units = FXCollections.observableArrayList(QuantityUnit.values());
        units.sort(Comparator.comparing(QuantityUnit::getBezeichnung));

        return column -> {
            ComboBoxTreeTableCell<EfsElementDTO, QuantityUnit> cell = new ComboBoxTreeTableCell<>(units);
            cell.setConverter(new QuantityUnitStringConverter());
            cell.setValidation(Objects::nonNull);
            cell.setPropertyName(EfsElementTreeItemPropertyNames.QUANTITY_UNIT);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnWeightCode() {
        Collection<String> weightControlFlags = Stream.concat(Stream.of(StringConstant.EMPTY),
                Arrays.stream(WeightControlFlag.values()).map(WeightControlFlag::getValue)).toList();
        return column -> {
            ComboBoxTreeTableCell<EfsElementDTO, String> cell = new ComboBoxTreeTableCell<>(weightControlFlags);
            cell.setMaxTextLength(1);
            cell.setUpperCase(true);
            cell.setValidation(weightControlFlags::contains);
            cell.setPropertyName(EfsElementTreeItemPropertyNames.WEIGHT_CONTROL_FLAG);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Double>, TreeTableCell<EfsElementDTO, Double>> forColumnWeightES(
            String propertyName) {
        return column -> {
            NumberFieldTreeTableCell<EfsElementDTO, Double> cell = new NumberFieldTreeTableCell<>(
                    new DoubleStringConverter(3, false), true, true);
            cell.setValidation(weight -> weight == null || weight.intValue() >= 0);
            cell.setPropertyName(propertyName);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Double>, TreeTableCell<EfsElementDTO, Double>> forColumnReadOnly(
            String propertyName) {
        return forColumnReadOnly(propertyName, false);
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Double>, TreeTableCell<EfsElementDTO, Double>> forColumnReadOnly(
            String propertyName, boolean grayOutZero) {
        return column -> {
            ReadOnlyTreeTableCell<EfsElementDTO, Double> cell = new ReadOnlyTreeTableCell<>(new DoubleStringConverter(),
                    grayOutZero, true);
            cell.setPropertyName(propertyName);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Double>, TreeTableCell<EfsElementDTO, Double>> forColumnWeightPrioES(
            String propertyName) {
        return column -> {
            ReadOnlyTreeTableCell<EfsElementDTO, Double> cell = new ReadOnlyTreeTableCell<>(new DoubleStringConverter(),
                    true, true);
            cell.setPropertyName(propertyName);
            cell.setEditable(false);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forStringColumn(
            int maxTextLength, String propertyName) {
        return column -> {
            TextFieldTreeTableCell<EfsElementDTO, String> cell = new TextFieldTreeTableCell<>();
            cell.setMaxTextLength(maxTextLength);
            initCell(cell);
            cell.setPropertyName(propertyName);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Date>, TreeTableCell<EfsElementDTO, Date>> forDateColumn(
            String propertyName) {
        return column -> {
            DatePickerTreeTableCell<EfsElementDTO> cell = new DatePickerTreeTableCell<>(
                    new DateStringConverter("dd.MM.yyyy"));
            cell.setPropertyName(propertyName);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Long>, TreeTableCell<EfsElementDTO, Long>> forColumnSort() {
        return column -> {
            NumberFieldTreeTableCell<EfsElementDTO, Long> cell = new NumberFieldTreeTableCell<>(
                    new LongStringConverter());
            cell.setPropertyName(EfsElementTreeItemPropertyNames.TIS_SORT);
            cell.setValidation(Objects::nonNull);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, Integer>, TreeTableCell<EfsElementDTO, Integer>> forOptionalNr() {
        return column -> {
            NumberFieldTreeTableCell<EfsElementDTO, Integer> cell = new NumberFieldTreeTableCell<>(
                    new IntegerStringConverter());
            cell.setPropertyName(EfsElementTreeItemPropertyNames.WAHLWEISE_NR);
            cell.setEditable(false);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forStringColumnReadOnly(
            String propertyName) {
        return column -> {
            TextFieldTreeTableCell<EfsElementDTO, String> cell = new TextFieldTreeTableCell<>();
            initCell(cell);
            cell.setPropertyName(propertyName);
            cell.setEditable(false);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnStartEndKey(
            int maxTextLength, String propertyName) { // NO_UCD (use default)
        return column -> {
            TextFieldTreeTableCell<EfsElementDTO, String> cell = new TextFieldTreeTableCell<>();
            cell.setConverter(new EinsatzEntfallConverter());
            cell.setMaxTextLength(maxTextLength);
            initCell(cell);
            cell.setPropertyName(propertyName);

            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, String>, TreeTableCell<EfsElementDTO, String>> forColumnPrNumberRule(
            int maxTextLength, String propertyName) {
        return column -> {
            PrNumberRuleTreeTableCell cell = new PrNumberRuleTreeTableCell();
            cell.setMaxTextLength(maxTextLength);
            cell.setPropertyName(propertyName);
            return addEventFilter(cell);
        };
    }

    public static Callback<TreeTableColumn<EfsElementDTO, CogCoordinates>, TreeTableCell<EfsElementDTO, CogCoordinates>> forColumnCog() {
        return column -> {
            CogTreeTableCell<EfsElementDTO> cell = new CogTreeTableCell<>();
            cell.setPropertyName(EfsElementTreeItemPropertyNames.COG);
            initCell(cell);

            return addEventFilter(cell);
        };
    }

    private static <E extends IEfsElementForDTO, T> void initCell(AbstractTreeTableCell<E, T> cell) {
        EfsCellUtil.formatCell(cell);
        cell.selectedProperty().addListener((observable, oldValue, newValue) -> setEditable(cell));
    }

    private static <E, T> TreeTableCell<E, T> addEventFilter(TreeTableCell<E, T> cell) {
        cell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            if (event.getClickCount() % 2 == 0 && event.getButton().equals(MouseButton.PRIMARY)) {
                event.consume();
            }
        });

        return cell;
    }

    // FIXME setEditable Cell -> right!!
    private static <E extends IEfsElementForDTO, T> void setEditable(AbstractTreeTableCell<E, T> cell) {
        E efsElement = getEfsElement(cell);
        cell.setEditable(efsElement != null && !efsElement.isDeleted());
    }

    private static <E extends IEfsElementForDTO, T> E getEfsElement(AbstractTreeTableCell<E, T> cell) {
        TreeTableRow<E> tableRow = cell.getTableRow();
        return tableRow == null ? null : tableRow.getItem();
    }
}
