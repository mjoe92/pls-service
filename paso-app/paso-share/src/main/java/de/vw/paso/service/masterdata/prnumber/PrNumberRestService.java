package de.vw.paso.service.masterdata.prnumber;

public interface PrNumberRestService {

    String URL = "/api/pr-number";

    PrNumberListDTO loadPrNumbersForConfig(Long configId);

    PrNumberListDTO loadAll();
}
