package de.vw.paso.service.modelimport;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.logic.modelimport.ModelManager;
import de.vw.paso.mapper.ModelImportMapper;
import de.vw.paso.mapper.ModelMapper;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;
import de.vw.paso.utility.StringConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ModelRestService.URL)
public class ModelRestController implements ModelRestService {

    private final ModelManager modelManager;

    public ModelRestController(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    @Override
    @GetMapping
    public ModelImportListDTO loadModelImports(@RequestParam String salesKey, @RequestParam String modelYear,
            @RequestParam String salesRegionId) {
        Integer year = !modelYear.equals(StringConstant.EMPTY) ? Integer.parseInt(modelYear) : null;
        String salesRegId = !salesRegionId.equals(StringConstant.EMPTY) ? salesRegionId : null;
        List<ModelImportDTO> modelImportDTOS = modelManager.loadModelImports(salesKey, year, salesRegId).stream()
                .map(ModelImportMapper::toDTO).toList();

        return new ModelImportListDTO(modelImportDTOS);
    }

    @Override
    @PutMapping
    public ModelImportDTO updateModel(@RequestBody ModelUpdateDTO modelUpdateDTO)
            throws SalesRegionNotRelevantException {
        ModelImport modelImport = modelManager.importModels(modelUpdateDTO.salesKey(), modelUpdateDTO.modelYear(),
                modelUpdateDTO.salesRegionId());

        return ModelImportMapper.toDTO(modelImport);
    }

    @Override
    @GetMapping(path = "/{modelImportId}")
    public ModelSetDTO loadModels(@PathVariable Long modelImportId) {
        Set<ModelDTO> modelDTOS = modelManager.loadModels(modelImportId).stream().map(ModelMapper::toDto)
                .collect(Collectors.toSet());
        return new ModelSetDTO(modelDTOS);
    }
}
