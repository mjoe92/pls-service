package de.vw.paso.client.stueckliste.efs.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.explorer.vehicleconfig.converter.DateTimeStringConverter;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

public abstract class AbstractFgSetCostGroupExcelExporter extends AbstractExcelExporter {

    private static final String DEFAULT_WORKBOOK_DATA_FORMAT = "#,##0";

    protected CellStyle cellStyle;
    protected CellStyle centerCellStyle;
    protected CellStyle greyCellStyle;
    protected CellStyle darkRedCellStyle;
    protected CellStyle darkGreyCellStyle;

    protected final String fileName;
    protected final List<VehicleConfigDTO> vehicleConfigs;
    protected int summaryRowIndex;

    protected abstract String bundleKeyPrefix();

    public AbstractFgSetCostGroupExcelExporter(String fileName, List<VehicleConfigDTO> vehicleConfigs) {
        this.fileName = fileName;
        this.vehicleConfigs = vehicleConfigs;
        this.sheetData = new ArrayList<>();
    }

    @Override
    protected String getFileName() {
        return fileName;
    }

    @Override
    protected void applyStyle() {
        short height = 600;
        sheet.createFreezePane(0, autoFilterRowIndex);
        sheet.getRow(higherRowIndex).setHeight(height);
        sheet.getColumnHelper().setColWidth(0, calculateColumnWidth(30));
        sheet.getColumnHelper().setColWidth(2, calculateColumnWidth(400));

        for (int index : sheet.getColumnBreaks()) {
            sheet.getColumnHelper().setColWidth(index, calculateColumnWidth(20));
        }
    }

    @Override
    protected void buildCellStyles() {
        cellStyle = getDataCellStyleBuilder().setDataFormat(
                getWorkbook().createDataFormat().getFormat(DEFAULT_WORKBOOK_DATA_FORMAT)).build();
        centerCellStyle = getDataCellStyleBuilder().setCellHorizontalAlignment(HorizontalAlignment.CENTER).build();
        greyCellStyle = getDataCellStyleBuilder().setFontBold().setCellColor(IndexedColors.GREY_25_PERCENT)
                .setDataFormat(getWorkbook().createDataFormat().getFormat(DEFAULT_WORKBOOK_DATA_FORMAT)).build();

        darkRedCellStyle = getDataCellStyleBuilder().setCellColor(IndexedColors.DARK_RED).setFontBold()
                .setFontColor(IndexedColors.WHITE).setFontSize(10.0F)
                .setDataFormat(getWorkbook().createDataFormat().getFormat(DEFAULT_WORKBOOK_DATA_FORMAT)).build();
        darkGreyCellStyle = getDataCellStyleBuilder().setFontSize(10.0F).setFontBold().setFontColor(IndexedColors.WHITE)
                .setCellColor(IndexedColors.GREY_50_PERCENT)
                .setDataFormat(getWorkbook().createDataFormat().getFormat(DEFAULT_WORKBOOK_DATA_FORMAT)).build();
    }

    @Override
    protected void buildTitle() {
        int columnIndex = 2;

        CellStyle titleCellStyleBold = getDataCellStyleBuilder().setCellHorizontalAlignment(HorizontalAlignment.CENTER)
                .setFontBold().setFontSize(11.0F).build();
        String title = getI18NPrefixes("export.title");
        createCellData(rowIndex, columnIndex++, titleCellStyleBold, title);

        rowIndex++;

        CellStyle titleCellStyle = getDataCellStyleBuilder().setCellColor(IndexedColors.GREY_25_PERCENT)
                .setCellAlignmentCenter().build();

        int lastColIndex;
        int vehicleConfigIndex = 0;
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            vehicleConfigIndex++;
            lastColIndex = columnIndex + 3;

            sheet.groupColumn(columnIndex, lastColIndex - 1);
            CellRangeAddress cellAddresses = new CellRangeAddress(rowIndex, rowIndex, columnIndex, lastColIndex);
            sheet.addMergedRegion(cellAddresses);

            VehicleProjectDTO vehicleProject = vehicleConfig.getVehicleProject();
            title = vehicleProject.getProjectName() + StringConstant.SPACE_SLASH_SPACE + vehicleProject.getProductKey()
                    + StringConstant.SPACE_DASH_SPACE + vehicleConfig.getName();
            createCellData(rowIndex, columnIndex, titleCellStyleBold, title);
            addEmptyGreyCellsForHeader(columnIndex + 1, 3, greyCellStyle);
            createCellData(rowIndex, lastColIndex + 1, cellStyle, StringUtils.EMPTY);

            rowIndex++;

            title = I18N.getString("ksu.valid.date",
                    new DateTimeStringConverter().toString(vehicleConfig.getValidDate()));
            rowIndex = createCellDataWithAddress(rowIndex, columnIndex, greyCellStyle, title, lastColIndex);

            title = I18N.getString("ksu.creationdate",
                    DATE_FORMATTER.format(vehicleConfig.getTimestampCreate().toLocalDateTime()));
            rowIndex = createCellDataWithAddress(rowIndex, columnIndex, greyCellStyle, title, lastColIndex);

            title = I18N.getString("ksu.changedate",
                    DATE_FORMATTER.format(vehicleConfig.getTimestampChange().toLocalDateTime()));
            rowIndex = createCellDataWithAddress(rowIndex, columnIndex, greyCellStyle, title, lastColIndex);

            columnIndex = ++lastColIndex + 1;
            rowIndex -= 4;

            if (vehicleConfigIndex >= 2) {
                sheet.groupColumn(columnIndex, columnIndex + 2);
                cellAddresses = new CellRangeAddress(rowIndex, rowIndex, columnIndex, columnIndex + 3);
                sheet.addMergedRegion(cellAddresses);
                createCellData(rowIndex, columnIndex, titleCellStyle, "Delta");

                addEmptyGreyCellsForHeader(columnIndex + 1, 3, greyCellStyle);

                for (int i = 0; i < 3; i++) {
                    rowIndex++;
                    addEmptyGreyCellsForHeader(columnIndex, 4, greyCellStyle);
                }

                columnIndex += 5;
                rowIndex -= 3;
            }
        }

