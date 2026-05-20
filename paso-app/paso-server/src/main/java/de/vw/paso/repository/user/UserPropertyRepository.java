package de.vw.paso.repository.user;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface UserPropertyRepository extends JpaRepository<UserProperty, Long> {

    @Modifying
    @Transactional
    Long deleteByUserAndTypeAndUserData(User user, PropertyType type, String userData);

    @Modifying
    @Transactional
    Long deleteByTypeAndUserData(PropertyType type, String userData);

    @Modifying
    @Transactional
    void deleteByUserAndType(User user, PropertyType type);

    @Modifying
    @Transactional
    void deleteByUser(User user);

    @Modifying
    @Transactional
    int deleteByTimestampChangeBefore(Date date);

    UserProperty findFirstByTypeOrderByTimestampChange(PropertyType type);

    UserProperty findByUserAndTypeAndUserData(User user, PropertyType type, String userData);

    List<UserProperty> findByTypeAndUserData(PropertyType type, String userData);

    List<UserProperty> findAllByUserAndType(User user, PropertyType type);

    Long countByUserAndType(User user, PropertyType type);

    Optional<UserProperty> getByUserAndType(User user, PropertyType type);

    List<UserProperty> getByUser(User user);
}
