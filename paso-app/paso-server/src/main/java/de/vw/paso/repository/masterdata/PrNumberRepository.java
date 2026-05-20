package de.vw.paso.repository.masterdata;

import java.util.Collection;

import de.vw.paso.pr.PrNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrNumberRepository extends JpaRepository<PrNumber, Long> {

    Collection<PrNumber> findByNameIn(Collection<String> names);

    PrNumber findFirstByNameOrderByIdDesc(String name);

    boolean existsByNameAndDescriptionDeAndDescriptionEn(String number, String descriptionDe, String descriptionEn);
}
