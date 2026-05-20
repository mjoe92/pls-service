package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.Collection;
import java.util.List;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.utility.EfsElementUtil;

public class FlatDisplayStrategy extends AbstractDisplayStrategyForTrees<EfsElementDTO> {

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithoutDeletion(
            Collection<Filter<EfsElementDTO>> filters) {
        return new FlatDisplayStrategy(false, filters);
    }

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithDeletion(
            Collection<Filter<EfsElementDTO>> filters) {
        return new FlatDisplayStrategy(true, filters);
    }

    private FlatDisplayStrategy(boolean showDeletedElements, Collection<Filter<EfsElementDTO>> filters) {
        super(showDeletedElements, filters);
    }

    public EfsElementTreeModel createDisplayModel(Collection<EfsElementDTO> nodes) {
        Collection<EfsElementDTO> sortedNodes = EfsElementUtil.sortParentsFirst(nodes);
        EfsElementDTO efs = PartListFactory.createEfsElement();
        EfsElementMaraDTO efsElementMara = PartListFactory.createEfsElementMara();

        efs.setEfsElementMara(efsElementMara);

        EfsElementTreeModel model = new EfsElementTreeModel(efs);

        for (EfsElementDTO node : sortedNodes) {
            if (isNodeValid(node)) {
                addNode(model, node);
            }
        }

        return model;
    }

    @Override
    protected <TI extends AbstractTreeItem<EfsElementDTO>> TI addNode(AbstractTreeModel<TI, EfsElementDTO> model,
            EfsElementDTO node) {
        return model.addElement(node, false);
    }

    @Override
    public EfsElementDTO getParentForCreatingNewElement(List<EfsElementDTO> efsElements) {
        return null;
    }

    @Override
    public void updateNode(IAggregatedEfsTreeModel model, EfsElementDTO nodeToUpdate) {
        model.updateNode(nodeToUpdate, isNodeValid(nodeToUpdate), false);
    }

    @Override
    public boolean allowsCopy() {
        return false;
    }

    @Override
    public boolean allowsMove() {
        return false;
    }
}