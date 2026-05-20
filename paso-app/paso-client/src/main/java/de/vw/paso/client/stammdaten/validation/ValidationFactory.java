package de.vw.paso.client.stammdaten.validation;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

import javafx.beans.Observable;

import de.vw.paso.client.base.BaseController;
import org.slf4j.LoggerFactory;

/**
 * @author eryllan
 * @version $Revision:  $
 * @created 15.01.2015
 */
public class ValidationFactory {

    public static Predicate<String> getStringPredicate(int minLength) {
        return str -> str != null && str.trim().length() >= minLength;
    }

    /// /FIXME Ugly construct working with reflection and downcasts, needto be replaced but something cleaner
    public AbstractStammdatenValidator getValidator(BaseController<?> controller, Observable... params) {
        StammdatenValidator declaredAnnotation = controller.getClass().getAnnotation(StammdatenValidator.class);

        if (declaredAnnotation == null) {
            return (AbstractStammdatenValidator) newInstanceOf(getDefaultValidatorClassName(controller), params);
        }

        return (AbstractStammdatenValidator) newInstanceOf(declaredAnnotation.className(), params);
    }

    private String getDefaultValidatorClassName(BaseController<?> controller) {
        String[] paths = controller.getClass().getName().split("\\.");
        paths[paths.length - 1] = paths[paths.length - 1].replace("Controller", "Validator");
        return String.join(".", paths);
    }

    @SuppressWarnings("unchecked")
    private AbstractStammdatenValidator newInstanceOf(String className, Observable... params) {
        try {
            Class<AbstractStammdatenValidator> clazz = (Class<AbstractStammdatenValidator>) Class.forName(className);
            Constructor<AbstractStammdatenValidator> declaredConstructor = clazz.getDeclaredConstructor(Object[].class);

            // Das muss hier Object sein, da man keine primitiven Arrays von Observable erstellen kann
            return declaredConstructor.newInstance(new Object[] { params });
        } catch (Exception e) {
            LoggerFactory.getLogger(ValidationFactory.class).error("Could not create new Instance", e);
        }
        return null;
    }

}
