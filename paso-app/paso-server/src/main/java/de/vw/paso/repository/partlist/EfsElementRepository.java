package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.EfsElement;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EfsElementRepository extends JpaRepository<EfsElement, Long> {

    List<EfsElement> findAllByVehiclePartListId(long partListId);

    @Query("""
            SELECT ee FROM EfsElement ee
            JOIN VehiclePartList pl ON pl.id = ee.vehiclePartListId LEFT OUTER JOIN FETCH ee.efsElementMara mara
            WHERE pl.vehicleConfig.id = :vehicleConfigId AND ee.deleted = 0
            ORDER BY ee.tisSort
            """)
    List<EfsElement> loadNonDeletedEfsElementsByVehicleConfigId(long vehicleConfigId);

    List<EfsElement> findAllByParentId(long parentId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE EfsElement AS ee SET ee.deleted = 1, ee.revision = :newRevision
            WHERE ee.id IN :ids
            """)
    int setDeleted(@NotNull Collection<Long> ids, @NotNull Long newRevision);

    @Modifying
    @Transactional
    @Query("""
            UPDATE EfsElement AS ee SET ee.parentId = :parentId
            WHERE ee.id = :id
            """)
    void setParent(@NotNull Long id, @NotNull Long parentId);

    @Query("""
            SELECT ee FROM EfsElement ee
            WHERE ee.parentId IN(:parentIds) AND ee.deleted = 0
            """)
    Collection<EfsElement> findChildren(Collection<Long> parentIds);

    @Query("""
            SELECT ee FROM EfsElement ee
            WHERE ee.parentId IN(:parentIds)
            """)
    Collection<EfsElement> findChildrenWithDeleted(Collection<Long> parentIds);

    @Query("""
            SELECT COUNT(e.costGroup) FROM EfsElement as e
            WHERE e.costGroup LIKE %:costGroup
            """)
    Long countCostGroupConstraintIssues(String costGroup);

    @Query("""
            SELECT COUNT(e.setKey) FROM EfsElement as e
            WHERE e.setKey LIKE %:setKey
            """)
    Long countSetKeyConstraintIssues(String setKey);

    List<EfsElement> findByVehiclePartListIdIn(Collection<Long> vehiclePartListsId);
}
