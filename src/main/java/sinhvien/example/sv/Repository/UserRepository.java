package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sinhvien.example.sv.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}