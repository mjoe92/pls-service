package de.vw.paso.client.stueckliste.fzgkonfig.content.stueckliste;

import java.util.List;
import java.util.function.Supplier;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.service.ServiceController;
import de.vw.paso.client.control.menu.DatenstandMenuItem;
import de.vw.paso.client.control.textfield.PasoCustomTextField;
import de.vw.paso.client.control.textfield.PasoNumberField;
import de.vw.paso.client.stueckliste.fzgkonfig.content.fzgprojekt.FzgProjektController;
import de.vw.paso.client.validation.Validator;
import de.vw.paso.delegate.stueckliste.tiwhimport.TiWhImportRestClientHolder;
import de.vw.paso.service.tiwhimport.IImportPartListConsumer;
import de.vw.paso.service.tiwhimport.ILoadDataStatusConsumer;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import org.apache.commons.lang3.StringUtils;

public class StuecklisteControl extends Region implements ILoadDataStatusConsumer, IImportPartListConsumer {

  private VBox container;

  private HBox containerProduktschluessel;

  private HBox containerDatenstand;

  private Label description = new Label();

  private PasoCustomTextField<String> produktschluesselText = new PasoCustomTextField<>();

  private MenuButton menuButtonDatenstand = new MenuButton();

  private Label datenstand = new Label();

  private Button buttonUpdateStueckliste = new Button();

  private final ToggleGroup toggleGroup = new ToggleGroup();

  private ObjectProperty<TiWhImportDTO> selectedTiWhImport = new SimpleObjectProperty<>();

  private FzgProjektController controller;

  private Supplier<TiWhImportDTO> tiWhImportSupplier;

  public StringProperty descriptionProperty() {
    return description.textProperty();
  }

  public void setDescription(String text) {
    descriptionProperty().set(text);
  }

  public String getDescription() {
    return descriptionProperty().get();
  }

  public StringProperty produktschluesselTextProperty() {
    return produktschluesselText.textProperty();
  }

  public String getProduktschluesselText() {
    return produktschluesselTextProperty().get();
  }

  public StuecklisteControl() {
    container = new VBox(10);
    getChildren().add(container);
    containerProduktschluessel = new HBox(10);
    container.getChildren().add(containerProduktschluessel);
    containerDatenstand = new HBox(10);
    container.getChildren().add(containerDatenstand);
  }

  public void addValidators() {
    controller.addValidator(new Validator<>(produktschluesselTextProperty(),
      text -> text.isEmpty() || text.length() == produktschluesselText.maxTextLengthProperty().get(),
      I18N.getString("validation.produktschluessel")));
  }

  public void init(FzgProjektController controller, int produktschluesselLength,
    Supplier<TiWhImportDTO> tiWhImportSupplier) {
    this.controller = controller;
    this.tiWhImportSupplier = tiWhImportSupplier;

    addProduktschluessel(produktschluesselLength);
    addDatenstand();
    addUpdateButton();

    initTextfieldProduktschluessel();
  }

  private void addDatenstand() {
    datenstand.setText(I18N.getString("datenstand"));
    datenstand.maxWidthProperty().set(200);
    datenstand.minWidthProperty().set(200);
    addChild(containerDatenstand, datenstand);
    menuButtonDatenstand.maxWidthProperty().set(310);
    menuButtonDatenstand.minWidthProperty().set(310);
    addChild(containerDatenstand, menuButtonDatenstand);
  }

  private void addUpdateButton() {
    addChild(containerDatenstand, buttonUpdateStueckliste);
    buttonUpdateStueckliste.setText(I18N.getString("stueckliste.update"));
    buttonUpdateStueckliste.setOnAction((e) -> updateStueckliste());
  }

  private void addProduktschluessel(int produktschluesselLength) {
    addChild(containerProduktschluessel, description);
    description.maxWidthProperty().set(200);
    description.minWidthProperty().set(200);
    addChild(containerProduktschluessel, produktschluesselText);
    produktschluesselText.maxWidthProperty().set(80);
    produktschluesselText.minWidthProperty().set(80);
    produktschluesselText.upperCaseProperty().set(true);
    produktschluesselText.maxTextLengthProperty().set(produktschluesselLength);
  }

  private void addChild(Pane pane, Control control) {
    pane.getChildren().add(control);
  }

