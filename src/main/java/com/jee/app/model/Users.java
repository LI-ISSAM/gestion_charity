package com.jee.app.model;
import jakarta.persistence.*;
import com.jee.app.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data           
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Column(unique = true , nullable = false)  
    @Email
    private String email;

    @NotBlank
    private String motDePasse;

    @Enumerated(EnumType.STRING) 
    private Role role;

    private LocalDateTime createdAt;
    @Column(name="profile_picture")
    private String profilePicture;


    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }
    
    public String getEmail() {
    			return email;
    }
    public String getMotDePasse() {
				return motDePasse;
	}

 
}