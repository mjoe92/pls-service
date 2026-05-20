package de.vw.paso.client.stueckliste.compare.partlist.combine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.stueckliste.compare.partlist.PartListCompareRow;
import de.vw.paso.client.stueckliste.compare.partlist.PartListCompareStatus;
import de.vw.paso.client.stueckliste.compare.partlist.PartlistCompareTreeItem;
import de.vw.paso.client.stueckliste.compare.partlist.combine.nodematcher.INodeIdentityProvider;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.utility.EfsElementResolver;
import de.vw.paso.utility.EfsElementUtil;
import lombok.Getter;

public class DefaultCombineStrategy implements ITreeCombineStrategy {

  @Getter
  private INodeIdentityProvider<?> identityProvider;

  public DefaultCombineStrategy(INodeIdentityProvider<?> identityProvider) {
    this.identityProvider = identityProvider;
  }

  @Override
  public PartlistCompareTreeItem createTree(List<VehiclePartListDTO> partLists) {
    Map<VehiclePartListDTO, List<EfsElementDTO>> dataMap = new HashMap<>();
    partLists.parallelStream().forEach(partList -> {
      Set<EfsElementDTO> elementsInPartList = EfsElementResolver.getElementsInPartList(partList);
      Set<EfsElementDTO> filteredElements = elementsInPartList.stream().filter(e -> !e.isDeleted())
        .collect(Collectors.toSet());
      dataMap.put(partList, EfsElementUtil.sortByCheckingStructure(filteredElements));
    });

    PartlistCompareTreeItem root = new PartlistCompareTreeItem(null);
    Map<Object, PartlistCompareTreeItem> treeItemMap = new HashMap<>();
    List<EfsElementDTO> efsElements = dataMap.get(partLists.get(0));
    for (EfsElementDTO element : efsElements) {
      addElement(element, root, treeItemMap, PartListCompareStatus.DELETED);
    }

    for (int i = 1; i < partLists.size(); i++) {
      List<EfsElementDTO> nextDataSet = dataMap.get(partLists.get(i));

      combine(nextDataSet, root, treeItemMap);
    }

    sortTree(root);

    return root;
  }

  private void sortTree(PartlistCompareTreeItem root) {
    root.getSourceChildren().sort((o1, o2) -> {
      EfsElementDTO baseElement1 = o1.getValue().getBaseElement();
      EfsElementDTO baseElement2 = o2.getValue().getBaseElement();
      if (baseElement1 == baseElement2) {
        return 0;
      }
      if (baseElement1 == null) {
        return -1;
      }
      if (baseElement2 == null) {
        return 1;
      }
      Long tisSort1 = baseElement1.getTisSort();
      Long tisSort2 = baseElement2.getTisSort();
      // Implicit null check.
      if (Objects.equals(tisSort1, tisSort2)) {
        return 0;
      }
      if (tisSort1 == null) {
        return -1;
      }
      if (tisSort2 == null) {
        return 1;
      }
      return tisSort1.compareTo(tisSort2);
    });
    for (TreeItem<PartListCompareRow> child : root.getSourceChildren()) {
      sortTree((PartlistCompareTreeItem) child);
    }
  }

  private void addElement(EfsElementDTO element, PartlistCompareTreeItem root,
    Map<Object, PartlistCompareTreeItem> treeItemMap, PartListCompareStatus status) {
    PartlistCompareTreeItem treeITem = new PartlistCompareTreeItem(new PartListCompareRow(element, status));
    Object identity = identityProvider.getIdentity(element);
    treeItemMap.put(identity, treeITem);

    EfsElementDTO parent = element.getParent();
    if (parent == null) {
      root.getSourceChildren().add(treeITem);
    } else {
      Object parentIdentity = identityProvider.getIdentity(parent);
      PartlistCompareTreeItem parentTreeItem = treeItemMap.get(parentIdentity);
      parentTreeItem.getSourceChildren().add(treeITem);
    }
  }

  private void combine(List<EfsElementDTO> efsElements, PartlistCompareTreeItem root,
    Map<Object, PartlistCompareTreeItem> treeItemMap) {
    for (EfsElementDTO element : efsElements) {
      Object identity = identityProvider.getIdentity(element);
      PartlistCompareTreeItem treeItem = treeItemMap.get(identity);
      if (treeItem != null) {
        PartListCompareRow value = treeItem.getValue();
        value.addElement(element);
        boolean rowHasDifferentValues = calculateStatus(value);
        if (rowHasDifferentValues) {
          value.setRowStatus(PartListCompareStatus.CHANGED);
        } else if (value.getRowStatus() != PartListCompareStatus.CHANGED) {
          value.setRowStatus(PartListCompareStatus.UNCHANGED);
        }
      } else {
        addElement(element, root, treeItemMap, PartListCompareStatus.ADDED);
      }
    }
  }

  private boolean calculateStatus(PartListCompareRow value) {
    return PartListCompareUtil.calculatePropertyChanges(value);
  }
}
