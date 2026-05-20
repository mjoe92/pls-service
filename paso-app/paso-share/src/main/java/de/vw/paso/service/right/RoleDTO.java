package de.vw.paso.service.right;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.vw.paso.service.user.UserDTO;

public class RoleDTO {

    private Long id;
    private String name;
    private String description;
    private Set<UserDTO> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<UserDTO> getUsers() {
        return users;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsers(Set<UserDTO> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleDTO roleDTO = (RoleDTO) o;
        return Objects.equals(id, roleDTO.id) && Objects.equals(name, roleDTO.name) && Objects.equals(description,
            roleDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