        rowIndex += 4;
        sheet.groupRow(higherRowIndex + 1, rowIndex - 1);
    }

    @Override
    protected void buildHeader() {
        int columnIndex = 0;

        createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.fg"));
        createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.set"));
        createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.description"));

        int headerCount = 1;
        if (vehicleConfigs.size() == 2) {
            headerCount = vehicleConfigs.size() + 1;
        } else if (vehicleConfigs.size() > 2) {
            headerCount = vehicleConfigs.size() + (vehicleConfigs.size() - 1);
        }

        for (int index = 0; index < headerCount; index++) {
            createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.platform"));
            createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.system"));
            createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.hut"));
            createCellData(rowIndex, columnIndex++, greyCellStyle, getI18NPrefixes("cell.weightall"));

            sheet.setColumnBreak(columnIndex);
            columnIndex++;
        }

        rowIndex++;

        CellStyle headerCellStyle = getHeaderCellStyleBuilder().setCellColor(IndexedColors.GREY_25_PERCENT).build();
        addEmptyGreyCellsForHeader(0, columnIndex - 1, headerCellStyle);

        rowIndex++;
    }

    protected void fillDeltaWeights(Map<ApCompareGroup, Double> lastWeights, Map<ApCompareGroup, Double> weights,
            int columnIndex, CellStyle style) {
        Double lastPlatform = getWeight(lastWeights, ApCompareGroup.PLATFORM);
        Double lastSystem = getWeight(lastWeights, ApCompareGroup.SYSTEM);
        Double lastHut = getWeight(lastWeights, ApCompareGroup.HUT);
        Double lastAll = getWeight(lastWeights, null);

        Double platform = getWeight(weights, ApCompareGroup.PLATFORM);
        Double system = getWeight(weights, ApCompareGroup.SYSTEM);
        Double hut = getWeight(weights, ApCompareGroup.HUT);
        Double all = getWeight(weights, null);

        createCellData(rowIndex, columnIndex++, style, platform - lastPlatform);
        createCellData(rowIndex, columnIndex++, style, system - lastSystem);
        createCellData(rowIndex, columnIndex++, style, hut - lastHut);
        createCellData(rowIndex, columnIndex++, style, all - lastAll);
        createCellData(rowIndex, columnIndex, cellStyle, null);
    }

    protected CellStyleBuilder getDataCellStyleBuilder() {
        return getDefaultCellStyleBuilder().setCellBorder(XSSFCellBorder.BorderSide.TOP, BorderStyle.HAIR)
                .setCellBorder(XSSFCellBorder.BorderSide.LEFT, BorderStyle.HAIR)
                .setCellBorder(XSSFCellBorder.BorderSide.RIGHT, BorderStyle.HAIR)
                .setCellBorder(XSSFCellBorder.BorderSide.BOTTOM, BorderStyle.HAIR);
    }

    private void addEmptyGreyCellsForHeader(int columnIndex, int numberOfCells, CellStyle cellStyle) {
        for (int index = 0; index < numberOfCells; index++) {
            createCellData(rowIndex, columnIndex++, cellStyle, StringUtils.EMPTY);
        }
    }

    private CellStyleBuilder getHeaderCellStyleBuilder() {
        return getDefaultCellStyleBuilder().setCellAlignmentCenter()
                .setCellBorder(XSSFCellBorder.BorderSide.BOTTOM, BorderStyle.MEDIUM)
                .setCellColor(IndexedColors.GREY_25_PERCENT).setFontBold();
    }

    private Double getWeight(Map<ApCompareGroup, Double> lastWeights, ApCompareGroup apCompareGroup) {
        Double lastPlatform = lastWeights.get(apCompareGroup);
        return lastPlatform == null ? 0.0 : lastPlatform;
    }

    private String getI18NPrefixes(String nonPrefixedKey) {
        return I18N.getString(bundleKeyPrefix() + StringConstant.DOT + nonPrefixedKey);
    }
}
