package de.vw.paso.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NewOldWrapper<T> {
  private T oldElement;
  private T newElement;
}
