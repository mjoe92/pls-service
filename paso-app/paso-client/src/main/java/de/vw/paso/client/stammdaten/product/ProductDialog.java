package de.vw.paso.client.stammdaten.product;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.ProductVMO;
import de.vw.paso.delegate.stammdaten.setversion.SetVersionRestClientHolder;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;

public class ProductDialog extends BaseDialogController<ProductVMO> {

    private final static String SET_VERSIONS = "label.setversions";

    private final ProductVMO productVMO;
    private final List<ProductVMO> productVMOS;

    private List<SetVersionDTO> setVersionDTOS;
    private ComboBox<String> setVersions;

    public ProductDialog(String title, List<ProductVMO> productVMOList, ProductVMO productVMO) {
        Future<List<SetVersionDTO>> setVersionsFuture = getSetVersions();
        this.productVMO = productVMO;
        this.productVMOS = productVMOList;

        super.initialize(title, () -> initializeDialog(setVersionsFuture));
    }

    @Override
    protected ChangeListener<String> getValidationListener() {
        return (observable, oldValue, newValue) -> {
            String productKey = productVMO.getProductKey();
            String currSetVersion = setVersions.getValue();
            commitButton.setDisable(productVMOS.stream().anyMatch(
                prod -> prod.getSetVersionDTO().getName().equals(currSetVersion) && prod.getProductKey()
                    .equals(productKey)));
        };
    }

    @Override
    protected ListChangeListener<?> getValidationListenerForList() {
        return null;
    }

    @Override
    protected ProductVMO dialogResult() {
        String setVersionName = setVersions.getValue();
        SetVersionDTO currentlySelectedSetVersion = setVersionDTOS.stream()
            .filter(setVersionDTO -> setVersionDTO.getName().equals(setVersionName)).findAny().orElseThrow();

        productVMO.setSetVersionDTO(currentlySelectedSetVersion);
        productVMO.setSetVersionId(currentlySelectedSetVersion.getId());
        return productVMO;
    }

    private void initializeDialog(Future<List<SetVersionDTO>> setVersionsFuture) {
        try {
            setVersionDTOS = setVersionsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        setVersions = new ComboBox<>(
            FXCollections.observableList(setVersionDTOS.stream().map(SetVersionDTO::getName).toList()));
        setVersions.setValue(productVMO.getSetVersionDTO().getName());
        setVersions.valueProperty().addListener(getValidationListener());

        addLabelAndInputFieldToGrid(I18N.getString(SET_VERSIONS), setVersions);
    }

    private Future<List<SetVersionDTO>> getSetVersions() {
        try (ExecutorService executor = Executors.newFixedThreadPool(1)) {
            return executor.submit(() -> SetVersionRestClientHolder.getInstance().loadSetVersions().setVersions());
        }
    }
}
