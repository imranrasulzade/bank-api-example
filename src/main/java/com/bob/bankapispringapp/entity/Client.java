package com.bob.bankapispringapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.cfg.defs.EmailDef;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
@Data
@NoArgsConstructor
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

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private List<ClientProperties> clientPropertiesList;




    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_authorities",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    private Set<Authority> authorities = new HashSet<>();



    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }
}