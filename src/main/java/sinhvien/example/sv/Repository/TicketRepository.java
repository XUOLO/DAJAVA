package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.Ticket;
import sinhvien.example.sv.Entity.User;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUserAndDepartment(User user, Department department);

    //    List<Ticket> findByStatus(Ticket.TicketStatus status);
    List<Ticket> findByUserId(Long userId);

    @Query("SELECT t FROM Ticket t JOIN t.department d WHERE CONCAT(t.subject, d.name, t.id) LIKE %?1%")
    List<Ticket> findAll(String keyword);







}