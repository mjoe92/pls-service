package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.delegate.stueckliste.partlistviewgroup.PartListViewGroupRestClientHolder;
import de.vw.paso.partlist.domain.ApCompareGroup;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.partlistviewgroup.PartListViewGroupDTO;
import de.vw.paso.utility.EfsElementUtil;
import de.vw.paso.utility.EfsWeightUtil;
import de.vw.paso.utility.StringConstant;

public class PartListGroupDisplayStrategy extends AbstractDisplayStrategyForTrees<EfsElementDTO> {

    private static final String ENGINE_CODE = "ZP4";
    private static final String GEARBOX_CODE = "ZP3";

    private final EfsElementDTO engine;
    private final EfsElementDTO gearbox;

    private EfsElementTreeModel elementTreeModel;
    private Collection<EfsElementDTO> nodes;
    private Collection<PartListViewGroupDTO> partListViewGroups;

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithoutDeletion(
            Collection<Filter<EfsElementDTO>> filters, PartListViewMode viewMode, EfsElementDTO motor,
            EfsElementDTO gearbox) {

        return new PartListGroupDisplayStrategy(false, filters, viewMode, motor, gearbox);
    }

    public static AbstractDisplayStrategyForTrees<EfsElementDTO> getStrategyWithDeletion(
            Collection<Filter<EfsElementDTO>> filters, PartListViewMode viewMode, EfsElementDTO motor,
            EfsElementDTO gearbox) {

        return new PartListGroupDisplayStrategy(true, filters, viewMode, motor, gearbox);
    }

    private PartListGroupDisplayStrategy(boolean showDeletedElements, Collection<Filter<EfsElementDTO>> filters,
            PartListViewMode partListViewMode, EfsElementDTO engine, EfsElementDTO gearbox) {
        super(showDeletedElements, filters);
        this.engine = engine;
        this.gearbox = gearbox;

        loadPartListViewGroups(partListViewMode);
    }

    private void loadPartListViewGroups(PartListViewMode partListViewMode) {
        partListViewGroups = PartListViewGroupRestClientHolder.getInstance()
                .loadPartListViewGroupsByPartListViewMode(partListViewMode).partListViewGroupDTOList();
    }

    @Override
    public AbstractTreeModel<EfsElementTreeItem, EfsElementDTO> createDisplayModel(Collection<EfsElementDTO> nodes) {
        EfsElementDTO efs = createRootEfsElement(null);

        this.nodes = nodes;
        this.elementTreeModel = new EfsElementTreeModel(efs);

        buildTreeModel(efs);

        return elementTreeModel;
    }

    @Override
    public EfsElementDTO getParentForCreatingNewElement(List<EfsElementDTO> efsElements) {
        return null;
    }

    @Override
    public boolean allowsCopy() {
        return false;
    }

    @Override
    public boolean allowsMove() {
        return false;
    }

    @Override
    public void updateNode(IAggregatedEfsTreeModel model, EfsElementDTO nodeToUpdate) {
        model.updateNode(nodeToUpdate, isNodeValid(nodeToUpdate), true);
    }

    @Override
    protected <TI extends AbstractTreeItem<EfsElementDTO>> TI addNode(AbstractTreeModel<TI, EfsElementDTO> model,
            EfsElementDTO node) {
        return model.addElement(node, true);
    }

    private void buildTreeModel(EfsElementDTO efs) {
        partListViewGroups.forEach(partListViewGroup -> {
            Collection<EfsElementDTO> efsElements = new ArrayList<>();
            Collection<EfsElementDTO> parents = new ArrayList<>();
            EfsElementDTO efsRoot = createRootEfsElement(efs);

            EfsElementTreeItem efsElementGroupTreeItem = new EfsElementTreeItem(efsRoot,
                    new SimpleObjectProperty<>(efsRoot.getEfsElementMara()));
            efsElementGroupTreeItem.setPropertyGroupString(partListViewGroup.getName());

            elementTreeModel.getRoot().getSourceChildren().add(efsElementGroupTreeItem);

            EfsElementTreeItem groupRootTreeItem = filterEfsElementsForViewGroup(partListViewGroup, efsElements,
                    parents, efsRoot);

            createRootTreeItemsInGroups(efsElements, parents, efsElementGroupTreeItem, groupRootTreeItem, efsRoot);

            efsElements = EfsElementUtil.sortByCheckingStructure(efsElements, efsRoot);

            addAllEfsElementToTreeModel(efsElements);

            efsElements.addAll(efsRoot.getChildren());

            efsElementGroupTreeItem.setWeightNode(
                    EfsWeightUtil.calculate(efsElements).getOrDefault(ApCompareGroup.SUM, 0d));
        });
    }

