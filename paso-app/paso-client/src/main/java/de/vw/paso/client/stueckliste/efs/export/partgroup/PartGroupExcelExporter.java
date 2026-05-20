package de.vw.paso.client.stueckliste.efs.export.partgroup;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractFgSetCostGroupExcelExporter;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.PartGroupTreeObject;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.SpecPartGroupCategory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

public class PartGroupExcelExporter extends AbstractFgSetCostGroupExcelExporter {

    private final PartGroupTreeItem rootItem;

    private double platformWeightUnknown;
    private double systemWeightUnknown;
    private double hutWeightUnknown;
    private double weightAllUnknown;

    public PartGroupExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs, PartGroupTreeItem rootItem) {
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
        return "partgroup";
    }

    @Override
    protected void buildBody() {
        int childIndex = 0;
        int columnIndex = 0;
        int lastColIndex = 0;
        int firstRow = rowIndex;
        Collection<TreeItem<PartGroupTreeObject>> children = rootItem.getChildren();
        for (TreeItem<PartGroupTreeObject> treeItem : children) {
            childIndex++;

            if (childIndex == children.size()) {
                rowIndex++;
                firstRow += 3;
            }

            checkChildren(treeItem);

            int lastRow = rowIndex - 1;
            sheet.groupRow(firstRow, lastRow);
            firstRow = lastRow + 2;

            PartGroupTreeItem partGroupTreeItem = (PartGroupTreeItem) treeItem;
            Integer category = partGroupTreeItem.getUserObject().getAggregationObject().getCategory();

            CellStyle colorCellStyle =
                    partGroupTreeItem.propertySummaryRow().get() ? darkRedCellStyle : darkGreyCellStyle;

            if (category != null && category >= 100) {
                String categoryStr = SpecPartGroupCategory.getStringForCategory(category);
                createCellData(rowIndex, columnIndex++, colorCellStyle, categoryStr);
            } else {
                createCellData(rowIndex, columnIndex++, colorCellStyle, category);
            }

            createCellData(rowIndex, columnIndex++, colorCellStyle, null);

            PartGroupVMO aggregationObject = partGroupTreeItem.getValue().getAggregationObject();
            createCellData(rowIndex, columnIndex++, colorCellStyle, aggregationObject.getDescription());

            Double platformWeight = partGroupTreeItem.getWeightPlatform();
            createCellData(rowIndex, columnIndex++, colorCellStyle, platformWeight);

            Double systemWeight = partGroupTreeItem.getWeightSystem();
            createCellData(rowIndex, columnIndex++, colorCellStyle, systemWeight);

            Double hutWeight = partGroupTreeItem.getWeightHut();
            createCellData(rowIndex, columnIndex++, colorCellStyle, hutWeight);

            Double weightAll = partGroupTreeItem.getWeightAll();
            createCellData(rowIndex, columnIndex, colorCellStyle, weightAll);

            String desc = aggregationObject.getDescription();
            //todo: better solution for unknown group detection
            if ("Unknown".equals(desc)) {
                if (platformWeight != null) {
                    platformWeightUnknown += platformWeight;
                }

                if (systemWeight != null) {
                    systemWeightUnknown += systemWeight;
                }

                if (hutWeight != null) {
                    hutWeightUnknown += hutWeight;
                }

                if (weightAll != null) {
                    weightAllUnknown += weightAll;
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
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, I18N.getString("partgroup.unknown.summary"));

        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, platformWeightUnknown);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, systemWeightUnknown);
        createCellData(rowIndex, columnIndex++, darkGreyCellStyle, hutWeightUnknown);
        createCellData(rowIndex, columnIndex, darkGreyCellStyle, weightAllUnknown);
    }

    private boolean checkChildren(TreeItem<PartGroupTreeObject> treeItem) {
        Collection<TreeItem<PartGroupTreeObject>> children = treeItem.getChildren();
        if (children == null || children.isEmpty()) {
            return false;
        }

        int firstRow = rowIndex;
        int childIndex = 0;
        for (TreeItem<PartGroupTreeObject> item : children) {
            childIndex++;

            boolean isChildren = checkChildren(item);
            fillChildCellData(item, isChildren);

            if (!isChildren && childIndex == children.size()) {
                sheet.groupRow(firstRow, rowIndex - 1);
            }
        }

        return true;
    }

    private void fillChildCellData(TreeItem<PartGroupTreeObject> item, boolean highlighted) {
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

        PartGroupVMO partGroupVMO = item.getValue().getAggregationObject();
        String partGroupStr = getPartGroupString(partGroupVMO);
        createCellData(rowIndex, columnIndex++, firstHalfStyle, partGroupStr);
        createCellData(rowIndex, columnIndex++, lastHalfStyle, partGroupVMO.getDescription());

        PartGroupTreeItem partGroupTreeItem = (PartGroupTreeItem) item;
        createCellData(rowIndex, columnIndex++, lastHalfStyle, partGroupTreeItem.getWeightPlatform());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, partGroupTreeItem.getWeightSystem());
        createCellData(rowIndex, columnIndex++, lastHalfStyle, partGroupTreeItem.getWeightHut());
        createCellData(rowIndex, columnIndex, lastHalfStyle, partGroupTreeItem.getWeightAll());

        rowIndex++;
    }

    private String getPartGroupString(PartGroupVMO partGroup) {
        if (partGroup.isCategory()) {
            return partGroup.getCategory().toString();
        } else if (partGroup.isMgr()) {
            String partGroupStr = PartGroupUtil.groupToString(partGroup.getMgr());

            Integer mgrEnd = partGroup.getMgrEnd();
            if (mgrEnd != null) {
                partGroupStr += "-" + PartGroupUtil.groupToString(mgrEnd);
            }

            return partGroupStr;
        }

        return PartGroupUtil.groupToString(partGroup.getMgr()) + "." + PartGroupUtil.groupToString(partGroup.getUgr());
    }
}