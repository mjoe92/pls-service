package de.vw.paso.client.stueckliste.efs.views.suche.cell;

import java.lang.reflect.Method;
import java.util.List;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

import de.vw.paso.client.control.cell.ReadOnlyTableCell;
import de.vw.paso.client.util.ReflectionUtil;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;

public class EfsSucheCellFactory<E extends AbstractEfsElementDTO, T>
        implements Callback<TableColumn<E, T>, TableCell<E, T>> {

    private final StringConverter<T> converter;
    private final Class<T> dataType;

    public EfsSucheCellFactory(StringConverter<T> converter) {
        this.converter = converter;
        this.dataType = getToStringMethodType();
    }

    @Override
    public TableCell<E, T> call(TableColumn<E, T> param) {
        ReadOnlyTableCell<E, T> readOnlyTableCell = new ReadOnlyTableCell<>(dataType);
        if (converter != null) {
            readOnlyTableCell.setConverter(converter);
        }

        return readOnlyTableCell;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getToStringMethodType() {
        if (converter != null) {
            List<Method> declaredMethodsByName = ReflectionUtil.getDeclaredMethodsByName(converter.getClass(),
                    "toString");

            for (Method m : declaredMethodsByName) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                // Die toString-Methode hat nur ein Argument
                Class<?> parameterType = parameterTypes[0];

                if (!Object.class.equals(parameterType)) {
                    return (Class<T>) parameterType;
                }
            }
        }

        return null;
    }

}
