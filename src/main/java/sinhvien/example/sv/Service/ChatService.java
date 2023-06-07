package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Chat;
import sinhvien.example.sv.Entity.Department;
import sinhvien.example.sv.Entity.User;
import sinhvien.example.sv.Repository.ChatRepository;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public Chat getChat(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    public List<Chat> getChatsByUserAndDepartment(User user, Department department) {
        return chatRepository.findByUserAndDepartment(user, department);
    }

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    public void deleteChat(Long id) {
        chatRepository.deleteById(id);
    }
}