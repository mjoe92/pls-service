package de.vw.paso.client.main.ribbonmenu;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.TextAlignment;

import de.vw.paso.client.control.ribbonmenu.RibbonMenuButton;

public class RibbonDropDownButton extends RibbonMenuButton {

    private ToggleGroup toggleGroup = new ToggleGroup();

    private Consumer<DropDownMenuItem> onSelectionChangedCallback;

    public RibbonDropDownButton(String text) {
        super(text);

        init();
    }

    private void init() {
        getStyleClass().remove(STYLE_RIBBON_BUTTON);
        getStyleClass().add(STYLE_RIBBON_MENU_BUTTON);
        setPrefWidth(130);
        setAlignment(Pos.CENTER);
        setTextAlignment(TextAlignment.CENTER);

        toggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            DropDownMenuItem item = (DropDownMenuItem) newValue;
            setSelectedItem(item);
            if (onSelectionChangedCallback != null) {
                onSelectionChangedCallback.accept(item);
            }
        });
    }

    public void addMenuItem(DropDownMenuItem item) {
        item.setToggleGroup(toggleGroup);
        getItems().add(item);
    }

    public void setSelectedItem(DropDownMenuItem item) {
        item.setSelected(true);
        setText(item.getText());
        setGraphic(item.createIcon());
    }

    public void setOnSelectionChanged(Consumer<DropDownMenuItem> callback) {
        onSelectionChangedCallback = callback;
    }
}
