package de.vw.paso.client.stammdaten.vertriebsregion;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import de.vw.paso.client.base.BaseDialogController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.valueobject.SalesRegionVMO;
import org.apache.commons.lang3.StringUtils;

public class SalesRegionDialog extends BaseDialogController<SalesRegionVMO> {

  private static final String LABEL_SALES_REGION_ID = "label.region.salesregion";
  private static final String LABEL_DESCRIPTION_DE = "label.region.description.de";
  private static final String LABEL_DESCRIPTION_EN = "label.region.description.en";
  private static final String LABEL_RELEVANT = "label.region.relevant";
  private static final String COMBO_BOX_VALUE_RELEVANT = "yes";
  private static final String COMBO_BOX_VALUE_NOT_RELEVANT = "no";

  private final TextField salesRegionIdTextField = new TextField();
  private final TextField descriptionDeTextField = new TextField();
  private final TextField descriptionEnTextField = new TextField();
  private final ComboBox<String> relevantComboBox = new ComboBox<>();

  private SalesRegionVMO selectedItem;
  private List<SalesRegionVMO> items;

  SalesRegionDialog(String title, SalesRegionVMO salesRegion, List<SalesRegionVMO> allSalesRegions) {
    this.items = allSalesRegions;
    this.selectedItem = salesRegion;

    initialize(title, this::initContent);
  }

  private void initContent() {
    setPromptText(StringUtils.EMPTY, salesRegionIdTextField);
    setPromptText(StringUtils.EMPTY, descriptionDeTextField);
    setPromptText(StringUtils.EMPTY, descriptionEnTextField);

    initComboBox(relevantComboBox,
      List.of(I18N.getString(COMBO_BOX_VALUE_RELEVANT), I18N.getString(COMBO_BOX_VALUE_NOT_RELEVANT)),
      convertRelevantValueToString(selectedItem.getRelevant()));

    setTextToInputField(selectedItem.getSalesRegion(), salesRegionIdTextField);
    setTextToInputField(selectedItem.getDescriptionDe(), descriptionDeTextField);
    setTextToInputField(selectedItem.getDescriptionEn(), descriptionEnTextField);

    addLabelAndInputFieldToGrid(I18N.getString(LABEL_SALES_REGION_ID), salesRegionIdTextField);
    addLabelAndInputFieldToGrid(I18N.getString(LABEL_DESCRIPTION_DE), descriptionDeTextField);
    addLabelAndInputFieldToGrid(I18N.getString(LABEL_DESCRIPTION_EN), descriptionEnTextField);
    addLabelAndInputFieldToGrid(I18N.getString(LABEL_RELEVANT), relevantComboBox);

    addValidationListenerToInputField(salesRegionIdTextField);
    addValidationListenerToInputField(descriptionDeTextField);
    addValidationListenerToInputField(descriptionEnTextField);
    addValidationListenerToInputField(relevantComboBox);

    salesRegionIdTextField.textProperty().addListener(this::salesRegionValidator);
  }

  @Override
  protected ChangeListener getValidationListener() {
    return (observable, oldValue, newValue) -> commitButton.setDisable(
      (salesRegionIdTextField.getCharacters().length() < 3) || StringUtils.isEmpty(descriptionDeTextField.getText())
        || isInvalid());
  }

  @Override
  protected ListChangeListener getValidationListenerForList() {
    return null;
  }

  @Override
  protected boolean isInvalid() {
    return items.stream().anyMatch(
      item -> item.getSalesRegion().equals(salesRegionIdTextField.getText()) && !selectedItem.getSalesRegion()
        .equals(item.getSalesRegion()));
  }

  @Override
  protected SalesRegionVMO dialogResult() {
    final SalesRegionVMO newSalesRegion = new SalesRegionVMO();

    newSalesRegion.setSalesRegion(salesRegionIdTextField.getText());
    newSalesRegion.setDescriptionDe(descriptionDeTextField.getText());
    newSalesRegion.setDescriptionEn(descriptionEnTextField.getText());
    newSalesRegion.setRelevant(convertRelevantComboBoxValueToInteger());

    return newSalesRegion;
  }

  private String convertRelevantValueToString(final Integer relevant) {
    return I18N.getString((relevant != 1) ? COMBO_BOX_VALUE_NOT_RELEVANT : COMBO_BOX_VALUE_RELEVANT);
  }

  private Integer convertRelevantComboBoxValueToInteger() {
    return (I18N.getString(COMBO_BOX_VALUE_RELEVANT).equals(relevantComboBox.getValue())) ? 1 : 0;
  }

  private void salesRegionValidator(ObservableValue<? extends String> observable, String oldValue, String newValue) {

    salesRegionIdTextField.setText((newValue.length() > 3) ? oldValue : newValue.toUpperCase());
    if (!newValue.isEmpty()) {
      final boolean numericFirstValue = StringUtils.isNumeric(String.valueOf(newValue.charAt(0)));

      if (numericFirstValue) {
        salesRegionIdTextField.setText(oldValue);
      }
    }
  }
}
