package de.vw.paso.client.stueckliste.compare.partlist.combine.selection;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectionResult {

    private boolean checkPath = true;
    private boolean combineAll = true;

    private List<MethodWrapper> selectedProperties = new ArrayList<>();

}
