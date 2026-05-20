package de.vw.paso.client.i18n;

import java.util.Arrays;

public enum AvailableLanguages {
  EN("English"),
  DE("Deutsch");

  public final String language;

  private AvailableLanguages(String lang) {
    this.language = lang;
  }

  public static boolean isAvailable(String lang) {
    return Arrays.stream(AvailableLanguages.values()).anyMatch(availableLanguage -> availableLanguage.name().equals(lang));
  }

  @Override
  public String toString() {
    return this.language;
  }
}
