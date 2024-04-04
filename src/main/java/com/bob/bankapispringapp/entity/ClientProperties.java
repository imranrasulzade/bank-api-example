package com.bob.bankapispringapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "client_properties",
        uniqueConstraints={@UniqueConstraint(columnNames={"client_id", "property_key"})})
public class ClientProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @NotNull
    @Column(name = "property_key")
    private String propertyKey;

    @NotNull
    private String propertyValue;
}
