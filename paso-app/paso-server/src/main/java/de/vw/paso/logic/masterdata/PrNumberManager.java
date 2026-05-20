package de.vw.paso.logic.masterdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.mapper.PrNumberFamilyMapper;
import de.vw.paso.mapper.PrNumberMapper;
import de.vw.paso.pr.PrNumber;
import de.vw.paso.pr.PrNumberAssignment;
import de.vw.paso.pr.PrNumberFamily;
import de.vw.paso.pr.VehicleConfigPrNumberMapping;
import de.vw.paso.repository.masterdata.PrNumberAssignmentRepository;
import de.vw.paso.repository.masterdata.PrNumberRepository;
import de.vw.paso.repository.masterdata.VehicleConfigPrNumberMappingRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberListDTO;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrNumberManager {

    private static final String GERMAN = "de";

    private final PrNumberRepository prNumberRepository;
    private final PrNumberAssignmentRepository prNumberAssignmentRepository;
    private final VehicleConfigPrNumberMappingRepository vehicleConfigPrNumberMappingRepository;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final UserPropertyManager userPropertyManager;

    public PrNumberManager(PrNumberRepository prNumberRepository,
            PrNumberAssignmentRepository prNumberAssignmentRepository,
            VehicleConfigPrNumberMappingRepository vehicleConfigPrNumberMappingRepository,
            VehicleConfigRepository vehicleConfigRepository, UserPropertyManager userPropertyManager) {
        this.prNumberRepository = prNumberRepository;
        this.prNumberAssignmentRepository = prNumberAssignmentRepository;
        this.vehicleConfigPrNumberMappingRepository = vehicleConfigPrNumberMappingRepository;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.userPropertyManager = userPropertyManager;
    }

    @Transactional(readOnly = true)
    public PrNumberListDTO loadAll() {
        boolean isGerman = isCurrentLanguageGerman();
        Collection<PrNumber> prNumbers = prNumberRepository.findAll();

        // todo: ideally this should be, but we have too many assignments -> we ignore them but should be somehow considered
        //
        //        Collection<PrNumberDTO> prNumberDTOS = new ArrayList<>(prNumbers.size());
        //        for (PrNumber prNumber : prNumbers) {
        //            Collection<Long> assignmentIds = prNumberAssignmentRepository.loadAssignmentIdsByPrNumberId(
        //                    prNumber.getId());
        //
        //            for (Long assignmentId : assignmentIds) {
        //                PrNumberDTO prNumberDTO = toPrNumberDTO(prNumber, assignmentId, isGerman);
        //                prNumberDTOS.add(prNumberDTO);
        //            }
        //        }

        Collection<PrNumberDTO> prNumberDTOS = prNumbers.stream()
                .map(prNumber -> toPrNumberDTO(prNumber, null, isGerman)).toList();
        return new PrNumberListDTO(prNumberDTOS);
    }

    @Transactional(readOnly = true)
    public PrNumberListDTO loadPrNumbersForVehicle(Long vehicleConfigId) {
        VehicleConfig config = vehicleConfigRepository.findById(vehicleConfigId).orElseThrow();

        Long projectId = config.getVehicleProject().getId();
        Map<String, PrNumberAssignment> prNumberIdToLatestAssignment = loadLatestAssignmentByPrNumberName(projectId);

        if (!config.isEditable()) {
            Collection<VehicleConfigPrNumberMapping> mappings = vehicleConfigPrNumberMappingRepository.findAllPrNumberIdsByConfigId(
                    config.getId());
            for (VehicleConfigPrNumberMapping mapping : mappings) {
                Optional<PrNumberAssignment> assignment = prNumberAssignmentRepository.findById(
                        mapping.getPrAssignmentId());
                assignment.ifPresent(found -> prNumberIdToLatestAssignment.put(found.getPrNumber().getName(), found));
            }
        }

        return mapPrNumbersWithAssignment(prNumberIdToLatestAssignment.values());
    }

    public Map<String, PrNumberAssignment> loadLatestAssignmentByPrNumberName(Long projectId) {
        Collection<PrNumberAssignment> prAssignments = prNumberAssignmentRepository.loadByVehicleProjectId(projectId);

        return prAssignments.stream().collect(
                Collectors.toMap(assignment -> assignment.getPrNumber().getName(), Function.identity(),
                        this::compareLatestId, () -> new HashMap<>(128)));
    }

    private PrNumberAssignment compareLatestId(PrNumberAssignment first, PrNumberAssignment second) {
        return first.getId() > second.getId() ? first : second;
    }

    private PrNumberListDTO mapPrNumbersWithAssignment(Collection<PrNumberAssignment> PrNumberAssignment) {
        Collection<PrNumberDTO> result = new ArrayList<>(PrNumberAssignment.size());
        if (!PrNumberAssignment.isEmpty()) {
            boolean isGerman = isCurrentLanguageGerman();
            for (PrNumberAssignment assignment : PrNumberAssignment) {
                PrNumber prNumber = assignment.getPrNumber();
                String prNumberName = prNumber.getName();
                String additionalName = assignment.getDescription();

                PrNumberFamilyDTO prNumberFamily = toPrNumberFamilyDTO(prNumber.getPrNumberFamily(), assignment.getId(),
                        isGerman);

                String desc = isGerman ? prNumber.getDescriptionDe() : prNumber.getDescriptionEn();

                Date startDate = DateUtil.toDate(assignment.getStartDate());
                Date endDate = DateUtil.toDate(assignment.getEndDate());
                PrNumberDTO prNumberDTO = PrNumberMapper.toDTO(prNumber.getId(), assignment.getId(), prNumberName, desc,
                        Integer.parseInt(assignment.getStatus()), startDate, assignment.getStartKey(), endDate,
                        assignment.getEndKey(), additionalName, prNumberFamily);

                result.add(prNumberDTO);
            }
        }

        return new PrNumberListDTO(result);
    }

    private boolean isCurrentLanguageGerman() {
        return GERMAN.equalsIgnoreCase(userPropertyManager.getCurrentUserLanguage());
    }

    private PrNumberDTO toPrNumberDTO(PrNumber prNumber, Long assignmentId, boolean isGerman) {
        String desc = isGerman ? prNumber.getDescriptionDe() : prNumber.getDescriptionEn();
        PrNumberFamilyDTO prNumberFamilyDTO = toPrNumberFamilyDTO(prNumber.getPrNumberFamily(), assignmentId, isGerman);

        return PrNumberMapper.toDTO(prNumber.getId(), assignmentId, prNumber.getName(), desc, null, null, null, null,
                null, null, prNumberFamilyDTO);
    }

    private PrNumberFamilyDTO toPrNumberFamilyDTO(PrNumberFamily prNumberFamily, Long assignmentId, boolean isGerman) {
        Collection<PrNumberDTO> prNumbers = prNumberFamily.getPrNumbers().stream()
                .map(prNumber -> PrNumberMapper.toDTO(prNumber, assignmentId, isGerman)).toList();

        String description = isGerman ? prNumberFamily.getDescriptionDe() : prNumberFamily.getDescriptionEn();
        return PrNumberFamilyMapper.toDto(prNumberFamily.getId(), prNumberFamily.getName(), description, prNumbers);
    }
}
