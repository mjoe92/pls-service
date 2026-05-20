package de.vw.paso.client.control.cell;

import java.util.Date;
import java.util.Map;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.StringConverter;
import javafx.util.converter.DateStringConverter;

import com.google.common.eventbus.Subscribe;
import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeObject;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeObject;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeObject;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.client.util.converter.IntegerStringConverter;
import de.vw.paso.client.util.converter.LongStringConverter;
import de.vw.paso.client.util.converter.NumberStringConverter;
import de.vw.paso.client.util.highlight.SelectionHighlightManagerUtil;
import de.vw.paso.compare.costgroup.CostGroupCompareRow;
import de.vw.paso.compare.fgset.FGSetCompareRow;
import de.vw.paso.compare.partgroup.PartGroupCompareRow;

public class ReadOnlyTreeTableCell<S, T> extends AbstractTreeTableCell<S, T> {

    private static final String FG_SET_SUMMARY_ITEM_STYLE = "fg-set-highlight-summary-item-cell";
    private static final String FG_SET_SUMMARY_ITEM_STYLE_COMPARE = "fg-set-highlight-summary-item-cell-compare";
    private static final String COST_GROUP_SUMMARY_ITEM_STYLE = "cost-group-highlight-summary-item-cell";
    private static final String COST_GROUP_SUMMARY_ITEM_STYLE_COMPARE = "cost-group-highlight-summary-item-cell-compare";
    private static final String PART_GROUP_SUMMARY_ITEM_STYLE = "part-group-highlight-summary-item-cell";
    private static final String PART_GROUP_SUMMARY_ITEM_STYLE_COMPARE = "part-group-highlight-summary-item-cell-compare";
    private static final String NUMBER_FIELD_CELL = "align-center-right";

    private boolean grayOutZero;
    private boolean isNormalized;

    public ReadOnlyTreeTableCell(final Class<?> dataType) {
        super();

        if (dataType != null) {
            if (Integer.class.isAssignableFrom(dataType) || Double.class.isAssignableFrom(dataType)
                    || Long.class.isAssignableFrom(dataType)) {
                this.getStyleClass().add(NUMBER_FIELD_CELL);
            } else if (Date.class.isAssignableFrom(dataType)) {
                this.getStyleClass().add("align-center");
            }
        }
    }

    public ReadOnlyTreeTableCell(final StringConverter<T> converter) {
        this(converter, false);
    }

    public ReadOnlyTreeTableCell(final StringConverter<T> converter, final boolean grayOutZero) {
        this(converter, grayOutZero, false);
    }

    public ReadOnlyTreeTableCell(final StringConverter<T> converter, final boolean grayOutZero,
            final boolean isNormalized) {
        super();

        this.isNormalized = isNormalized;
        this.grayOutZero = grayOutZero;

        if (converter instanceof IntegerStringConverter || converter instanceof DoubleStringConverter
                || converter instanceof LongStringConverter) {
            this.getStyleClass().add(NUMBER_FIELD_CELL);
        } else if (converter instanceof DateStringConverter) {
            this.getStyleClass().add("align-center");
        }

        setConverter(converter);
    }

