package de.vw.paso.client.explorer.vehicleconfig.tree;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.util.icon.BrandIcon;
import de.vw.paso.masterdata.Brand;

public abstract class AbstractFzgConfigCell<T extends VehicleConfigTreeObj> extends TreeCell<T> {

    private static final int FAVORITE_ICON_LARGE_SIZE = 18;
    private static final int FAVORITE_ICON_SMALL_SIZE = 16;

    protected static final String CELL_STYLE_CLASS_NO_PRODUCT = "fzg-config-tree-cell-no-product";

    protected HBox hBoxCell;
    protected ImageView brandImage;
    protected HBox favoriteImageBox;
    protected ImageView favoriteIcon;
    protected Label first;
    protected Label second;
    protected Label third;
    protected Label fourth;

    public AbstractFzgConfigCell() {
        hBoxCell = new HBox(2);
        hBoxCell.setMinHeight(20);
        hBoxCell.setAlignment(Pos.CENTER_LEFT);

        brandImage = new ImageView();

        first = new Label();
        first.setPadding(Insets.EMPTY);

        Insets insets = new Insets(0, 0, 0, 2);
        second = new Label();
        second.setPadding(insets);

        third = new Label();
        third.setPadding(insets);

        fourth = new Label();
        fourth.setPadding(insets);

        TreeItem<T> treeItem = getTreeItem();
        if (treeItem != null) {
            InvalidationListener treeItemGraphicListener = observable -> updateDisplay(getItem());
            WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(
                treeItemGraphicListener);
            treeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
        }

        HBox.setHgrow(brandImage, Priority.ALWAYS);
        HBox.setHgrow(first, Priority.ALWAYS);
        HBox.setHgrow(second, Priority.ALWAYS);
        HBox.setHgrow(third, Priority.ALWAYS);
        HBox.setHgrow(fourth, Priority.ALWAYS);

        hBoxCell.getChildren().addAll(brandImage, first, second, third, fourth);

        setGraphic(hBoxCell);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        updateDisplay(item);
    }

    protected abstract Image displayFavorite(T item);

    protected abstract void displayVehicle(T item);

    protected abstract void displayBrand(Brand item);

    protected abstract void displayOther(T item);

    protected abstract void displayRecentlyUsed(T item);

    protected Image getBrandIcon(Brand brand) {
        try {
            return BrandIcon.valueOf(brand.name()).getImage();
        } catch (Exception ignored) {
            return BrandIcon.UNKNOWN.getImage();
        }
    }

    private void updateDisplay(T item) {
        brandImage.setImage(null);

        first.getStyleClass().remove(CELL_STYLE_CLASS_NO_PRODUCT);
        first.setText(null);

        second.setText(null);
        second.getStyleClass().remove(CELL_STYLE_CLASS_NO_PRODUCT);

        third.setText(null);
        third.getStyleClass().remove(CELL_STYLE_CLASS_NO_PRODUCT);

        fourth.setText(null);
        fourth.getStyleClass().remove(CELL_STYLE_CLASS_NO_PRODUCT);

        setGraphic(null);
        setText(null);

        List<Node> nodes = hBoxCell.getChildren();
        nodes.remove(favoriteImageBox);

        if (item == null) {
            return;
        }

        if (item.isVehicleProject()) {
            displayVehicle(item);
        } else if (item.isBrand()) {
            brandImage.setImage(getBrandIcon(item.getBrand()));

            displayBrand(item.getBrand());
        } else if (item.isVehicleConfig()) {
            displayRecentlyUsed(item);
        } else {
            displayOther(item);
        }

        if (item.getVehicleConfigTreeObjType() == null && getTreeItem().isLeaf()) {
            if (favoriteImageBox == null) {
                favoriteImageBox = createFavouriteIcon();
            }

            Image image = displayFavorite(item);
            favoriteIcon.setImage(image);
            nodes.addFirst(favoriteImageBox);
        }

        setGraphic(hBoxCell);
    }

    private HBox createFavouriteIcon() {
        favoriteIcon = new ImageView();
        favoriteIcon.setOnMouseEntered(event -> {
            favoriteIcon.setFitWidth(FAVORITE_ICON_LARGE_SIZE);
            favoriteIcon.setFitHeight(FAVORITE_ICON_LARGE_SIZE);
        });
        favoriteIcon.setOnMouseExited(event -> {
            favoriteIcon.setFitWidth(FAVORITE_ICON_SMALL_SIZE);
            favoriteIcon.setFitHeight(FAVORITE_ICON_SMALL_SIZE);
        });

        HBox favouriteImageBox = new HBox(favoriteIcon);
        favouriteImageBox.setPadding(Insets.EMPTY);
        favouriteImageBox.setMinWidth(FAVORITE_ICON_LARGE_SIZE);

        HBox.setHgrow(favouriteImageBox, Priority.NEVER);

        return favouriteImageBox;
    }
}
