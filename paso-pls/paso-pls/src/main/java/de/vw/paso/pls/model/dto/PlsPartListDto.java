package de.vw.paso.pls.model.dto;

import java.util.Date;
import java.util.List;

import de.vw.paso.pll.model.FilteredOutPart;
import de.vw.paso.pll.model.PlsEfsElement;

public record PlsPartListDto(String productDataId, Date productDataImportDate, PlsEfsElement rootElement,
                             List<FilteredOutPart> filteredOutParts) {

}
