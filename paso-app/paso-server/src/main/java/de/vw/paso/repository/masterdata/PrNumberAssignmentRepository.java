package de.vw.paso.repository.masterdata;

import java.time.LocalDate;
import java.util.Collection;

import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.pr.PrNumber;
import de.vw.paso.pr.PrNumberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PrNumberAssignmentRepository extends JpaRepository<PrNumberAssignment, Long> {

    @Query("""
            FROM PrNumberAssignment pz
            JOIN VehicleProject vp ON vp.productKey = pz.product.productKey
            WHERE vp.id = :vehicleProjectId""")
    Collection<PrNumberAssignment> loadByVehicleProjectId(Long vehicleProjectId);

    @Modifying
    @Query("""
            DELETE FROM PrNumberAssignment assignment
            WHERE assignment.id NOT IN (
              SELECT mapping.id.prAssignmentId
              FROM VehicleConfigPrNumberMapping mapping
            )""")
    void deleteUnusedEntries();

    boolean existsByProductAndPrNumberAndStartDateAndEndDateAndStatusAndDescription(Product product, PrNumber prNumber,
            LocalDate startDate, LocalDate endDate, String status, String description);
}
