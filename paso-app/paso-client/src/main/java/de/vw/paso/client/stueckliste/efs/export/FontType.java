package de.vw.paso.client.stueckliste.efs.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FontType {

  ARIEL("Arial"),
  CALIBRI("Calibri"),
  COURIER_NEW("Courier New"),
  TIMES_NEW_ROMAN("Times New Roman");

  private final String fontName;

}
