package de.vw.paso.repository.vehicle;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.vw.paso.masterdata.Brand;
import de.vw.paso.pls.Status;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehicleConfigRepository extends JpaRepository<VehicleConfig, Long> {

    Collection<VehicleConfig> findByIdIn(Collection<Long> vehicleConfigurationIds);

    Collection<VehicleConfig> findAllByDeletionDate(Date date);

    Collection<VehicleConfig> findByIdInAndDeletionDateNotNull(Collection<Long> vehicleConfigIds);

    Collection<VehicleConfig> findByIdInAndVehicleProjectId(Collection<Long> ids, long vehicleProjectId);

    Collection<VehicleConfig> findByVehicleProjectIdInAndIdIn(Collection<Long> vehicleProjectIds, Collection<Long> ids);

    List<VehicleConfig> findBySetVersionId(Long id);

    @Query("""
            SELECT VC FROM VehicleConfig VC
            WHERE VC.vehicleProject IN (
              SELECT VP FROM VehicleProject VP WHERE VP.brandCode = :brand
            ) AND VC.id IN :ids
            """)
    Collection<VehicleConfig> findAllByBrandAndIds(Brand brand, Set<Long> ids);

    @Query("""
            SELECT VC FROM VehicleConfig VC
            WHERE VC.vehicleProject IN (
              SELECT VP FROM VehicleProject VP WHERE VP.productKey = :productKey
            ) AND VC.id IN :ids
            """)
    Collection<VehicleConfig> findByVehicleProductKeyAndIds(String productKey, Set<Long> ids);

    @Query("""
            SELECT vp.id, count(*) FROM VehicleProject vp
            JOIN VehicleConfig vc ON vp.id = vc.vehicleProject.id
            WHERE vc.deletionDate IS NULL GROUP BY vp.id
            """)
    Collection<VehicleProjectConfigurationCountDTO> getConfigurationCountForVehicleProjects();

    @Query("""
            SELECT VC.plsProductDataId, VC.vehicleProject.productKey FROM VehicleConfig VC
            WHERE VC.plsProductDataId IS NOT NULL
            AND VC.vehiclePartList IS NULL AND VC.deletionDate IS NULL
            AND VC.status IN ('PENDING', 'UNKNOWN')
            """)
    Collection<PendingProductDataIdDTO> getPendingProductDataId();

    @Query("""
            SELECT VC FROM VehicleConfig VC
            WHERE VC.plsProductDataId = :productDataID
            """)
    Collection<VehicleConfig> getVehicleConfigByProductDataID(String productDataID);

    Collection<VehicleConfig> findByTimestampCreateLessThan(Timestamp creationDate);

    Collection<VehicleConfig> findByTimestampCreateLessThanAndStatusIn(Timestamp creationDate,
            Collection<Status> status);

    Collection<VehicleConfig> findByDeletionDateBefore(Date date);
}
