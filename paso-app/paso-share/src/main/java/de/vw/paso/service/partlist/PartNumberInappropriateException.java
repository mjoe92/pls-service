package de.vw.paso.service.partlist;

import de.vw.paso.exception.AbstractServerValidationException;
import de.vw.paso.exception.ServiceConsumer;

public class PartNumberInappropriateException extends AbstractServerValidationException {

    public PartNumberInappropriateException(String partNumber) {
        super(partNumber);
    }

    @Override
    public void accept(ServiceConsumer consumer) {
        ((ISaveEfsElementConsumer) consumer).handle(this);
    }

    @Override
    public String getMessageKey() {
        return "validation.PartnumberInappropriateException";
    }
}