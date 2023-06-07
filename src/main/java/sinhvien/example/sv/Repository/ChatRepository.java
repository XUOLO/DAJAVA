package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sinhvien.example.sv.Entity.Chat;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.User;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByUserAndDepartment(User user, Department department);
}