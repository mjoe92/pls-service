package de.vw.paso.client.stueckliste.efs.views.inspector.solver.gws;

import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.views.inspector.solver.AbstractSolutionDialog;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.partlist.domain.WeightControlFlag;

public class GWSConfirmDialog extends AbstractSolutionDialog<Boolean> {

    private final WeightControlFlag gws;

    public GWSConfirmDialog(Collection<TreeItem<InspectorTreeItemObject>> selectedItems, WeightControlFlag gws) {
        super(selectedItems);
        this.gws = gws;

        initialize(I18N.getString("gws.dialog.title"), this::initDialog);
    }

    private void initDialog() {
        setHeaderText(I18N.getString("gws.dialog.headerText"));
        initContent();
        addStylesheet();
        commitButton.setDisable(false);
    }

    private void initContent() {
        double weightDiff = GWSSolutionCalculator.calculateGWSWeightDiff(getSelectedItems(), gws);
        Label label = new Label(I18N.getString("gws.dialog.numberOfAffectedItems.label.colon", weightDiff));

        GridPane content = new GridPane();
        content.setHgap(5);
        content.setVgap(5);
        content.add(label, 0, 0, 2, 1);
        getDialogPane().setContent(content);
    }

    @Override
    protected ChangeListener<?> getValidationListener() {
        return null;
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected Boolean dialogResult() {
        return true;
    }
}
