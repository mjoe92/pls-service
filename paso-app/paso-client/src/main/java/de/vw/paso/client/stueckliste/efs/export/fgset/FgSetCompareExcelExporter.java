package de.vw.paso.client.stueckliste.efs.export.fgset;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractFgSetCostGroupExcelExporter;
import de.vw.paso.compare.fgset.FGSetCompareRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

public class FgSetCompareExcelExporter extends AbstractFgSetCostGroupExcelExporter {

    private final TreeItem<FGSetCompareRow> rootItem;
    private final Map<String, SetKeyDTO> setKeyMap;

    private final ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists;
    private final Map<Long, Map<ApCompareGroup, Double>> mapOfWeightsInPartList;

    public FgSetCompareExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs,
            TreeItem<FGSetCompareRow> rootItem, Map<String, SetKeyDTO> setKeyMap,
            ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists) {
        super(fileName, vehicleConfigs);

        this.rootItem = rootItem;
        this.setKeyMap = setKeyMap;
        this.mapOfDeltaWeightsOfPartLists = mapOfDeltaWeightsOfPartLists;
        mapOfWeightsInPartList = new HashMap<>();
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
        Collection<TreeItem<FGSetCompareRow>> children = rootItem.getChildren();
        for (TreeItem<FGSetCompareRow> fgSetCompare : children) {
            childIndex++;
            if (childIndex == children.size() - 1) {
                sheet.setRowBreak(rowIndex++);

                summaryRowIndex = rowIndex++;

                sheet.setRowBreak(rowIndex++);

                rowIndex++;
                firstRow += 3;
            }

            if (childIndex != children.size()) {
                checkChildren(fgSetCompare);

                int lastRow = rowIndex - 1;
                sheet.groupRow(firstRow, lastRow);
                firstRow = lastRow + 2;

                String setKeyStr = fgSetCompare.getValue().getSetKeyStr();

                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, setKeyStr);
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, null);

                SetKeyDTO setKey = setKeyMap.get(setKeyStr);
                String desc = setKey == null ? I18N.getString("table.row.unknown") : setKey.getDescription();
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, desc);

                columnIndex = fillCellWeights(columnIndex, fgSetCompare, darkGreyCellStyle, true);

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

        createCellData(rowIndex, columnIndex, summaryRowStyle, null);
        createCellData(rowIndex, columnIndex, summaryRowStyle, null);
        createCellData(rowIndex, columnIndex, summaryRowStyle, I18N.getString("fgset.unknown.summary"));

        ApCompareGroup[] apCompareGroups = ApCompareGroup.values();
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            for (ApCompareGroup apGroup : apCompareGroups) {
                Double weight = mapOfWeightsInPartList.get(vehicleConfig.getId()).get(apGroup);
                createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, weight);
            }
            createCellData(summaryRowIndex, columnIndex++, cellStyle, null);

            index++;
            if (index >= 2) {
                for (ApCompareGroup apGroup : apCompareGroups) {
                    Long vehicleConfigId = vehicleConfigs.get(index - 2).getId();
                    Double deltaWeight = mapOfDeltaWeightsOfPartLists.get(vehicleConfigId).stream()
                            .filter(e -> e.get(apGroup) != null).findFirst().get().get(apGroup);
                    createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, deltaWeight);
                }
                createCellData(summaryRowIndex, columnIndex++, cellStyle, null);
            }
        }
    }

    private boolean checkChildren(TreeItem<FGSetCompareRow> treeItem) {
        if (treeItem.getChildren() == null || treeItem.getChildren().isEmpty()) {
            return false;
        }

        int firstRow = rowIndex;
        int childIndex = 0;
        for (TreeItem<FGSetCompareRow> item : treeItem.getChildren()) {
            childIndex++;

            boolean isChildren = checkChildren(item);
            fillChildCellData(item, isChildren);

            if (!isChildren && childIndex == treeItem.getChildren().size()) {
                sheet.groupRow(firstRow, rowIndex - 1);
            }
        }

        return true;
    }

    private void fillChildCellData(TreeItem<FGSetCompareRow> item, boolean highlighted) {
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

        String setKeyStr = item.getValue().getSetKeyStr();
        createCellData(rowIndex, columnIndex++, firstHalfStyle, setKeyStr);

        SetKeyDTO setKey = setKeyMap.get(setKeyStr);
        String desc = setKey == null ? StringUtils.EMPTY : setKey.getDescription();
        createCellData(rowIndex, columnIndex++, lastHalfStyle, desc);

        fillCellWeights(columnIndex, item, lastHalfStyle, false);

        rowIndex++;
    }

    private int fillCellWeights(int columnIndex, TreeItem<FGSetCompareRow> item, CellStyle style, boolean mainGroup) {
        Map<ApCompareGroup, Double> lastWeights = new HashMap<>();
        int configCount = 0;

        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            configCount++;
            Map<ApCompareGroup, Double> weights = item.getValue().getWeights(vehicleConfig.getId());

            if (mapOfWeightsInPartList.get(vehicleConfig.getId()) == null) {
                mapOfWeightsInPartList.put(vehicleConfig.getId(), weights);
            } else if (mainGroup) {
                for (ApCompareGroup acGroup : ApCompareGroup.values()) {
                    Double weight = weights.get(acGroup);
                    mapOfWeightsInPartList.get(vehicleConfig.getId())
                            .merge(acGroup, weight == null ? 0.0 : weight, Double::sum);
                }
            }

            for (ApCompareGroup acGroup : ApCompareGroup.values()) {
                createCellData(rowIndex, columnIndex++, style, weights.get(acGroup));
            }
            createCellData(rowIndex, columnIndex++, style, null);

            if (configCount >= 2) {
                fillDeltaWeights(lastWeights, weights, columnIndex, style);
                columnIndex += 5;
            }

            lastWeights = weights;
        }

        return columnIndex;
    }
}