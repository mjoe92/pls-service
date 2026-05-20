package de.vw.paso.client.stueckliste.efs.export;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

public final class CellStyleBuilder {

  private final XSSFCellStyle cellStyle;
  private final XSSFFont font;

  public CellStyleBuilder(final XSSFWorkbook workbook) {
    cellStyle = workbook.createCellStyle();
    font = workbook.createFont();
  }

  public static XSSFColor asColor(int r, int g, int b) {
    return new XSSFColor(new byte[] { (byte) r, (byte) g, (byte) b });
  }

  public CellStyleBuilder setDataFormat(final short format) {
    cellStyle.setDataFormat(format);

    return this;
  }

  public CellStyleBuilder setWordWrap(final boolean isEnabled) {
    cellStyle.setWrapText(isEnabled);

    return this;
  }

  public CellStyleBuilder setCellColor(final IndexedColors color) {
    cellStyle.setFillForegroundColor(color.getIndex());
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    return this;
  }

  public CellStyleBuilder setCellColor(final XSSFColor color) {
    cellStyle.setFillForegroundColor(color);
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    return this;
  }

  public CellStyleBuilder setCellHorizontalAlignment(final HorizontalAlignment alignment) {
    cellStyle.setAlignment(alignment);

    return this;
  }

  public CellStyleBuilder setCellVerticalAlignment(final VerticalAlignment alignment) {
    cellStyle.setVerticalAlignment(alignment);

    return this;
  }

  public CellStyleBuilder setCellAlignmentCenter() {
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    return this;
  }

  public CellStyleBuilder setCellRotated(final boolean isRotated) {
    if (isRotated) {
      cellStyle.setRotation((short) 90);
    }

    return this;
  }

  public CellStyleBuilder setCellBorder(final XSSFCellBorder.BorderSide side, final BorderStyle style) {
    switch (side) {
      case TOP:
        cellStyle.setBorderTop(style);
        break;

      case LEFT:
        cellStyle.setBorderLeft(style);
        break;

      case RIGHT:
        cellStyle.setBorderRight(style);
        break;

      case BOTTOM:
        cellStyle.setBorderBottom(style);
        break;
    }

    cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
    cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
    cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

    return this;
  }

  public CellStyleBuilder setFontColor(final IndexedColors color) {
    font.setColor(color.getIndex());

    return this;
  }

  public CellStyleBuilder setFontSize(final float size) {
    font.setFontHeight((short) (size * 20));

    return this;
  }

  public CellStyleBuilder setFontType(final FontType type) {
    font.setFontName(type.getFontName());

    return this;
  }

  public CellStyleBuilder setFontBold() {
    font.setBold(true);

    return this;
  }

  public CellStyleBuilder setFontItalic() {
    font.setItalic(true);

    return this;
  }

  public CellStyleBuilder setFontStrikeout() {
    font.setStrikeout(true);

    return this;
  }

  public CellStyleBuilder setFontUnderline(final FontUnderline underline) {
    font.setUnderline(underline.getByteValue());

    return this;
  }

  public CellStyle build() {
    cellStyle.setFont(font);

    return cellStyle;
  }

}
