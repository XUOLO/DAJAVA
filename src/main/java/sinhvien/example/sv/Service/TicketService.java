package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.Ticket;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.TicketRepository;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public List<Ticket> getTicketsByUserAndDepartment(User user, Department department) {
        return ticketRepository.findByUserAndDepartment(user, department);
    }

    public List<Ticket> getTicketsByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    public void saveTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
}