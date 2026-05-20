package de.vw.paso.client.control.cell;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RemoveSummaryHighlightStylingEvent<T> {

  @Getter
  private T eventOrigin;

  public RemoveSummaryHighlightStylingEvent(T value) {
    eventOrigin = value;
  }
}
