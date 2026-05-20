package de.vw.paso.client.stueckliste.efs.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.util.FileUtil;
import de.vw.paso.client.util.UserProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;

public abstract class AbstractExcelExporter {

    private static final String DEFAULT_FILE_NAME = "PasoExcelExport_" + new Date();
    private static final String HEADER_TEXT = "provided by PASO based on TI-Syncro";
    private static final double COLUMN_WIDTH_MULTIPLIER = 1.0 / 9.0;

    protected static final DateTimeFormatter DATE_AND_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private XSSFWorkbook workbook;
    protected XSSFSheet sheet;

    protected int rowIndex;
    protected int autoFilterRowIndex;
    protected int higherRowIndex;
    protected Collection<CellData> sheetData;
    protected CellStyle confidentialityCellStyle;

    protected AbstractExcelExporter() {
    }

    protected abstract String getFileName();

    protected Collection<CellData> prepareData() {
        rowIndex = 0;

        buildCellStyles();
        buildConfidentialityHeader();
        rowIndex++;
        higherRowIndex = rowIndex + 1;
        buildTitle();
        buildHeader();
        autoFilterRowIndex = rowIndex;
        buildBody();

        return sheetData;
    }

    protected void buildCellStyles() {
        // Can be overridden
    }

    protected void createCellData(int rowIndex, int columnIndex, CellStyle titleCellStyleBold, Object data) {
        CellData cellData = new CellData(rowIndex, columnIndex, titleCellStyleBold, data, null, false);
        sheetData.add(cellData);
    }

    protected int createCellDataWithAddress(int rowIndex, int columnIndex, CellStyle headerCellStyle, Object data,
            int lastColIndex) {
        createCellData(rowIndex, columnIndex, headerCellStyle, data);

        CellRangeAddress cellAddresses = new CellRangeAddress(rowIndex, rowIndex++, columnIndex, lastColIndex);
        sheet.addMergedRegion(cellAddresses);

        return rowIndex;
    }

    protected void buildConfidentialityHeader() {
        int startColumnIndex = 0;
        int endLastColumnIndex = 20;
        CellStyle headerCellStyle = getDefaultCellStyleBuilder().setFontColor(IndexedColors.RED).setFontBold().build();
        String title = I18N.getString("ksu.confidential");
        rowIndex = createCellDataWithAddress(rowIndex, startColumnIndex, headerCellStyle, title, endLastColumnIndex);

        title = I18N.getString("ksu.exporter", UserProperties.getUser().getId(),
                DATE_AND_TIME_FORMATTER.format(LocalDateTime.now()));
        rowIndex = createCellDataWithAddress(rowIndex, startColumnIndex, confidentialityCellStyle, title,
                endLastColumnIndex);

        title = I18N.getString("ksu.class.title");
        rowIndex = createCellDataWithAddress(rowIndex, startColumnIndex, confidentialityCellStyle, title,
                endLastColumnIndex);
    }

    protected abstract void buildTitle();

    protected abstract void buildHeader();

    protected abstract void buildBody();

    public final void export(String sheetName) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            this.workbook = workbook;
            sheet = workbook.createSheet(sheetName);
            confidentialityCellStyle = getDefaultCellStyleBuilder().build();

            applyPrintSetup();
            prepareExport();
            applyStyle();
            writeToFile();
        }
    }

    protected final XSSFWorkbook getWorkbook() {
        return workbook;
    }

    protected void applyPrintSetup() {
        short scale = 75;
        workbook.sheetIterator().forEachRemaining(sheet -> {
            PrintSetup printSetup = sheet.getPrintSetup();

            printSetup.setLandscape(true);
            printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
            printSetup.setScale(scale);
        });
    }

    protected void applyStyle() {
        /* Note that this code block is only an empty stub. */
    }

    protected void addHeader(XSSFSheet sheet) {
        XSSFHeaderFooter header = (XSSFHeaderFooter) sheet.getHeader();
        CTHeaderFooter ctHeaderFooter = header.getHeaderFooter();

        header.setRight(HEADER_TEXT);

        ctHeaderFooter.setAlignWithMargins(false);
    }

    protected final double calculateColumnWidth(double point) {
        return COLUMN_WIDTH_MULTIPLIER * point;
    }

    private void prepareExport() {
        for (CellData cellData : prepareData()) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rowIndex = cellData.rowIndex();
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }

            int columnIndex = cellData.columnIndex();
            XSSFCell cell = row.createCell(columnIndex);
            CellStyle style = cellData.style();
            if (style != null) {
                cell.setCellStyle(style);
            }

            String text = cellData.comment();
            if (text != null) {
                Comment comment = createComment(text, rowIndex, columnIndex);
                cell.setCellComment(comment);
            }

            Object data = cellData.data();
            if (data == null) {
                cell.setCellType(CellType.BLANK);
                continue;
            }

            if (cellData.isFormula()) {
                cell.setCellFormula(data.toString());
                continue;
            }

            switch (data) {
                case String string -> cell.setCellValue(string);
                case Date date -> cell.setCellValue(date);
                case Boolean flag -> cell.setCellValue(flag);
                case Calendar calendar -> cell.setCellValue(calendar);
                case RichTextString richTextString -> cell.setCellValue(richTextString);
                case Integer integerNumber -> {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(integerNumber);
                }
                case Float floatNumber -> {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(floatNumber);
                }
                case Double doubleNumber -> {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(doubleNumber);
                }
                default -> cell.setCellValue(data.toString());
            }
        }
    }

    protected CellStyleBuilder getDefaultCellStyleBuilder() {
        return new CellStyleBuilder(workbook).setFontType(FontType.ARIEL).setFontSize(8.0F);
    }

    private void writeToFile() throws IOException {
        String fileName = getFileName();
        if (StringUtils.isEmpty(fileName)) {
            fileName = DEFAULT_FILE_NAME;
        }

        File destinationFile = FileUtil.openSaveExcelDialog(fileName, null);
        if (destinationFile == null) {
            return;
        }

        try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            workbook.write(outputStream);
        }

        FileUtil.openFileWithAssociatedProgram(destinationFile);
    }

    protected Comment createComment(String text, int col, int row) {
        CreationHelper factory = workbook.getCreationHelper();

        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(col);
        anchor.setCol2(col + 5);
        anchor.setRow1(row);
        anchor.setRow2(row + 15);

        RichTextString richTextString = factory.createRichTextString(text);

        Comment comment = drawing.createCellComment(anchor);
        comment.setString(richTextString);

        return comment;
    }
}
