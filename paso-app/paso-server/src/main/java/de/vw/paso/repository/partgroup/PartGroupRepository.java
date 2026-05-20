package de.vw.paso.repository.partgroup;

import java.util.List;

import de.vw.paso.masterdata.domain.PartGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PartGroupRepository extends JpaRepository<PartGroup, Long> {

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM PartGroup
            WHERE mgr = :mgr
            """)
    void deleteMgr(Integer mgr);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM PartGroup
            WHERE mgr = :mgr AND ugr = :ugr
            """)
    void deleteUgr(Integer mgr, Integer ugr);

    @Query("""
            FROM PartGroup
            WHERE mgr = :mgr
            AND mgrEnd IS NULL
            AND ugr IS NOT NULL
            """)
    List<PartGroup> loadUgrByMgr(Integer mgr);

    @Query("""
            FROM PartGroup
            WHERE (
              category = :category
              AND mgr = :mgr
              AND mgrEnd IS NULL
              AND ugr IS NULL
            ) OR (
              category = :category
              AND mgr <= :mgr AND :mgr <= mgrEnd
              AND ugr IS NULL)
            """)
    PartGroup loadMgrByMgr(Integer category, Integer mgr);
}
