package de.vw.paso.consumer.modelimport;

import static org.mockito.Mockito.mock;

import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.mapper.ModelMapper;
import de.vw.paso.model.Model;
import de.vw.paso.service.modelimport.ILoadModelsConsumer;
import de.vw.paso.service.modelimport.ModelRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadModelsConsumer extends AbstractTestConsumer<Set<Model>> implements ILoadModelsConsumer {

    @Autowired
    private ModelRestService service = mock(ModelRestService.class);

    @Override
    public void loadModels(Long modelImportId) {
        run(() -> service.loadModels(modelImportId).modelDTOSet().stream().map(ModelMapper::toEntity)
                .collect(Collectors.toSet()));
    }

}
