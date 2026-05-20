package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.inspector.InspectorIgnore;
import de.vw.paso.partlist.domain.inspector.InspectorIgnorePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectorIgnoreRepository extends JpaRepository<InspectorIgnore, InspectorIgnorePK> {

  List<InspectorIgnore> findByEfsElementIdIn(Collection<Long> efsElementsIds);
}
