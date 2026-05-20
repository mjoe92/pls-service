package de.vw.paso.client.stammdaten;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoAlert;
import de.vw.paso.client.control.textfield.PasoCustomTextFieldClearable;
import de.vw.paso.client.main.ribbonmenu.stammdaten.RibbonMenuMasterDataListener;
import de.vw.paso.client.main.ribbonmenu.stammdaten.RibbonMenuStammdatenEvent;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.stammdaten.brand.BrandController;
import de.vw.paso.client.stammdaten.costgroup.CostGroupController;
import de.vw.paso.client.stammdaten.fzgprojekt.VehicleProjectController;
import de.vw.paso.client.stammdaten.partgroup.PartGroupController;
import de.vw.paso.client.stammdaten.product.ProductController;
import de.vw.paso.client.stammdaten.pst.PSTController;
import de.vw.paso.client.stammdaten.setkey.SetKeyController;
import de.vw.paso.client.stammdaten.setversion.SetVersionController;
import de.vw.paso.client.stammdaten.vertriebsregion.SalesRegionsController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.icon.StammdatenIcon;

@FXController(name = "master-data-tab")
public class MasterDataTabController extends AbstractMainTabController
    implements Initializable, RibbonMenuMasterDataListener {

    @FXML
    private Tab stammdatenTab;
    @FXML
    private SplitPane splitPaneStammdaten;
    @FXML
    private PasoCustomTextFieldClearable stammdatenSearch;
    @FXML
    private ListView<MasterDataCategory> listViewStammdaten;
    @FXML
    private BorderPane stammdatenWorkArea;

    private BooleanProperty disablePropertyAdd;
    private BooleanProperty disablePropertyEdit;
    private BooleanProperty disablePropertyRemove;
    private BooleanProperty disablePropertyRefresh;
    private BooleanProperty disablePropertyResetFilters;

    private PasoWildCardPattern patternSearchTerm;

    private AbstractMasterDataController<?> currentStammdatenController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        stammdatenTab.setGraphic(new ImageView(StammdatenIcon.STAMMDATEN_16X16.getImage()));
        listViewStammdaten.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> handleCategorySelected(newValue));
        stammdatenSearch.textProperty().addListener((observable, oldValue, newValue) -> handleStammdatenSearch());
    }

    private void handleCategorySelected(MasterDataCategory newValue) {
        if (newValue == null) {
            stammdatenWorkArea.setCenter(null);
            bindDisablePropertyRibbonMenu(null);
            currentStammdatenController = null;
            return;
        }

        var categoryController = getController(newValue);
        AbstractMasterDataController<?> stammdatenController = load(categoryController);
        stammdatenController.start();
        bindDisablePropertyRibbonMenu(stammdatenController);
        stammdatenWorkArea.setCenter((Node) stammdatenController.getControl());
        currentStammdatenController = stammdatenController;
    }

    private Class<? extends AbstractMasterDataController<?>> getController(MasterDataCategory category) {
        return switch (category) {
            case MARKE -> BrandController.class;
            case SALES_REGION -> SalesRegionsController.class;
            case FZG_PROJECT -> VehicleProjectController.class;
            case SET_VERSION -> SetVersionController.class;
            case SET_KEY -> SetKeyController.class;
            case COST_GROUP -> CostGroupController.class;
            case PART_GROUP -> PartGroupController.class;
            case PRODUCT -> ProductController.class;
            case PST -> PSTController.class;
        };
    }

    private void bindDisablePropertyRibbonMenu(AbstractMasterDataController<?> currentController) {
        disablePropertyAdd.unbind();
        disablePropertyEdit.unbind();
        disablePropertyRemove.unbind();
        disablePropertyRefresh.unbind();
        disablePropertyResetFilters.unbind();

        if (currentController == null) {
            disablePropertyAdd.set(true);
            disablePropertyEdit.set(true);
            disablePropertyRemove.set(true);
            disablePropertyRefresh.set(true);
            disablePropertyResetFilters.set(true);
        } else {
            disablePropertyAdd.bind(currentController.disablePropertyAdd());
            disablePropertyEdit.bind(currentController.disablePropertyEdit());
            disablePropertyRemove.bind(currentController.disablePropertyRemove());
            disablePropertyRefresh.bind(currentController.disablePropertyRefresh());
            disablePropertyResetFilters.bind(currentController.disablePropertyResetFilters());
        }
    }

    @Override
    public void start() {
        ObservableList<MasterDataCategory> categoryList = FXCollections.observableArrayList();
        for (MasterDataCategory category : MasterDataCategory.values()) {
            String categoryName = category.toString();
            if (patternSearchTerm == null || patternSearchTerm.matches(categoryName) != null) {
                categoryList.add(category);
            }
        }

        listViewStammdaten.setItems(categoryList);
    }

    @Override
    public Tab getControl() {
        return stammdatenTab;
    }

    @Override
    public Parent getStyleableParent() {
        return splitPaneStammdaten;
    }

    private void handleStammdatenSearch() {
        setPatternSearchTerm(stammdatenSearch.getText());
    }

    private void setPatternSearchTerm(String searchTerm) {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTerm);
        } catch (Exception exception) {
            handleException(exception);
        }

        start();
    }

    @Override
    public RibbonMenuStammdatenEvent getRibbonMenuEvent() {
        return new RibbonMenuStammdatenEvent(this);
    }

    @Override
    public void handleActionAdd() {
        currentStammdatenController.handleActionAdd();
    }

    @Override
    public void handleActionEdit() {
        currentStammdatenController.handleActionEdit();
    }

    @Override
    public void handleActionDelete() {
        String deleteMessageKey = currentStammdatenController.getCustomDeleteMessageKey();

        Alert alert = new PasoAlert(Alert.AlertType.CONFIRMATION, I18N.getString(deleteMessageKey), ButtonType.YES,
            ButtonType.NO);
        alert.setTitle(I18N.getString("currentStammdatenController.deleteTitle"));
        alert.setHeaderText(I18N.getString("currentStammdatenController.deleteHeader"));

        Optional<ButtonType> buttonType = alert.showAndWait();
        buttonType.ifPresent(result -> {
            if (result == ButtonType.YES) {
                currentStammdatenController.handleActionDelete();
            }
        });
    }

    @Override
    public void handleActionRefresh() {
        currentStammdatenController.handleActionRefresh();
    }

    @Override
    public void handleActionResetFilters() {
        currentStammdatenController.handleActionResetFilters();
    }

    @Override
    public BooleanProperty disablePropertyAdd() {
        if (disablePropertyAdd == null) {
            disablePropertyAdd = new SimpleBooleanProperty(true);
        }

        return disablePropertyAdd;
    }

    @Override
    public BooleanProperty disablePropertyEdit() {
        if (disablePropertyEdit == null) {
            disablePropertyEdit = new SimpleBooleanProperty(true);
        }

        return disablePropertyEdit;
    }

    @Override
    public BooleanProperty disablePropertyRemove() {
        if (disablePropertyRemove == null) {
            disablePropertyRemove = new SimpleBooleanProperty(true);
        }

        return disablePropertyRemove;
    }

    @Override
    public BooleanProperty disablePropertyRefresh() {
        if (disablePropertyRefresh == null) {
            disablePropertyRefresh = new SimpleBooleanProperty(true);
        }

        return disablePropertyRefresh;
    }

    @Override
    public final BooleanProperty disablePropertyResetFilters() {
        if (disablePropertyResetFilters == null) {
            disablePropertyResetFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyResetFilters;
    }
}
