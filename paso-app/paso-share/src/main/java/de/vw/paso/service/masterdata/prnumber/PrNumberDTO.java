package de.vw.paso.service.masterdata.prnumber;

import java.util.Date;

public record PrNumberDTO(Long id, Long assignmentId, String name, String description, Integer status, Date startDate,
                          String startKey, Date endDate, String endKey, String additionalName,
                          PrNumberFamilyDTO prNumberFamily) implements PrNumberNameProvider {

    @Override
    public String getName() {
        return name;
    }
}
