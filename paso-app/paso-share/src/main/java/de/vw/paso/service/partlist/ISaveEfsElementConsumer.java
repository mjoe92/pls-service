package de.vw.paso.service.partlist;

import de.vw.paso.exception.CannotResolveOnServerSideException;
import de.vw.paso.exception.IParamServiceConsumer;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface ISaveEfsElementConsumer extends IParamServiceConsumer, IMaraHandlingConsumer {

    void saveEfsElement(EfsElementDTO efsElement)
            throws NullElementException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            PartNumberInappropriateException, CreateDeletedEfsElementException, EditingDeletedEfsElementException;

    void handle(PartNumberInappropriateException exception);
}