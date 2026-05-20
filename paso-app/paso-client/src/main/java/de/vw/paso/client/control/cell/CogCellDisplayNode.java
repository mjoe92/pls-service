package de.vw.paso.client.control.cell;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

import de.vw.paso.client.stueckliste.util.CogUtil;

class CogCellDisplayNode extends AbstractCogCellNode<Label> {

    CogCellDisplayNode() {
        super(true);
    }

    @Override
    protected void addPartNodes(Label x, Label y, Label z) {
        getChildren().addAll(new Label("["), x, new Label(CogUtil.COG_SEPARATOR), y, new Label(CogUtil.COG_SEPARATOR),
                z, new Label("]"));
    }

    @Override
    protected Label createPartNode() {
        return createLabel();
    }

    private Label createLabel() {
        Label l = new Label();
        l.setTextAlignment(TextAlignment.RIGHT);
        l.setAlignment(Pos.CENTER_RIGHT);
        l.setPrefWidth(42);
        l.setMinWidth(42);
        return l;
    }

    @Override
    protected void setText(Label node, String text) {
        node.setText(text);
    }

    @Override
    protected String getText(Label node) {
        return node.getText();
    }
}
