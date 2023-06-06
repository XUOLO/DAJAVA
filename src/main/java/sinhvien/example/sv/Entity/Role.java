package sinhvien.example.sv.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Table(name="role")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "name",length = 50,nullable = false)
    @Size(max = 50, message = "Your name must be less than 50 character")
    @NotBlank(message = "Your name is required")
    private String name;

    @Column(name = "description",length = 250 )
    @Size(max = 250, message = "description must be less than 250 character")
    @NotBlank(message = "Your name is required")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
