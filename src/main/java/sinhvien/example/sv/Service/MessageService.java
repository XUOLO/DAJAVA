package sinhvien.example.sv.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sinhvien.example.sv.Entity.Chat;
import sinhvien.example.sv.Entity.Message;
import sinhvien.example.sv.Repository.MessageRepository;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message getMessage(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    public List<Message> getMessagesByChat(Chat chat) {
        return messageRepository.findByChat(chat);
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}