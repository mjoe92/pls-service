package de.vw.paso.service.partlist;

import java.util.List;

import de.vw.paso.exception.CannotResolveOnServerSideException;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.IListParamServiceConsumer;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.jspecify.annotations.NonNull;

public interface IMoveEfsElementConsumer
        extends IListParamServiceConsumer, IMaraHandlingConsumer, IAppendToDeletedElementConsumer {

    void moveEfsElements(@NonNull IPartListChildDTO toParent, @NonNull List<EfsElementDTO> efsElements)
            throws EmptyListException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            AppendToDeletedElementException, NullElementException, MovingHierachyConflictException;

    void handle(MovingHierachyConflictException e);
}
