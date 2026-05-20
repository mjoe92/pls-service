package de.vw.paso.client.control.cell;

import static de.vw.paso.client.stueckliste.util.PartGroupUtil.groupToString;

import javafx.scene.control.TreeTableRow;

import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeItem;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.compare.partgroup.PartGroupCompareRow;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.utility.SpecPartGroupCategory;

public class PartGroupTreeTableCell<S> extends AbstractTreeTableCell<S, Integer> {

    private static final String NUMBER_FIELD_CELL = "align-center-right";
    private static final String PART_GROUP_SEPARATOR = ".";

    public PartGroupTreeTableCell(final Class<?> dataType) {
        super();

        if (dataType != null) {
            if (Integer.class.isAssignableFrom(dataType) || Double.class.isAssignableFrom(dataType)
                    || Long.class.isAssignableFrom(dataType)) {
                this.getStyleClass().add(NUMBER_FIELD_CELL);
            }
        }
    }

    @Override
    public void updateItem(final Integer item, final boolean empty) {
        super.updateItem(item, empty);

        CellUtils.updateItem(this);

        if (item != null) {
            TreeTableRow<S> row = getTreeTableRow();

            if (row != null && row.getTreeItem() != null && row.getTreeItem().getValue() != null && (
                    row.getTreeItem() instanceof PartGroupTreeItem || row.getTreeItem()
                            .getValue() instanceof PartGroupCompareRow)) {
                getStyleClass().remove(NUMBER_FIELD_CELL);

                if (row.getTreeItem() instanceof PartGroupTreeItem) {
                    PartGroupVMO value = ((PartGroupTreeItem) row.getTreeItem()).getValue().getAggregationObject();

                    formatPartGroupString(item, value, row);
                } else if (row.getTreeItem().getValue() instanceof PartGroupCompareRow) {
                    PartGroupDTO value = ((PartGroupCompareRow) row.getTreeItem().getValue()).getPartGroup();

                    formatPartGroupString(item, PartGroupVMO.toVMO(value), row);
                }
            }
        }
    }

    private void formatPartGroupString(Integer item, PartGroupVMO value, TreeTableRow<S> row) {
        if (value.isMgr() && value.getMgr() != null) {
            if (value.getMgrEnd() != null) {
                setText(PartGroupUtil.groupToString(value.getMgr()) + " - " + groupToString(value.getMgrEnd()));
            } else {
                setText(PartGroupUtil.groupToString(value.getMgr()));

                setNormalPartGroupText(value, row);

                setPartGroupTextForCompareRow(value, row);
            }
        } else if (value.isUgr()) {
            setText(PartGroupUtil.groupToString(value.getMgr()) + PART_GROUP_SEPARATOR + PartGroupUtil.groupToString(
                    value.getUgr()));
        } else {
            String text = SpecPartGroupCategory.getStringForCategory(item);

            if (text != null) {
                setText(text);
            } else {
                setText(item + "");
            }
        }
    }

    private void setPartGroupTextForCompareRow(PartGroupVMO value, TreeTableRow<S> row) {
        if (row.getTreeItem().getValue() instanceof PartGroupCompareRow) {
            if (((PartGroupCompareRow) row.getTreeItem().getParent().getValue()).getPartGroup() == null) {
                String category = SpecPartGroupCategory.getStringForCategory(
                        ((PartGroupCompareRow) row.getTreeItem().getChildren().get(0).getValue()).getPartGroup()
                                .getCategory());
                if (category == null) {
                    setText(PartGroupUtil.groupToString(value.getMgr()));
                } else {
                    setText(category + PART_GROUP_SEPARATOR + PartGroupUtil.groupToString(value.getMgr()));
                }
            }
        }
    }

    private void setNormalPartGroupText(PartGroupVMO value, TreeTableRow<S> row) {
        if (value.getCategory() != null && value.getCategory()
                .equals(SpecPartGroupCategory.NORM_PART_GROUP.getCategory())) {
            if (row.getTreeItem() instanceof PartGroupTreeItem) {
                if (((PartGroupTreeItem) row.getTreeItem().getParent()).getValue().getId() == null) {
                    setText(SpecPartGroupCategory.getStringForCategory(value.getCategory()) + PART_GROUP_SEPARATOR
                            + PartGroupUtil.groupToString(value.getMgr()));
                }
            }
        }
    }

}
