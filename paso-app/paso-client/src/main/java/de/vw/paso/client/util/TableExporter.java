package de.vw.paso.client.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import de.vw.paso.client.stueckliste.efs.export.CellStyleBuilder;
import de.vw.paso.service.user.UserDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class TableExporter {

  private TableExporter() {
    throw new UnsupportedOperationException("Util class");
  }

  public static void export(TreeTableView<?> tableView, OutputStream out, TableExporterConfig config)
    throws IOException {
    try (XSSFWorkbook wb = new XSSFWorkbook()) {
      XSSFSheet sheet = wb.createSheet("Export");
      sheet.setRowSumsBelow(false);
      List<TreeTableColumn<?, ?>> columns = new ArrayList<>(
        tableView.getColumns().filtered(TableColumnBase::isVisible));
      int nextRow = 0;
      nextRow = writeHeader(sheet, columns, nextRow);
      nextRow = writeColumns(columns, sheet, nextRow);
      sheet.createFreezePane(0, nextRow);
      writeData(tableView, sheet, nextRow, config);

      for (int i = 0; i < columns.size(); i++) {
        sheet.autoSizeColumn(i);
        sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1200); //Random Excel unit. Found by try and error
      }

      wb.write(out);
    }
  }

  private static int writeColumns(List<? extends TreeTableColumn<?, ?>> columns, XSSFSheet sheet, int nextRow) {
    XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
    cellStyle.setFillForegroundColor(CellStyleBuilder.asColor(224, 233, 183));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setBorderTop(BorderStyle.THIN);
    cellStyle.setTopBorderColor(CellStyleBuilder.asColor(0, 0, 0));
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setRightBorderColor(CellStyleBuilder.asColor(0, 0, 0));
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBottomBorderColor(CellStyleBuilder.asColor(0, 0, 0));
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setLeftBorderColor(CellStyleBuilder.asColor(0, 0, 0));
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    XSSFRow row = sheet.createRow(nextRow);
    for (int i = 0; i < columns.size(); i++) {
      TreeTableColumn<?, ?> tableColumn = columns.get(i);
      XSSFCell cell = row.createCell(i);
      Object title = tableColumn.getText();
      cell.setCellStyle(cellStyle);
      cell.setCellValue(title != null ? title.toString() : "");
    }
    sheet.setAutoFilter(new CellRangeAddress(nextRow, nextRow, 0, columns.size() - 1));
    return ++nextRow;
  }

  private static <S> void writeData(TreeTableView<S> tableView, XSSFSheet sheet, int nextRow,
    TableExporterConfig config) {
    TreeItem<S> root = tableView.getRoot();
    ObservableList<TreeItem<S>> children = root.getChildren();
    writeRows(tableView, children, sheet, nextRow, 0, config);
  }

  private static int writeHeader(XSSFSheet sheet, List<? extends TreeTableColumn<?, ?>> columns, int nextRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    XSSFRow row = sheet.createRow(nextRow);
    XSSFCell cell = row.createCell(0);
    XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
    cellStyle.setFillForegroundColor(CellStyleBuilder.asColor(169, 195, 59));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    XSSFFont font = sheet.getWorkbook().createFont();
    font.setBold(true);
    cellStyle.setFont(font);
    cell.setCellStyle(cellStyle);
    UserDTO user = UserProperties.getUser();
    cell.setCellValue("Vertraulich: Erzeugt aus PASO am " + sdf.format(new Date()) + " von " + user.getLastName() + ", "
      + user.getFirstName() + " (" + user.getId() + ")");
    sheet.addMergedRegion(new CellRangeAddress(nextRow, nextRow, 0, columns.size() - 1));
    nextRow += 2;
    return nextRow;
  }

  private static <S> int writeRow(TreeItem<S> item, TreeTableView<S> treeTableView, XSSFSheet sheet, int nextRow,
    TableExporterConfig config) {
    ObservableList<? extends TreeTableColumn<S, ?>> columns = treeTableView.getColumns();
    XSSFRow row = sheet.createRow(nextRow++);
    for (int i = 0; i < columns.size(); i++) {
      TreeTableColumn<S, ?> column = columns.get(i);
      if (column.isVisible()) {
        Object cellValue;
        if (config.repeatingCellChecker.useParentValue(item, column)) {
          cellValue = column.getCellData(item.getParent());
        } else {
          cellValue = column.getCellData(item);
        }
        if (cellValue != null) {
          XSSFCell cell = row.createCell(i);
          cell.setCellValue(cellValue.toString());
        }
      }
    }
    return nextRow;
  }

  private static <S> int writeRows(TreeTableView<S> tableView, ObservableList<TreeItem<S>> children, XSSFSheet sheet,
    int nextRow, int treeLevel, TableExporterConfig config) {
    for (TreeItem<S> item : children) {
      nextRow = writeRow(item, tableView, sheet, nextRow, config);
      if (!item.getChildren().isEmpty()) {
        int startGroup = nextRow;
        nextRow = writeRows(tableView, item.getChildren(), sheet, nextRow, treeLevel++, config);
        sheet.groupRow(startGroup, nextRow);
        sheet.setRowGroupCollapsed(startGroup, !item.isExpanded());
      }
    }
    return nextRow;
  }

  public static class TableExporterConfig {

    private RepeatingCellChecker repeatingCellChecker;

    public TableExporterConfig(RepeatingCellChecker repeatingCellChecker) {
      this.repeatingCellChecker = repeatingCellChecker;
    }

    public static TableExporterConfig createDefault() {
      return new TableExporterConfig((treeItem, column) -> false);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof TableExporterConfig that)) {
        return false;
      }
      return Objects.equals(repeatingCellChecker, that.repeatingCellChecker);
    }

    public RepeatingCellChecker getRepeatingCellChecker() {
      return repeatingCellChecker;
    }

    @Override
    public int hashCode() {
      return Objects.hash(repeatingCellChecker);
    }

    public void setRepeatingCellChecker(RepeatingCellChecker repeatingCellChecker) {
      this.repeatingCellChecker = repeatingCellChecker;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", TableExporterConfig.class.getSimpleName() + "[", "]").add(
        "repeatingCellChecker=" + repeatingCellChecker).toString();
    }
  }

  public interface RepeatingCellChecker {

    boolean useParentValue(TreeItem<?> treeItem, TreeTableColumn<?, ?> column);
  }
}
