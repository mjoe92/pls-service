package de.vw.paso.services.modelimport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Set;

import de.vw.paso.consumer.modelimport.ImportModelConsumer;
import de.vw.paso.consumer.modelimport.LoadModelImportsConsumer;
import de.vw.paso.consumer.modelimport.LoadModelsConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.exception.DataNotFoundException;
import de.vw.paso.model.Model;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;
import de.vw.paso.status.ImportStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ModelImportTests extends AbstractServiceTests {

    @Autowired
    private LoadModelImportsConsumer loadModelImportsConsumer;
    @Autowired
    private ImportModelConsumer importModelConsumer;
    @Autowired
    private LoadModelsConsumer loadModelsConsumer;

    @Test
    public void loadingNoModelImportsThatAreNotExisting() {
        loadModelImportsConsumer.loadModelImports("AA", 2080, "A1A");

        assertEquals(0, loadModelImportsConsumer.getResult().size());
    }

    @Test
    public void loadingNoModelImportsThatAreNotRelevant() {
        loadModelImportsConsumer.loadModelImports("5G", 2018, "X99");

        assertEquals(0, loadModelImportsConsumer.getResult().size());
    }

    @Test
    public void loadingModelImportsThatAreExisting() {
        loadModelImportsConsumer.loadModelImports("5T", 2015, "X0A");

        assertEquals(1, loadModelImportsConsumer.getResult().size());
    }

    @Test
    public void loadingMultipleModelImportsThatAreExisting() {
        loadModelImportsConsumer.loadModelImports("5G", 2014, "X9X");

        assertEquals(2, loadModelImportsConsumer.getResult().size());
    }

    @Test
    public void importModelThatIsExisting() {
        importModelConsumer.importModels("AA", 2080, "X0A");

        ModelImport modelImport = importModelConsumer.getResult();
        assertEquals("AA", modelImport.getSalesKey());
        assertEquals(Integer.valueOf(2080), modelImport.getModelYear());
        assertEquals("X0A", modelImport.getSalesRegion().getId());
        modelImport.setChange("test");
        validateDefaultInitialValues(modelImport);
        assertSame(ImportStatus.REQUESTED, modelImport.getImportStatus());
    }

    @Test
    public void notImportingModelThatIsNotExisting() {
        importModelConsumer.importModels("AA", 2080, "A1A");
        importModelConsumer.getResult(DataNotFoundException.class);
    }

    @Test
    public void notImportingModelThatIsNotRelevant() {
        importModelConsumer.importModels("AA", 2080, "X8H");

        ModelImport modelImport = importModelConsumer.getResult(SalesRegionNotRelevantException.class);
        assertEquals("AA", modelImport.getSalesKey());
        assertEquals(Integer.valueOf(2080), modelImport.getModelYear());
        assertEquals("X8H", modelImport.getSalesRegion().getId());
        assertEquals(Integer.valueOf(0), modelImport.getSalesRegion().getRelevant());
    }

    @Test
    public void loadingModelsFromImport() {
        loadModelImportsConsumer.loadModelImports("5G", 2014, "X9X");
        ModelImport modelImport = loadModelImportsConsumer.getResult().iterator().next();

        loadModelsConsumer.loadModels(modelImport.getId());

        Set<Model> models = loadModelsConsumer.getResult();
        assertEquals(91, models.size());
    }

}
