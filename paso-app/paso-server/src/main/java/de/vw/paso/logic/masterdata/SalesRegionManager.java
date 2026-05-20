package de.vw.paso.logic.masterdata;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.repository.masterdata.SalesRegionRepository;
import de.vw.paso.util.DataNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesRegionManager {

    private final SalesRegionRepository salesRegionServiceRepository;

    public SalesRegionManager(SalesRegionRepository salesRegionServiceRepository) {
        this.salesRegionServiceRepository = salesRegionServiceRepository;
    }

    public Collection<SalesRegion> loadSalesRegions() {
        return salesRegionServiceRepository.findAll();
    }

    @Transactional
    public void updateRelevance(Collection<String> salesRegionIds, Integer relevant) {
        salesRegionServiceRepository.updateRelevance(salesRegionIds, relevant);
    }

    @Transactional
    public SalesRegion addRegion(SalesRegion salesRegion) {
        return salesRegionServiceRepository.save(salesRegion);
    }

    @Transactional
    public void deleteRegions(Collection<String> salesRegions) {
        salesRegionServiceRepository.deleteSalesRegionWithIds(salesRegions);
    }

    @Transactional
    public SalesRegion updateRegion(SalesRegion salesRegion, String oldSalesRegionId) {
        SalesRegion regionToUpdate = salesRegionServiceRepository.findById(oldSalesRegionId)
                .orElseThrow(() -> new DataNotFoundException("Sales region to update not found"));

        if (!salesRegion.getId().equals(oldSalesRegionId)) {
            salesRegionServiceRepository.deleteById(oldSalesRegionId);
            salesRegionServiceRepository.save(salesRegion);

            return salesRegion;
        }

        regionToUpdate.setId(salesRegion.getId());
        regionToUpdate.setDescriptionDe(salesRegion.getDescriptionDe());
        regionToUpdate.setDescriptionEn(salesRegion.getDescriptionEn());
        regionToUpdate.setRelevant(salesRegion.getRelevant());
        regionToUpdate.setTimestampChange(salesRegion.getTimestampChange());

        return regionToUpdate;
    }

    public Map<String, Integer> countConstrainIssues(Collection<String> ids) {
        return salesRegionServiceRepository.countConstrainIssues(ids).stream().collect(
                Collectors.toMap(item -> String.valueOf(item.id()), item -> item.sum().intValue(),
                        (first, second) -> second));
    }
}
