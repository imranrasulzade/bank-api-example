package com.bob.bankapispringapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ClientForExcel {
    private String username;

    private String name;

    private String surname;

    private String email;

    private String phone;

    private Date birthdate;

    private Integer status;
}
