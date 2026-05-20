package de.vw.paso.repository.masterdata;

import java.util.Collection;

import de.vw.paso.masterdata.domain.SalesRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SalesRegionRepository extends JpaRepository<SalesRegion, String> {

    @Query("""
            SELECT id, SUM(c) FROM (
              SELECT vc.salesRegion.id AS id, count(*) AS c
              FROM VehicleConfig vc
              WHERE vc.salesRegion.id IN :ids
              GROUP BY vc.salesRegion.id
            UNION
              SELECT mi.salesRegion.id AS id, COUNT(*) AS c
              FROM ModelImport mi
              WHERE mi.salesRegion.id IN :ids
              GROUP BY mi.salesRegion.id
            ) AS temp GROUP BY id""")
    Collection<ConstraintIssuesDTO> countConstrainIssues(Collection<String> ids);

    @Modifying
    @Query("""
            UPDATE SalesRegion AS s SET s.relevant = :relevant
            WHERE s.id IN :salesRegionIds
            """)
    void updateRelevance(Collection<String> salesRegionIds, Integer relevant);

    @Modifying
    @Query("""
            DELETE FROM SalesRegion sr
            WHERE sr.id IN :ids
            """)
    void deleteSalesRegionWithIds(Collection<String> ids);

}
