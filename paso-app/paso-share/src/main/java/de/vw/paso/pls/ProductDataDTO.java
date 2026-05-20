package de.vw.paso.pls;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDataDTO {

    private String id;
    private String productId;
    private PartListStatus status;
    private Date importDate;

    public String getId() {
        return id;
    }
}
