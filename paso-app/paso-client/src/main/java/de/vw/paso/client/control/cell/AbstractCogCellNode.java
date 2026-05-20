package de.vw.paso.client.control.cell;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

import de.vw.paso.client.stueckliste.util.CogUtil;

public abstract class AbstractCogCellNode<T extends Node> extends HBox {

    private T xNode;
    private T yNode;
    private T zNode;

    private boolean rounded;

    AbstractCogCellNode(boolean rounded) {
        this.rounded = rounded;
        xNode = createPartNode();
        yNode = createPartNode();
        zNode = createPartNode();
        addPartNodes(xNode, yNode, zNode);
    }

    protected abstract void addPartNodes(T x, T y, T z);

    protected abstract T createPartNode();

    void setCoordinates(CogCoordinates coord) {
        setText(xNode, coordinateToString(coord.getCogX()));
        setText(yNode, coordinateToString(coord.getCogY()));
        setText(zNode, coordinateToString(coord.getCogZ()));
    }

    protected abstract void setText(T node, String text);

    protected abstract String getText(T node);

    private String coordinateToString(Double d) {
        return CogUtil.format(d, rounded);
    }

    T getXNode() {
        return xNode;
    }

    T getYNode() {
        return yNode;
    }

    T getZNode() {
        return zNode;
    }
}
