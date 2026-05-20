package de.vw.paso.service.pls;

import java.util.Date;

public record CreateSubPartListDTO(Long efsElementId, String productDataId, String prNumbers, Date date) {
}
