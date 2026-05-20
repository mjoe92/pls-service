package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;

public class FgSetTreeObject extends AggregatedEfsTreeObject<SetKeyDTO> {

    public FgSetTreeObject() {
        this(new SetKeyDTO(null, null, null, null));
    }

    public FgSetTreeObject(SetKeyDTO setKeyDTO) {
        this(setKeyDTO, new ArrayList<>());
    }

    public FgSetTreeObject(SetKeyDTO setKeyDTO, List<EfsElementDTO> efsElements) {
        super(setKeyDTO, efsElements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getId() {
        return getAggregationObject().getSetKeyName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getParentId() {
        return getAggregationObject().getParentName();
    }

    public boolean isKnown() {
        return Objects.nonNull(getAggregationObject()) && Objects.nonNull(getAggregationObject().getDescription());
    }
}
