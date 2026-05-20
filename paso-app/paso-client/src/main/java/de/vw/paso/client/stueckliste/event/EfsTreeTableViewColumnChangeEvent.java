package de.vw.paso.client.stueckliste.event;

import java.util.List;

import javafx.scene.control.TreeTableColumn;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EfsTreeTableViewColumnChangeEvent {

    private final List<TreeTableColumn<EfsElementDTO, ?>> list;

}
