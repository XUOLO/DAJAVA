package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT t FROM Ticket t JOIN t.department d WHERE CONCAT(t.subject, d.name, t.code, t.name) LIKE %?1%")
    List<Ticket> findAll(String keyword);


    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> countTicketByStatus();

//    @Query(value ="Select Sum(DonGia*SoLuong) From chitiethoadon cthd JOIN hoadon hd on hd.MaHD = cthd.MaHD WHERE hd.NgayDat like %:date% ", nativeQuery = true)
//    String totalTicket_Date(@Param("date") String date);
//
//    @Query(value ="Select Sum(DonGia*SoLuong) From chitiethoadon cthd JOIN hoadon hd on hd.MaHD = cthd.MaHD WHERE hd.NgayDat like %:month% ", nativeQuery = true)
//    String totalTicket_Month(@Param("month") String month);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = '1'")
    Long countOpenTickets();
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = '2'")
    Long countInProgressTickets();
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = '3'")
    Long countResolveTickets();
}