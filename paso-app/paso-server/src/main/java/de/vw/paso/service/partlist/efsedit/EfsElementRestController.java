package de.vw.paso.service.partlist.efsedit;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.vw.paso.exception.CannotResolveOnServerSideException;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.logic.partlist.EfsElementManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.IPartListChild;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.service.partlist.AppendToDeletedElementException;
import de.vw.paso.service.partlist.CreateDeletedEfsElementException;
import de.vw.paso.service.partlist.DeleteNonPersistedEfsElementException;
import de.vw.paso.service.partlist.EditingDeletedEfsElementException;
import de.vw.paso.service.partlist.EfsEditValidations;
import de.vw.paso.service.partlist.MovingHierachyConflictException;
import de.vw.paso.service.partlist.PartNumberInappropriateException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.util.UnauthorizedException;
import de.vw.paso.utility.StringConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//todo: refactor
@RestController
@RequestMapping(path = EfsElementRestService.URL)
public class EfsElementRestController implements EfsElementRestService {

    private static final Logger LOG = LoggerFactory.getLogger(EfsElementRestController.class);

    private final EfsElementManager efsElementManager;
    private final EfsEditValidations validations;
    private final UserManager userManager;
    private final VehiclePartListRepository vehiclePartListRepository;

    public EfsElementRestController(EfsElementManager efsElementManager, UserManager userManager,
            VehiclePartListRepository vehiclePartListRepository) {
        this.efsElementManager = efsElementManager;
        this.userManager = userManager;
        this.validations = new EfsEditValidations(efsElementManager::getOne,
                node -> efsElementManager.getAllIdsInHierarchy(node.getId(), true));

        this.vehiclePartListRepository = vehiclePartListRepository;
    }

    @Override
    @PostMapping(path = EfsElementRestService.COPY_EFS)
    @Transactional
    public EfsElementListDTO copyEfsElements(@RequestBody CopyOrMoveEfsElementDTO copyOrMoveEfsElementDTO)
            throws SameMaraInHierachyException, AppendToDeletedElementException, CannotResolveOnServerSideException,
            NullElementException, EmptyListException {
        return copyImpl(copyOrMoveEfsElementDTO.efsElementListDTO(), copyOrMoveEfsElementDTO.newParent());
    }

    @Override
    @PostMapping(path = EfsElementRestService.COPY_PART_LIST)
    @Transactional
    public EfsElementListDTO copyEfsElementsPartList(@RequestBody CopyOrMoveVehiclePartListDTO copyOrMoveEfsElementDTO)
            throws SameMaraInHierachyException, AppendToDeletedElementException, CannotResolveOnServerSideException,
            NullElementException, EmptyListException {
        return copyImpl(copyOrMoveEfsElementDTO.efsElementListDTO(), copyOrMoveEfsElementDTO.newParent());
    }

    @Override
    @PostMapping
    public void createEfs(@RequestBody VehicleConfigDTO vehicleConfig) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    @PutMapping(path = EfsElementRestService.DELETE)
    @Transactional
    public EfsElementListDTO deleteEfsElements(@RequestBody EfsElementListDTO toDelete)
            throws NullElementException, EmptyListException, DeleteNonPersistedEfsElementException {
        List<EfsElementDTO> efsElementDTOS = toDelete.efsElementDTOS();
        List<EfsElement> efsElements = new ArrayList<>(efsElementDTOS.size());
        for (EfsElementDTO dto : efsElementDTOS) {
            EfsElement entity = EfsElementMapper.toEntity(dto);
            entity.setChange(userManager.getCurrentUserId());
            efsElements.add(entity);
        }

        validations.deleteEfsElement(efsElementDTOS);
        Collection<EfsElement> deletedElements = efsElementManager.deleteEfsElements(efsElements);
        List<EfsElementDTO> mappedDeletedElements = deletedElements.stream().map(EfsElementMapper::toDto).toList();
        return new EfsElementListDTO(mappedDeletedElements);
    }

    @Override
    @GetMapping(path = "/{vehicleConfigId}")
    @Transactional
    public EfsElementListDTO loadPartList(@PathVariable Long vehicleConfigId) {
        List<EfsElementDTO> efsElementDTOS = efsElementManager.loadPartList(vehicleConfigId).stream()
                .map(EfsElementMapper::toDto).collect(toCollection(ArrayList::new));
        return new EfsElementListDTO(efsElementDTOS);
    }

    @Override
    @PostMapping(path = EfsElementRestService.MOVE_EFS)
    @Transactional
    public EfsElementListDTO moveEfsElements(@RequestBody CopyOrMoveEfsElementDTO copyOrMoveEfsElementDTO)
            throws MovingHierachyConflictException, SameMaraInHierachyException, AppendToDeletedElementException,
            CannotResolveOnServerSideException, NullElementException, EmptyListException {
        return moveImpl(copyOrMoveEfsElementDTO.newParent(), copyOrMoveEfsElementDTO.efsElementListDTO());
    }

    @Override
    @PostMapping(path = EfsElementRestService.MOVE_PART_LIST)
    @Transactional
    public EfsElementListDTO moveEfsElementsPartList(@RequestBody CopyOrMoveVehiclePartListDTO copyOrMoveEfsElementDTO)
            throws MovingHierachyConflictException, SameMaraInHierachyException, AppendToDeletedElementException,
            CannotResolveOnServerSideException, NullElementException, EmptyListException {
        return moveImpl(copyOrMoveEfsElementDTO.newParent(), copyOrMoveEfsElementDTO.efsElementListDTO());
    }

