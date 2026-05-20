package de.vw.paso.client.stammdaten.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  eryllan
 * @created 20.01.2015
 * @version $Revision:  $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

//FIXME Ugly construct working with reflection and downcasts, needto be replaced but something cleaner
public @interface StammdatenValidator {

	/**
	 * @return name of the files fxml, css, bundle
	 */
	String className() default "";

}
