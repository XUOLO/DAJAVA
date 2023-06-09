package sinhvien.example.sv.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name",length = 250,nullable = false)
    @Size(max = 250, message = "name must be less than 250 character")
    @NotBlank(message = "Your name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "subject",length = 250,nullable = false)
    @Size(max = 250, message = "subject must be less than 250 character")
    @NotBlank(message = "Your subject is required")
    private String subject;

    @Column(name = "message",length = 500,nullable = false)
    @Size(max = 500, message = "message must be less than 500 character")
    @NotBlank(message = "Your message is required")
    private String message;

    @Column(name = "email",length = 150,nullable = false)
    @Size(max = 150, message = "Email must be less than 150 character")
    @NotBlank(message = "Your email is required")
    private String email;
    private String phone;

    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatusString() {
        switch (status) {
            case "1":
                return "Open";
            case "2":
                return "In Progress";
            case "3":
                return "Resolved";
            default:
                return "Unknown";
        }
    }

    public String getStatus() {
        return status;
    }

    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    // getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}