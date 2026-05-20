package de.vw.paso.repository.right;

import de.vw.paso.right.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("""
            SELECT r FROM Role r
            LEFT OUTER JOIN FETCH r.users
            WHERE r.id = :id
            """)
    Role getRoleWithUsers(Long id);

}
