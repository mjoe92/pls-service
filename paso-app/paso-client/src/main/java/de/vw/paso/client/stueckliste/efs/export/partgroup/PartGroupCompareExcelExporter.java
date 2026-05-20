package de.vw.paso.client.stueckliste.efs.export.partgroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractFgSetCostGroupExcelExporter;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.compare.partgroup.PartGroupCompareRow;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.SpecPartGroupCategory;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

public class PartGroupCompareExcelExporter extends AbstractFgSetCostGroupExcelExporter {

    private final TreeItem<PartGroupCompareRow> rootItem;
    private final Map<String, PartGroupDTO> partGroupMap;

    private final ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists;
    private final Map<Long, Map<ApCompareGroup, Double>> mapOfWeightsInPartList = new HashMap<>();

    public PartGroupCompareExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs,
            TreeItem<PartGroupCompareRow> rootItem, Map<String, PartGroupDTO> partGroupMap,
            ListMultimap<Long, Map<ApCompareGroup, Double>> mapOfDeltaWeightsOfPartLists) {
        super(fileName, vehicleConfigs);

        this.rootItem = rootItem;
        this.partGroupMap = partGroupMap;
        this.mapOfDeltaWeightsOfPartLists = mapOfDeltaWeightsOfPartLists;
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

        Collection<TreeItem<PartGroupCompareRow>> children = rootItem.getChildren();
        for (TreeItem<PartGroupCompareRow> partGroupItem : children) {
            childIndex++;
            if (childIndex == children.size() - 1) {
                sheet.setRowBreak(rowIndex++);

                summaryRowIndex = rowIndex++;

                sheet.setRowBreak(rowIndex++);

                rowIndex++;
                firstRow += 3;
            }

            if (childIndex != children.size()) {
                checkChildren(partGroupItem);

                int lastRow = rowIndex - 1;
                sheet.groupRow(firstRow, lastRow);
                firstRow = lastRow + 2;

                PartGroupDTO partGroup = partGroupMap.get(partGroupItem.getValue().getPartGroupStr());
                String partGroupStr, desc;
                if (partGroup == null) {
                    partGroupStr = null;
                    desc = I18N.getString("table.row.unknown");
                } else {
                    partGroupStr = getPartGroupString(partGroup);
                    desc = partGroup.getDescription();
                }

                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, partGroupStr);
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, null);
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, desc);

                columnIndex = fillCellWeights(columnIndex, partGroupItem, darkGreyCellStyle, true);

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
        createCellData(summaryRowIndex, columnIndex++, summaryRowStyle, I18N.getString("partgroup.unknown.summary"));

        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            index++;
            Long vehicleConfigId = vehicleConfig.getId();
            ApCompareGroup[] apCompareGroups = ApCompareGroup.values();
            for (ApCompareGroup apGroup : apCompareGroups) {
                Double weight = mapOfWeightsInPartList.get(vehicleConfigId).get(apGroup);
                createCellData(rowIndex, columnIndex++, darkGreyCellStyle, weight);
            }
            createCellData(rowIndex, columnIndex++, cellStyle, null);

            if (index >= 2) {
                for (ApCompareGroup apGroup : apCompareGroups) {
                    vehicleConfigId = vehicleConfigs.get(index - 2).getId();
                    Double weight = mapOfDeltaWeightsOfPartLists.get(vehicleConfigId).stream()
                            .filter(e -> e.get(apGroup) != null).findFirst().get().get(apGroup);
                    createCellData(rowIndex, columnIndex++, summaryRowStyle, weight);
                }
                createCellData(rowIndex, columnIndex++, cellStyle, null);
            }
        }
    }

    private boolean checkChildren(TreeItem<PartGroupCompareRow> treeItem) {
        Collection<TreeItem<PartGroupCompareRow>> children = treeItem.getChildren();
        if (children == null || children.isEmpty()) {
            return false;
        }

        int firstRow = rowIndex;
        int childIndex = 0;
        for (TreeItem<PartGroupCompareRow> item : children) {
            childIndex++;

            boolean isChildren = checkChildren(item);
            fillChildCellData(item, isChildren);

            if (childIndex == children.size()) {
                sheet.groupRow(firstRow, rowIndex - 1);
            }
        }

        return true;
    }

    private void fillChildCellData(TreeItem<PartGroupCompareRow> item, boolean highlighted) {
        int columnIndex = 0;

        PartGroupDTO partGroup = item.getValue().getPartGroup();
        boolean isUnknown = partGroup != null && partGroup.getCategory() == null;

        String partGroupStr = item.getValue().getPartGroupStr();
        if (isUnknown && !partGroup.isUgr()) {
            partGroupStr = partGroupStr.charAt(0) + "-" + partGroupStr;
        }
        partGroup = partGroupMap.get(partGroupStr);

        String desc;
        if (partGroup == null) {
            partGroupStr = partGroupStr.substring(partGroupStr.length() - 3);
            desc = StringUtils.EMPTY;
        } else {
            partGroupStr = getPartGroupString(partGroup);
            desc = partGroup.getDescription();
        }

        CellStyle firstHalfStyle, lastHalfStyle;
        if (highlighted) {
            firstHalfStyle = greyCellStyle;
            lastHalfStyle = greyCellStyle;
        } else {
            firstHalfStyle = centerCellStyle;
            lastHalfStyle = cellStyle;
        }

        createCellData(rowIndex, columnIndex++, firstHalfStyle, null);
        createCellData(rowIndex, columnIndex++, firstHalfStyle, partGroupStr);
        createCellData(rowIndex, columnIndex++, lastHalfStyle, desc);

        fillCellWeights(columnIndex, item, lastHalfStyle, false);

        rowIndex++;
    }

    private int fillCellWeights(int columnIndex, TreeItem<PartGroupCompareRow> item, CellStyle style,
            boolean mainGroup) {
        Map<ApCompareGroup, Double> lastWeights = new HashMap<>();
        int configCount = 0;

        ApCompareGroup[] apCompareGroups = ApCompareGroup.values();
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            configCount++;
            Long vehicleConfigId = vehicleConfig.getId();
            Map<ApCompareGroup, Double> weights = item.getValue().getWeights(vehicleConfigId);

            Map<ApCompareGroup, Double> apGroupToDoubleMap = mapOfWeightsInPartList.get(vehicleConfigId);
            if (apGroupToDoubleMap == null) {
                mapOfWeightsInPartList.put(vehicleConfigId, weights);
            } else if (mainGroup) {
                for (ApCompareGroup value : apCompareGroups) {
                    Double weight = weights.get(value);
                    apGroupToDoubleMap.merge(value, weight == null ? 0.0 : weight, Double::sum);
                }
            }

            for (ApCompareGroup value : apCompareGroups) {
                Double weight = weights.get(value);
                createCellData(rowIndex, columnIndex++, style, weight);
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

    private String getPartGroupString(PartGroupDTO partGroup) {
        if (partGroup.isCategory()) {
            Integer category = partGroup.getCategory();

            return category < 100 ? category.toString() : SpecPartGroupCategory.getStringForCategory(category);
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