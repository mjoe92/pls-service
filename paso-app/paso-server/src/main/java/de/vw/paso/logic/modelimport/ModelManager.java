package de.vw.paso.logic.modelimport;

import java.util.ArrayList;
import java.util.Collection;

import de.vw.paso.exception.DataNotFoundException;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.model.Model;
import de.vw.paso.model.ModelImport;
import de.vw.paso.repository.masterdata.SalesRegionRepository;
import de.vw.paso.repository.modelimport.ModelImportRepository;
import de.vw.paso.service.masterdata.salesregion.SalesRegionNotRelevantException;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.utility.StringConstant;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModelManager {

    private final ModelImportRepository modelImportRepository;
    private final SalesRegionRepository salesRegionRepository;
    private final UserManager userManager;

    public ModelManager(ModelImportRepository modelImportRepository, SalesRegionRepository salesRegionRepository,
            UserManager userManager) {
        this.modelImportRepository = modelImportRepository;
        this.salesRegionRepository = salesRegionRepository;
        this.userManager = userManager;
    }

    @Transactional
    public Collection<ModelImport> loadModelImports(String key, Integer modelYear, String salesRegionId) {
        Collection<ModelImport> modelImports = modelImportRepository.findAllBySalesKey(key);

        if (modelYear == null && salesRegionId == null) {
            return modelImports;
        }

        Collection<ModelImport> modelImportList = new ArrayList<>();
        for (ModelImport modelImport : modelImports) {
            if (modelImport != null && modelImport.getModelYear().equals(modelYear) && modelImport.getSalesRegion()
                    .getId().matches(salesRegionId)) {
                modelImportList.add(modelImport);
            }
        }

        return modelImportList;
    }

    @Transactional
    public ModelImport importModels(String salesKey, Integer modelYear, String salesRegionId)
            throws SalesRegionNotRelevantException {

        SalesRegion salesRegion = salesRegionRepository.findById(salesRegionId)
                .orElseThrow(() -> new DataNotFoundException("Could not load sales region"));
        ModelImport modelImport = new ModelImport();

        modelImport.setUserCreate(userManager.getCurrentUserId());
        modelImport.setSalesKey(salesKey);
        modelImport.setModelYear(modelYear);
        modelImport.setSalesRegion(salesRegion);
        modelImport.setImportStatus(ImportStatus.REQUESTED);
        modelImport.setChange(userManager.getCurrentUserId());

        if (salesRegion.getRelevant() == 0) {
            throw new SalesRegionNotRelevantException(StringConstant.EMPTY, modelImport);
        }

        return modelImportRepository.save(modelImport);
    }

    // Todo - Consider EntityGraph
    @Transactional
    public Collection<Model> loadModels(Long modelImportId) {
        //     ModelImport modelImport = modelImportRepository.findByIdYearAndRegion(modelImportId, modelYear, salesRegionId).get();
        ModelImport modelImport = modelImportRepository.findById(modelImportId)
                .orElseThrow(() -> new DataNotFoundException("Could not load model import"));

        Hibernate.initialize(modelImport.getModels());

        return modelImport.getModels();
    }
}
