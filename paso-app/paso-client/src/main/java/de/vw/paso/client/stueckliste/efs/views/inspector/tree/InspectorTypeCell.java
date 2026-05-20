package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;

import de.vw.paso.client.util.icon.StatusIcon;
import de.vw.paso.partlist.domain.inspector.InspectorSeverity;

public class InspectorTypeCell extends TreeTableCell<InspectorTreeItemObject, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        setText(null);

        if (item == null) {
            return;
        }

        setText(item);

        TreeItem<InspectorTreeItemObject> treeItem = getTableRow().getTreeItem();
        if (treeItem != null && treeItem.getValue().isTypeNode()) {
            ImageView severityImage = createImageForSeverity(treeItem.getValue().getType().getSeverity());
            setGraphic(severityImage);
        }
    }

    private ImageView createImageForSeverity(InspectorSeverity severity) {
        return new ImageView(switch (severity) {
            case ERROR -> StatusIcon.ERROR_16X16.getImage();
            case WARNING -> StatusIcon.WARNING_16X16.getImage();
            case INFO -> StatusIcon.INFO_16X16.getImage();
        });
    }
}
