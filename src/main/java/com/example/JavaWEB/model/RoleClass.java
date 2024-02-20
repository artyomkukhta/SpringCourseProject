package com.example.JavaWEB.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class RoleClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    public RoleClass() {
    }

    @Autowired
    public RoleClass(Role role) {
        this.role = role;

    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
