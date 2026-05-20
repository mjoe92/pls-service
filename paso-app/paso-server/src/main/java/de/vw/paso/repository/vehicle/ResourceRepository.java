package de.vw.paso.repository.vehicle;

import de.vw.paso.user.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> { }
