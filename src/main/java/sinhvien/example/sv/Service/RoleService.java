package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.RoleRepository;
import sinhvien.example.sv.Repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    public List<Role> getAllRole(){

        return roleRepository.findAll();
    }
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
    public Role getRole(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    public void updateUserRole(User user, Role role) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.clear();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}