    @Override
    public void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);

        CellUtils.updateItem(this);
        updateStyling();

        if (getConverter() != null && NumberStringConverter.class.isAssignableFrom(getConverter().getClass())) {
            getStyleClass().remove(NumberFieldTreeTableCell.ZERO_WEIGHT_CLASS);

            final NumberStringConverter conv = (NumberStringConverter) getConverter();

            if (grayOutZero && conv.isZero((Number) item)) {
                getStyleClass().add(NumberFieldTreeTableCell.ZERO_WEIGHT_CLASS);
            }
        }

        if (isNormalized) {
            EfsCellUtil.normalizeNumberTextField(this);
        }

        if (item == null) {
            getStyleClass().remove(FG_SET_SUMMARY_ITEM_STYLE);
            getStyleClass().remove(COST_GROUP_SUMMARY_ITEM_STYLE);
        }
    }

    private void updateStyling() {
        final Map<TreeTableView, TreeItem> summaryItemMap = SelectionHighlightManagerUtil.getSummaryItemMap();
        final Map<TreeTableView, TreeTableColumn> summaryItemColumnMap = SelectionHighlightManagerUtil.getSummaryItemColumnMap();
        final TreeTableView<S> treeTableView = this.getTreeTableView();
        final TreeItem item = this.getTreeTableRow().getTreeItem();
        if (summaryItemMap != null && summaryItemMap.containsKey(treeTableView)) {

            if (item != null && summaryItemMap.get(treeTableView) != null && item.equals(
                    summaryItemMap.get(treeTableView)) && summaryItemColumnMap.get(treeTableView) != null
                    && this.getTableColumn().equals(summaryItemColumnMap.get(treeTableView))) {

                setStylingForSummaryItem();

            } else {
                if (this.getTreeTableView().getRoot().getValue() instanceof FgSetTreeObject) {
                    this.getStyleClass().remove(FG_SET_SUMMARY_ITEM_STYLE);
                } else if (this.getTreeTableView().getRoot().getValue() instanceof CostGroupTreeObject) {
                    this.getStyleClass().remove(COST_GROUP_SUMMARY_ITEM_STYLE);
                } else if (this.getTreeTableView().getRoot().getValue() instanceof FGSetCompareRow) {
                    this.getStyleClass().remove(FG_SET_SUMMARY_ITEM_STYLE_COMPARE);
                } else if (this.getTreeTableView().getRoot().getValue() instanceof CostGroupCompareRow) {
                    this.getStyleClass().remove(COST_GROUP_SUMMARY_ITEM_STYLE_COMPARE);
                } else if (this.getTreeTableView().getRoot().getValue() instanceof PartGroupTreeObject) {
                    this.getStyleClass().remove(PART_GROUP_SUMMARY_ITEM_STYLE);
                } else if (this.getTreeTableView().getRoot().getValue() instanceof PartGroupCompareRow) {
                    this.getStyleClass().remove(PART_GROUP_SUMMARY_ITEM_STYLE_COMPARE);
                }
            }
        }
    }

    public void setStylingForSummaryItem() {
        S rootObject = this.getTreeTableView().getRoot().getValue();

        if (rootObject instanceof FgSetTreeObject) {
            this.getStyleClass().add(FG_SET_SUMMARY_ITEM_STYLE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, true);
        } else if (rootObject instanceof FGSetCompareRow) {
            this.getStyleClass().add(FG_SET_SUMMARY_ITEM_STYLE_COMPARE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, true);
        } else if (rootObject instanceof CostGroupTreeObject) {
            this.getStyleClass().add(COST_GROUP_SUMMARY_ITEM_STYLE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, true);
        } else if (rootObject instanceof CostGroupCompareRow) {
            this.getStyleClass().add(COST_GROUP_SUMMARY_ITEM_STYLE_COMPARE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, true);
        } else if (rootObject instanceof PartGroupTreeObject) {
            this.getStyleClass().add(PART_GROUP_SUMMARY_ITEM_STYLE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, true);
        } else if (rootObject instanceof PartGroupCompareRow) {
            this.getStyleClass().add(PART_GROUP_SUMMARY_ITEM_STYLE_COMPARE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, true);
        } else {
            return;
        }

        try {
            EventBus.getInstance().unregister(this);
        } catch (Exception e) {
            //there was no registration yet, ignore
        }

        EventBus.getInstance().register(this);
    }

    @Subscribe
    public void handleRemoveSummaryHighlightStylingEvent(RemoveSummaryHighlightStylingEvent event) {
        if (event.getEventOrigin() instanceof FgSetTreeObject) {
            this.getStyleClass().remove(FG_SET_SUMMARY_ITEM_STYLE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, false);
        } else if (event.getEventOrigin() instanceof FGSetCompareRow) {
            this.getStyleClass().remove(FG_SET_SUMMARY_ITEM_STYLE_COMPARE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, false);
        } else if (event.getEventOrigin() instanceof CostGroupTreeObject) {
            this.getStyleClass().remove(COST_GROUP_SUMMARY_ITEM_STYLE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, false);
        } else if (event.getEventOrigin() instanceof CostGroupCompareRow) {
            this.getStyleClass().remove(COST_GROUP_SUMMARY_ITEM_STYLE_COMPARE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, false);
        } else if (event.getEventOrigin() instanceof PartGroupTreeObject) {
            this.getStyleClass().remove(PART_GROUP_SUMMARY_ITEM_STYLE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, false);
        } else if (event.getEventOrigin() instanceof PartGroupCompareRow) {
            this.getStyleClass().remove(PART_GROUP_SUMMARY_ITEM_STYLE_COMPARE);
            SelectionHighlightManagerUtil.updateSummaryMaps(this, false);
        }
    }

}
