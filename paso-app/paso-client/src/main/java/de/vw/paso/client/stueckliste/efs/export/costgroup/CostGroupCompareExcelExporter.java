package de.vw.paso.client.stueckliste.efs.export.costgroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractFgSetCostGroupExcelExporter;
import de.vw.paso.compare.costgroup.CostGroupCompareRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

public class CostGroupCompareExcelExporter extends AbstractFgSetCostGroupExcelExporter {

    private final TreeItem<CostGroupCompareRow> rootItem;
    private final Map<String, CostGroupDTO> costGroupMap;

    private final ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists;
    private final Map<Long, Map<ApCompareGroup, Double>> mapOfWeightsInPartList;

    public CostGroupCompareExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs,
            TreeItem<CostGroupCompareRow> rootItem, Map<String, CostGroupDTO> costGroupMap,
            ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists) {
        super(fileName, vehicleConfigs);

        this.rootItem = rootItem;
        this.costGroupMap = costGroupMap;
        this.mapOfDeltaWeightsOfPartLists = mapOfDeltaWeightsOfPartLists;
        mapOfWeightsInPartList = new HashMap<>();
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

        Collection<TreeItem<CostGroupCompareRow>> children = rootItem.getChildren();
        for (TreeItem<CostGroupCompareRow> costGroupItem : children) {
            childIndex++;
            if (childIndex == children.size() - 1) {
                sheet.setRowBreak(rowIndex++);

                summaryRowIndex = rowIndex++;

                sheet.setRowBreak(rowIndex++);

                rowIndex++;
                firstRow += 3;
            }

            if (childIndex != children.size()) {
                checkChildren(costGroupItem);

                int lastRow = rowIndex - 1;
                sheet.groupRow(firstRow, lastRow);
                firstRow = lastRow + 2;

                String costGroupStr = costGroupItem.getValue().getCostGroupStr();
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, costGroupStr);
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, null);

                CostGroupDTO costGroup = costGroupMap.get(costGroupStr);
                String desc = costGroup == null ? I18N.getString("table.row.unknown") : costGroup.getDescription();
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, desc);

                columnIndex = fillCellWeights(columnIndex, costGroupItem, darkGreyCellStyle, true);

                rowIndex++;
                lastColIndex = columnIndex;
                columnIndex = 0;
            }
        }

        createSummaryRow();

        CellRangeAddress cellAddresses = new CellRangeAddress(autoFilterRowIndex - 2, rowIndex, 0, lastColIndex - 2);
        sheet.setAutoFilter(cellAddresses);
    }

    private void createSummaryRow() {
        int columnIndex = 0;
        int index = 0;

        CellStyle summaryRowStyle = getDataCellStyleBuilder().setCellColor(IndexedColors.DARK_RED).setFontBold()
                .setFontColor(IndexedColors.WHITE).setFontSize(10.0F)
                .setDataFormat(getWorkbook().createDataFormat().getFormat("#,##0")).build();

        createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, null);
        createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, null);
        createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, I18N.getString("costgroup.unknown.summary"));

        ApCompareGroup[] apCompareGroups = ApCompareGroup.values();
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            for (ApCompareGroup acGroup : apCompareGroups) {
                Double weight = mapOfWeightsInPartList.get(vehicleConfig.getId()).get(acGroup);
                createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, weight);
            }
            createCellData(summaryRowIndex, columnIndex++, cellStyle, null);

            index++;
            if (index >= 2) {
                for (ApCompareGroup acGroup : apCompareGroups) {
                    Long vehicleConfigId = vehicleConfigs.get(index - 2).getId();
                    Double deltaWeight = mapOfDeltaWeightsOfPartLists.get(vehicleConfigId).stream()
                            .filter(e -> e.get(acGroup) != null).findFirst().get().get(acGroup);
                    createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, deltaWeight);
                }

                createCellData(summaryRowIndex, columnIndex++, cellStyle, null);
            }
        }
    }

    private boolean checkChildren(TreeItem<CostGroupCompareRow> treeItem) {
        if (treeItem.getChildren() == null || treeItem.getChildren().isEmpty()) {
            return false;
        }

        int firstRow = rowIndex;
        int childIndex = 0;
        for (TreeItem<CostGroupCompareRow> item : treeItem.getChildren()) {
            childIndex++;

            boolean isChildren = checkChildren(item);
            fillChildCellData(item, isChildren);

            if (childIndex == treeItem.getChildren().size()) {
                sheet.groupRow(firstRow, rowIndex - 1);
            }
        }

        return true;
    }

    private void fillChildCellData(TreeItem<CostGroupCompareRow> item, boolean highlighted) {
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

        String costGroupStr = item.getValue().getCostGroupStr();
        createCellData(rowIndex, columnIndex++, firstHalfStyle, costGroupStr);

        CostGroupDTO costGroup = costGroupMap.get(costGroupStr);
        String description = costGroup == null ? StringUtils.EMPTY : costGroup.getDescription();
        createCellData(rowIndex, columnIndex++, lastHalfStyle, description);

        fillCellWeights(columnIndex, item, lastHalfStyle, false);

        rowIndex++;
    }

    private int fillCellWeights(int columnIndex, TreeItem<CostGroupCompareRow> item, CellStyle style,
            boolean mainGroup) {
        Map<ApCompareGroup, Double> lastWeights = new HashMap<>();
        int configCount = 0;

        ApCompareGroup[] apCompareGroups = ApCompareGroup.values();
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            configCount++;
            Long vehicleConfigId = vehicleConfig.getId();
            Map<ApCompareGroup, Double> weights = item.getValue().getWeights(vehicleConfigId);

            if (mapOfWeightsInPartList.get(vehicleConfigId) == null) {
                mapOfWeightsInPartList.put(vehicleConfigId, weights);
            } else if (mainGroup) {
                for (ApCompareGroup acGroup : apCompareGroups) {
                    Double weight = weights.get(acGroup);
                    mapOfWeightsInPartList.get(vehicleConfigId)
                            .merge(acGroup, weight == null ? 0.0 : weight, Double::sum);
                }
            }

            for (ApCompareGroup acGroup : apCompareGroups) {
                createCellData(rowIndex, columnIndex++, style, weights.get(acGroup));
            }
            createCellData(rowIndex, columnIndex++, cellStyle, null);

            if (configCount >= 2) {
                fillDeltaWeights(lastWeights, weights, columnIndex, style);
                columnIndex += 5;
            }

            lastWeights = weights;
        }

        return columnIndex;
    }
}