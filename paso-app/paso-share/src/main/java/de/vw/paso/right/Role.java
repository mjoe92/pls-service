package de.vw.paso.right;

import java.util.HashSet;
import java.util.Set;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = Role.TABLE_ROLE)
public class Role extends AbstractEntity<Long> {

    static final String TABLE_ROLE = "ROLE";

    private static final String PK_ROLE_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = PK_ROLE_ID)
    private Long id;

    @Column(name = COLUMN_NAME)
    private String name;

    @Column(name = COLUMN_DESCRIPTION)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST }, mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
