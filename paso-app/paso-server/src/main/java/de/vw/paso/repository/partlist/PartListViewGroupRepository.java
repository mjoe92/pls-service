package de.vw.paso.repository.partlist;

import java.util.List;

import de.vw.paso.partlist.domain.PartListViewGroup;
import de.vw.paso.partlist.domain.PartListViewMode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartListViewGroupRepository extends JpaRepository<PartListViewGroup, Long> {

  List<PartListViewGroup> findAllByPartListViewMode(PartListViewMode partListViewMode);

}
