package com.bob.bankapispringapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "customer_properties",
        uniqueConstraints={@UniqueConstraint(columnNames={"customer_id", "property_key"})})
public class CustomerProps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @Column(name = "property_key")
    private String propertyKey;

    @NotNull
    @Column(name = "property_value")
    private String propertyValue;
}
