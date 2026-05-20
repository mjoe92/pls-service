package de.vw.paso.service.masterdata;

import java.util.Collection;

import de.vw.paso.logic.masterdata.SalesRegionManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.SalesRegionMapper;
import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionIssuesDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionListDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionRestService;
import de.vw.paso.service.masterdata.salesregion.SalesRegionUpdateDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SalesRegionRestService.URL)
public class SalesRegionRestController implements SalesRegionRestService {

    private final SalesRegionManager salesRegionManager;
    private final UserManager userManager;

    public SalesRegionRestController(SalesRegionManager salesRegionManager, UserManager userManager) {
        this.salesRegionManager = salesRegionManager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public SalesRegionListDTO loadSalesRegions() {
        return new SalesRegionListDTO(
                salesRegionManager.loadSalesRegions().stream().map(SalesRegionMapper::toDTO).toList());
    }

    @Override
    @PutMapping(path = SalesRegionRestService.UPDATE_RELEVANCE + "{relevant}")
    public void updateRelevance(@RequestBody Collection<String> salesRegionIds, @PathVariable Integer relevant) {
        userManager.requireAdminUser();

        salesRegionManager.updateRelevance(salesRegionIds, relevant);
    }

    @Override
    @PostMapping
    public SalesRegionDTO addSalesRegion(@RequestBody SalesRegionDTO salesRegion) {
        userManager.requireAdminUser();

        return SalesRegionMapper.toDTO(salesRegionManager.addRegion(toEntity(salesRegion)));
    }

    @Override
    @PutMapping(path = SalesRegionRestService.UPDATE_SALES_REGION)
    public SalesRegionDTO updateSalesRegion(@RequestBody SalesRegionUpdateDTO salesRegionUpdateDTO) {
        userManager.requireAdminUser();

        SalesRegion salesRegion = salesRegionManager.updateRegion(toEntity(salesRegionUpdateDTO.salesRegion()),
                salesRegionUpdateDTO.oldSalesRegion());
        return SalesRegionMapper.toDTO(salesRegion);
    }

    @Override
    @DeleteMapping
    public void deleteSalesRegions(@RequestParam Collection<String> regions) {
        userManager.requireAdminUser();

        salesRegionManager.deleteRegions(regions);
    }

    @Override
    @GetMapping(path = SalesRegionRestService.COUNT_ISSUES)
    public SalesRegionIssuesDTO countConstrainIssues(@RequestParam Collection<String> ids) {
        return new SalesRegionIssuesDTO(salesRegionManager.countConstrainIssues(ids));
    }

    private SalesRegion toEntity(SalesRegionDTO salesRegionDTO) {
        SalesRegion salesRegion = SalesRegionMapper.toEntity(salesRegionDTO);
        salesRegion.setChange(userManager.getCurrentUserId());

        return salesRegion;
    }
}
