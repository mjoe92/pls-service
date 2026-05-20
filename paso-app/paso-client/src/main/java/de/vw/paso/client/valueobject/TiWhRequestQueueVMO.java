package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.pls.TiWhRequestQueueDTO;

public class TiWhRequestQueueVMO {

    private StringProperty productIdProperty = new SimpleStringProperty(this, "productId");
    private ObjectProperty<Date> requestSequenceProperty = new SimpleObjectProperty<>(this, "requestSequence");
    private ObjectProperty<Set<String>> requesterIdsProperty = new SimpleObjectProperty<>(this, "requesterIds");
    private ObjectProperty<Boolean> requestedProperty = new SimpleObjectProperty<>(this, "requested");
    private ObjectProperty<Boolean> processingProperty = new SimpleObjectProperty<>(this, "processing");

    public final ObjectProperty<Boolean> requestedProperty() {
        return requestedProperty;
    }

    public final StringProperty productIdProperty() {
        return productIdProperty;
    }

    public final ObjectProperty<Date> requestSequenceProperty() {
        return requestSequenceProperty;
    }

    public final ObjectProperty<Set<String>> requesterIdsProperty() {
        return requesterIdsProperty;
    }

    public final ObjectProperty<Boolean> processingProperty() {
        return processingProperty;
    }

    public final void setRequested(boolean value) {
        requestedProperty.setValue(value);
    }

    private void setProcessing(boolean value) {
        processingProperty.setValue(value);
    }

    public final void setProductId(final String value) {
        productIdProperty.setValue(value);
    }

    public final void setRequestSequence(final Date value) {
        requestSequenceProperty.setValue(value);
    }

    public final void setRequesterIds(final Set<String> value) {
        requesterIdsProperty.setValue(value);
    }

    public static TiWhRequestQueueVMO toVMO(TiWhRequestQueueDTO q) {
        TiWhRequestQueueVMO vmo = new TiWhRequestQueueVMO();
        vmo.setProductId(q.productId());
        vmo.setRequestSequence(q.requestSequence());
        vmo.setRequesterIds(q.requesterIds());
        vmo.setRequested(q.requested());
        vmo.setProcessing(q.processing());
        return vmo;
    }

    public static List<TiWhRequestQueueVMO> toVMOs(Collection<TiWhRequestQueueDTO> q) {
        return q.stream().map(TiWhRequestQueueVMO::toVMO).collect(Collectors.toList());
    }

}
