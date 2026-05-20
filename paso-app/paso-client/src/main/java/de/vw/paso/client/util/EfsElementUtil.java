package de.vw.paso.client.util;

import java.util.List;

import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class EfsElementUtil {

    public static Integer countNumberOfPartsForItem(ApCompareGroup ap, List<EfsElementDTO> treeItem) {
        int count = treeItem.stream().filter(e -> ap.containsAp(e.getAp())).mapToInt(efsElementDTO -> {
            if (efsElementDTO.getQuantityUnit().equals(QuantityUnit.PIECE.getShortName())) {
                return efsElementDTO.getQuantity();
            } else {
                return 1;
            }
        }).sum();

        return count == 0 ? null : count;
    }

    public static Integer countNumberOfPartsSumForItem(List<EfsElementDTO> treeItem) {
        int count = treeItem.stream().mapToInt(efsElementDTO -> {
            if (efsElementDTO.getQuantityUnit().equals(QuantityUnit.PIECE.getShortName())) {
                return efsElementDTO.getQuantity();
            } else {
                return 1;
            }
        }).sum();

        return count == 0 ? null : count;
    }
}
