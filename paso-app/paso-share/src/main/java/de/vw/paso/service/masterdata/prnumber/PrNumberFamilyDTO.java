package de.vw.paso.service.masterdata.prnumber;

import java.util.ArrayList;
import java.util.Collection;

public record PrNumberFamilyDTO(Long id, String name, String description, Collection<PrNumberDTO> prNumbers) {

    public PrNumberFamilyDTO() {
        this(null, null, null, new ArrayList<>());
    }
}
