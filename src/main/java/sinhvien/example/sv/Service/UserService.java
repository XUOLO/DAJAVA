package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRoles(role);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> GetAllUser(){
        return userRepository.findAll();
    }
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
