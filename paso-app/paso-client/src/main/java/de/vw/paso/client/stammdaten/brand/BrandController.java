package de.vw.paso.client.stammdaten.brand;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.stammdaten.AbstractMasterDataTableViewController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.icon.BrandIcon;
import de.vw.paso.masterdata.Brand;

@FXController(name = "brand-tab")
public class BrandController extends AbstractMasterDataTableViewController<Brand> {

    @FXML
    private TableColumn<Brand, ImageView> columnLogo;
    @FXML
    private TableColumn<Brand, String> columnKey;
    @FXML
    private TableColumn<Brand, String> columnAppellation;

    @Override
    protected void doLoad(Consumer<List<Brand>> callback) {
        callback.accept(Arrays.asList(Brand.values()));
    }

    @Override
    protected Comparator<? super Brand> getItemComparator() {
        return Comparator.comparing(Enum::name);
    }

    @Override
    protected boolean getFilterCriteria(Brand item, PasoWildCardPattern pattern) {
        return pattern == null || pattern.matches(item.getBrandName()) != null || pattern.matches(item.name()) != null;
    }

    @Override
    protected void initializeView() {
    }

    @Override
    protected void initTableColumns() {
        columnLogo.setCellValueFactory(
            p -> new SimpleObjectProperty<>(new ImageView(BrandIcon.getImageForBrand(p.getValue()))));
        columnLogo.setStyle("-fx-alignment: CENTER;");

        columnKey.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
        columnAppellation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBrandName()));
    }

    @Override
    protected void setItems(List<Brand> items) {
        Brand prevSelectedItem = productTableView.getSelectionModel().getSelectedItem();

        super.setItems(items);

        selectTableItem(prevSelectedItem);
    }

    private void selectTableItem(Brand selectingItem) {
        if (selectingItem == null) {
            return;
        }

        List<Brand> items = productTableView.getItems();
        for (int index = 0; index < items.size(); index++) {
            Brand item = items.get(index);
            if (selectingItem.equals(item)) {
                productTableView.getSelectionModel().select(index);
                productTableView.scrollTo(index);
                productTableView.requestFocus();

                return;
            }
        }
    }
}
