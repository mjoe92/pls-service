package de.vw.paso.client.stueckliste.compare.partlist.combine;

import java.util.List;

import de.vw.paso.client.stueckliste.compare.partlist.PartlistCompareTreeItem;
import de.vw.paso.service.user.VehiclePartListDTO;

public interface ITreeCombineStrategy {

    PartlistCompareTreeItem createTree(List<VehiclePartListDTO> partLists);

}
