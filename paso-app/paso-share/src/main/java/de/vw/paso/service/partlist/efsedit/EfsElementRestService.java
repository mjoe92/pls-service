package de.vw.paso.service.partlist.efsedit;

import de.vw.paso.exception.CannotResolveOnServerSideException;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.service.partlist.AppendToDeletedElementException;
import de.vw.paso.service.partlist.CreateDeletedEfsElementException;
import de.vw.paso.service.partlist.DeleteNonPersistedEfsElementException;
import de.vw.paso.service.partlist.EditingDeletedEfsElementException;
import de.vw.paso.service.partlist.MovingHierachyConflictException;
import de.vw.paso.service.partlist.PartNumberInappropriateException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public interface EfsElementRestService {

    String URL = "/api/efs-edit";
    String SAVE_All = "/save-all";
    String DELETE = "/delete";
    String COPY_EFS = "/copy-efs";
    String MOVE_EFS = "/move-efs";
    String COPY_PART_LIST = "/copy-partlist";
    String MOVE_PART_LIST = "/move-partlist";

    EfsElementListDTO loadPartList(Long vehicleConfigId);

    void createEfs(VehicleConfigDTO vehicleConfig);

    EfsElementDTO saveEfsElement(EfsElementDTO efsElementDTO)
            throws CreateDeletedEfsElementException, SameMaraInHierachyException, EditingDeletedEfsElementException,
            PartNumberInappropriateException, NullElementException, CannotResolveOnServerSideException;

    EfsElementListDTO saveEfsElements(SaveEfsElementListDTO changeDto)
            throws CreateDeletedEfsElementException, SameMaraInHierachyException, EditingDeletedEfsElementException,
            PartNumberInappropriateException, NullElementException, CannotResolveOnServerSideException;

    EfsElementListDTO deleteEfsElements(EfsElementListDTO efsElementListDTO)
            throws NullElementException, EmptyListException, DeleteNonPersistedEfsElementException;

    EfsElementListDTO copyEfsElements(CopyOrMoveEfsElementDTO copyOrMoveEfsElementDTO)
            throws SameMaraInHierachyException, AppendToDeletedElementException, CannotResolveOnServerSideException,
            NullElementException, EmptyListException;

    EfsElementListDTO moveEfsElements(CopyOrMoveEfsElementDTO copyOrMoveEfsElementDTO)
            throws MovingHierachyConflictException, SameMaraInHierachyException, AppendToDeletedElementException,
            CannotResolveOnServerSideException, NullElementException, EmptyListException;

    EfsElementListDTO copyEfsElementsPartList(CopyOrMoveVehiclePartListDTO copyOrMoveEfsElementDTO)
            throws SameMaraInHierachyException, AppendToDeletedElementException, CannotResolveOnServerSideException,
            NullElementException, EmptyListException;

    EfsElementListDTO moveEfsElementsPartList(CopyOrMoveVehiclePartListDTO copyOrMoveEfsElementDTO)
            throws MovingHierachyConflictException, SameMaraInHierachyException, AppendToDeletedElementException,
            CannotResolveOnServerSideException, NullElementException, EmptyListException;
}