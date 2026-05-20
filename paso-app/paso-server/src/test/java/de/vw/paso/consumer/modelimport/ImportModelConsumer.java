package de.vw.paso.consumer.modelimport;

import static org.mockito.Mockito.mock;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.mapper.ModelImportMapper;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotExistingException;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;
import de.vw.paso.service.modelimport.IImportModelConsumer;
import de.vw.paso.service.modelimport.ModelImportDTO;
import de.vw.paso.service.modelimport.ModelRestService;
import de.vw.paso.service.modelimport.ModelUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportModelConsumer extends AbstractTestConsumer<ModelImport> implements IImportModelConsumer {

    @Autowired
    private ModelRestService service = mock(ModelRestService.class);

    @Override
    public void handle(SalesRegionNotExistingException exception) {
        super.handle(exception, exception.getModelImport());
    }

    @Override
    public void handle(SalesRegionNotRelevantException exception) {
        super.handle(exception, exception.getModelImport());
    }

    @Override
    public void importModels(String salesKey, Integer modelYear, String salesTag) {
        run(() -> convertToEntity(salesKey, modelYear, salesTag));
    }

    private ModelImport convertToEntity(String salesKey, Integer modelYear, String salesTag)
            throws SalesRegionNotExistingException, SalesRegionNotRelevantException {
        ModelUpdateDTO modelUpdateDTO = new ModelUpdateDTO(salesKey, modelYear, salesTag);
        ModelImportDTO updated = service.updateModel(modelUpdateDTO);

        return ModelImportMapper.toEntity(updated);
    }
}
