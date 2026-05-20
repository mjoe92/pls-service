package de.vw.paso.repository.partlist;

import java.util.List;

import de.vw.paso.partlist.domain.CostGroup;
import de.vw.paso.partlist.domain.CostGroupVersionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CostGroupRepository extends JpaRepository<CostGroup, CostGroupVersionPK> {

    @Query("""
            SELECT c FROM CostGroup AS c
            WHERE (
              c.id.version = (
                SELECT MAX(cc.id.version) FROM CostGroup AS cc
              )
            )""")
    List<CostGroup> findAllRelevant();

    List<CostGroup> findAllByIdVersion(long version);

    @Query("""
            SELECT c FROM CostGroup AS c
            WHERE c.id.costGroup LIKE :costGroup%
            AND c.id.version = :version
            """)
    List<CostGroup> findAllChildren(String costGroup, long version);

    @Query("""
            SELECT MAX(c.id.version) FROM CostGroup AS c
            """)
    Long findLastVersion();

    @Modifying
    @Transactional
    @Query("""
            UPDATE CostGroup AS c
            SET c.id.costGroup = :costGroup, c.description = :description, c.parent.id.costGroup = :parent
            WHERE c.id.costGroup = :oldCostGroup AND c.id.version = :version
            """)
    void updateCostGroups(String costGroup, long version, String description, String parent, String oldCostGroup);

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM CostGroup AS c
            WHERE c.id.costGroup LIKE :removedCostGroup%
            AND c.id.version = :version
            """)
    void removeCostGroups(String removedCostGroup, long version);

}
