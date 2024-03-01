package com.bob.bankapispringapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String phone;

    @NotNull
    private LocalDate birthdate;

    @NotNull
    private Integer status;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_authorities",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    private Set<Authority> authorities = new HashSet<>();

}