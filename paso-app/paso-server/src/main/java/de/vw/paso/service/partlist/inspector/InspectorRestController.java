package de.vw.paso.service.partlist.inspector;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementAggregateMapping;
import de.vw.paso.partlist.domain.inspector.InspectorIgnore;
import de.vw.paso.partlist.domain.inspector.InspectorIgnorePK;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingDTO;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingListDTO;
import de.vw.paso.repository.partlist.EfsElementAggregateMappingRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.InspectorIgnoreRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = InspectorRestService.URL)
public class InspectorRestController implements InspectorRestService {

    private final InspectorIgnoreRepository inspectorIgnoreRepository;
    private final EfsElementAggregateMappingRepository efsElementAggregateMappingRepository;
    private final EfsElementRepository efsElementRepository;

    public InspectorRestController(InspectorIgnoreRepository inspectorIgnoreRepository,
            EfsElementAggregateMappingRepository efsElementAggregateMappingRepository,
            EfsElementRepository efsElementRepository) {
        this.inspectorIgnoreRepository = inspectorIgnoreRepository;
        this.efsElementAggregateMappingRepository = efsElementAggregateMappingRepository;
        this.efsElementRepository = efsElementRepository;
    }

    @Override
    @GetMapping(path = "/{partListId}")
    @Transactional
    public InspectorIgnoresDTO loadIgnoreEntries(@PathVariable Long partListId) {
        List<Long> efsElementsIds = efsElementRepository.findAllByVehiclePartListId(partListId).stream()
                .map(EfsElement::getId).toList();
        List<InspectorIgnore> inspectorIgnores = inspectorIgnoreRepository.findByEfsElementIdIn(efsElementsIds);
        List<InspectorIgnoreDTO> inspectorIgnoreDTOS = inspectorIgnores.stream().map(this::convertToInspectorIgnoreDTO)
                .toList();
        return new InspectorIgnoresDTO(inspectorIgnoreDTOS);
    }

    @Override
    @DeleteMapping(path = DELETE_LIST)
    public void deleteIgnores(@RequestBody InspectorIgnoresDTO toDelete) {
        Collection<InspectorIgnorePK> ids = toDelete.inspectorIgnoredList().stream()
                .map(toUnignore -> new InspectorIgnorePK(toUnignore.type(), toUnignore.efsElementId())).toList();
        inspectorIgnoreRepository.deleteAllById(ids);
    }

    @Override
    @PostMapping(path = SAVE_LIST)
    public void saveIgnoreEntries(@RequestBody InspectorIgnoresDTO ignoreList) {
        Collection<InspectorIgnore> inspectorIgnores = ignoreList.inspectorIgnoredList().stream()
                .map(this::convertToInspectorIgnoreEntity).toList();
        inspectorIgnoreRepository.saveAll(inspectorIgnores);
    }

    @Override
    @GetMapping(path = LOAD_AGGREGATE_MAPPING + "/{vehiclePartListId}")
    public EfsElementAggregateMappingListDTO loadAggregateMapping(@PathVariable Long vehiclePartListId) {
        Collection<EfsElementAggregateMapping> aggregateMappings = efsElementAggregateMappingRepository.findByVehiclePartListId(
                vehiclePartListId);
        List<EfsElementAggregateMappingDTO> efsElementAggregateMappingDTOS = aggregateMappings.stream()
                .map(this::convertToEfsElementAggregateMappingDTO).toList();
        return new EfsElementAggregateMappingListDTO(efsElementAggregateMappingDTOS);
    }

    private InspectorIgnoreDTO convertToInspectorIgnoreDTO(InspectorIgnore inspectorIgnore) {
        return new InspectorIgnoreDTO(inspectorIgnore.getType(), inspectorIgnore.getEfsElementId());
    }

    private InspectorIgnore convertToInspectorIgnoreEntity(InspectorIgnoreDTO inspectorIgnoreDTO) {
        InspectorIgnore entity = new InspectorIgnore();
        entity.setType(inspectorIgnoreDTO.type());
        entity.setEfsElementId(inspectorIgnoreDTO.efsElementId());

        return entity;
    }

    private EfsElementAggregateMappingDTO convertToEfsElementAggregateMappingDTO(
            EfsElementAggregateMapping efsElementAggregateMapping) {
        return new EfsElementAggregateMappingDTO(efsElementAggregateMapping.getEfsElementId(),
                efsElementAggregateMapping.getProductDataId(), efsElementAggregateMapping.getImportDate(),
                efsElementAggregateMapping.getPlsFileLockId());
    }
}
