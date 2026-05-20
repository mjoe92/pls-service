package de.vw.paso.pls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusResponseWithProductKey {

  private String productKey;

  private Status status;

  private String message;

}
