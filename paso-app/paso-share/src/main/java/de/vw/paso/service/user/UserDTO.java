package de.vw.paso.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.vw.paso.core.domain.AbstractModifiableDTO;
import de.vw.paso.service.right.RoleDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.utility.StringConstant;

public class UserDTO extends AbstractModifiableDTO<String> {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private Set<RoleDTO> roles;
    private Collection<UserGroupDTO> userGroups;
    private String inactivityInfo;

    public UserDTO() {
        roles = new HashSet<>();
        userGroups = new ArrayList<>();
    }

    public static Comparator<? super UserDTO> getComparator() {
        return Comparator.comparing(UserDTO::toString);
    }

    //todo: this is not correct -> admin should NOT be defined by id here!!!
    @JsonIgnore
    public boolean isAdmin() {
        return getRoles().stream().map(RoleDTO::getId).collect(Collectors.toSet()).contains(1L);
    }

    @Override
    public String toString() {
        return lastName + StringConstant.COMMA_SPACE + firstName + StringConstant.COMMA_SPACE_LEFT_PARENTHESIS + id
                + StringConstant.RIGHT_PARENTHESIS;
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

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public Collection<UserGroupDTO> getUserGroups() {
        return userGroups;
    }

    public String getInactivityInfo() {
        return inactivityInfo;
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

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }

    public void setUserGroups(List<UserGroupDTO> userGroups) {
        this.userGroups = userGroups;
    }

    public void setInactivityInfo(String inactivityInfo) {
        this.inactivityInfo = inactivityInfo;
    }
}
