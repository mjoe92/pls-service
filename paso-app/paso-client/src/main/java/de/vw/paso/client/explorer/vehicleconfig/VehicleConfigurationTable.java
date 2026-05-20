package de.vw.paso.client.explorer.vehicleconfig;

import java.io.IOException;
import java.util.Date;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.cell.TableCellFactory;
import de.vw.paso.client.control.table.CustomTableView;
import de.vw.paso.client.explorer.vehicleconfig.converter.DateTimeStringConverter;
import de.vw.paso.client.util.FXStyleConstants;
import de.vw.paso.client.util.FxmlException;
import de.vw.paso.client.util.StatusUtil;
import de.vw.paso.client.util.converter.DoubleStringConverter;
import de.vw.paso.pls.Status;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

public class VehicleConfigurationTable extends CustomTableView<VehicleConfigDTO> {

    private static final String FXML_FILE = "fzgprojekt-tableview.fxml";

    @FXML
    private TableColumn<VehicleConfigDTO, String> columnStatus;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnPositionInQueue;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnVehicleProject;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnVehicleMotor;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnVehicleGearbox;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnVehicleConfiguration;
    @FXML
    private TableColumn<VehicleConfigDTO, Date> columnVehicleValidDate;
    @FXML
    private TableColumn<VehicleConfigDTO, Double> columnWeight;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnModelKey;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnModellVertriebsregionKennzeichen;
    @FXML
    private TableColumn<VehicleConfigDTO, Integer> columnModelYear;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnModelDescription;
    @FXML
    private TableColumn<VehicleConfigDTO, Date> columnModelStartDate;
    @FXML
    private TableColumn<VehicleConfigDTO, Date> columnModelEndDate;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnModelStatus;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnOwnerGroup;
    @FXML
    private TableColumn<VehicleConfigDTO, Date> columnTimestampCreate;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnUserCreatedBy;
    @FXML
    private TableColumn<VehicleConfigDTO, Date> columnTimestampChange;
    @FXML
    private TableColumn<VehicleConfigDTO, String> columnUserChangedBy;

    public VehicleConfigurationTable() {
        init();
        this.makeFilterable();
    }

    private void init() {
        loadFxml();
        getSortOrder().add(columnVehicleConfiguration);
        initColumns();
    }

    private void initColumns() {
        columnStatus.setCellValueFactory(data -> createCellStatus(data.getValue()));
        columnPositionInQueue.setCellValueFactory(data -> createPositionQueue(data.getValue()));

        columnVehicleProject.setCellValueFactory(data -> createVehicleProjectDescription(data.getValue()));
        columnVehicleMotor.setCellValueFactory(data -> createMotor(data.getValue()));
        columnVehicleGearbox.setCellValueFactory(data -> createGearBox(data.getValue()));

        columnVehicleConfiguration.setCellValueFactory(config -> new SimpleStringProperty(config.getValue().getName()));

        columnVehicleValidDate.setCellValueFactory(data -> createValidDate(data.getValue()));
        columnVehicleValidDate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnWeight.setCellValueFactory(data -> createGewicht(data.getValue()));
        columnWeight.setCellFactory(new TableCellFactory<>(new DoubleStringConverter(3, true)));

        columnModelKey.setCellValueFactory(data -> createModelKey(data.getValue()));
        columnModellVertriebsregionKennzeichen.setCellValueFactory(
                data -> createModelSalesRegionLicense(data.getValue()));
        columnModelYear.setCellValueFactory(data -> createModelYear(data.getValue()));
        columnModelDescription.setCellValueFactory(data -> createModelDescription(data.getValue()));

        columnModelStartDate.setCellValueFactory(data -> createModelStartDate(data.getValue()));
        columnModelStartDate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnModelEndDate.setCellValueFactory(data -> createModelEndDate(data.getValue()));
        columnModelEndDate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnModelStatus.setCellValueFactory(data -> createModelStatus(data.getValue()));

        columnOwnerGroup.setCellValueFactory(data -> createOwnerGroup(data.getValue()));

        columnTimestampCreate.setCellValueFactory(data -> createTimestampCreate(data.getValue()));
        columnTimestampCreate.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnUserCreatedBy.setCellValueFactory(data -> createUserCreateBy(data.getValue()));

        columnTimestampChange.setCellValueFactory(data -> createTimestampChange(data.getValue()));
        columnTimestampChange.setCellFactory(new TableCellFactory<>(new DateTimeStringConverter()));

        columnUserChangedBy.setCellValueFactory(data -> createUserChangedBy(data.getValue()));

        setCellAlignments();
    }

    private void setCellAlignments() {
        columnVehicleValidDate.setStyle(FXStyleConstants.ALIGNMENT_CENTER);
        columnWeight.setStyle(FXStyleConstants.ALIGNMENT_RIGHT);
        columnModelStartDate.setStyle(FXStyleConstants.ALIGNMENT_CENTER);
        columnModelEndDate.setStyle(FXStyleConstants.ALIGNMENT_CENTER);
        columnTimestampChange.setStyle(FXStyleConstants.ALIGNMENT_CENTER);
    }

