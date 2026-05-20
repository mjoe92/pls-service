package de.vw.paso.service.masterdata.salesregion;

import java.util.Collection;

public interface SalesRegionRestService {

    String URL = "/api/sales-region";
    String UPDATE_RELEVANCE = "/update-relevance/";
    String UPDATE_SALES_REGION = "/update-sales-region";
    String COUNT_ISSUES = "/count-issues";

    SalesRegionListDTO loadSalesRegions();

    void updateRelevance(Collection<String> salesRegionIds, Integer relevant);

    SalesRegionDTO addSalesRegion(SalesRegionDTO salesRegion);

    SalesRegionDTO updateSalesRegion(SalesRegionUpdateDTO salesRegionUpdateDTO);

    void deleteSalesRegions(Collection<String> regions);

    SalesRegionIssuesDTO countConstrainIssues(Collection<String> ids);
}
