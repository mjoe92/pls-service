package de.vw.paso.service.vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.pls.Status;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.modelimport.ModelImportDTO;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.user.ResourceDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.time.FastDateFormat;

public class VehicleConfigDTO extends AbstractModifiableDTO<Long> {

    private Long id;
    private ResourceDTO resource;
    private VehicleProjectDTO vehicleProject;
    private ModelImportDTO modelImport;
    private ModelDTO model;
    private SalesRegionDTO salesRegion;
    private VehiclePartListDTO vehiclePartList;
    private String name;
    private String description;
    private Date validDate;
    //todo: remove and replace with added prNumberIds everywhere
    private String prNumberString;
    private Integer modelYear;
    private Long setVersionId;
    private SetVersionDTO setVersion;
    private Long costGroupVersion;
    private Date deletionDate;
    private Collection<VehicleConfigCategoryStatusDTO> vehicleConfigCategoryStatus;
    private String plsProductDataId;
    private Long plsDataId;
    private String plsDataLockId;
    private TiWhImportDTO tiWhImportVehicle;
    private TiWhImportDTO tiWhImportMotor;
    private TiWhImportDTO tiWhImportGearbox;
    private Status status = Status.INCOMPLETE;
    private Integer requestPosition;
    private boolean smartFixesActive;
    private Collection<UserGroupDTO> userGroups;
    private UserGroupDTO ownerGroup;
    private boolean editAllowed;
    private boolean updateDefaultSetVersion;
    private Map<Long, Collection<Long>> prFamilyToNumberIds;

    public VehicleConfigDTO() {
        vehicleConfigCategoryStatus = new ArrayList<>();
        userGroups = new ArrayList<>();
    }

    @Override
    public String toString() {
        String creationDate = getTimestampCreate() == null ? ", No Creation Date" :
                StringConstant.COMMA_SPACE + FastDateFormat.getInstance("dd.MM.yyyy", Locale.GERMANY)
                                             .format(getTimestampCreate());
        return vehicleProject.getProjectName() + StringConstant.COMMA_SPACE + name + creationDate;
    }

    public static Comparator<? super VehicleConfigDTO> getComparator() {
        return Comparator.comparing(VehicleConfigDTO::vehicleProjectName).thenComparing(VehicleConfigDTO::getName)
                .thenComparing(VehicleConfigDTO::getTimestampCreate);
    }

    @JsonIgnore
    public boolean isEditable() {
        return status.canRequestPartList();
    }

    public String vehicleProjectName() {
        if (vehicleProject == null) {
            return "no vehicle project";
        }

        return vehicleProject.getProjectName() == null ? "no project name" : getVehicleProject().getProjectName();
    }

    @Override
    public Long getId() {
        return id;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public VehicleProjectDTO getVehicleProject() {
        return vehicleProject;
    }

    public ModelImportDTO getModelImport() {
        return modelImport;
    }

    public ModelDTO getModel() {
        return model;
    }

    public SalesRegionDTO getSalesRegion() {
        return salesRegion;
    }

    public VehiclePartListDTO getVehiclePartList() {
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

    public SetVersionDTO getSetVersion() {
        return setVersion;
    }

    public Long getCostGroupVersion() {
        return costGroupVersion;
    }

    public Date getDeletionDate() {
        return deletionDate;
    }

    public Collection<VehicleConfigCategoryStatusDTO> getVehicleConfigCategoryStatus() {
        return vehicleConfigCategoryStatus;
    }

    public Long getPlsDataId() {
        return plsDataId;
    }

    public String getPlsDataLockId() {
        return plsDataLockId;
    }

    public TiWhImportDTO getTiWhImportVehicle() {
        return tiWhImportVehicle;
    }

    public TiWhImportDTO getTiWhImportMotor() {
        return tiWhImportMotor;
    }

    public TiWhImportDTO getTiWhImportGearbox() {
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

    public Collection<UserGroupDTO> getUserGroups() {
        return userGroups;
    }

    public UserGroupDTO getOwnerGroup() {
        return ownerGroup;
    }

    public boolean isEditAllowed() {
        return editAllowed;
    }

    public boolean isUpdateDefaultSetVersion() {
        return updateDefaultSetVersion;
    }

    public String getPlsProductDataId() {
        return plsProductDataId;
    }

    public Map<Long, Collection<Long>> getPrFamilyToNumberIds() {
        return prFamilyToNumberIds;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    public void setVehicleProject(VehicleProjectDTO vehicleProject) {
        this.vehicleProject = vehicleProject;
    }

    public void setModelImport(ModelImportDTO modelImport) {
        this.modelImport = modelImport;
    }

    public void setModel(ModelDTO model) {
        this.model = model;
    }

    public void setSalesRegion(SalesRegionDTO salesRegion) {
        this.salesRegion = salesRegion;
    }

    public void setVehiclePartList(VehiclePartListDTO vehiclePartList) {
        this.vehiclePartList = vehiclePartList;
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

    public void setSetVersion(SetVersionDTO setVersion) {
        this.setVersion = setVersion;
    }

    public void setCostGroupVersion(Long costGroupVersion) {
        this.costGroupVersion = costGroupVersion;
    }

    public void setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
    }

    public void setVehicleConfigCategoryStatus(Collection<VehicleConfigCategoryStatusDTO> vehicleConfigCategoryStatus) {
        this.vehicleConfigCategoryStatus = vehicleConfigCategoryStatus;
    }

    public void setPlsDataId(Long plsDataId) {
        this.plsDataId = plsDataId;
    }

    public void setPlsDataLockId(String plsDataLockId) {
        this.plsDataLockId = plsDataLockId;
    }

    public void setTiWhImportVehicle(TiWhImportDTO tiWhImportVehicle) {
        this.tiWhImportVehicle = tiWhImportVehicle;
    }

    public void setTiWhImportMotor(TiWhImportDTO tiWhImportMotor) {
        this.tiWhImportMotor = tiWhImportMotor;
    }

    public void setTiWhImportGearbox(TiWhImportDTO tiWhImportGearbox) {
        this.tiWhImportGearbox = tiWhImportGearbox;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRequestPosition(Integer requestPosition) {
        this.requestPosition = requestPosition;
    }

    public void setSmartFixesActive(boolean smartFixesActive) {
        this.smartFixesActive = smartFixesActive;
    }

    public void setUserGroups(Collection<UserGroupDTO> userGroups) {
        this.userGroups = userGroups;
    }

    public void setOwnerGroup(UserGroupDTO ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public void setEditAllowed(boolean editAllowed) {
        this.editAllowed = editAllowed;
    }

    public void setUpdateDefaultSetVersion(boolean updateDefaultSetVersion) {
        this.updateDefaultSetVersion = updateDefaultSetVersion;
    }

    public void setPlsProductDataId(String plsProductDataId) {
        this.plsProductDataId = plsProductDataId;
    }

    public void setPrFamilyToNumberIds(Map<Long, Collection<Long>> prFamilyToNumberIds) {
        this.prFamilyToNumberIds = prFamilyToNumberIds;
    }
}
