package de.vw.paso.client.stueckliste.efs.export.fgset;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractFgSetCostGroupExcelExporter;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.FgSetTreeObject;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

public class FgSetExcelExporter extends AbstractFgSetCostGroupExcelExporter {

    private final FgSetTreeItem rootItem;

    private double platformWeight;
    private double systemWeight;
    private double hutWeight;
    private double weightAll;
    private double platformWeightExcludingUnknown;
    private double systemWeightExcludingUnknown;
    private double hutWeightExcludingUnknown;
    private double weightAllExcludingUnknown;

    public FgSetExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs, FgSetTreeItem rootItem) {
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
        return "fgset";
    }

    @Override
    protected void buildBody() {
        int childIndex = 0;
        int columnIndex = 0;
        int lastColIndex = 0;
        int firstRow = rowIndex;
        Collection<TreeItem<FgSetTreeObject>> children = rootItem.getChildren();
        for (TreeItem<FgSetTreeObject> treeItem : children) {
            childIndex++;

            if (childIndex == children.size()) {
                rowIndex++;
                firstRow += 3;
            }

            checkChildren(treeItem);

            int lastRow = rowIndex - 1;
            sheet.groupRow(firstRow, lastRow);
            firstRow = lastRow + 2;

            FgSetTreeItem fgSetTreeItem = (FgSetTreeItem) treeItem;
            boolean isSumRow = fgSetTreeItem.propertySummaryRow().get();
            CellStyle colorStyle = isSumRow ? darkRedCellStyle : darkGreyCellStyle;
            SetKeyDTO aggregationObject = treeItem.getValue().getAggregationObject();
            createCellData(rowIndex, columnIndex++, colorStyle, aggregationObject.getSetKeyName());
            createCellData(rowIndex, columnIndex++, colorStyle, null);
            createCellData(rowIndex, columnIndex++, colorStyle, aggregationObject.getDescription());

            Double platformWeight = fgSetTreeItem.getWeightPlatform();
            createCellData(rowIndex, columnIndex++, colorStyle, platformWeight);

            Double systemWeight = fgSetTreeItem.getWeightSystem();
            createCellData(rowIndex, columnIndex++, colorStyle, systemWeight);

            Double hutWeight = fgSetTreeItem.getWeightHut();
            createCellData(rowIndex, columnIndex++, colorStyle, hutWeight);

            Double weightAll = fgSetTreeItem.getWeightAll();
            createCellData(rowIndex, columnIndex++, colorStyle, weightAll);

            if (!isSumRow) {
                if (platformWeight != null) {
                    this.platformWeight += platformWeight;
                }

                if (systemWeight != null) {
                    this.systemWeight += systemWeight;
                }

                if (hutWeight != null) {
                    this.hutWeight += hutWeight;
                }

                if (weightAll != null) {
                    this.weightAll += weightAll;
                }
            }

            if (fgSetTreeItem.getChildren().isEmpty()) {
                if (platformWeight != null) {
                    this.platformWeightExcludingUnknown += platformWeight;
                }

                if (systemWeight != null) {
                    this.systemWeightExcludingUnknown += systemWeight;
                }

                if (hutWeight != null) {
                    this.hutWeightExcludingUnknown += hutWeight;
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
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, I18N.getString("fgset.unknown.summary"));

        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, platformWeightExcludingUnknown - platformWeight);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, systemWeightExcludingUnknown - systemWeight);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, hutWeightExcludingUnknown - hutWeight);
        createCellData(rowIndex, columnIndex, darkGreyCellStyle, weightAllExcludingUnknown - weightAll);
    }

    private boolean checkChildren(TreeItem<FgSetTreeObject> treeItem) {
        Collection<TreeItem<FgSetTreeObject>> children = treeItem.getChildren();
        if (children == null || children.isEmpty()) {
            return false;
        }

        int firstRow = rowIndex;
        int childIndex = 0;
        for (TreeItem<FgSetTreeObject> item : children) {
            childIndex++;

            boolean isChildren = checkChildren(item);
            fillChildCellData(item, isChildren);

            if (!isChildren && childIndex == children.size()) {
                sheet.groupRow(firstRow, rowIndex - 1);
            }
        }

        return true;
    }

    private void fillChildCellData(TreeItem<FgSetTreeObject> item, boolean highlighted) {
        CellStyle firstHalfStyle, lastHalfStyle;
        if (highlighted) {
            firstHalfStyle = greyCellStyle;
            lastHalfStyle = greyCellStyle;
        } else {
            firstHalfStyle = centerCellStyle;
            lastHalfStyle = cellStyle;
        }

        int columnIndex = 0;
        createCellData(rowIndex, columnIndex++, firstHalfStyle, null);

        SetKeyDTO aggregationObject = item.getValue().getAggregationObject();
        createCellData(rowIndex, columnIndex++, firstHalfStyle, aggregationObject.getSetKeyName());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, aggregationObject.getDescription());

        FgSetTreeItem fgSetTreeItem = (FgSetTreeItem) item;
        createCellData(rowIndex, columnIndex++, lastHalfStyle, fgSetTreeItem.getWeightPlatform());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, fgSetTreeItem.getWeightSystem());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, fgSetTreeItem.getWeightHut());
        createCellData(rowIndex, columnIndex, lastHalfStyle, fgSetTreeItem.getWeightAll());

        rowIndex++;
    }
}