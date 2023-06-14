package sinhvien.example.sv.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.RoleRepository;
import sinhvien.example.sv.Repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRoles(role);
    }

    public void saveUser(User user) {

        Set<Role> roles;

        if(user.getRoles() != null) {
            roles = user.getRoles();
        } else {
            roles = new HashSet<>();
        }
        Role customerRole = roleRepository.findByName("CUSTOMER");

        roles.add(customerRole); // Gán vai trò CUSTOMER cho tài khoản đăng kí
        user.setRoles(roles);

        userRepository.save(user);
    }
    public int countCustomers() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            for (Role role : user.getRoles()) {
                if (role.getName().equals("CUSTOMER")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
    public int countEmployeesAndAdmins() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            for (Role role : user.getRoles()) {
                if (role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
    public User editUser(User user) {
        // Lấy danh sách các vai trò được chọn trong form
        Set<Role> selectedRoles = user.getRoles();

        // Lấy thông tin người dùng từ cơ sở dữ liệu
        User existingUser = userRepository.findById(user.getId()).orElse(null);

        // Nếu thông tin người dùng không tồn tại, trả về null
        if (existingUser == null) {
            return null;
        }

        // Cập nhật thông tin người dùng từ form
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        // Xóa tất cả các vai trò của người dùng hiện tại
        existingUser.getRoles().clear();

        // Thêm tất cả các vai trò được chọn trong form vào đối tượng User
        existingUser.getRoles().addAll(selectedRoles);

        // Lưu thay đổi vào cơ sở dữ liệu
        return userRepository.save(existingUser);
    }
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("Không tìm thấy User với id = " + id);
        }
    }
    public void updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với id là " + id));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRoles(user.getRoles());

        userRepository.save(existingUser);
    }
    public List<User> GetAllUser(){
        return userRepository.findAll();
    }
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    public User getUserByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public String getPasswordByUsername(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, String.class);
    }

    public String getSaltByUsername(String username) {
        String sql = "SELECT salt FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, String.class);
    }
    public void updatePassword(String username, String newPasswordHash, String newSalt) {
        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE username = ?";
        jdbcTemplate.update(sql, newPasswordHash, newSalt, username);
    }
}
