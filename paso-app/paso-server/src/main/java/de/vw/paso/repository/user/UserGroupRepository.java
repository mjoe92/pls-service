package de.vw.paso.repository.user;

import java.util.List;

import de.vw.paso.user.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

  List<UserGroup> findByName(String name);
}
