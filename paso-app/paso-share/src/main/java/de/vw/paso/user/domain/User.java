package de.vw.paso.user.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.right.Role;
import de.vw.paso.utility.StringConstant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = User.TABLE_USER)
public final class User extends AbstractModifiableEntity<String> {

    static final String TABLE_USER = "PASO_USER";

    private static final String PK_USER_ID = "USER_ID";
    private static final String COLUMN_FIRST_NAME = "FIRST_NAME";
    private static final String COLUMN_LAST_NAME = "LAST_NAME";
    private static final String COLUMN_EMAIL = "EMAIL";
    private static final String COLUMN_ACTIVE = "ACTIVE";
    public static final String COST_CENTER = "COST_CENTER";
    public static final String COST_CENTER_CHANGED_AT = "COST_CENTER_CHANGED_AT";

    @Id
    @Column(name = PK_USER_ID)
    private String id;

    @Column(name = COLUMN_FIRST_NAME)
    private String firstName;

    @Column(name = COLUMN_LAST_NAME)
    private String lastName;

    @Column(name = COLUMN_EMAIL)
    private String email;

    @Column(name = COLUMN_ACTIVE)
    private Boolean active;

    @Column(name = COST_CENTER)
    private String costCenter;

    @Column(name = COST_CENTER_CHANGED_AT)
    private Timestamp costCenterChangedAt;

    @Transient
    private String preferredLanguage;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "user_role_mapping",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_role", referencedColumnName = "id") })
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private List<UserGroup> userGroups = new ArrayList<>();

    public boolean hasChanges(User user) {
        return !user.getFirstName().equals(firstName) || !user.getLastName().equals(lastName) || !user.getEmail()
                .equals(email);
    }

    public boolean costCenterChanged(User user) {
        return !user.getCostCenter().equals(costCenter);
    }

    @Override
    public String toString() {
        return lastName + StringConstant.COMMA_SPACE + firstName + StringConstant.COMMA_SPACE_LEFT_PARENTHESIS + id
                + StringConstant.RIGHT_PARENTHESIS;
    }

    public boolean isAdmin() {
        return roles != null && getRoles().stream().anyMatch(role -> role.getId() == 1L);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActive() {
        return active;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public Timestamp getCostCenterChangedAt() {
        return costCenterChangedAt;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public void setCostCenterChangedAt(Timestamp costCenterChangedAt) {
        this.costCenterChangedAt = costCenterChangedAt;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }
}
