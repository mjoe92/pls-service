package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EfsElementHistoryRepository extends JpaRepository<EfsElementHistory, Long> {

    Collection<EfsElementHistory> findByEfsElementIdOrderByRevision(long efsElementId);

    Collection<EfsElementHistory> findAllByVehiclePartListIdOrderByRevision(long vehiclePartListId);

    //todo: can be deleted
    List<EfsElementHistory> findAllByVehiclePartListIdAndEfsElementIdOrderByRevision(long vehiclePartListId,
            Long efsElementId);

    @Query("""
            SELECT ee FROM EfsElementHistory eeh
            INNER JOIN eeh.efsElement ee
            WHERE ee.vehiclePartListId = :vehiclePartListId
            """)
    Collection<EfsElement> findAllEfsElementByEfsElementVehiclePartListId(long vehiclePartListId);

    Collection<EfsElementHistory> findAllByVehiclePartListIdAndRevisionGreaterThanEqual(Long vehiclePartListId,
            Long revision);

    @Query("""
            SELECT history.id, history.efsElement.id, history.nodeLabel, history.revision hrev, efs.revision curr_rev
            FROM EfsElementHistory history
            LEFT join EfsElement efs ON history.efsElement.id = efs.id
            WHERE history.vehiclePartListId = :vehiclePartListId
            AND efs.revision > :revision
            AND history.revision = (
              SELECT max(element.revision) FROM EfsElementHistory element
              WHERE element.vehiclePartListId = :vehiclePartListId
              AND element.efsElement.id = history.efsElement.id
              AND element.revision < (:revision + 1) )
            """)
    Collection<EfsElementHistoryToGoTo2DTO> findHistoryElementsToGoTo2(Long vehiclePartListId, Long revision);

    void deleteByVehiclePartListIdIn(Collection<Long> vehiclePartListIds);
}
