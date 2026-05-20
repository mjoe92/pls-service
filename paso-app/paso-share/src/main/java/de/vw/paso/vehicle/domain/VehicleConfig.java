package de.vw.paso.vehicle.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.model.Model;
import de.vw.paso.model.ModelImport;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.pls.Status;
import de.vw.paso.tiwh.domain.TiWhImport;
import de.vw.paso.user.domain.Resource;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.time.FastDateFormat;

@Entity
@Table(name = "VEHICLE_CONFIG")
public final class VehicleConfig extends AbstractModifiableEntity<Long> {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("dd.MM.yyyy", Locale.GERMANY);

    @Id
    @Column(name = "VEHICLE_CONFIG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RESOURCE_ID", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VEHICLE_PROJECT_ID", nullable = false)
    private VehicleProject vehicleProject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MODEL_IMPORT_ID")
    private ModelImport modelImport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MODEL_ID")
    private Model model;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SALES_REGION_ID")
    private SalesRegion salesRegion;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "VEHICLE_PART_LIST_ID")
    private VehiclePartList vehiclePartList;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "VALID_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date validDate;

    @Column(name = "PR_NUMBER_STRING", length = 4000)
    private String prNumberString;

    @Column(name = "MODEL_YEAR", columnDefinition = "int(4)")
    private Integer modelYear;

    @Column(name = "SET_VERSION_ID", nullable = false)
    private Long setVersionId;

    @ManyToOne
    @JoinColumn(name = "SET_VERSION_ID", nullable = false, insertable = false, updatable = false)
    private SetVersion setVersion;

    @Column(name = "COST_GROUP_VERSION", nullable = false)
    private Long costGroupVersion;

    @Column(name = "DELETION_DATE")
    @Temporal(TemporalType.DATE)
    private Date deletionDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "vehicleConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleConfigCategoryStatus> vehicleConfigCategoryStatus = new ArrayList<>();

    @Column(name = "PLS_PRODUCT_DATA_ID")
    private String plsProductDataId;

    @Column(name = "PLS_DATA_ID")
    private Long plsDataId;

    @Column(name = "PLS_DATA_LOCK_ID")
    private String plsDataLockId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VEHICLE_TI_WH_IMPORT_ID")
    private TiWhImport tiWhImportVehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MOTOR_TI_WH_IMPORT_ID")
    private TiWhImport tiWhImportMotor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GEARBOX_TI_WH_IMPORT_ID")
    private TiWhImport tiWhImportGearbox;

