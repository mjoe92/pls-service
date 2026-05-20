package de.vw.paso.client.stueckliste.efs.export.costgroup;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractFgSetCostGroupExcelExporter;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.CostGroupTreeObject;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

public class CostGroupExcelExporter extends AbstractFgSetCostGroupExcelExporter {

    private final CostGroupTreeItem rootItem;

    private double platformWeight;
    private double systemWeight;
    private double hutWeight;
    private double weightAll;
    private double platformWeightExcludingUnknown;
    private double systemWeightExcludingUnknown;
    private double hutWeightExcludingUnknown;
    private double weightAllExcludingUnknown;

    public CostGroupExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs, CostGroupTreeItem rootItem) {
        super(fileName, vehicleConfigs);

        this.rootItem = rootItem;
    }

    @Override
    protected void applyStyle() {
        short height = 600;
        sheet.createFreezePane(0, autoFilterRowIndex);
        sheet.getColumnHelper().setColWidth(0, calculateColumnWidth(30));
        sheet.getColumnHelper().setColWidth(2, calculateColumnWidth(400));
        sheet.getRow(higherRowIndex).setHeight(height);
    }

    @Override
    protected String bundleKeyPrefix() {
        return "costgroup";
    }

    @Override
    protected void buildBody() {
        int childIndex = 0;
        int columnIndex = 0;
        int lastColIndex = 0;
        int firstRow = rowIndex;
        Collection<TreeItem<CostGroupTreeObject>> children = rootItem.getChildren();
        for (TreeItem<CostGroupTreeObject> treeItem : children) {
            childIndex++;

            if (childIndex == children.size()) {
                rowIndex++;
                firstRow += 3;
            }

            checkChildren(treeItem);

            int lastRow = rowIndex - 1;
            sheet.groupRow(firstRow, lastRow);
            firstRow = lastRow + 2;

            CostGroupTreeItem costGroupTreeItem = (CostGroupTreeItem) treeItem;
            boolean isSumRow = costGroupTreeItem.propertySummaryRow().get();
            CellStyle colorStyle = isSumRow ? darkRedCellStyle : darkGreyCellStyle;

            CostGroupDTO aggregationObject = treeItem.getValue().getAggregationObject();
            createCellData(rowIndex, columnIndex++, colorStyle, aggregationObject.getCostGroupName());
            createCellData(rowIndex, columnIndex++, colorStyle, null);
            createCellData(rowIndex, columnIndex++, colorStyle, aggregationObject.getDescription());

            Double platformWeight = costGroupTreeItem.getWeightPlatform();
            createCellData(rowIndex, columnIndex++, colorStyle, platformWeight);

            Double systemWeight = costGroupTreeItem.getWeightSystem();
            createCellData(rowIndex, columnIndex++, colorStyle, systemWeight);

            Double weightHut = costGroupTreeItem.getWeightHut();
            createCellData(rowIndex, columnIndex++, colorStyle, weightHut);

            Double weightAll = costGroupTreeItem.getWeightAll();
            createCellData(rowIndex, columnIndex++, colorStyle, weightAll);

            if (!isSumRow) {
                if (platformWeight != null) {
                    this.platformWeight += platformWeight;
                }

                if (systemWeight != null) {
                    this.systemWeight += systemWeight;
                }

                if (weightHut != null) {
                    this.hutWeight += weightHut;
                }

                if (weightAll != null) {
                    this.weightAll += weightAll;
                }
            }

            if (!costGroupTreeItem.getChildren().isEmpty()) {
                if (platformWeight != null) {
                    this.platformWeightExcludingUnknown += platformWeight;
                }

                if (systemWeight != null) {
                    this.systemWeightExcludingUnknown += systemWeight;
                }

                if (weightHut != null) {
                    this.hutWeightExcludingUnknown += weightHut;
                }

                if (weightAll != null) {
                    this.weightAllExcludingUnknown += weightAll;
                }
            }

            rowIndex++;
            lastColIndex = columnIndex;
            columnIndex = 0;
        }

        createSummaryRow();

        sheet.groupColumn(lastColIndex - 3, lastColIndex - 1);

        CellRangeAddress cellAddresses = new CellRangeAddress(autoFilterRowIndex - 2, rowIndex, 0, lastColIndex);
        sheet.setAutoFilter(cellAddresses);
    }

    private void createSummaryRow() {
        int columnIndex = 0;

        rowIndex++;

        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, null);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, null);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, I18N.getString("costgroup.unknown.summary"));

        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, platformWeightExcludingUnknown - platformWeight);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, systemWeightExcludingUnknown - systemWeight);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, hutWeightExcludingUnknown - hutWeight);
        createCellData(rowIndex, columnIndex, darkGreyCellStyle, weightAllExcludingUnknown - weightAll);
    }

    private boolean checkChildren(TreeItem<CostGroupTreeObject> treeItem) {
        Collection<TreeItem<CostGroupTreeObject>> children = treeItem.getChildren();
        if (children == null || children.isEmpty()) {
            return false;
        }

        int firstRow = rowIndex;
        int childIndex = 0;
        for (TreeItem<CostGroupTreeObject> item : children) {
            childIndex++;

            boolean isChildren = checkChildren(item);
            fillChildCellData(item, isChildren);

            if (!isChildren && childIndex == children.size()) {
                sheet.groupRow(firstRow, rowIndex - 1);
            }
        }

        return true;
    }

    private void fillChildCellData(TreeItem<CostGroupTreeObject> item, boolean highlighted) {
        CellStyle firstHalfStyle, lastHalfStyle;
        if (highlighted) {
            firstHalfStyle = greyCellStyle;
            lastHalfStyle = greyCellStyle;
        } else {
            firstHalfStyle = centerCellStyle;
            lastHalfStyle = cellStyle;
        }

        int columnIndex = 0;
        createCellData(rowIndex, columnIndex++, greyCellStyle, null);

        CostGroupDTO aggregationObject = item.getValue().getAggregationObject();
        createCellData(rowIndex, columnIndex++, firstHalfStyle, aggregationObject.getCostGroupName());
        createCellData(rowIndex, columnIndex++, firstHalfStyle, aggregationObject.getDescription());

        CostGroupTreeItem costGroupTreeItem = (CostGroupTreeItem) item;
        createCellData(rowIndex, columnIndex++, lastHalfStyle, costGroupTreeItem.getWeightPlatform());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, costGroupTreeItem.getWeightSystem());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, costGroupTreeItem.getWeightHut());
        createCellData(rowIndex, columnIndex, lastHalfStyle, costGroupTreeItem.getWeightAll());

        rowIndex++;
    }
}