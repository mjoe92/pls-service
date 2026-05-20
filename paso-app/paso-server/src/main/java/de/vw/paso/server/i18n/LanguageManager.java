package de.vw.paso.server.i18n;

import java.util.Locale;

public class LanguageManager {

  private static LanguageManager instance;

  private final Locale defaultLocale = Locale.of("en", "EN");
  private final Locale currentLocale = Locale.getDefault();

  private LanguageManager() {
  }

  public static LanguageManager getInstance() {
    if (instance == null) {
      instance = new LanguageManager();
    }
    return instance;
  }

  public Locale getCurrentLocale() {
    if (currentLocale == null) {
      return defaultLocale;
    }
    return currentLocale;
  }

}
