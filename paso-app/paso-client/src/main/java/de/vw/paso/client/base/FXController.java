package de.vw.paso.client.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that should be used on concrete classes of {@link BaseController} so that other Controllers can load
 * this particular class with FXML via {@link #name()}. Also does some CSS and resource bundle handling with the {@link #name()}.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FXController {

  /**
   * @return name of the files fxml, css, bundle
   */
  String name();

}
