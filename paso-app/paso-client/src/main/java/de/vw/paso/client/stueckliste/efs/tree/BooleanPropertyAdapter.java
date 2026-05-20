package de.vw.paso.client.stueckliste.efs.tree;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

import de.vw.paso.utility.VoidWithException;

public class BooleanPropertyAdapter extends BooleanProperty {

    private final BooleanProperty delegator;

    public BooleanPropertyAdapter(BooleanProperty delegator) {
        this.delegator = delegator;
    }

    public void run(VoidWithException validation) {
        try {
            validation.run();
            set(false);
        } catch (Exception exception) {
            set(true);
        }
    }

    @Override
    public void addListener(ChangeListener<? super Boolean> listener) {
        delegator.addListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        delegator.addListener(listener);
    }

    @Override
    public BooleanBinding and(ObservableBooleanValue other) {
        return delegator.and(other);
    }

    @Override
    public ObjectProperty<Boolean> asObject() {
        return delegator.asObject();
    }

    @Override
    public StringBinding asString() {
        return delegator.asString();
    }

    @Override
    public void bind(ObservableValue<? extends Boolean> observable) {
        delegator.bind(observable);
    }

    @Override
    public void bindBidirectional(Property<Boolean> other) {
        delegator.bindBidirectional(other);
    }

    @Override
    public boolean equals(Object arg0) {
        return delegator.equals(arg0);
    }

    @Override
    public boolean get() {
        return delegator.get();
    }

    @Override
    public Object getBean() {
        return delegator.getBean();
    }

    @Override
    public String getName() {
        return delegator.getName();
    }

    @Override
    public Boolean getValue() {
        return delegator.getValue();
    }

    @Override
    public int hashCode() {
        return delegator.hashCode();
    }

    @Override
    public boolean isBound() {
        return delegator.isBound();
    }

    @Override
    public BooleanBinding isEqualTo(ObservableBooleanValue other) {
        return delegator.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableBooleanValue other) {
        return delegator.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding not() {
        return delegator.not();
    }

    @Override
    public BooleanBinding or(ObservableBooleanValue other) {
        return delegator.or(other);
    }

    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {
        delegator.removeListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        delegator.removeListener(listener);
    }

    @Override
    public void set(boolean value) {
        delegator.set(value);
    }

    @Override
    public void setValue(Boolean v) {
        delegator.setValue(v);
    }

    @Override
    public String toString() {
        return delegator.toString();
    }

    @Override
    public void unbind() {
        delegator.unbind();
    }

    @Override
    public void unbindBidirectional(Property<Boolean> other) {
        delegator.unbindBidirectional(other);
    }
}
