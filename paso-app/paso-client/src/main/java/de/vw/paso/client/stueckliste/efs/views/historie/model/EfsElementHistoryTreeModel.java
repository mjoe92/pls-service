package de.vw.paso.client.stueckliste.efs.views.historie.model;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.AbstractEfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementHistoryTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.client.stueckliste.efs.views.historie.EfsHistoryUtil;
import de.vw.paso.client.stueckliste.efs.views.historie.cell.AenderungsartTreeTableCell;
import de.vw.paso.client.util.ReflectionUtil;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementDTOWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EfsElementHistoryTreeModel extends AbstractTreeModel<EfsElementHistoryTreeItem, IEfsElementForDTO>
        implements IAggregatedEfsTreeModel {

    private static final Logger LOG = LoggerFactory.getLogger(EfsElementHistoryTreeModel.class);

    public EfsElementHistoryTreeModel(EfsElementDTOWrapper efsElement) {
        super(efsElement);
    }

    public void setEfsHistoryElemente(Collection<EfsElementDTOWrapper> efsHistoryElements) {
        removeAllElements();

        Comparator<IEfsElementForDTO> comparator = Comparator.comparing(IEfsElementForDTO::getRevision);
        List<IEfsElementForDTO> castedList = efsHistoryElements.stream().map(e -> (IEfsElementForDTO) e)
                .sorted(comparator.reversed()).toList();

        addElements(castedList);

        calculateChangeType();

        compareRevisions();
    }

    private void calculateChangeType() {
        for (EfsElementHistoryTreeItem treeItem : getTreeItems()) {
            // if the AbstractEfsElement has no lower revisions, it is of type "TYPE_NEW"
            if (!(EfsHistoryUtil.hasLowerRevision(getTreeItems(), treeItem))) {
                treeItem.setTypeChange(AenderungsartTreeTableCell.TYPE_NEW);
            } else if (treeItem.isDeleted()) {
                treeItem.setTypeChange(AenderungsartTreeTableCell.TYPE_DELETE);
            } else {
                treeItem.setTypeChange(AenderungsartTreeTableCell.TYPE_UPDATE);
            }
        }
    }

    private void compareRevisions() {
        for (EfsElementHistoryTreeItem element : getTreeItems()) {
            for (String propertyName : AbstractEfsElementTreeItem.getPropertyNamesCompare()) {
                /*
                 * TODO DSt: Instead of using some names and reflection, we should register some lambdas or something, so that the getter are not shown as 'not used'
                 */
                Method m = ReflectionUtil.getGetter(element.getClass(), propertyName);

                if (m == null) {
                    LOG.warn("{} defined for comparing but not getter method found. Check EfsElementHistoryTreeItem!",
                            propertyName);
                    return;
                }

                try {
                    EfsElementHistoryTreeItem lowerRev = EfsHistoryUtil.getLowerRevision(getTreeItems(), element);
                    // if a minor revision is found, the columns must be compared
                    if (lowerRev == null) {
                        return;
                    }

                    Object objCurrent = m.invoke(element);
                    Object objLower = m.invoke(lowerRev);

                    if (objCurrent == null && objLower == null) {
                        element.setChange(propertyName, false);
                    } else if (objCurrent == null || !objCurrent.equals(objLower)) {
                        element.setChange(propertyName, true);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error while invoking method " + m.getName() + " on object " + element);
                }
            }
        }
    }

    @Override
    protected EfsElementHistoryTreeItem createTreeItem(IEfsElementForDTO userObject) {
        ObjectProperty<AbstractEfsElementMaraDTO> propertyEfsElementMara = new SimpleObjectProperty<>(
                userObject.getEfsElementMara());

        EfsElementHistoryTreeItem treeItem = new EfsElementHistoryTreeItem(
                ((EfsElementDTOWrapper) userObject).getEfsElement(), propertyEfsElementMara);
        cacheTreeItem(treeItem);
        return treeItem;
    }

    @Override
    public void updateNode(EfsElementDTO nodeToUpdate, boolean isNodeValid, boolean isHierarchical) {
        //nothing to update
    }
}
