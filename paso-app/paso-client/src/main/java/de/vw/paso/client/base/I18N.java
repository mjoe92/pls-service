package de.vw.paso.client.base;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.vw.paso.client.PasoApplication;
import de.vw.paso.client.util.UserProperties;

public final class I18N {

  private static final String DEFAULT_RESOURCE_BUNDLE_NAME =
    PasoApplication.class.getPackage().getName() + ".application-bundle";

  private static final ResourceBundle RESOURCE_BUNDLE;

  static {
    RESOURCE_BUNDLE = ResourceBundle.getBundle(I18N.DEFAULT_RESOURCE_BUNDLE_NAME,
      Locale.of(UserProperties.getPreferredLanguage()));
  }

  private I18N() {
    throw new IllegalArgumentException("Util class");
  }

  public static ResourceBundle getBundle() {
    return RESOURCE_BUNDLE;
  }

  public static String getString(String key) {
    return RESOURCE_BUNDLE.getString(key);
  }

  public static String getString(String key, Object... args) {
    return MessageFormat.format(RESOURCE_BUNDLE.getString(key), args);
  }
}
