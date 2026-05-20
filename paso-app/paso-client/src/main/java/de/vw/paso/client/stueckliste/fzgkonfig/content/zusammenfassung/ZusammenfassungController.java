package de.vw.paso.client.stueckliste.fzgkonfig.content.zusammenfassung;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.fzgkonfig.content.AbstractContentController;
import de.vw.paso.client.stueckliste.fzgkonfig.menu.status.VehicleConfigCategoryRegistry;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.validation.Validator;
import de.vw.paso.delegate.stammdaten.setversion.SetVersionRestClientHolder;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringCommonTermsUtil;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.VehicleConfigCategory;

@FXController(name = "zusammenfassung")
public class ZusammenfassungController extends AbstractContentController {

    @FXML
    private VBox paneZusammenfassung;
    @FXML
    private CheckBox smartFixCheckBox;
    @FXML
    private CheckBox applyDefaultSetVersionCheckBox;
    @FXML
    protected ComboBox<SetVersionDTO> setVersionCheckBox;
    @FXML
    private Button copyButton;

    @Override
    public void start() {
        super.start();
        loadSetVersions();

        addValidators();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        copyButton.setText(I18N.getString("copy"));
        copyButton.setGraphic(new ImageView(ActionIcon.COPY_16X16.getImage()));

        setVersionCheckBox.valueProperty().addListener((ov, oldValue, newValue) -> updateSetVersion(newValue));
        smartFixCheckBox.selectedProperty()
                .addListener((observableValue, oldValue, newValue) -> updateSmartFix(newValue));
        applyDefaultSetVersionCheckBox.selectedProperty()
                .addListener((observableValue, oldValue, newValue) -> updateDefaultSetVersion(newValue));
    }

    private void updateDefaultSetVersion(Boolean newValue) {
        getVehicleConfig().setUpdateDefaultSetVersion(newValue);
        forceDirty();
    }

    private void updateSmartFix(boolean newValue) {
        if (getVehicleConfig().isSmartFixesActive() == newValue) {
            return;
        }

        getVehicleConfig().setSmartFixesActive(newValue);
        setDirty();
    }

    private void updateSetVersion(SetVersionDTO newValue) {
        if (newValue == null) {
            return;
        }

        VehicleConfigDTO vehicleConfig = getVehicleConfig();
        Long newVehicleConfigId = newValue.getId();
        if (Objects.equals(vehicleConfig.getSetVersionId(), newVehicleConfigId)) {
            return;
        }

        vehicleConfig.setSetVersionId(newVehicleConfigId);
        vehicleConfig.setSetVersion(newValue);

        vehicleConfig.getVehicleProject().getProductDTO().setSetVersionId(newVehicleConfigId);

        forceDirty();
    }

    @Override
    protected void onVehicleConfigChanged() {
        VehicleConfigDTO vehicleConfig = getVehicleConfig();

        smartFixCheckBox.setSelected(vehicleConfig.isSmartFixesActive());
        smartFixCheckBox.setDisable(!isEditable());

        initUI();
    }

    private void initUI() {
        paneZusammenfassung.getChildren().clear();
        int blockCount = 0;
        for (VehicleConfigCategory fzgConfigStatusCategory : VehicleConfigCategory.values()) {
            blockCount++;

            switch (fzgConfigStatusCategory) {
                case FZG_PROJEKT -> {
                    createBlockHeader(blockCount, fzgConfigStatusCategory);
                    loadFzgProjektData();
                }
                case MODELL -> {
                    createBlockHeader(blockCount, fzgConfigStatusCategory);
                    loadModellData();
                }
                case KONFIGURATION -> {
                    createBlockHeader(blockCount, fzgConfigStatusCategory);
                    loadKonfigurationData();
                }
            }
        }
    }

