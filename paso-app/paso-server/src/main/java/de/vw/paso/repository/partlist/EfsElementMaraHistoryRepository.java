package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementMaraHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EfsElementMaraHistoryRepository extends JpaRepository<EfsElementMaraHistory, Long> {

    Collection<EfsElementMaraHistory> findAllByEfsElementMaraIdInOrderByRevision(Collection<Long> efsElementMaraIds);

    @Query("""
            SELECT ee FROM EfsElementMaraHistory eemh
            INNER JOIN EfsElement ee ON ee.efsElementMara.id = eemh.efsElementMara.id
            AND ee.vehiclePartListId = :vehiclePartListId
            """)
    Collection<EfsElement> findAllEfsElementByEfsElementVehiclePartListId(long vehiclePartListId);

    @Query("""
            SELECT history.id, history.efsElementMara.id, history.revision max_rev, mara.revision curr_rev
            FROM EfsElementMaraHistory history
            LEFT JOIN EfsElementMara mara ON history.efsElementMara.id = mara.id
            WHERE history.vehiclePartListId = :vehiclePartListId
            AND mara.revision > :revision
            AND history.revision = (
              SELECT max(element.revision) FROM EfsElementMaraHistory element
              WHERE element.vehiclePartListId = :vehiclePartListId
              AND element.efsElementMara.id = history.efsElementMara.id
              AND element.revision < (:revision + 1) )
            """)
    List<MaraHistoryElementToGoTo2DTO> findHistoryElementsToGoTo2(Long vehiclePartListId, Long revision);

    Collection<EfsElementMaraHistory> findAllByVehiclePartListIdAndRevisionGreaterThanEqual(Long partListId,
            Long revision);

    @Modifying
    @Transactional
    void deleteByVehiclePartListIdIn(Collection<Long> vehiclePartListIds);
}
