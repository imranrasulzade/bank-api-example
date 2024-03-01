package com.bob.bankapispringapp.entity;

import com.bob.bankapispringapp.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = " Account number can not be null")
    @Size(min = 15, max = 25)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = " Currency can not be null")
    private Currency currency;

    @NotNull(message = "amount can not be null")
    private Double amount;

    @NotNull(message = "iban can not be null")
    private String iban;

    @NotNull(message = " Client can not be null")
    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @NotNull(message = " Branch can not be null")
    @JoinColumn(name = "branch_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Branch branch;

    @NotNull(message = "Status can not be null")
    private Integer status;



}
