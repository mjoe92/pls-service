package de.vw.paso.delegate.stueckliste;

import java.util.Collection;
import java.util.List;

import de.vw.paso.delegate.stueckliste.efsedit.EfsElementRestClientHolder;
import de.vw.paso.exception.CannotResolveOnServerSideException;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.exception.ServerException;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.service.partlist.CreateDeletedEfsElementException;
import de.vw.paso.service.partlist.DeleteNonPersistedEfsElementException;
import de.vw.paso.service.partlist.EditingDeletedEfsElementException;
import de.vw.paso.service.partlist.PartNumberInappropriateException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.CopyOrMoveEfsElementDTO;
import de.vw.paso.service.partlist.efsedit.CopyOrMoveVehiclePartListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementRestService;
import de.vw.paso.service.partlist.efsedit.SaveEfsElementListDTO;
import de.vw.paso.service.user.VehiclePartListDTO;

public class EfsEditLoadAdapter {

    private final EfsElementRestService efsElementRestService;

    public EfsEditLoadAdapter() {
        efsElementRestService = EfsElementRestClientHolder.getInstance();
    }

    public List<EfsElementDTO> loadPartList(Long vehicleConfigId) {
        return efsElementRestService.loadPartList(vehicleConfigId).efsElementDTOS();
    }

    public EfsElementDTO saveEfsElement(EfsElementDTO newEfsElement)
            throws NullElementException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            PartNumberInappropriateException, CreateDeletedEfsElementException, EditingDeletedEfsElementException {
        return efsElementRestService.saveEfsElement(newEfsElement);
    }

    public Collection<EfsElementDTO> saveEfsElements(Collection<EfsElementDTO> changedElements)
            throws EditingDeletedEfsElementException, SameMaraInHierachyException, PartNumberInappropriateException,
            CannotResolveOnServerSideException, NullElementException, CreateDeletedEfsElementException {

        SaveEfsElementListDTO cleanedChangedMap = new SaveEfsElementListDTO(changedElements);
        return efsElementRestService.saveEfsElements(cleanedChangedMap).efsElementDTOS();
    }

    public List<EfsElementDTO> deleteEfsElements(List<EfsElementDTO> efsElements)
            throws DeleteNonPersistedEfsElementException, EmptyListException, NullElementException {
        return efsElementRestService.deleteEfsElements(new EfsElementListDTO(efsElements)).efsElementDTOS();
    }

    public List<EfsElementDTO> copyEfsElements(IPartListChildDTO parent, List<EfsElementDTO> copyEfsElements)
            throws ServerException {
        if (parent instanceof VehiclePartListDTO dto) {
            return efsElementRestService.copyEfsElementsPartList(
                    new CopyOrMoveVehiclePartListDTO(dto, new EfsElementListDTO(copyEfsElements))).efsElementDTOS();
        } else if (parent instanceof EfsElementDTO dto) {
            return efsElementRestService.copyEfsElements(
                    new CopyOrMoveEfsElementDTO(dto, new EfsElementListDTO(copyEfsElements))).efsElementDTOS();
        }

        throw new IllegalArgumentException("Parent must be either a vehicle part list or an efs element");
    }

    public List<EfsElementDTO> moveEfsElements(IPartListChildDTO parent, List<EfsElementDTO> efsElements)
            throws ServerException {
        if (parent instanceof VehiclePartListDTO dto) {
            return efsElementRestService.moveEfsElementsPartList(
                    new CopyOrMoveVehiclePartListDTO(dto, new EfsElementListDTO(efsElements))).efsElementDTOS();
        } else if (parent instanceof EfsElementDTO dto) {
            return efsElementRestService.moveEfsElements(
                    new CopyOrMoveEfsElementDTO(dto, new EfsElementListDTO(efsElements))).efsElementDTOS();
        }

        throw new IllegalArgumentException("Parent must be either a vehicle part list or an efs element");
    }
}