package de.vw.paso.client.util.highlight;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import de.vw.paso.client.control.cell.ReadOnlyTreeTableCell;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import lombok.Getter;

public class SelectionHighlightManagerUtil {

    private static BooleanProperty dohighLight = new SimpleBooleanProperty(true);
    private static BooleanProperty controlPressed = new SimpleBooleanProperty(false);

    public static BooleanProperty getDohighLightProperty() {
        return dohighLight;
    }

    public static BooleanProperty getControlPressed() {
        return controlPressed;
    }

    @Getter
    private static Map<TreeTableView, TreeItem> summaryItemMap = new HashMap<>();
    @Getter
    private static Map<TreeTableView, TreeTableColumn> summaryItemColumnMap = new HashMap<>();

    public static boolean toggleDoHighlighting() {
        dohighLight.set(!dohighLight.get());
        return dohighLight.get();
    }

    public static void toggleControlPressed() {
        controlPressed.set(!controlPressed.get());
    }

    public static boolean isDohighLight() {
        return getDohighLightProperty().get();
    }

    public static boolean isControlPressed() {
        return getControlPressed().get();
    }

    public static Image getCrossHighlightImage() {
        if (SelectionHighlightManagerUtil.isDohighLight()) {
            return StuecklisteIcon.CROSSHIGHLIGHT_32X32.getImage();
        } else {
            return StuecklisteIcon.SINGLEHIGHLIGHT_32X32.getImage();
        }
    }

    public static void toggleHighlighting(RibbonButton buttonCrossHighlightToggle) {
        boolean newState = SelectionHighlightManagerUtil.toggleDoHighlighting();
        if (newState) {
            buttonCrossHighlightToggle.setGraphic(new ImageView(StuecklisteIcon.CROSSHIGHLIGHT_32X32.getImage()));
        } else {
            buttonCrossHighlightToggle.setGraphic(new ImageView(StuecklisteIcon.SINGLEHIGHLIGHT_32X32.getImage()));
        }
    }

    public static <S, T> void updateSummaryMaps(ReadOnlyTreeTableCell readOnlyCell, Boolean addToMaps) {
        final TreeTableView treeTableView = readOnlyCell.getTreeTableView();
        if (summaryItemMap.get(treeTableView) != null && summaryItemColumnMap.get(treeTableView) != null
                && !addToMaps) {
            summaryItemMap.remove(treeTableView);
            summaryItemColumnMap.remove(treeTableView);
            readOnlyCell.updateItem(readOnlyCell.getItem(), readOnlyCell.isEmpty());
        }
        if (addToMaps) {
            summaryItemMap.put(treeTableView, readOnlyCell.getTreeTableRow().getTreeItem());
            summaryItemColumnMap.put(treeTableView, readOnlyCell.getTableColumn());
        }
    }

}
