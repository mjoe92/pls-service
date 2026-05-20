package de.vw.paso.client.stueckliste.efs.header;

import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.google.common.eventbus.Subscribe;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.stueckliste.efs.control.AggregatTextFieldControl;
import de.vw.paso.client.stueckliste.efs.control.AggregatTextFieldEvent;
import de.vw.paso.client.stueckliste.efs.control.AggregateImageTextFieldControl;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;
import de.vw.paso.client.stueckliste.fzgkonfig.VehicleConfigChangedEvent;
import de.vw.paso.client.util.icon.HeaderIcon;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

@FXController(name = "efs-header-fahrzeug")
public class EfsHeaderFahrzeugController extends BaseController<GridPane> implements Initializable {

    private static final String WEIGHT_UNIT = "kg";

    private final ObjectProperty<EventHandler<AggregatTextFieldEvent>> propertyAggregatAction;

    @FXML
    protected GridPane paneEfsHeader;
    @FXML
    protected TextField textFieldFzgProject;
    @FXML
    protected TextField textFieldFzgConfig;
    @FXML
    protected TextField textFieldFzgStartDate;
    @FXML
    protected TextField textFieldModelKey;
    @FXML
    protected TextField textFieldModelLand;
    @FXML
    protected TextField textFieldModelYear;
    @FXML
    protected TextField textFieldModelDescription;
    @FXML
    protected TextField textFieldModelStartDate;
    @FXML
    protected TextField textFieldModelEndDate;
    @FXML
    protected TextField textFieldTimestampCreated;
    @FXML
    protected TextField textFieldUserCreated;
    @FXML
    protected TextField textFieldTimestampChanged;
    @FXML
    protected TextField textFieldUserChanged;
    @FXML
    protected TextField textFieldStatus;
    @FXML
    protected AggregatTextFieldControl textFieldMotor;
    @FXML
    protected AggregatTextFieldControl textFieldGearbox;
    @FXML
    protected AggregateImageTextFieldControl textWeightSelected;
    @FXML
    protected AggregateImageTextFieldControl textWeightFiltered;
    @FXML
    protected AggregateImageTextFieldControl textWeightAll;
    @FXML
    protected AggregateImageTextFieldControl textSelectedPositions;
    @FXML
    protected AggregateImageTextFieldControl textFilteredPositions;
    @FXML
    protected AggregateImageTextFieldControl textPositions;

    private Double filteredWeight;
    private Double selectedWeight;
    private Double partListWeight;
    private Integer selectedPositions;
    private Integer filteredPositions;
    private Integer partListPositions;

    public EfsHeaderFahrzeugController() {
        propertyAggregatAction = new SimpleObjectProperty<>(this, "AggregatTextFieldControl");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        int imageSize = 12;
        setTextField(textWeightSelected, imageSize, "label.stueckliste.gewicht.selected.tooltip");
        setTextField(textWeightFiltered, imageSize, "label.stueckliste.gewicht.filtered.tooltip");
        setTextField(textWeightAll, imageSize, "label.stueckliste.gewicht.all.tooltip");
        setTextField(textSelectedPositions, imageSize, "label.stueckliste.positions.selected.tooltip");
        setTextField(textFilteredPositions, imageSize, "label.stueckliste.positions.filtered.tooltip");
        setTextField(textPositions, imageSize, "label.stueckliste.positions.all.tooltip");

        int position = 0;
        setPositions(position);
        setSelectedPositions(position);
        setFilteredPositions(position);
    }

    @Override
    public GridPane getControl() {
        return paneEfsHeader;
    }

    @Override
    public Parent getStyleableParent() {
        return paneEfsHeader;
    }

    @FXML
    public void handleActionShowAggregat(AggregatTextFieldEvent event) {
        aggregatActionProperty().get().handle(event);
    }

    public void setVehicleConfig(VehicleConfigDTO vehicleConfig) {
        setFahrzeug(vehicleConfig);
        setModell(vehicleConfig.getModel());
        setDatenstand(vehicleConfig);
    }

    public void setAggregateAndWeight(EfsElementTreeItem motor, EfsElementTreeItem getriebe, Double gewicht) {
        if (textFieldMotor != null) {
            textFieldMotor.setAggregat(motor);
        }

        if (textFieldGearbox != null) {
            textFieldGearbox.setAggregat(getriebe);
        }

        setWeight(gewicht);
        setSelectedWeight(null);
        setFilteredWeight(null);

        if (textWeightSelected != null) {
            textWeightSelected.setImageView(HeaderIcon.SELECTED_16x16.getImage());
        }

        if (textWeightFiltered != null) {
            textWeightFiltered.setImageView(HeaderIcon.FILTER_16x16.getImage());
        }

        if (textWeightAll != null) {
            textWeightAll.setImageView(HeaderIcon.ALL_16x16.getImage());
        }

        if (textSelectedPositions != null) {
            textSelectedPositions.setImageView(HeaderIcon.SELECTED_16x16.getImage());
        }

        if (textFilteredPositions != null) {
            textFilteredPositions.setImageView(HeaderIcon.FILTER_16x16.getImage());
        }

        if (textPositions != null) {
            textPositions.setImageView(HeaderIcon.ALL_16x16.getImage());
        }
    }

