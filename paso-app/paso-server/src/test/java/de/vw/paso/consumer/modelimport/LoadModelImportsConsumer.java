package de.vw.paso.consumer.modelimport;

import static org.mockito.Mockito.mock;

import java.util.Collection;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.mapper.ModelImportMapper;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.modelimport.ILoadModelImportsConsumer;
import de.vw.paso.service.modelimport.ModelImportListDTO;
import de.vw.paso.service.modelimport.ModelRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadModelImportsConsumer extends AbstractTestConsumer<Collection<ModelImport>>
        implements ILoadModelImportsConsumer {

    @Autowired
    private ModelRestService service = mock(ModelRestService.class);

    @Override
    public void loadModelImports(String salesKey, Integer modelYear, String salesTag) {
        run(() -> loadImports(salesKey, modelYear.toString(), salesTag));
    }

    @Override
    public void loadModelImports(String salesKey) {
        run(() -> loadImports(salesKey, null, null));
    }

    private Collection<ModelImport> loadImports(String salesKey, String modelYear, String salesRegionId) {
        ModelImportListDTO loaded = service.loadModelImports(salesKey, modelYear, salesRegionId);

        return loaded.modelImportDTOList().stream().map(ModelImportMapper::toEntity).toList();
    }
}
