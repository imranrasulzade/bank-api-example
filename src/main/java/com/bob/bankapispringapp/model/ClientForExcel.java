package com.bob.bankapispringapp.model;

import lombok.Data;

import java.util.Date;

@Data
public class ClientForExcel {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Date birthdate;
    private String status;
}
