package sinhvien.example.sv.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sinhvien.example.sv.Entity.Chat;
import sinhvien.example.sv.Entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChat(Chat chat);
}