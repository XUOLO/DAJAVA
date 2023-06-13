package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sinhvien.example.sv.Entity.Role;
import sinhvien.example.sv.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    List<User> findAll();

    Optional<User> findById(Long id);
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    public User findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    User findByUsername(String username);
    List<User> findByRoles(Role role);
    List<User> findByRolesName(String roleName);





    Boolean existsByEmail(String email);



    boolean existsByUsername(String username);
}