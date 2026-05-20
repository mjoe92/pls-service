package de.vw.paso.services.partlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static testutil.TestUtils.saveUserGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import de.vw.paso.consumer.partlist.CopyEfsElementConsumer;
import de.vw.paso.consumer.partlist.CreateVehiclePartListConsumer;
import de.vw.paso.consumer.partlist.DeleteEfsElementConsumer;
import de.vw.paso.consumer.partlist.MoveEfsElementConsumer;
import de.vw.paso.consumer.partlist.SaveEfsElementConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.partlist.EfsWeightManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.mapper.EfsElementMaraMapper;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.IPartListChild;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.repository.partlist.EfsElementMaraRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.StringConstant;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractEfsTests extends AbstractServiceTests {

    protected static final int PERSISTED_EFS_ELEMENT_COUNT = 5;

    @Autowired
    protected EfsElementRepository efsElementRepository;
    @Autowired
    protected EfsElementMaraRepository efsElementMaraRepository;
    @Autowired
    protected VehiclePartListRepository vehiclePartListRepository;
    @Autowired
    protected SaveEfsElementConsumer saveEfsElementConsumer;
    @Autowired
    protected CopyEfsElementConsumer copyEfsElementConsumer;
    @Autowired
    protected DeleteEfsElementConsumer deleteEfsElementConsumer;
    @Autowired
    protected MoveEfsElementConsumer moveEfsElementConsumer;
    @Autowired
    protected EfsWeightManager efsWeightManager;
    @Autowired
    protected UserManager userManager;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private VehicleConfigRepository vehicleConfigRepository;

    private VehiclePartListDTO vehiclePartList;

    protected VehiclePartListDTO vehiclePartList() {
        return update(vehiclePartList);
    }

    protected Long initialRevision;

    private VehiclePartListDTO emptyPartList;

    protected VehiclePartListDTO emptyPartList() {
        return update(emptyPartList);
    }

    protected EfsElementDTO efsElement;

    private List<EfsElementDTO> persistedEfsElements;

    protected List<EfsElementDTO> persistedEfsElements() {
        return update(persistedEfsElements);
    }

    @Autowired
    private CreateVehiclePartListConsumer createVehiclePartListConsumer;

    protected static void validateCopiedElement(EfsElementDTO originalElement, EfsElementDTO copiedElement) {
        EfsElementDTO syncOriginalElement = synchronize(originalElement);
        EfsElementDTO syncCopiedElement = synchronize(copiedElement);

        assertEquals(syncOriginalElement.isDeleted(), syncCopiedElement.isDeleted());
        assertNotEquals(syncOriginalElement.getId(), syncCopiedElement.getId());
        assertNotEquals(syncOriginalElement.getTimestampCreate(), syncCopiedElement.getTimestampCreate());
        assertNotEquals(syncOriginalElement, syncCopiedElement.getRevision());
        assertNotEquals(syncOriginalElement.getTimestampChange(), syncCopiedElement.getTimestampChange());

        assertEquals(syncOriginalElement.getPartNumber(), syncCopiedElement.getPartNumber());
        assertEquals(syncOriginalElement.getType(), syncCopiedElement.getType());
        //    assertEquals(originalElement.getEfsElementMara(), copiedElement.getEfsElementMara());

        assertEquals(syncOriginalElement.getAggregate(), syncCopiedElement.getAggregate());
        assertEquals(syncOriginalElement.getBeginDate(), syncCopiedElement.getBeginDate());
        assertEquals(syncOriginalElement.getBeginDateKey(), syncCopiedElement.getBeginDateKey());
        assertEquals(syncOriginalElement.getEndDate(), syncCopiedElement.getEndDate());
        assertEquals(syncOriginalElement.getEndDateKey(), syncCopiedElement.getEndDateKey());
        assertEquals(syncOriginalElement.getWeightControlFlag(), syncCopiedElement.getWeightControlFlag());
        assertEquals(syncOriginalElement.getQuantity(), syncCopiedElement.getQuantity());
        assertEquals(syncOriginalElement.getQuantityUnit(), syncCopiedElement.getQuantityUnit());
        assertEquals(syncOriginalElement.getNodeId(), syncCopiedElement.getNodeId());
        assertEquals(syncOriginalElement.getNodeLabel(), syncCopiedElement.getNodeLabel());
        assertEquals(syncOriginalElement.getNodeType(), syncCopiedElement.getNodeType());
        assertEquals(syncOriginalElement.getPrNumberRule(), syncCopiedElement.getPrNumberRule());
        assertEquals(syncOriginalElement.getGap(), syncCopiedElement.getGap());
        assertEquals(syncOriginalElement.getSetKey(), syncCopiedElement.getSetKey());
        assertEquals(syncOriginalElement.getTisSort(), syncCopiedElement.getTisSort());
        assertEquals(syncOriginalElement.getDescription1(), syncCopiedElement.getDescription1());
        assertEquals(syncOriginalElement.getDescription2(), syncCopiedElement.getDescription2());
        assertEquals(syncOriginalElement.getTiWhImportId(), syncCopiedElement.getTiWhImportId());
    }

    @BeforeEach
    public void initTestCase() {
        vehiclePartList = createVehiclePartList();
        emptyPartList = createVehiclePartList();
        persistedEfsElements = createEfsElements();
        persistedEfsElements.sort(Comparator.comparing(EfsElementDTO::getPartNumber));
        efsElement = createEfsElement(99, vehiclePartList());
        initialRevision = vehiclePartList().getRevision();
    }

    protected void evaluateEfsElementReversion(long revisionNumber, EfsElementDTO... efsElements) {
        for (EfsElementDTO efsElement : efsElements) {
            assertEquals(Long.valueOf(revisionNumber), efsElement.getRevision());
        }
    }

    protected void evaluatePartListReversion(EfsElementDTO targetEfsElement, long revisionNumber) {
        assertEquals(Long.valueOf(revisionNumber), getPartListRevision(targetEfsElement));
    }

    public VehiclePartListDTO createVehiclePartList() {
        VehicleConfigDTO vehicleConfig = createFzgConfigWithNameAndVehicleProject();

        /*
         we create proper connections between user groups and vehicle configs with this, so that
         we'll be able to bypass the authentication phase without creating bloat in the tcs
         */
        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository);
        saveUserGroup(userManager, userGroupRepository, "EOSTESI", vehicleConfigRepository);

        vehicleConfig.setTiWhImportVehicle(getTiWhImport("YYY"));
        saveVehicleConfigConsumer.saveVehicleConfig(vehicleConfig, null);
        vehicleConfig = saveVehicleConfigConsumer.getResult();
        createVehiclePartListConsumer.createVehiclePartList(vehicleConfig.getId());
        vehicleConfig = createVehiclePartListConsumer.getResult();

        return vehicleConfig.getVehiclePartList();
    }

    // FIXME - change it if createEfsElement is implemented
    protected EfsElementDTO createAndSaveEfsElement(int tnrIndex, IPartListChildDTO parent) {
        EfsElementDTO efsElement = createEfsElement(tnrIndex, parent);

        saveEfsElementConsumer.saveEfsElement(efsElement);

        return saveEfsElementConsumer.getResult();
    }

    private EfsElementDTO createEfsElement(int tnrIndex, IPartListChildDTO parent) {
        EfsElementMaraDTO mara = PartListFactory.createEfsElementMara("BEZEICHNUNG1",
                StringConstant.EMPTY + tnrIndex + "111111111");
        mara.setVehiclePartListId(parent.getVehiclePartListId());
        EfsElementDTO parentNode = parent.asParent();
        Long parentId = parentNode == null ? null : parentNode.getId();

        EfsElementDTO element = PartListFactory.createEfsElement(parentId, mara, 111, "G",
                parent.getVehiclePartListId());
        element.setWeightControlFlag(WeightControlFlag.YES);

        return element;
    }

    protected EfsElement createEfsElement(EfsElementMara mara, IPartListChild parent) {
        EfsElement element = parent.asParent();
        Long id = element == null ? null : element.getId();

        return EfsElementMapper.toEntity(
                PartListFactory.createEfsElement(id, EfsElementMaraMapper.toDto(mara), 111, "G",
                        parent.getVehiclePartListId()));
    }

    protected EfsElementDTO createAndSaveEfsElement(int tnrIndex) {
        return createAndSaveEfsElement(tnrIndex, vehiclePartList);
    }

    private List<EfsElementDTO> createEfsElements() {
        List<EfsElementDTO> persistedEfsElements = new ArrayList<>();

        for (int index = 10; index < 10 + AbstractEfsTests.PERSISTED_EFS_ELEMENT_COUNT; index++) {
            persistedEfsElements.add(createAndSaveEfsElement(index));
        }

        return persistedEfsElements;
    }

    protected void evaluateAllElementsInPartListSize(int allExpected, int deletedElements,
            VehiclePartListDTO partList) {
        Collection<EfsElement> allExpectedElements = efsElementRepository.findAll().stream()
                .filter((element) -> element.getVehiclePartListId().equals(partList.getId())).toList();

        assertEquals(allExpected, allExpectedElements.size());
        assertEquals(deletedElements, allExpectedElements.stream().filter((EfsElement::isDeleted)).count());
    }

    private EfsElementDTO update(EfsElementDTO element) {
        return EfsElementMapper.toDto(efsElementRepository.findById(element.getId()).get());
    }

    private List<EfsElementDTO> update(List<EfsElementDTO> elements) {
        List<EfsElementDTO> returnValues = new ArrayList<>(elements.size());
        for (EfsElementDTO element : elements) {
            returnValues.add(synchronize(element));
        }

        return returnValues;
    }

    @Transactional
    protected VehiclePartListDTO update(VehiclePartListDTO element) {
        return vehiclePartListRepository.findById(element.getId())
                .map(entity -> VehiclePartListMapper.toDTO(entity, entity.getVehicleConfig())).orElse(null);
    }

    protected Long getPartListRevision(EfsElementDTO element) {
        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(
                efsElementRepository.findById(element.getId()).get().getVehiclePartListId()).get();
        return vehiclePartList.getRevision();
    }

    protected void evaluateParentAndPartList(IPartListChildDTO parent, EfsElementDTO copiedEfsElement) {
        if (parent instanceof EfsElementDTO) {
            parent = update(parent.asParent());
        }

        if (parent instanceof VehiclePartListDTO vehiclePartListDTO) {
            parent = update(vehiclePartListDTO);
        }

        assertEquals(synchronize(parent.asParent()), synchronize(copiedEfsElement).getParent());
        assertEquals(parent.getVehiclePartListId(), copiedEfsElement.getVehiclePartListId());
    }

    protected void evaluateMaraRevision(long revision, EfsElementDTO element) {
        assertEquals(Long.valueOf(revision), element.getEfsElementMara().getRevision());
    }

    protected EfsElementDTO getOriginal(int index) {
        EfsElement original = efsElementRepository.findById(persistedEfsElements.get(index).getId()).get();
        return EfsElementMapper.toDto(original);
    }

    protected void moveOtherBReturningNewA(EfsElementDTO A, EfsElementDTO B) {
        A.setParent(B);
        A.setEntityChange(true);
        saveEfsElementConsumer.saveEfsElement(A);
        EfsElementMapper.toEntity(saveEfsElementConsumer.getResult());
    }

    protected void evaluateRevisionVehiclePartList(long expectedRevision) {
        assertEquals(expectedRevision, vehiclePartList().getRevision().longValue());
    }

    protected void evaluateRevisionEmptyPartList(long expectedRevision) {
        assertEquals(expectedRevision, emptyPartList().getRevision().longValue());
    }

    protected static EfsElementDTO synchronize(EfsElementDTO element) {
        return element == null ? null : EfsElementResolver.getElement(element.getId());
    }
}
