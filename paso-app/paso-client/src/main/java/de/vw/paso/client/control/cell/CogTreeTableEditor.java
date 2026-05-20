package de.vw.paso.client.control.cell;

import javafx.scene.control.Label;

import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.stueckliste.util.CogUtil;

public class CogTreeTableEditor extends AbstractCogCellNode<PasoCustomTextField> {

    CogTreeTableEditor() {
        super(false);
    }

    @Override
    protected void addPartNodes(PasoCustomTextField x, PasoCustomTextField y, PasoCustomTextField z) {
        getChildren().addAll(x, new Label(CogUtil.COG_SEPARATOR), y, new Label(CogUtil.COG_SEPARATOR), z);
    }

    @Override
    protected PasoCustomTextField createPartNode() {
        PasoCustomTextField<String> tf = new PasoCustomTextField<>();
        tf.setPrefWidth(65);
        tf.setMinWidth(65);
        tf.setValidation(e -> CogUtil.isValidCoordinate(tf.getText()));
        return tf;
    }

    @Override
    protected void setText(PasoCustomTextField node, String text) {
        node.setText(text);
    }

    @Override
    protected String getText(PasoCustomTextField node) {
        return node.getText();
    }

    CogCoordinates getCogCoordinates() {
        return new CogCoordinates(CogUtil.parseCoordinate(getXNode().getText()),
                CogUtil.parseCoordinate(getYNode().getText()), CogUtil.parseCoordinate(getZNode().getText()));
    }

    boolean isCogValid() {
        return CogUtil.isValidCoordinate(getXNode().getText()) && CogUtil.isValidCoordinate(getYNode().getText())
                && CogUtil.isValidCoordinate(getZNode().getText());
    }
}
