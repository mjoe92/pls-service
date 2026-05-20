package de.vw.paso.client.stammdaten.product;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stammdaten.AbstractMasterDataTableViewController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.valueobject.ProductVMO;
import de.vw.paso.delegate.stueckliste.product.ProductRestClientHolder;

@FXController(name = "product-tab")
public class ProductController extends AbstractMasterDataTableViewController<ProductVMO> implements Initializable {

    @FXML
    private GridPane gridPaneTab;
    @FXML
    private TableColumn<ProductVMO, String> colProductKey;
    @FXML
    private TableColumn<ProductVMO, String> colSetVersionName;

    @Override
    protected void openEditDialog(ProductVMO selectedItem, Consumer<Optional<ProductVMO>> callback) {
        ProductDialog productDialog = new ProductDialog(I18N.getString(DIALOG_TITLE_EDIT), productTableView.getItems(),
            selectedItem);
        productDialog.showAndWait().ifPresent(productVMO -> doEdit(selectedItem, productVMO, null));
    }

    @Override
    protected void doEdit(ProductVMO selectedItem, ProductVMO newItem, Consumer<ProductVMO> callback) {
        ProductRestClientHolder.getInstance().updateProduct(ProductVMO.toDTO(newItem));
        doLoad(this::setItems);
    }

    @Override
    protected void doLoad(Consumer<List<ProductVMO>> callback) {
        doAsync(() -> ProductVMO.toProductVMOList(ProductRestClientHolder.getInstance().getProducts().productDTOSet()),
            products -> accept(callback, products));
    }

    private void accept(Consumer<List<ProductVMO>> callback, List<ProductVMO> products) {
        callback.accept(products);
    }

    @Override
    protected Comparator<? super ProductVMO> getItemComparator() {
        return Comparator.comparing(ProductVMO::getProductKey);
    }

    @Override
    protected boolean getFilterCriteria(ProductVMO item, PasoWildCardPattern pattern) {
        return pattern == null || pattern.matches(item.getProductKey()) != null
            || pattern.matches(item.getSetVersionDTO().getName()) != null;
    }

    @Override
    protected void initializeView() {
        productTableView.setEditable(true);
    }

    @Override
    protected void initTableColumns() {
        colProductKey.setCellValueFactory(cellData -> cellData.getValue().getProductKeyProperty());
        colSetVersionName.setCellValueFactory(cellData -> cellData.getValue().getSetVersionName());
    }

    @Override
    protected void setItems(List<ProductVMO> items) {
        ProductVMO selectedItem = productTableView.getSelectionModel().getSelectedItem();

        items.sort(getItemComparator());
        super.setItems(items);

        selectTableItems(selectedItem);
    }

    private void selectTableItems(ProductVMO prevSelectedItem) {
        if (prevSelectedItem == null) {
            return;
        }

        List<ProductVMO> items = productTableView.getItems();
        for (int i = 0; i < items.size(); i++) {
            ProductVMO item = items.get(i);
            if (item.getProductKey().equals(prevSelectedItem.getProductKey())) {
                productTableView.getSelectionModel().select(i);
                productTableView.scrollTo(i);
                productTableView.requestFocus();

                return;
            }
        }
    }

    @Override
    protected boolean disableRefresh() {
        return false;
    }

    @Override
    protected <T> void handleTableSelection(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        disablePropertyEdit().set(newValue == null);
    }
}
