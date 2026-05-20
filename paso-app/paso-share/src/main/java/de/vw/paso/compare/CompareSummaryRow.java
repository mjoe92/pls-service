package de.vw.paso.compare;

import java.util.List;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompareSummaryRow {

    private EfsElementDTO firstEfsElement;
    private EfsElementDTO secondEfsElement;
    private Double deltaWeight;
    private String changeState;
    private List<String> changedColumnNames;
    private boolean isChange = false;

}