    private void loadFzgProjektData() {
        VehicleProjectDTO vehicleProject = getVehicleConfig().getVehicleProject();
        String projectName = vehicleProject.getProjectName();
        String description = vehicleProject.getDescription();
        String productKey = vehicleProject.getProductKey();
        String salesKey = vehicleProject.getSalesKey();
        Integer firstModelYearInt = vehicleProject.getFirstModelYear();
        String firstModelYear = firstModelYearInt == null ? StringConstant.EMPTY : firstModelYearInt.toString();
        String platform = vehicleProject.getPlatform();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String validDate = dateFormat.format(getVehicleConfig().getValidDate());
        String ownerGroupName = getVehicleConfig().getOwnerGroup().getName();

        String productKeyStr = I18N.getString("tablecolumn.product");
        String validDateStr = I18N.getString("valid.date");
        String firstModelYearStr = I18N.getString("tablecolumn.firstmodelyear");
        String ownerUserGroupStr = I18N.getString("owner.user.group");
        String platformStr = I18N.getString("tablecolumn.platform");

        GridPane pane = new GridPane(5, 5);
        addSection(pane, projectName, description);
        addSection(pane, productKeyStr, productKey + StringConstant.SLASH_SPACE + salesKey);
        addSection(pane, firstModelYearStr, firstModelYear);
        addSection(pane, platformStr, platform);
        addSection(pane, validDateStr, validDate);
        addSection(pane, ownerUserGroupStr, ownerGroupName);

        addInfoNode(pane);
    }

    private void addSection(GridPane pane, String title, String desc) {
        int rowCount = pane.getRowCount();
        pane.add(new Label(title), 0, rowCount);
        pane.add(new Label(desc), 1, rowCount);
        pane.getRowConstraints().add(new RowConstraints());
    }

    private void loadModellData() {
        ModelDTO model = getVehicleConfig().getModel();
        String desc = model == null ? I18N.getString("modell.ohne") : model.getDescription();
        addInfoText(desc);
    }

    private void loadKonfigurationData() {
        String prNumberString = getVehicleConfig().getPrNumberString();
        addInfoText(prNumberString);

        copyButton.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(prNumberString);

            Clipboard.getSystemClipboard().setContent(content);
        });
    }

    private void createBlockHeader(int number, VehicleConfigCategory menuCategory) {
        HBox headerBox = new HBox();
        headerBox.setSpacing(5);

        Label lblNr = new Label(number + StringConstant.DOT);
        headerBox.getChildren().add(lblNr);

        Label lblTitle = new Label(getMenuTitle(menuCategory));
        headerBox.getChildren().add(lblTitle);

        lblNr.getStyleClass().add("headerText");
        lblTitle.getStyleClass().add("headerText");

        paneZusammenfassung.getChildren().add(headerBox);
    }

    private String getMenuTitle(VehicleConfigCategory menuCategory) {
        String resBundleStr = StringCommonTermsUtil.HEADER_LOW_CASE + StringConstant.DOT
                + VehicleConfigCategoryRegistry.getControllerClass(menuCategory).getSimpleName()
                .replace("Controller", StringConstant.EMPTY).toLowerCase();

        return I18N.getString(resBundleStr);
    }

    private void addInfoText(String text) {
        Label label = new Label(text);
        label.setWrapText(true);

        addInfoNode(label);
    }

    private void addInfoNode(Region node) {
        node.setPadding(new Insets(0, 0, 0, 10));

        paneZusammenfassung.getChildren().add(node);
    }

    private void loadSetVersions() {
        doAsync(() -> SetVersionRestClientHolder.getInstance().loadSetVersions().setVersions(),
                this::updateComboBoxItems);
    }

    private void updateComboBoxItems(List<SetVersionDTO> setVersions) {
        setVersionCheckBox.getItems().setAll(setVersions);

        VehicleConfigDTO vehicleConfig = getVehicleConfig();

        SetVersionDTO selectedSetVersion = setVersions.stream()
                .filter(setVersion -> Objects.equals(setVersion.getId(), vehicleConfig.getSetVersionId())).findFirst()
                .orElseGet(setVersions::getFirst);

        setVersionCheckBox.getSelectionModel().select(selectedSetVersion);
    }

    private void addValidators() {
        // Dummy as this controller is always valid.
        addValidator(new Validator<>(new SimpleBooleanProperty(true), e -> true, StringConstant.EMPTY));
    }
}
