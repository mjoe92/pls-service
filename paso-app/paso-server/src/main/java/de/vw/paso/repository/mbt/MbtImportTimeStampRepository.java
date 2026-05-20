package de.vw.paso.repository.mbt;

import java.util.Optional;

import de.vw.paso.service.pls.MbtImportTimeStamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MbtImportTimeStampRepository extends JpaRepository<MbtImportTimeStamp, Long> {

    Optional<MbtImportTimeStamp> findByFileName(String fileName);
}
