package de.vw.paso.logic.pls;

import java.util.Date;
import java.util.List;

public record PlsPartList(String productDataId, Date productDataImportDate, PlsEfsElement rootElement,
                          List<FilteredOutPart> filteredOutParts) {

}
