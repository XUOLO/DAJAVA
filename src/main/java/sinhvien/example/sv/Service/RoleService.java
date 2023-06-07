package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    public List<Role> getAllRole(){

        return roleRepository.findAll();
    }
    public Role getRole(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}