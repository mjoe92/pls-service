package de.vw.paso.client.explorer.vehicleconfig;

import java.util.Map;

import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VehicleConfigTreeObj {

    private Brand brand;
    private VehicleProjectDTO vehicleProject;
    private VehicleConfigDTO vehicleConfig;
    private String value;
    private VehicleConfigTreeObjType vehicleConfigTreeObjType;
    private Map<Long, Long> configCount;

    private Boolean isFavorite;

    public boolean isVehicleProject() {
        return vehicleProject != null;
    }

    public boolean isBrand() {
        return brand != null;
    }

    public boolean isFavoritesGroup() {
        return VehicleConfigTreeObjType.FAVORITES == vehicleConfigTreeObjType;
    }

    public boolean isRecentlyUsedGroup() {
        return VehicleConfigTreeObjType.RECENTLY_USED == vehicleConfigTreeObjType;
    }

    public boolean isProductKey() {
        return VehicleConfigTreeObjType.PRODUCT_KEY == vehicleConfigTreeObjType;
    }

    public boolean isVehicleConfig() {
        return vehicleConfig != null;
    }

    public String getValue() {
        return getProductKey();
    }

    public String getProductKey() {
        return value;
    }

    public long getConfigCount() {
        if (isVehicleProject()) {
            Long count = configCount.get(vehicleProject.getId());
            return count == null ? 0L : count;
        }

        return 0L;
    }

    public void toggleFavorite() {
        isFavorite = !isFavorite;
    }

    public VehicleConfigTreeObjType getVehicleConfigTreeObjType() {
        return vehicleConfigTreeObjType;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VehicleConfigTreeObj otherTreeObj) {
            VehicleProjectDTO otherVehicleProject = otherTreeObj.getVehicleProject();
            VehicleProjectDTO vehicleProject = getVehicleProject();

            VehicleConfigDTO otherVehicleConfig = otherTreeObj.getVehicleConfig();
            VehicleConfigDTO vehicleConfig = getVehicleConfig();
            if (otherVehicleProject != null) {
                if (vehicleProject != null && otherVehicleProject.getId().equals(vehicleProject.getId())) {
                    return true;
                }
            } else if (otherVehicleConfig != null) {
                if (vehicleConfig != null && otherVehicleConfig.getId().equals(vehicleConfig.getId())) {
                    return true;
                }
            }
        }

        return super.equals(other);
    }

    @Override
    public String toString() {
        if (isBrand()) {
            return brand.name() + StringConstant.SPACE_DASH_SPACE + brand.getBrandName();
        }

        return isVehicleProject() ?
                vehicleProject.getProductKey() + StringConstant.SPACE_DASH_SPACE + vehicleProject.getProjectName() :
                StringConstant.EMPTY;
    }

    @Override
    public int hashCode() {
        //todo: what are these numbers? -> create normal hashcode
        return new HashCodeBuilder(17, 37).append(vehicleProject).append(vehicleConfig).toHashCode();
    }

    public Brand getBrand() {
        return brand;
    }

    public VehicleProjectDTO getVehicleProject() {
        return vehicleProject;
    }

    public VehicleConfigDTO getVehicleConfig() {
        return vehicleConfig;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setVehicleProject(VehicleProjectDTO vehicleProject) {
        this.vehicleProject = vehicleProject;
    }

    public void setVehicleConfig(VehicleConfigDTO vehicleConfig) {
        this.vehicleConfig = vehicleConfig;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVehicleConfigTreeObjType(VehicleConfigTreeObjType vehicleConfigTreeObjType) {
        this.vehicleConfigTreeObjType = vehicleConfigTreeObjType;
    }

    public void setConfigCount(Map<Long, Long> configCount) {
        this.configCount = configCount;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
