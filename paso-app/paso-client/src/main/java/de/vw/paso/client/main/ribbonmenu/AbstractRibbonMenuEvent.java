package de.vw.paso.client.main.ribbonmenu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractRibbonMenuEvent<T> {

  private T listener;
  private String title;

  public AbstractRibbonMenuEvent(T listener) {
    this(listener, null);
  }
}
