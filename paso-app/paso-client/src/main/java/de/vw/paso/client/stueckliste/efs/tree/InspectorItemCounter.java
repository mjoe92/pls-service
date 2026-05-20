package de.vw.paso.client.stueckliste.efs.tree;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InspectorItemCounter {

    private int completeCount;
    private int aggregateCount;
    private Set<Long> elementIdsInInspector;
}
