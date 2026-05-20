package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.transformation.FilteredList;

import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.fzgkonfig.content.modell.ModellItem;
import de.vw.paso.client.stueckliste.fzgkonfig.content.modell.ModellTreeModel;
import de.vw.paso.service.modelimport.ModelDTO;

public class ModelDisplayStrategy {

    public ModellTreeModel createDisplayModel(final FilteredList<ModelDTO> nodes) {
        final ModellTreeModel modellTreeModel = new ModellTreeModel();

        sort(nodes).forEach(model -> addNode(modellTreeModel, model));

        return modellTreeModel;
    }

    private static List<ModelDTO> sort(final Collection<ModelDTO> nodes) {
        return nodes.stream().sorted(Comparator.comparing(ModelDTO::getModelKey)).collect(Collectors.toList());
    }

    private ModellItem addNode(final AbstractTreeModel<ModellItem, ModelDTO> model, final ModelDTO node) {
        return model.addElement(node, true);
    }
}
