package de.vw.paso.client.main.ribbonmenu;

import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.control.RadioMenuItem;

public class DropDownMenuItem extends RadioMenuItem {

    private final Supplier<Node> createIconFunction;

    public DropDownMenuItem(String s, Supplier<Node> createIconFunction) {
        super(s, createIconFunction.get());
        this.createIconFunction = createIconFunction;
    }

    public Node createIcon() {
        return createIconFunction.get();
    }
}
