package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.Ticket;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public List<Ticket> getTicketsByUserAndDepartment(User user, Department department) {
        return ticketRepository.findByUserAndDepartment(user, department);
    }

//    public List<Ticket> getTicketsByStatus(Ticket.TicketStatus status) {
//        return ticketRepository.findByStatus(status);
//    }

    public Ticket saveTicket(Ticket ticket) {
        ticketRepository.save(ticket);
        return ticket;
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> getTicketsForUser(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<Ticket> searchTicketAdmin(String keyword) {

        if(keyword!=null){
                return ticketRepository.findAll(keyword);
        }
        return ticketRepository.findAll();
    }
    public List<Ticket> searchTickets(Long userId, String keyword) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId); // Lấy danh sách tất cả các ticket của user
        List<Ticket> matchedTickets = new ArrayList<>(); // Tạo danh sách rỗng để lưu các ticket khớp với từ khóa tìm kiếm

        // Duyệt qua danh sách các ticket của user để tìm các ticket khớp với từ khóa tìm kiếm
        for (Ticket ticket : tickets) {
            String keywordLowerCase = keyword.toLowerCase();
            String subjectLowerCase = ticket.getSubject().toLowerCase();
            String phoneLowerCase = ticket.getPhone().toLowerCase();
            String idLowerCase = ticket.getId().toString().toLowerCase();
            String departmentLowerCase = ticket.getDepartment().getName().toLowerCase();
            if (subjectLowerCase.contains(keywordLowerCase) || phoneLowerCase.contains(keywordLowerCase) || idLowerCase.contains(keywordLowerCase) || departmentLowerCase.contains(keywordLowerCase)) {
                matchedTickets.add(ticket); // Thêm ticket vào danh sách các ticket khớp
            }
        }

        return matchedTickets; // Trả về danh sách các ticket khớp với từ khóa tìm kiếm
    }


    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }
    public Page<Ticket> findPaginatedRequest(int pageNo, int pageSize){
        Pageable pageable= PageRequest.of(pageNo - 1,pageSize);
        return this.ticketRepository.findAll(pageable);
    }
}