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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
