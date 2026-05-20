package de.vw.paso.client.control.ribbonmenu;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class RibbonMenuGroupItemHBox extends HBox {

    public RibbonMenuGroupItemHBox() {
        super();
        init();
    }

    private void init() {
        this.getStyleClass().add("ribbon-menu-group-item-hbox");
    }

    public void addButton(RibbonButton... button) {
        this.getChildren().addAll(button);
    }

    public void addToggleButton(RibbonMenuToggleButton button) {
        this.getChildren().add(button);
    }

    public void addItemBox(Node node) {
        this.getChildren().add(node);
    }
}