    /**
     * The {@link Status} of the vehicle config accounts whether everything is set by the user
     * and the status of the part list request/creation.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PLS_STATUS", columnDefinition = "varchar(50)")
    private Status status = Status.INCOMPLETE;

    @Column(name = "PLS_POSITION")
    private Integer requestPosition;

    @Column(name = "SMART_FIX_ACTIVE", columnDefinition = "int(i)")
    private boolean smartFixesActive;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "vehicleConfigs")
    private Set<UserGroup> userGroups = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = UserGroup.USER_GROUP_ID, nullable = false)
    private UserGroup ownerGroup;

    public void setOwnerGroup(UserGroup ownerGroup) {
        if (this.ownerGroup != null) {
            this.ownerGroup.getOwnedVehicleConfigs().remove(this);
        }

        this.ownerGroup = ownerGroup;

        if (ownerGroup != null) {
            this.ownerGroup.getOwnedVehicleConfigs().add(this);
        }
    }

    public void setUserGroups(Collection<UserGroup> userGroups) {
        for (UserGroup userGroup : this.userGroups) {
            userGroup.getVehicleConfigs().remove(this);
        }

        this.userGroups.clear();
        this.userGroups.addAll(userGroups);

        for (UserGroup userGroup : userGroups) {
            userGroup.getVehicleConfigs().add(this);
        }
    }

    public void setVehiclePartList(VehiclePartList vehiclePartList) {
        this.vehiclePartList = vehiclePartList;

        if (vehiclePartList != null) {
            vehiclePartList.setVehicleConfig(this);
        }
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setVehicleConfigCategoryStatus(Collection<VehicleConfigCategoryStatus> vehicleConfigCategoryStatus) {
        this.vehicleConfigCategoryStatus.clear();
        this.vehicleConfigCategoryStatus.addAll(vehicleConfigCategoryStatus);

        for (VehicleConfigCategoryStatus configCategoryStatus : vehicleConfigCategoryStatus) {
            configCategoryStatus.setVehicleConfig(this);
        }
    }

    @Transient
    public boolean isEditable() {
        return getStatus() == null || Status.INCOMPLETE == getStatus();
    }

    @Override
    public String toString() {
        String creationDate = getTimestampCreate() == null ? ", No Creation Date" :
                StringConstant.COMMA_SPACE + DATE_FORMAT.format(getTimestampCreate());
        return getVehicleProject().getProjectName() + StringConstant.COMMA_SPACE + getName() + creationDate;
    }

    public String getVehicleProjectName() {
        return getVehicleProject().getProjectName();
    }

    public static Comparator<? super VehicleConfig> getComparator() {
        return Comparator.comparing(VehicleConfig::getVehicleProjectName).thenComparing(VehicleConfig::getName)
                .thenComparing(VehicleConfig::getTimestampCreate);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    public VehicleProject getVehicleProject() {
        return vehicleProject;
    }

    public ModelImport getModelImport() {
        return modelImport;
    }

    public Model getModel() {
        return model;
    }

    public SalesRegion getSalesRegion() {
        return salesRegion;
    }

    public VehiclePartList getVehiclePartList() {
        return vehiclePartList;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getValidDate() {
        return validDate;
    }

    public String getPrNumberString() {
        return prNumberString;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public Long getSetVersionId() {
        return setVersionId;
    }

    public SetVersion getSetVersion() {
        return setVersion;
    }

    public Long getCostGroupVersion() {
        return costGroupVersion;
    }

    public Date getDeletionDate() {
        return deletionDate;
    }

    public List<VehicleConfigCategoryStatus> getVehicleConfigCategoryStatus() {
        return vehicleConfigCategoryStatus;
    }

    public String getPlsProductDataId() {
        return plsProductDataId;
    }

    public Long getPlsDataId() {
        return plsDataId;
    }

    public String getPlsDataLockId() {
        return plsDataLockId;
    }

    public TiWhImport getTiWhImportVehicle() {
        return tiWhImportVehicle;
    }

    public TiWhImport getTiWhImportMotor() {
        return tiWhImportMotor;
    }

    public TiWhImport getTiWhImportGearbox() {
        return tiWhImportGearbox;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getRequestPosition() {
        return requestPosition;
    }

    public boolean isSmartFixesActive() {
        return smartFixesActive;
    }

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public UserGroup getOwnerGroup() {
        return ownerGroup;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setVehicleProject(VehicleProject vehicleProject) {
        this.vehicleProject = vehicleProject;
    }

    public void setModelImport(ModelImport modelImport) {
        this.modelImport = modelImport;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setSalesRegion(SalesRegion salesRegion) {
        this.salesRegion = salesRegion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public void setPrNumberString(String prNumberString) {
        this.prNumberString = prNumberString;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public void setSetVersionId(Long setVersionId) {
        this.setVersionId = setVersionId;
    }

    public void setSetVersion(SetVersion setVersion) {
        this.setVersion = setVersion;
    }

    public void setCostGroupVersion(Long costGroupVersion) {
        this.costGroupVersion = costGroupVersion;
    }

    public void setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
    }

    public void setVehicleConfigCategoryStatus(List<VehicleConfigCategoryStatus> vehicleConfigCategoryStatus) {
        this.vehicleConfigCategoryStatus = vehicleConfigCategoryStatus;
    }

    public void setPlsProductDataId(String plsProductDataId) {
        this.plsProductDataId = plsProductDataId;
    }

    public void setPlsDataId(Long plsDataId) {
        this.plsDataId = plsDataId;
    }

    public void setPlsDataLockId(String plsDataLockId) {
        this.plsDataLockId = plsDataLockId;
    }

    public void setTiWhImportVehicle(TiWhImport tiWhImportVehicle) {
        this.tiWhImportVehicle = tiWhImportVehicle;
    }

    public void setTiWhImportMotor(TiWhImport tiWhImportMotor) {
        this.tiWhImportMotor = tiWhImportMotor;
    }

    public void setTiWhImportGearbox(TiWhImport tiWhImportGearbox) {
        this.tiWhImportGearbox = tiWhImportGearbox;
    }

    public void setRequestPosition(Integer requestPosition) {
        this.requestPosition = requestPosition;
    }

    public void setSmartFixesActive(boolean smartFixesActive) {
        this.smartFixesActive = smartFixesActive;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }
}