    private ObservableValue<String> createCellStatus(VehicleConfigDTO config) {
        Status status = config.getStatus();
        return status == null ? null : new SimpleStringProperty(StatusUtil.getName(status));
    }

    private ObservableValue<String> createPositionQueue(VehicleConfigDTO config) {
        Integer requestPosition = config.getRequestPosition();
        return Status.PENDING != config.getStatus() || requestPosition == null ? null :
                new SimpleStringProperty(String.valueOf(requestPosition));
    }

    private ObservableValue<Double> createGewicht(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getVehicleWeight(config));
    }

    private ObservableValue<Date> createValidDate(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getVehicleValidDate(config));
    }

    private ObservableValue<String> createModelKey(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getModelKey(config));
    }

    private ObservableValue<String> createModelSalesRegionLicense(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null :
                new SimpleObjectProperty<>(getModelSalesRegionLicensePlate(config));
    }

    private ObservableValue<Integer> createModelYear(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getModelYear(config));
    }

    private ObservableValue<String> createModelDescription(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getModelDescription(config));
    }

    private ObservableValue<Date> createModelStartDate(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getModelEinsatz(config));
    }

    private ObservableValue<Date> createModelEndDate(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getModelEntfall(config));
    }

    private ObservableValue<Date> createTimestampCreate(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getTimestampCreate(config));
    }

    private ObservableValue<String> createUserCreateBy(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getUserCreate(config));
    }

    private ObservableValue<Date> createTimestampChange(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getTimestampChange(config));
    }

    private ObservableValue<String> createUserChangedBy(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getUserChangedBy(config));
    }

    private ObservableValue<String> createModelStatus(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null : new SimpleObjectProperty<>(getModelStatus(config));
    }

    private ObservableValue<String> createVehicleProjectDescription(VehicleConfigDTO config) {
        return config.getVehicleProject() == null ? null :
                new SimpleObjectProperty<>(getVehicleProjectDescription(config));
    }

    private ObservableValue<String> createMotor(VehicleConfigDTO config) {
        return config.getVehiclePartList() == null ? null : new SimpleObjectProperty<>(getMotor(config));
    }

    private ObservableValue<String> createGearBox(VehicleConfigDTO config) {
        return config.getVehiclePartList() == null ? null : new SimpleObjectProperty<>(getGearBox(config));
    }

    private ObservableValue<String> createOwnerGroup(VehicleConfigDTO config) {
        return config.getOwnerGroup() == null ? null : new SimpleObjectProperty<>(config.getOwnerGroup().getName());
    }

    private Double getVehicleWeight(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        VehiclePartListDTO partList = vehicleConfig.getVehiclePartList();
        if (partList == null) {
            return null;
        }

        Double gewicht = partList.getWeight();
        return gewicht == null ? 0.0 : gewicht / 1000.0;
    }

    private Date getVehicleValidDate(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null ? null : vehicleConfig.getValidDate();
    }

    private String getModelKey(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getModelKey();
    }

    private String getModelSalesRegionLicensePlate(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getModelImport().getSalesRegion().id();
    }

    private Integer getModelYear(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getModelImport().getModelYear();
    }

    private String getModelDescription(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getDescription();
    }

    private Date getModelEinsatz(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getBeginDate();
    }

    private Date getModelEntfall(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getEndDate();
    }

    private Date getTimestampCreate(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null ? null : vehicleConfig.getTimestampCreate();
    }

    private String getUserCreate(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null ? null : vehicleConfig.getUserCreate();
    }

    private Date getTimestampChange(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null ? null : vehicleConfig.getTimestampChange();
    }

    private String getUserChangedBy(VehicleConfigDTO vehicleConfig) {
        return vehicleConfig == null ? null : vehicleConfig.getUserChange();
    }

    private String getModelStatus(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        ModelDTO model = vehicleConfig.getModel();
        return model == null ? null : model.getStatus();
    }

    private String getVehicleProjectDescription(VehicleConfigDTO vehicleConfig) {
        if (vehicleConfig == null) {
            return null;
        }

        VehicleProjectDTO project = vehicleConfig.getVehicleProject();
        return project == null ? null :
                project.getProjectName() + StringConstant.SPACE_SLASH_SPACE + project.getProductKey();
    }

    private String getMotor(VehicleConfigDTO vehicleConfigDTO) {
        if (vehicleConfigDTO == null) {
            return null;
        }

        VehiclePartListDTO partList = vehicleConfigDTO.getVehiclePartList();
        return partList == null ? null : partList.getProductKeyMotor();
    }

    private String getGearBox(VehicleConfigDTO vehicleConfigDTO) {
        if (vehicleConfigDTO == null) {
            return null;
        }

        VehiclePartListDTO partList = vehicleConfigDTO.getVehiclePartList();
        return partList == null ? null : partList.getProductKeyGearbox();
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(I18N.getBundle());
        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new FxmlException(exception);
        }
    }
}