    @Override
    @PutMapping
    @Transactional
    public EfsElementDTO saveEfsElement(@RequestBody EfsElementDTO efsElementDTO)
            throws CreateDeletedEfsElementException, SameMaraInHierachyException, EditingDeletedEfsElementException,
            PartNumberInappropriateException, NullElementException, CannotResolveOnServerSideException {
        String currentUserId = userManager.getCurrentUserId();
        efsElementDTO.setChange(currentUserId);
        Map<Long, Boolean> vehicleConfigIdsWithWriteAccess = userManager.getVehicleConfigIdsWithWriteAccess();
        VehiclePartList vehiclePartList = vehiclePartListRepository.findById(efsElementDTO.getVehiclePartListId())
                .orElseThrow();
        Long vehicleConfigId = vehiclePartList.getVehicleConfig().getId();
        if (!(vehicleConfigIdsWithWriteAccess.getOrDefault(vehicleConfigId, false))) {
            throw new UnauthorizedException(
                    "User " + currentUserId + " doesn't have the right to edit vehicle config (id: " + vehicleConfigId
                            + StringConstant.RIGHT_PARENTHESIS);
        }

        validations.saveEfsElement(efsElementDTO);

        EfsElement entity = EfsElementMapper.toEntity(efsElementDTO);
        EfsElement saved = efsElementManager.saveEfsElement(entity);

        return EfsElementMapper.toDto(saved);
    }

    @Override
    @PutMapping(path = EfsElementRestService.SAVE_All)
    @Transactional
    public EfsElementListDTO saveEfsElements(@RequestBody SaveEfsElementListDTO changeDto)
            throws CreateDeletedEfsElementException, SameMaraInHierachyException, EditingDeletedEfsElementException,
            PartNumberInappropriateException, NullElementException, CannotResolveOnServerSideException {
        long start = System.currentTimeMillis();

        List<EfsElement> changeList = new ArrayList<>();
        for (EfsElementDTO efsElementDTO : changeDto.changedElements()) {
            efsElementDTO.setChange(userManager.getCurrentUserId());
            EfsElement entity = EfsElementMapper.toEntity(efsElementDTO);
            changeList.add(entity);
            validations.saveEfsElement(efsElementDTO);
        }

        LOG.info("Check Validations: {} ms", System.currentTimeMillis() - start);
        Collection<EfsElement> saved = efsElementManager.saveEfsElements(changeList);
        List<EfsElementDTO> efsElementDTOS = saved.stream().map(EfsElementMapper::toDto)
                .collect(toCollection(ArrayList::new));

        return new EfsElementListDTO(efsElementDTOS);
    }

    private EfsElementListDTO copyImpl(EfsElementListDTO copyOrMoveEfsElementDTO,
            IPartListChildDTO copyOrMoveEfsElementDTO1)
            throws EmptyListException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            NullElementException, AppendToDeletedElementException {
        List<EfsElementDTO> efsElementDTOS = copyOrMoveEfsElementDTO.efsElementDTOS();
        List<EfsElement> efsElements = efsElementDTOS == null ? new ArrayList<>() :
                efsElementDTOS.stream().map(EfsElementMapper::toEntity).toList();
        validations.copyEfsElements(copyOrMoveEfsElementDTO1, efsElementDTOS);

        Collection<EfsElement> copied = efsElementManager.copyEfsElements(
                covertToIPartListChild(copyOrMoveEfsElementDTO1), efsElements);
        List<EfsElementDTO> converted = copied.stream().map(EfsElementMapper::toDto)
                .collect(toCollection(ArrayList::new));

        return new EfsElementListDTO(converted);
    }

    private IPartListChild covertToIPartListChild(IPartListChildDTO iPartListChildDTO) {
        if (iPartListChildDTO instanceof EfsElementDTO efsElementDTO) {
            return EfsElementMapper.toEntity(efsElementDTO);
        }

        VehiclePartListDTO vehiclePartList = (VehiclePartListDTO) iPartListChildDTO;
        return VehiclePartListMapper.toEntityByDtoConfig(vehiclePartList, vehiclePartList.getVehicleConfig());
    }

    private EfsElementListDTO moveImpl(IPartListChildDTO copyOrMoveEfsElementDTO, EfsElementListDTO efsElementListDTO)
            throws EmptyListException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            NullElementException, AppendToDeletedElementException, MovingHierachyConflictException {
        // DTO-s will be modified, so save as a new list in case the original list is not modifiable.
        List<EfsElementDTO> efsElementsDTOs = new ArrayList<>(efsElementListDTO.efsElementDTOS());
        validations.moveEfsElements(copyOrMoveEfsElementDTO, efsElementsDTOs);

        List<EfsElement> efsElements = efsElementsDTOs.stream().map(EfsElementMapper::toEntity).toList();
        Collection<EfsElement> moved = efsElementManager.moveEfsElements(
                covertToIPartListChild(copyOrMoveEfsElementDTO), efsElements);

        efsElementsDTOs = moved.stream().map(EfsElementMapper::toDto).toList();
        return new EfsElementListDTO(efsElementsDTOs);
    }
}
