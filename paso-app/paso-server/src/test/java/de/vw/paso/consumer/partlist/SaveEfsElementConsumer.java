package de.vw.paso.consumer.partlist;

import java.util.List;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.service.partlist.ISaveEfsElementConsumer;
import de.vw.paso.service.partlist.PartNumberInappropriateException;
import de.vw.paso.service.partlist.SameMaraInHierachyException;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveEfsElementConsumer extends AbstractTestConsumer<EfsElementDTO> implements ISaveEfsElementConsumer {

    @Autowired
    private EfsElementRestService service;

    @Override
    public void saveEfsElement(final EfsElementDTO efsElement) {
        run(() -> service.saveEfsElement(efsElement));
        registerResult(() -> List.of(getResult()));
    }

    @Override
    public void handle(NullElementException exception) {
        super.handle(exception);
    }

    @Override
    public void handle(PartNumberInappropriateException exception) {
        super.handle(exception);
    }

    @Override
    public void handle(SameMaraInHierachyException exception) {
        super.handle(exception);
    }
}