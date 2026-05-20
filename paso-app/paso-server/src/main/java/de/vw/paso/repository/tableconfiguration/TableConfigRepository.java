package de.vw.paso.repository.tableconfiguration;

import java.util.List;

import de.vw.paso.tableconfig.TableConfig;
import de.vw.paso.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableConfigRepository extends JpaRepository<TableConfig, Long> {

  List<TableConfig> findByUserOrIsPublicTrue(User user);
}
