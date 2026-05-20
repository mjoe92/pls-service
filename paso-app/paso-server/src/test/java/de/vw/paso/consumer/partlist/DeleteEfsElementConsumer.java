package de.vw.paso.consumer.partlist;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.service.partlist.IDeleteEfsElementConsumer;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteEfsElementConsumer extends AbstractTestConsumer<List<EfsElementDTO>>
  implements IDeleteEfsElementConsumer {

  @Autowired
  private EfsElementRestService service;

  @Override
  public void deleteEfsElement(List<EfsElementDTO> efsElements) {
    run(() -> new ArrayList<>(service.deleteEfsElements(new EfsElementListDTO(efsElements)).efsElementDTOS()));

    registerResult(this::getResult);
  }

  @Override
  public void handle(EmptyListException exception) {
    super.handle(exception);
  }

  @Override
  public void handle(NullElementException exception) {
    super.handle(exception);
  }
}
