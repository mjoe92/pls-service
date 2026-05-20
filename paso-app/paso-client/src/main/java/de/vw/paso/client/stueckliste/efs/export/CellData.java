package de.vw.paso.client.stueckliste.efs.export;

import org.apache.poi.ss.usermodel.CellStyle;

public record CellData(int rowIndex, int columnIndex, CellStyle style, Object data, String comment,
                       boolean isFormula) { }
