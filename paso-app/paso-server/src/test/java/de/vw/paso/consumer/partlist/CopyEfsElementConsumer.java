package de.vw.paso.consumer.partlist;

import java.util.List;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.service.partlist.AppendToDeletedElementException;
import de.vw.paso.service.partlist.ICopyEfsElementConsumer;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.CopyOrMoveEfsElementDTO;
import de.vw.paso.service.partlist.efsedit.CopyOrMoveVehiclePartListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementRestService;
import de.vw.paso.service.user.VehiclePartListDTO;
import org.springframework.stereotype.Component;

@Component
public class CopyEfsElementConsumer extends AbstractTestConsumer<List<EfsElementDTO>>
  implements ICopyEfsElementConsumer {

  private final EfsElementRestService service;

  public CopyEfsElementConsumer(EfsElementRestService service) {
    this.service = service;
  }

  @Override
  public void copyEfsElements(IPartListChildDTO toParent, List<EfsElementDTO> efsElements) {
    if (toParent instanceof VehiclePartListDTO dto) {
      run(() -> service.copyEfsElementsPartList(new CopyOrMoveVehiclePartListDTO(dto, new EfsElementListDTO(efsElements)))
        .efsElementDTOS());
      registerResult(this::getResult);
      return;
    } else if (toParent instanceof EfsElementDTO dto) {
      run(() -> service.copyEfsElements(new CopyOrMoveEfsElementDTO(dto, new EfsElementListDTO(efsElements)))
        .efsElementDTOS());
      registerResult(this::getResult);
      return;
    }
    throw new IllegalArgumentException("Parent must be either a vehicle part list or an efs element");
  }

  @Override
  public void handle(EmptyListException exception) {
    super.handle(exception);
  }

  @Override
  public void handle(AppendToDeletedElementException exception) {
    super.handle(exception);
  }

  @Override
  public void handle(NullElementException exception) {
    super.handle(exception);
  }

  @Override
  public void handle(SameMaraInHierachyException exception) {
    super.handle(exception);
  }
}
