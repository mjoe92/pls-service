package de.vw.paso.repository.masterdata;

import de.vw.paso.pr.PrNumberFamily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrNumberFamilyRepository extends JpaRepository<PrNumberFamily, Long> {

    PrNumberFamily findFirstByNameOrderByIdDesc(String name);

    boolean existsByNameAndDescriptionDeAndDescriptionEn(String name, String descriptionDe, String descriptionEn);
}
