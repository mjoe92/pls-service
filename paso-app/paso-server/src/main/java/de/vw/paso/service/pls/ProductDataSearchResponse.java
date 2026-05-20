package de.vw.paso.service.pls;

import de.vw.paso.pls.PartListStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDataSearchResponse {

  private String id;
  private String productId;
  private PartListStatus importStatus;
  private String importDate;

}
