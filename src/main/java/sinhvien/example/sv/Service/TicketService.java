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
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }
    public Page<Ticket> findPaginatedRequest(int pageNo, int pageSize){
        Pageable pageable= PageRequest.of(pageNo - 1,pageSize);
        return this.ticketRepository.findAll(pageable);
    }
}