package de.vw.paso.client.stueckliste.efs.export.partlist;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.export.AbstractExcelExporter;
import de.vw.paso.client.stueckliste.efs.export.CellStyleBuilder;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PartListExcelExporter extends AbstractExcelExporter {

    private static final int START_CONFIDENTIAL = 0;
    private static final int END_CONFIDENTIAL = 10;

    private static final int START_HEADER_ROW_INDEX = 6;

    private static final XSSFColor WHITE_COLOR = new XSSFColor(Color.WHITE, null);

    private static final char MARK = 'X';

    private final String title;
    private final Collection<EfsElementDTO> efsElements;
    private final VehiclePartListDTO vehiclePartList;
    private final Map<EfsElementDTO, Integer> elementToLevelMap;

    private final Map<String, XSSFCellStyle> formattedStyleCache;
    private final Map<String, CellStyle> nodeStyleCache;
    private final Map<String, XSSFColor> colorCache;

    private int maxLevel;

    public PartListExcelExporter(String title, Collection<EfsElementDTO> efsElements,
            VehiclePartListDTO vehiclePartList) {
        this.title = title;
        this.efsElements = efsElements;
        this.vehiclePartList = vehiclePartList;
        this.elementToLevelMap = createElementToLevelMap(efsElements);

        sheetData = new ArrayList<>();
        formattedStyleCache = new HashMap<>();
        nodeStyleCache = new HashMap<>();
        colorCache = new HashMap<>();
    }

    @Override
    protected String getFileName() {
        return title;
    }

    @Override
    protected void buildConfidentialityHeader() {
        super.buildConfidentialityHeader();

        String title = I18N.getString("ksu.creationdate",
                DATE_FORMATTER.format(vehiclePartList.getTimestampCreate().toLocalDateTime()));
        rowIndex = createCellDataWithAddress(rowIndex, START_CONFIDENTIAL, confidentialityCellStyle, title,
                END_CONFIDENTIAL);

        title = I18N.getString("ksu.changedate",
                DATE_FORMATTER.format(vehiclePartList.getTimestampChange().toLocalDateTime()));
        rowIndex = createCellDataWithAddress(rowIndex, START_CONFIDENTIAL, confidentialityCellStyle, title,
                END_CONFIDENTIAL);
    }

    @Override
    protected void applyStyle() {
        initializeSheet();
    }

    @Override
    protected void buildTitle() {
    }

    @Override
    protected void buildHeader() {
        int columnIndex = 0;

        CellStyle cellStyle = createHeaderStyle(IndexedColors.YELLOW);

        short headerHeight = 450;
        XSSFRow row = sheet.createRow(START_HEADER_ROW_INDEX);
        row.setHeight(headerHeight);

        int columnWidth = 650;
        for (int i = 0; i <= maxLevel; i++) {
            XSSFCell cell = row.createCell(columnIndex, CellType.STRING);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(i);

            sheet.setColumnWidth(columnIndex++, columnWidth);
        }

        cellStyle = createHeaderStyle(IndexedColors.AQUA);
        for (PartListExcelColumn column : PartListExcelColumn.values()) {
            String message = column.getMessage();

            XSSFCell cell = row.createCell(columnIndex, CellType.STRING);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(message);

            String text = column.getComment();
            if (text != null) {
                Comment comment = createComment(text, columnIndex, rowIndex);
                cell.setCellComment(comment);
            }

            sheet.setColumnWidth(columnIndex++, column.getWidth());
        }
    }

    private CellStyle createHeaderStyle(IndexedColors color) {
        CellStyle cellStyle = getHeaderCellStyleBuilder().build();
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setFillForegroundColor(color.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return cellStyle;
    }

    @Override
    protected void buildBody() {
        rowIndex++;

        for (EfsElementDTO element : efsElements) {
            if (element.getDeleted() == 1) {
                continue;
            }

            for (PartListExcelColumn column : PartListExcelColumn.values()) {
                if (PartListExcelColumn.PARENT_NODE_NAME == column || PartListExcelColumn.NODE_NAME == column) {
                    continue; // we fill these columns when we reach OUTLINE_LEVEL
                }

                int columnIndex = maxLevel + 1 + column.ordinal();
                if (PartListExcelColumn.OUTLINE_LEVEL == column) {
                    createOutlineLevelAndNodeNames(element, columnIndex);
                    continue;
                }

                Object data = column.apply(element);
                if (data == null) {
                    continue;
                }

                String format = column.getFormat();
                XSSFCellStyle cellStyle =
                        format == null ? null : formattedStyleCache.computeIfAbsent(format, this::createCellStyle);

                createCellData(rowIndex, columnIndex, cellStyle, data);
            }

            rowIndex++;
        }
    }

    private XSSFCellStyle createCellStyle(String key) {
        XSSFWorkbook workbook = getWorkbook();

        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(key));
        return style;
    }

    private void createOutlineLevelAndNodeNames(EfsElementDTO element, int columnIndex) {
        int level = elementToLevelMap.get(element);
        createCellData(rowIndex, columnIndex, null, level);
        createCellData(rowIndex, level, null, MARK);

        createColoredCellData(element, level, PartListExcelColumn.PARENT_NODE_NAME, false);
        createColoredCellData(element, level, PartListExcelColumn.NODE_NAME, true);
    }

    private Map<EfsElementDTO, Integer> createElementToLevelMap(Collection<EfsElementDTO> efsElements) {
        Map<EfsElementDTO, Integer> result = new HashMap<>(efsElements.size());
        for (EfsElementDTO element : efsElements) {
            Integer level = (Integer) PartListExcelColumn.OUTLINE_LEVEL.apply(element);
            if (maxLevel < level) {
                maxLevel = level;
            }

            result.put(element, level);
        }

        return result;
    }

    private void createColoredCellData(EfsElementDTO element, int level, PartListExcelColumn column, boolean isChild) {
        String colorKey = level + StringConstant.COLON + isChild + StringConstant.COLON + element.isLeaf();
        XSSFColor color = colorCache.computeIfAbsent(colorKey,
                key -> createNodeCellColor(level, isChild, element.isLeaf()));

        String styleKey = column.name() + StringConstant.COLON + colorKey;
        CellStyle cellStyle = nodeStyleCache.computeIfAbsent(styleKey, key -> {
            XSSFCellStyle style = getWorkbook().createCellStyle();
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return style;
        });

        createCellData(rowIndex, maxLevel + column.ordinal() + 1, cellStyle, column.apply(element));
    }

    private void initializeSheet() {
        sheet.setAutoFilter(new CellRangeAddress(autoFilterRowIndex, sheet.getLastRowNum() + 1, 0,
                maxLevel + PartListExcelColumn.values().length));
        sheet.createFreezePane(0, autoFilterRowIndex + 1);
        sheet.setMargin(PageMargin.TOP, 1.0D);
        sheet.setMargin(PageMargin.LEFT, 0.0D);
        sheet.setMargin(PageMargin.RIGHT, 0.0D);
        sheet.setMargin(PageMargin.BOTTOM, 0.75D);
        sheet.setHorizontallyCenter(true);

        addHeader(sheet);
    }

    private CellStyleBuilder getHeaderCellStyleBuilder() {
        return new CellStyleBuilder(getWorkbook()).setCellAlignmentCenter().setFontBold();
    }

    private XSSFColor createNodeCellColor(int level, boolean isChildNodeColumn, boolean isLeaf) {
        if (isChildNodeColumn) {
            if (isLeaf) {
                return WHITE_COLOR;
            }

            level++;
        }

        float hue = ((float) level) / (maxLevel + 1);
        float saturation = 0.6f;
        float brightness = 0.7f;

        Color color = Color.getHSBColor(hue, saturation, brightness);
        return new XSSFColor(color, null);
    }
}
