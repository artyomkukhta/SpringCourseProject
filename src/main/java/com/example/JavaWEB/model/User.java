package com.example.JavaWEB.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;
    @NotNull
    @Size(min=3, message = "username must be at least 3 characters long")
    private String username;
    @NotNull
    @Size(min=3, message = "password must be at least 3 characters long")
    private String password;
    /*@NotNull
    @Size(min=3, message = "password must be at least 3 characters long")*/
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleClass role;
//    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    //   @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Card> cards;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Role getRole() {
        return role.getRole();
    }

    public void setRole(RoleClass role) {
        this.role = role;
    }
}