    private EfsElementTreeItem filterEfsElementsForViewGroup(PartListViewGroupDTO partListViewGroup,
            Collection<EfsElementDTO> efsElements, Collection<EfsElementDTO> parents, EfsElementDTO efsRoot) {
        EfsElementTreeItem groupRootTreeItem = null;
        if (partListViewGroup.getName().contains(ENGINE_CODE)) {
            if (engine != null) {
                efsRoot.getChildren().add(engine);
                groupRootTreeItem = new EfsElementTreeItem(engine,
                        new SimpleObjectProperty<>(engine.getEfsElementMara()));

                efsElements.addAll(EfsElementUtil.sortByCheckingStructure(getAllChildren(engine), engine));
            }
        } else if (partListViewGroup.getName().contains(GEARBOX_CODE)) {
            if (gearbox != null) {
                efsRoot.getChildren().add(gearbox);
                groupRootTreeItem = new EfsElementTreeItem(gearbox,
                        new SimpleObjectProperty<>(gearbox.getEfsElementMara()));

                efsElements.addAll(EfsElementUtil.sortByCheckingStructure(getAllChildren(gearbox), gearbox));
            }
        } else if (partListViewGroup.getCostGroup() != null) {
            Collection<EfsElementDTO> allEfsElement = new ArrayList<>(nodes);

            if (partListViewGroup.getCostGroup().length() == 1) {
                Collection<EfsElementDTO> startWithCostGroup = allEfsElement.stream()
                        .filter(efsElement -> efsElement.getCostGroup() != null && efsElement.getCostGroup()
                                .startsWith(partListViewGroup.getCostGroup())).toList();
                efsElements.addAll(startWithCostGroup);
            } else {
                Collection<EfsElementDTO> costGroups = allEfsElement.stream()
                        .filter(efsElement -> efsElement.getCostGroup() != null && efsElement.getCostGroup()
                                .equals(partListViewGroup.getCostGroup())).toList();
                efsElements.addAll(costGroups);
            }

            if (partListViewGroup.getPartGroups() != null) {
                String[] split = partListViewGroup.getPartGroups().split(StringConstant.SEMICOLON);
                Collection<EfsElementDTO> elementsByPartGroups = nodes.stream()
                        .filter(efsElement -> isNotRissAndMara(split, efsElement)).toList();
                efsElements.addAll(elementsByPartGroups);
            }

            Collection<EfsElementDTO> allParents = efsElements.stream()
                    .filter(efsElement -> efsElement.getParent() == null || !efsElements.contains(
                            efsElement.getParent())).toList();
            parents.addAll(allParents);
        }

        return groupRootTreeItem;
    }

    private void addAllEfsElementToTreeModel(Collection<EfsElementDTO> efsElements) {
        for (EfsElementDTO efsElement : efsElements) {
            if (isNodeValid(efsElement)) {
                addNode(elementTreeModel, efsElement);
            }
        }
    }

    private EfsElementDTO createRootEfsElement(EfsElementDTO efs) {
        EfsElementDTO efsRoot = PartListFactory.createEfsElement();
        EfsElementMaraDTO efsElementMaraRoot = PartListFactory.createEfsElementMara();

        efsRoot.setEfsElementMara(efsElementMaraRoot);
        efsRoot.setParent(efs);
        efsRoot.setChildren(new ArrayList<>());

        return efsRoot;
    }

    private boolean isNotRissAndMara(String[] partGroups, EfsElementDTO efsElement) {
        if (efsElement.getPartNumber().equals(SpecialPartNumberType.GAP.getLabel())) {
            return false;
        }

        for (String partGroup : partGroups) {
            String mgr = partGroup.substring(0, 3);
            String ugr = partGroup.substring(4);
            if (efsElement.getEfsElementMara().getPartNumberMittelgruppe().equals(mgr) && efsElement.getEfsElementMara()
                    .getPartNumberEndNumber().equals(ugr)) {
                return true;
            }
        }

        return false;
    }

    private void createRootTreeItemsInGroups(Collection<EfsElementDTO> efsElements, Collection<EfsElementDTO> parents,
            EfsElementTreeItem efsElementGroupTreeItem, EfsElementTreeItem groupRootTreeItem, EfsElementDTO efsRoot) {
        if (!parents.isEmpty()) {
            efsElements.removeAll(parents);
            efsRoot.getChildren().addAll(parents);

            for (EfsElementDTO efsElement : parents) {
                elementTreeModel.addElement(efsElementGroupTreeItem,
                        new EfsElementTreeItem(efsElement, new SimpleObjectProperty<>(efsElement.getEfsElementMara())));
            }
        } else if (groupRootTreeItem != null) {
            elementTreeModel.addElement(efsElementGroupTreeItem, groupRootTreeItem);
        }
    }
}