  private void initTextfieldProduktschluessel() {
    centerTextFieldValue(produktschluesselText);

    produktschluesselTextProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.length() == produktschluesselText.getMaxTextLength()) {
        loadDatenstand();
      }
    });
  }

  private void centerTextFieldValue(PasoCustomTextField<?> textField) {
    textField.getStyleClass().remove(PasoNumberField.STYLE_CLASS_NUMBER_TEXT_FIELD);
    textField.getStyleClass().add(PasoNumberField.STYLE_CLASS_TEXT_FIELD_CENTER);
  }

  private void setDatenstaende(List<TiWhImportDTO> tiWImporte, MenuButton menuButtonDatenstand, ToggleGroup toggleGroup,
    TiWhImportDTO selectedTiWhImport) {
    if (tiWImporte.isEmpty()) {
      toggleGroup.selectToggle(null);
    }

    tiWImporte.sort((m0, m1) -> m1.getTimestampChange().compareTo(m0.getTimestampChange()));

    menuButtonDatenstand.getItems().clear();

    if (tiWImporte.isEmpty()) {
      menuButtonDatenstand.setText(DatenstandMenuItem.getImportNoDataText());
      return;
    }

    menuButtonDatenstand.setText(null);

    for (TiWhImportDTO tiWhImport : tiWImporte) {
      RadioMenuItem menuItem = addDatenstandMenuItem(tiWhImport, false, menuButtonDatenstand, toggleGroup);

      if (selectedTiWhImport != null && tiWhImport.getId().equals(selectedTiWhImport.getId())) {
        menuItem.setSelected(true);
      }
    }
  }

  public void loadDatenstand() {
    String produktschluessel = getProduktschluesselText();
    if (StringUtils.isEmpty(produktschluessel)) {
      return;
    }
    updateTiWhImport();
    loadDataStatus(produktschluessel);
  }

  @Override
  public void loadDataStatus(String productKey) {
    ServiceController<List<TiWhImportDTO>> serviceController = new ServiceController<>();
    serviceController.setOnSucceeded(
      e -> setDatenstaende(serviceController.getValue(), menuButtonDatenstand, toggleGroup, getSelectedTiWhImport()));
    serviceController.setOnFailed(e -> controller.handleException(serviceController.getException()));
    serviceController.setExecutionTime(100);
    serviceController.start(
      () -> TiWhImportRestClientHolder.getInstance().loadDatenstande(productKey).tiWhImportDTOList());
  }

  public void updateTiWhImport() {
    if (tiWhImportSupplier.get() == null) {
      return;
    }

    setSelectedTiWhImport(tiWhImportSupplier.get());
  }

  private ReadOnlyObjectProperty<TiWhImportDTO> selectedTiWhImportProperty() {
    return this.selectedTiWhImport;
  }

  private TiWhImportDTO getSelectedTiWhImport() {
    return this.selectedTiWhImportProperty().get();
  }

  public final void setSelectedTiWhImport(final TiWhImportDTO selectedTiWhImport) {
    this.selectedTiWhImport.set(selectedTiWhImport);
  }

  private RadioMenuItem addDatenstandMenuItem(TiWhImportDTO tiWhImport, Boolean addFirst,
    MenuButton menuButtonDatenstand, ToggleGroup toggleGroup) {
    DatenstandMenuItem menuItem = new DatenstandMenuItem(tiWhImport);
    menuItem.setToggleGroup(toggleGroup);

    if (addFirst) {
      menuButtonDatenstand.getItems().add(0, menuItem);
      menuItem.setSelected(true);
    } else {
      menuButtonDatenstand.getItems().add(menuItem);
    }

    return menuItem;
  }

  public void fillTextField() {
    if (getSelectedTiWhImport() != null) {
      produktschluesselText.setText(getSelectedTiWhImport().getProductKey());
    }
  }

  public void initDatenstand() {
    buttonUpdateStueckliste.disableProperty().bind(Bindings.createBooleanBinding(
      () -> getProduktschluesselText().isEmpty()
        || getProduktschluesselText().length() != produktschluesselText.maxTextLengthProperty().get()
        || menuButtonDatenstand.textProperty().get() == null
        || isOneRequested(toggleGroup) && !menuButtonDatenstand.getItems().isEmpty(), produktschluesselTextProperty(),
      menuButtonDatenstand.textProperty(), menuButtonDatenstand.getItems(), toggleGroup.selectedToggleProperty()));
  }

  private boolean isOneRequested(ToggleGroup toggleGroup) {
    ObservableList<Toggle> toggles = toggleGroup.getToggles();

    boolean isOneAngefordert = false;
    for (Toggle toggle : toggles) {
      DatenstandMenuItem menuItem = (DatenstandMenuItem) toggle;

      if (menuItem.textProperty().get().contains(DatenstandMenuItem.getImportRequestedText())) {
        isOneAngefordert = true;
        break;
      }
    }
    return isOneAngefordert;
  }

  public void initMenuButton() {
    if (tiWhImportSupplier.get() == null) {
      menuButtonDatenstand.setText(I18N.getString("import.keine.daten"));
    }

    toggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal == null) {
        return;
      }

      DatenstandMenuItem menuItem = (DatenstandMenuItem) newVal;
      TiWhImportDTO tiWhImport = (TiWhImportDTO) menuItem.getDatenstand();

      String menuItemText = menuItem.getImportStatus(tiWhImport);
      menuButtonDatenstand.setText(menuItemText);

      if (tiWhImportSupplier.get() == null || !tiWhImport.getId().equals(tiWhImportSupplier.get().getId())) {
        controller.getVehicleConfig().setTiWhImportVehicle(tiWhImport);
        controller.updateMenuImportStatus();
      }
    });
  }

  @FXML
  private void updateStueckliste() {
    if (produktschluesselText.lengthProperty().get() == produktschluesselText.maxTextLengthProperty().get()) {
      importPartList(produktschluesselText.getText());
    }
  }

  @Override
  public void importPartList(String productKey) {
    ServiceController<TiWhImportDTO> serviceController = new ServiceController<>();
    serviceController.setOnSucceeded(
      e -> addDatenstandMenuItem(serviceController.getValue(), true, menuButtonDatenstand, toggleGroup));
    serviceController.setOnFailed(e -> controller.handleException(serviceController.getException()));
    serviceController.setExecutionTime(100);
    serviceController.start(() -> TiWhImportRestClientHolder.getInstance().importPartList(productKey));
  }

}
