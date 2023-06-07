package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    List<User> findByRoles(Role role);
}