    public void setSelectedPartList(Double selectedWeight, int numberOfSelectedElements) {
        setSelectedWeight(selectedWeight);
        setSelectedPositions(numberOfSelectedElements);
    }

    public void setFilteredPartList(Double filteredWeight, int numberOfFilteredElements) {
        setFilteredWeight(filteredWeight);
        setFilteredPositions(numberOfFilteredElements);
    }

    public void setWeight(Double weight) {
        partListWeight = weight;
        if (textWeightAll != null) {
            String text = formatWeight(weight);
            textWeightAll.setText(text);
        }
    }

    public void setPositions(int positions) {
        partListPositions = positions;
        if (textPositions != null) {
            textPositions.setText(NumberFormat.getInstance().format(positions));
        }
    }

    public final ObjectProperty<EventHandler<AggregatTextFieldEvent>> aggregatActionProperty() {
        return propertyAggregatAction;
    }

    public final void setAggregatAction(EventHandler<AggregatTextFieldEvent> handler) {
        propertyAggregatAction.set(handler);
    }

    @Subscribe
    private void handleVehicleConfigChangedEvent(VehicleConfigChangedEvent event) {
        setDatenstand(event.vehicleConfig());
    }

    private void setTextField(AggregateImageTextFieldControl textWeightSelected, int imageSize, String key) {
        if (textWeightSelected != null) {
            textWeightSelected.getTextField().setAlignment(Pos.CENTER_RIGHT);
            textWeightSelected.getImageView().setFitHeight(imageSize);
            textWeightSelected.getImageView().setFitWidth(imageSize);
            textWeightSelected.setTooltip(I18N.getString(key));
        }
    }

    private void setFahrzeug(VehicleConfigDTO vehicleConfig) {
        textFieldFzgProject.setText(
                vehicleConfig.getVehicleProject().getProjectName() + StringConstant.SPACE_SLASH_SPACE
                        + vehicleConfig.getVehicleProject().getProductKey());
        textFieldFzgConfig.setText(vehicleConfig.getName());
        textFieldFzgStartDate.setText(DateFormat.getDateInstance().format(vehicleConfig.getValidDate()));
    }

    private void setModell(ModelDTO model) {
        if (model != null) {
            textFieldModelKey.setText(model.getModelKey());
            textFieldModelLand.setText(model.getModelImport().getSalesRegion().id());
            textFieldModelYear.setText(model.getModelImport().getModelYear().toString());

            textFieldModelDescription.setText(model.getDescription());

            textFieldModelStartDate.setText(DateFormat.getDateInstance().format(model.getBeginDate()));
            textFieldModelEndDate.setText(DateFormat.getDateInstance().format(model.getEndDate()));
        }
    }

    private void setDatenstand(VehicleConfigDTO vehicleConfig) {
        VehiclePartListDTO vehiclePartList = vehicleConfig.getVehiclePartList();
        if (vehiclePartList != null) {
            textFieldTimestampCreated.setText(
                    DateFormat.getDateInstance().format(vehiclePartList.getTimestampCreate()));
            textFieldUserCreated.setText(vehiclePartList.getUserCreate());

            textFieldTimestampChanged.setText(
                    DateFormat.getDateInstance().format(vehiclePartList.getTimestampChange()));
            textFieldUserChanged.setText(vehiclePartList.getUserChange() != null ? vehiclePartList.getUserChange() :
                    vehiclePartList.getUserCreate());
        }

        textFieldStatus.setText("TODO");
    }

    private void setSelectedWeight(Double weight) {
        selectedWeight = weight;
        if (textWeightSelected != null) {
            String text = formatWeight(weight);
            textWeightSelected.setText(text);
        }
    }

    private void setFilteredWeight(Double weight) {
        filteredWeight = weight;
        if (textWeightFiltered != null) {
            String text = formatWeight(weight);
            textWeightFiltered.setText(text);
        }
    }

    private void setSelectedPositions(int positions) {
        selectedPositions = positions;
        if (textSelectedPositions != null) {
            textSelectedPositions.setText(NumberFormat.getInstance().format(positions));
        }
    }

    private void setFilteredPositions(int positions) {
        filteredPositions = positions;
        if (textFilteredPositions != null) {
            textFilteredPositions.setText(NumberFormat.getInstance().format(positions));
        }
    }

    private String formatWeight(Double weight) {
        double formattedWeight = weight == null ? 0.0 : weight / 1000.0;
        return formatNumber(formattedWeight) + StringConstant.SPACE + WEIGHT_UNIT;
    }

    private String formatNumber(double weight) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(3);

        return numberFormatter.format(weight);
    }

    public Double getFilteredWeight() {
        return filteredWeight;
    }

    public Double getSelectedWeight() {
        return selectedWeight;
    }

    public Double getPartListWeight() {
        return partListWeight;
    }

    public Integer getSelectedPositions() {
        return selectedPositions;
    }

    public Integer getFilteredPositions() {
        return filteredPositions;
    }

    public Integer getPartListPositions() {
        return partListPositions;
    }
}
