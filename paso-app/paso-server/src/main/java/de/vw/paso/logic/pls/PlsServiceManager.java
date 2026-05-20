package de.vw.paso.logic.pls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vw.paso.exception.PlsServiceException;
import de.vw.paso.logic.masterdata.PrNumberManager;
import de.vw.paso.logic.partlist.EfsElementManager;
import de.vw.paso.logic.partlist.EfsWeightManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.mapper.FilteredOutEfsElementMapper;
import de.vw.paso.mapper.VehicleConfigMapper;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementAggregateMapping;
import de.vw.paso.partlist.domain.EfsElementImport;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.FilteredOutEfsElement;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.partlist.domain.smartfix.SmartFix;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingDTO;
import de.vw.paso.pls.EfsAggregateInformationDTO;
import de.vw.paso.pls.PartListStatus;
import de.vw.paso.pls.ProductDataDTO;
import de.vw.paso.pls.Status;
import de.vw.paso.pls.TiWhRequestQueueDTO;
import de.vw.paso.pls.TiWhRequestQueueResponse;
import de.vw.paso.pr.PrNumberAssignment;
import de.vw.paso.pr.VehicleConfigPrNumberMapping;
import de.vw.paso.repository.masterdata.VehicleConfigPrNumberMappingRepository;
import de.vw.paso.repository.partlist.EfsElementAggregateMappingRepository;
import de.vw.paso.repository.partlist.EfsElementImportRepository;
import de.vw.paso.repository.partlist.EfsElementMaraRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.FilteredOutEfsElementRepository;
import de.vw.paso.repository.partlist.SmartFixRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.pls.CreateSubPartListDTO;
import de.vw.paso.service.pls.ImportedEfsElementsDTO;
import de.vw.paso.service.pls.PlsRequestResultDTO;
import de.vw.paso.service.pls.ProductDataSearchResponse;
import de.vw.paso.service.pls.StatusResponse;
import de.vw.paso.service.pls.SubPartListRequestDTO;
import de.vw.paso.service.pls.TiWhRequestQueueListDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.util.EfsElementEntityUtil;
import de.vw.paso.util.SmartFixEntityUtil;
import de.vw.paso.utility.DateUtil;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.dto.PartListRequestDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PlsServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(PlsServiceManager.class);

    public static final String PRODUCT_ID = "productId";
    public static final String REQUESTER = "requester";
    public static final String PRODUCT_DATA_ID = "productDataId";

    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT_DATE);

    private final EfsElementImportRepository efsElementImportRepository;
    private final EfsElementMaraRepository efsElementMaraRepository;
    private final EfsElementManager efsElementManager;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final VehiclePartListRepository vehiclePartListRepository;
    private final EfsElementRepository efsElementRepository;
    private final EfsElementAggregateMappingRepository efsElementAggregateMappingRepository;
    private final FilteredOutEfsElementRepository filteredOutEfsElementRepository;
    private final UserManager userManager;
    private final EfsWeightManager efsWeightManager;
    private final RestTemplate restTemplate;
    private final SmartFixRepository smartFixRepository;
    private final VehicleConfigPrNumberMappingRepository vehicleConfigPrNumberMappingRepository;
    private final PrNumberManager prNumberManager;

    @Value("${partlist-service.url}")
    private String plsUrl;

    public PlsServiceManager(EfsElementRepository efsElementRepository,
            EfsElementAggregateMappingRepository efsElementAggregateMappingRepository,
            EfsElementImportRepository efsElementImportRepository, EfsElementMaraRepository efsElementMaraRepository,
            FilteredOutEfsElementRepository filteredOutEfsElementRepository, EfsElementManager efsElementManager,
            EfsWeightManager efsWeightManager, VehicleConfigRepository vehicleConfigRepository,
            VehiclePartListRepository vehiclePartListRepository, UserManager userManager, RestTemplate restTemplate,
            SmartFixRepository smartFixRepository,
            VehicleConfigPrNumberMappingRepository vehicleConfigPrNumberMappingRepository,
            PrNumberManager prNumberManager) {
        this.efsElementAggregateMappingRepository = efsElementAggregateMappingRepository;
        this.efsElementImportRepository = efsElementImportRepository;
        this.efsElementMaraRepository = efsElementMaraRepository;
        this.filteredOutEfsElementRepository = filteredOutEfsElementRepository;
        this.efsElementManager = efsElementManager;
        this.efsWeightManager = efsWeightManager;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.vehiclePartListRepository = vehiclePartListRepository;
        this.userManager = userManager;
        this.efsElementRepository = efsElementRepository;
        this.restTemplate = restTemplate;
        this.smartFixRepository = smartFixRepository;
        this.vehicleConfigPrNumberMappingRepository = vehicleConfigPrNumberMappingRepository;
        this.prNumberManager = prNumberManager;
    }

    @Transactional
    public void createPartList(VehicleConfig vehicleConfig) throws PlsServiceException {
        VehiclePartList vehiclePartList = vehicleConfig.getVehiclePartList();
        if (vehiclePartList != null) {
            throw new PlsServiceException("VehicleConfig already contains a vehicle part list");
        }

        PlsPartList partList = getPartListFromPls(vehicleConfig.getValidDate(), vehicleConfig.getPrNumberString(),
                vehicleConfig.getPlsProductDataId());
        if (partList == null) {
            throw new RuntimeException("No response from part list service");
        }

        String fileLockId = addFileLockToProductData(vehicleConfig.getPlsProductDataId());
        importPartList(vehicleConfig, partList, fileLockId);

        //todo: this doesn't make any sense -> remove?
        if (vehicleConfig.isSmartFixesActive()) {
            Collection<SmartFix> fixes = smartFixRepository.findByActiveTrue();
            Collection<EfsElement> elements = efsElementRepository.findAllByVehiclePartListId(vehiclePartList.getId());
            Map<EfsElement, EfsElement> changedEntities = SmartFixEntityUtil.apply(elements, fixes);

            for (EfsElement changedEntity : changedEntities.values()) {
                efsElementManager.saveEfsElement(changedEntity);
            }
        }

        saveStatusAndPosition(vehicleConfig, Status.COMPLETE, null);
    }

    @Transactional
    public ImportedEfsElementsDTO createSubPartList(CreateSubPartListDTO createSubPartListDTO) {
        PlsPartList partList = getPartListFromPls(createSubPartListDTO.date(), createSubPartListDTO.prNumbers(),
                createSubPartListDTO.productDataId());
        if (partList == null) {
            throw new RuntimeException("No response sub part list from part list service");
        }

        String fileLockId = addFileLockToProductData(createSubPartListDTO.productDataId());

        EfsElementImport element = efsElementImportRepository.findById(createSubPartListDTO.efsElementId())
                .orElseThrow();
        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(element.getVehiclePartListId())
                .orElseThrow();

        EfsElementImport result = importSubPartList(element, vehiclePartList, partList, fileLockId);
        EfsElement resultEfsElement = EfsElementEntityUtil.convertToEfsElement(result);

        return new ImportedEfsElementsDTO(Collections.singletonList(EfsElementMapper.toDto(resultEfsElement)));
    }

    @Transactional
    public PlsRequestResultDTO requestPartList(PartListRequestDTO partListRequest) {
        VehicleConfig vehicleConfig = vehicleConfigRepository.findById(partListRequest.vehicleConfigId()).orElseThrow();
        if (vehicleConfig.getPlsProductDataId() != null) {
            throw new RuntimeException("Part list already requested");
        }

        StatusResponse response = requestPartList(partListRequest.productId());
        if (response == null) {
            throw new RuntimeException("No response body from part list service");
        }

        String productDataId = response.productDataId();
        vehicleConfig.setPlsProductDataId(productDataId);

        if (PartListStatus.PENDING == response.status()) {
            String productKey = vehicleConfig.getVehicleProject().getProductKey();
            Map<String, Integer> requestQueue = getTiWhQueuePositions();
            Integer requestPosition = requestQueue.get(productKey);

            saveStatusAndPosition(vehicleConfig, Status.ofPartList(response.status()), requestPosition);
            vehicleConfigRepository.save(vehicleConfig);
        }

        VehicleConfigDTO vehicleConfigDto = VehicleConfigMapper.toDto(vehicleConfig, false);
        return new PlsRequestResultDTO(response.status(), vehicleConfigDto);
    }

    public boolean checkPlsAvailable() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl);
        String urlStr = uriBuilder.toUriString();
        try {
            LOG.info("Check PLS url: {}", urlStr);
            ResponseEntity<PlsStatus> response = restTemplate.getForEntity(urlStr, PlsStatus.class);
            LOG.info("Response code: {}, PLS status: {}", response.getStatusCode(),
                    response.getBody() == null ? "Unknown PLS status" : response.getBody().getStatus());
            return true;
        } catch (Exception e) {
            LOG.debug("Got exception while checking pls", e);
            return false;
        }
    }

    public EfsElementImport createEfsElementForFilteredOutPart(PlsEfsElement element, Long vehiclePartListId) {
        Map<String, EfsElementMara> partNumberToMaraMap = new HashMap<>();

        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(vehiclePartListId).orElseThrow();
        return EfsElementMapper.toEntity(null, element, vehiclePartList, partNumberToMaraMap);
    }

    @Transactional
    public VehicleConfigDTO createPartList(Long configId) throws PlsServiceException {
        VehicleConfig config = vehicleConfigRepository.findById(configId).orElseThrow();

        createPartList(config);
        savePrNumberMapping(configId, config.getVehicleProject().getId());

        Map<Long, Boolean> access = userManager.getVehicleConfigIdsWithAccess();

        return VehicleConfigMapper.toDto(config, access.getOrDefault(configId, false));
    }

    public Collection<ProductDataDTO> getAvailableProductData(Collection<String> productIds) {
        Collection<ProductDataDTO> resultList = new ArrayList<>();

        for (String productId : productIds) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl + "/productData/search");
            uriBuilder.queryParam(PRODUCT_ID, productId);
            ResponseEntity<Collection<ProductDataSearchResponse>> response = restTemplate.exchange(
                    uriBuilder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() { });
            Collection<ProductDataSearchResponse> result = response.getBody();
            if (result == null) {
                throw new RuntimeException("No response body from part list service");
            }

            for (ProductDataSearchResponse resp : result) {
                resultList.add(toDTO(resp));
            }
        }
        return resultList;
    }

    public Collection<StatusResponse> getStatusForProductData(Collection<String> plsProductDataIds) {
        if (plsProductDataIds == null || plsProductDataIds.isEmpty()) {
            return List.of();
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl + "/partList/importStatus");
        for (String id : plsProductDataIds) {
            uriBuilder.queryParam("productDataIds", id);
        }

        ResponseEntity<Collection<StatusResponse>> request = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() { });

        Collection<StatusResponse> response = request.getBody();
        if (response == null) {
            throw new RuntimeException("Got no response body from part list service");
        }

        return response;
    }

    public Map<String, Integer> getTiWhQueuePositions() {
        Map<String, Integer> result = new HashMap<>();
        Collection<TiWhRequestQueueResponse> tiWhRequestQueue = getTiWhRequestQueueFromPls();

        int queuePosition = 0;
        for (TiWhRequestQueueResponse tiWhRequestQueueDTO : tiWhRequestQueue) {
            queuePosition++;
            result.put(tiWhRequestQueueDTO.productId(), queuePosition);
        }
        return result;
    }

    public TiWhRequestQueueListDTO getTiWhRequestQueue() {
        Collection<TiWhRequestQueueResponse> response = getTiWhRequestQueueFromPls();

        if (LOG.isInfoEnabled()) {
            LOG.info("TiWH request queue list = {}", response.stream().flatMap(t -> t.requesterIds().stream())
                    .collect(Collectors.joining(StringConstant.COMMA_SPACE)));
        }

        Collection<TiWhRequestQueueDTO> queues = response.stream()
                .map(e -> new TiWhRequestQueueDTO(e.productId(), DateUtil.convertToDate(e.requestSequence()),
                        e.requesterIds(), e.requested(), e.processing())).toList();
        return new TiWhRequestQueueListDTO(queues);
    }

    public void saveStatusAndPosition(VehicleConfig vehicleConfig, Status status, Integer position) {
        vehicleConfig.setStatus(status);
        vehicleConfig.setRequestPosition(position);
        vehicleConfigRepository.save(vehicleConfig);
    }

    public EfsAggregateInformationDTO getAggregateInformation(Collection<String> efsElementIds,
            Collection<String> productIds) {
        Collection<ProductDataDTO> availableProductData = getAvailableProductData(productIds);
        Map<String, Integer> requestQueue = getTiWhQueuePositions();

        Collection<EfsElementAggregateMappingDTO> mappingDTOs = efsElementAggregateMappingRepository.findAllById(
                efsElementIds.stream().map(Long::parseLong).toList()).stream().map(this::toDTO).toList();
        return new EfsAggregateInformationDTO(availableProductData, requestQueue, mappingDTOs);
    }

    public ProductDataDTO requestPartList(SubPartListRequestDTO request) {
        EfsElementAggregateMapping mapping = efsElementAggregateMappingRepository.findById(request.efsElementId())
                .orElse(null);
        if (mapping != null && mapping.getImportDate() != null) {
            throw new RuntimeException("Sub part list already requested");
        }

        StatusResponse response = requestPartList(request.productId());
        if (response == null) {
            throw new RuntimeException("No response body from part list service");
        }

        if (mapping == null) {
            mapping = new EfsElementAggregateMapping();
            mapping.setEfsElementId(request.efsElementId());
        }

        String productDataId = response.productDataId();
        mapping.setProductDataId(productDataId);
        mapping.setImportDate(new Date());
        efsElementAggregateMappingRepository.save(mapping);

        ProductDataDTO productDataDTO = new ProductDataDTO();
        productDataDTO.setId(productDataId);
        productDataDTO.setImportDate(new Date());
        productDataDTO.setStatus(PartListStatus.PENDING);
        productDataDTO.setProductId(request.productId());

        return productDataDTO;
    }

    private void savePrNumberMapping(Long configId, Long vehicleProjectId) {
        Map<String, PrNumberAssignment> prNumberIdToLatestAssignment = prNumberManager.loadLatestAssignmentByPrNumberName(
                vehicleProjectId);
        for (PrNumberAssignment assignment : prNumberIdToLatestAssignment.values()) {
            Long assignmentId = assignment.getId();
            VehicleConfigPrNumberMapping toSave = new VehicleConfigPrNumberMapping();
            toSave.setId(configId, assignmentId);

            vehicleConfigPrNumberMappingRepository.save(toSave);
        }
    }

    private String addFileLockToProductData(String productDataId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
                plsUrl + "/productData/{productDataId}/fileLock");

        Map<String, Object> pathVariables = new HashMap<>();

        pathVariables.put(PRODUCT_DATA_ID, productDataId);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                uriBuilder.buildAndExpand(pathVariables).toUriString(), null, String.class);

        return responseEntity.getBody();
    }

    private EfsElementImport createAndSaveElements(EfsElementImport parentElement, PlsEfsElement rootElement,
            VehiclePartList vehiclePartList) {
        Map<String, EfsElementMara> partNumberToMaraMap = new HashMap<>();

        EfsElementImport efsElement = EfsElementMapper.toEntity(parentElement, rootElement, vehiclePartList,
                partNumberToMaraMap);

        efsElementMaraRepository.saveAll(partNumberToMaraMap.values());
        efsElementImportRepository.save(efsElement);

        vehiclePartList.setWeight(efsWeightManager.calculateWeight(vehiclePartList).get(Long.MIN_VALUE));
        return efsElement;
    }

    private PlsPartList getPartListFromPls(Date validDate, String prNumbers, String productDataId) {
        LOG.info("Requesting part list for product data id {}", productDataId);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl + "/partList/{productDataId}");
        uriBuilder.queryParam("validDate", DATE_FORMAT.format(validDate));

        if (StringUtils.isNotEmpty(prNumbers)) {
            uriBuilder.queryParam("prNumbers", prNumbers);
        }

        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put(PRODUCT_DATA_ID, productDataId);

        ResponseEntity<PlsPartList> plsPartListResponse = restTemplate.getForEntity(
                uriBuilder.buildAndExpand(pathVariables).toUriString(), PlsPartList.class);
        return plsPartListResponse.getBody();
    }

    private Collection<TiWhRequestQueueResponse> getTiWhRequestQueueFromPls() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl + "/tiWhImport/queue");

        ResponseEntity<Collection<TiWhRequestQueueResponse>> request = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() { });

        return Optional.ofNullable(request.getBody()).orElse(List.of());
    }

    private void importPartList(VehicleConfig vehicleConfig, PlsPartList plsPartList, String fileLockId) {
        VehicleConfigDTO config = VehicleConfigMapper.toDto(vehicleConfig, false);
        VehiclePartListDTO vehiclePartListDto = PartListFactory.createVehiclePartList(config);
        vehiclePartListDto.setChange(vehicleConfig.getUserCreate());
        vehiclePartListDto.setUserCreate(vehicleConfig.getUserCreate());
        vehiclePartListDto.setProductKeyVehicle(vehicleConfig.getVehicleProject().getProductKey());

        VehiclePartList vehiclePartList = VehiclePartListMapper.toEntity(vehiclePartListDto, vehicleConfig);
        VehiclePartList savedVehiclePartList = vehiclePartListRepository.save(vehiclePartList);

        createAndSaveElements(null, plsPartList.rootElement(), savedVehiclePartList);

        vehicleConfig.setVehiclePartList(savedVehiclePartList);
        vehicleConfig.setPlsDataLockId(fileLockId);

        Collection<FilteredOutPart> filteredOutParts = plsPartList.filteredOutParts();
        Collection<FilteredOutEfsElement> filteredOutEfsElements = new ArrayList<>(filteredOutParts.size());
        for (FilteredOutPart part : filteredOutParts) {
            EfsElementImport efsElementImport = createEfsElementForFilteredOutPart(part.getFilteredOutPart(),
                    savedVehiclePartList.getId());
            FilteredOutEfsElement filteredElement = FilteredOutEfsElementMapper.toEntity(efsElementImport,
                    vehicleConfig.getId(), part.getReason());

            filteredOutEfsElements.add(filteredElement);
        }

        filteredOutEfsElementRepository.saveAll(filteredOutEfsElements);

        if (savedVehiclePartList.getWeight() == 0.0D) {
            Long vehiclePartListId = savedVehiclePartList.getId();
            Double weight = efsWeightManager.calculateWeight(vehiclePartListId).get(Long.MIN_VALUE);
            LOG.debug("part list weight for part list id {} is (PLS-service) : {}", vehiclePartListId, weight);
            vehiclePartListRepository.updateWeight(vehiclePartListId, weight);
        }
    }

    private EfsElementImport importSubPartList(EfsElementImport element, VehiclePartList vehiclePartList,
            PlsPartList partList, String fileLockId) {
        // todo: ensure it's always null
        element.setWeightControlFlag(null);
        element.setChange(userManager.getCurrentUserId());

        EfsElementImport result = createAndSaveElements(element, partList.rootElement(), vehiclePartList);

        EfsElementAggregateMapping mapping = efsElementAggregateMappingRepository.findById(element.getId())
                .orElseGet(() -> {
                    EfsElementAggregateMapping newMapping = new EfsElementAggregateMapping();
                    newMapping.setEfsElementId(element.getId());
                    newMapping.setProductDataId(partList.productDataId());
                    return newMapping;
                });

        mapping.setImportDate(partList.productDataImportDate());
        mapping.setPlsFileLockId(fileLockId);

        efsElementAggregateMappingRepository.save(mapping);

        return result;
    }

    private ProductDataDTO toDTO(ProductDataSearchResponse resp) {
        ProductDataDTO dto = new ProductDataDTO();
        dto.setId(resp.getId());
        dto.setProductId(resp.getProductId());
        try {
            dto.setImportDate(DATE_FORMAT.parse(resp.getImportDate()));
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + resp.getImportDate());
        }

        dto.setStatus(resp.getImportStatus());
        return dto;
    }

    private EfsElementAggregateMappingDTO toDTO(EfsElementAggregateMapping mapping) {
        return new EfsElementAggregateMappingDTO(mapping.getEfsElementId(), mapping.getProductDataId(),
                mapping.getImportDate(), mapping.getPlsFileLockId());
    }

    private StatusResponse requestPartList(String productId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(plsUrl + "/partList/request")
                .queryParam(PlsServiceManager.PRODUCT_ID, productId)
                .queryParam(PlsServiceManager.REQUESTER, userManager.getCurrentUserId());
        ResponseEntity<StatusResponse> request = restTemplate.getForEntity(uriBuilder.toUriString(),
                StatusResponse.class);

        return request.getBody();
    }
}
