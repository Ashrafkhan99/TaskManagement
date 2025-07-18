package com.at.taskmanagement.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "users") // avoid MySQL conflict with reserved word "user"
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String username;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String password; // stored hashed

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "assignedTo")
    @JsonIgnore
    private List<Task> assignedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // In a real app, you would return a list of roles/authorities.
        // For now, we'll return an empty list as a placeholder.
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        // The UserDetails interface requires a getUsername() method.
        // We are returning the 'username' field of our User entity.
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // You can add logic here to check if the user account has expired.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // You can add logic here to check if the user account is locked.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // You can add logic here to check if the user's credentials (password) have expired.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // You can add logic here to check if the user is enabled or disabled.
        return true;
    }
}
