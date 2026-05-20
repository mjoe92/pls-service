package de.vw.paso.repository.user;

import java.util.List;

import de.vw.paso.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

  @Query("select u from User u left outer join fetch u.roles where u.id = :id")
  User getUserWithRoles(@Param("id") String id);

  User findByIdIgnoreCase(String id);

  List<User> findAllByActive(Boolean active);
}
