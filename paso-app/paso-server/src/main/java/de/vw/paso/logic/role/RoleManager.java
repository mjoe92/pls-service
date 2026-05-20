package de.vw.paso.logic.role;

import java.util.List;

import de.vw.paso.repository.right.RoleRepository;
import de.vw.paso.right.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleManager {

    private final RoleRepository roleRepository;

    public RoleManager(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    public Role getRoleWithUsers(Long roleId) {
        return roleRepository.getRoleWithUsers(roleId);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> getRolesByIds(List<Long> roleIds) {
        return roleRepository.findAllById(roleIds);
    }
}
