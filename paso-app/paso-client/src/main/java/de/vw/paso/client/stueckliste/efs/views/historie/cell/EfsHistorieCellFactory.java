package de.vw.paso.client.stueckliste.efs.views.historie.cell;

import java.lang.reflect.Method;
import java.util.List;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.stueckliste.efs.control.EfsCellUtil;
import de.vw.paso.client.util.ReflectionUtil;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;

/**
 * @author eryllan
 * @version $Revision:  $
 * @created 26.02.2015
 */
public class EfsHistorieCellFactory<E extends IEfsElementForDTO, T>
        implements Callback<TreeTableColumn<E, T>, TreeTableCell<E, T>> {

    private final String propertyName;
    private final StringConverter<T> converter;
    private final Class<T> dataType;

    public EfsHistorieCellFactory(String propertyName, StringConverter<T> converter) {
        this.propertyName = propertyName;
        this.converter = converter;
        this.dataType = getToStringMethodType();
    }

    @Override
    public TreeTableCell<E, T> call(TreeTableColumn<E, T> param) {
        ReadOnlyTreeTableCell<E, T> historieReadOnlyTreeTableCell = new ReadOnlyTreeTableCell<>(dataType);
        if (converter != null) {
            historieReadOnlyTreeTableCell.setConverter(converter);
        }

        historieReadOnlyTreeTableCell.setPropertyName(this.propertyName);
        EfsCellUtil.formatCell(historieReadOnlyTreeTableCell);
        return (TreeTableCell<E, T>) historieReadOnlyTreeTableCell;
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
