package sinhvien.example.sv.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username",length = 50,nullable = false,unique = true)
    @NotBlank(message = "Username is required")
    @Size(max=50,message = "Username must be less than 50 character")
    private  String username;

    @Column(name = "password",length = 250,nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Book> getBooks() {
//        return books;
//    }
//
//    public void setBooks(List<Book> books) {
//        this.books = books;
//    }

    @Column(name = "email",length = 50,nullable = false)
    @Size(max = 50, message = "Email must be less than 50 character")
    @NotBlank(message = "Your email is required")
    private String email;

    @Column(name = "name",length = 50,nullable = false)
    @Size(max = 50, message = "Your name must be less than 50 character")
    @NotBlank(message = "Your name is required")
    private String name;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    private List<Book> books= new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Chat> chats;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets;

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

}
