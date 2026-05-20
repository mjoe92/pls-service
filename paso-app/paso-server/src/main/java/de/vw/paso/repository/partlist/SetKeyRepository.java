package de.vw.paso.repository.partlist;

import java.util.Collection;

import de.vw.paso.partlist.domain.SetKey;
import de.vw.paso.partlist.domain.SetKeyVersionPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetKeyRepository extends JpaRepository<SetKey, SetKeyVersionPK> {

    Collection<SetKey> findAllByIdVersion(long version);

    Collection<SetKey> findAllByParentSetKeyAndIdVersion(String parentSetKey, long version);

    Collection<SetKey> findByIdVersion(Long version);
}
