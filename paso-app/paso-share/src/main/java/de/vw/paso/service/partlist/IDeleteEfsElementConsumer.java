package de.vw.paso.service.partlist;

import java.util.List;

import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.IListParamServiceConsumer;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface IDeleteEfsElementConsumer extends IListParamServiceConsumer {

    void deleteEfsElement(List<EfsElementDTO> efsElements) throws EmptyListException, NullElementException;

}
